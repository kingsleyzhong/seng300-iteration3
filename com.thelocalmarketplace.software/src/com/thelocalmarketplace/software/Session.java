package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.attendant.HardwareListener;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.FundsListener;
import com.thelocalmarketplace.software.items.ItemListener;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.membership.MembershipListener;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.receipt.ReceiptListener;
import com.thelocalmarketplace.software.weight.Weight;
import com.thelocalmarketplace.software.weight.WeightListener;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Class facade representing the session of a self-checkout station
 *
 * Can be started and canceled. Becomes frozen when a weight discrepancy
 * occurs and unfrozen when weight discrepancy is fixed.
 *
 * Contains the funds of the system, the weight of the system, and a list
 * representing all the products that have been added to the system as well
 * as the quantity of those items.
 *
 * Has add bulky item functionality
 *
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev : 30177060
 * Enioluwafe Balogun : 30174298
 * Subeg Chahal : 30196531
 * Jun Heo : 30173430
 * Emily Kiddle : 30122331
 * Anthony Kostal-Vazquez : 30048301
 * Jessica Li : 30180801
 * Sua Lim : 30177039
 * Savitur Maharaj : 30152888
 * Nick McCamis : 30192610
 * Ethan McCorquodale : 30125353
 * Katelan Ng : 30144672
 * Arcleah Pascual : 30056034
 * Dvij Raval : 30024340
 * Chloe Robitaille : 30022887
 * Danissa Sandykbayeva : 30200531
 * Emily Stein : 30149842
 * Thi My Tuyen Tran : 30193980
 * Aoi Ueki : 30179305
 * Ethan Woo : 30172855
 * Kingsley Zhong : 30197260
 *
 */
public class Session {
	public ArrayList<SessionListener> listeners = new ArrayList<>();
	private ArrayList<HardwareListener> hardwareListeners = new ArrayList<>();
	private AbstractSelfCheckoutStation scs;
	protected SessionState sessionState;
	private SessionState prevState;
	private boolean disableSelf = false; // when true: disable the Session when it ends
	private Funds funds;
	private Weight weight;
	private ItemManager manager;
	private Receipt receipt;
	private Membership membership;
	private String membershipNumber;
	private boolean hasMembership = false;
	private boolean requestApproved = false;
	private boolean itemAdded = false;
	private boolean itemRemoved = false;

	private class ItemManagerListener implements ItemListener {
		private Session outerSession;

		private ItemManagerListener(Session s) {
			outerSession = s;
		}

		@Override
		public void anItemHasBeenAdded(Product product, Mass mass, BigDecimal price) {
			itemAdded = true;
			weight.update(mass);
			funds.update(price);
			for (SessionListener l : listeners) {
				l.itemAdded(outerSession, product, mass, weight.getExpectedWeight(), funds.getItemsPrice());
			}
		}

		@Override
		public void anItemHasBeenRemoved(Product product, Mass mass, BigDecimal price) {
			itemRemoved = true;
			weight.removeItemWeightUpdate(mass);
			funds.removeItemPrice(price);
			for (SessionListener l : listeners) {
				l.itemRemoved(outerSession, product, mass, weight.getExpectedWeight(), funds.getItemsPrice());
			}
		}

		@Override
		public void aPLUCodeHasBeenEntered(PLUCodedProduct product) {
			sessionState = SessionState.ADD_PLU_ITEM;
			stateChanged();
			for (SessionListener l : listeners) {
				l.pluCodeEntered(product);
			}
		}
	}

	private class WeightDiscrepancyListener implements WeightListener {
		private Session outerSession;

		private WeightDiscrepancyListener(Session s) {
			outerSession = s;
		}
		
		/**
		 * Upon a weightDiscrepancy, session should freeze
		 * 
		 * If the Customer has declared their intention to add bags to the scale, then
		 * checks the bags instead.
		 */
		@Override
		public void notifyDiscrepancy() {
			// <0 means that actual weight on scale is greater than expected
			if (weight.getExpectedWeight().inGrams().doubleValue()<weight.getActualWeight().inGrams().doubleValue()) {
				if(itemRemoved) {
					for (SessionListener l : listeners) {
						l.removeItemFromScaleDiscrepancy(outerSession);
					}
				}
				else {
					for (SessionListener l : listeners) {
						l.discrepancy(outerSession, "There is too much weight on the scale. Please remove un-entered items.");;
					}
				}
			}
			else {// actual weight on scale is less than expected
				if(itemAdded) {
					for (SessionListener l : listeners) {
						l.addItemToScaleDiscrepancy(outerSession);
					}
				}
				else {
					for (SessionListener l : listeners) {
						l.discrepancy(outerSession, "There is not enough weight on the scale. Put that item back.");;
					}
				}
			}
			// Only needed when the customer wants to add their own bags (this is how
			// Session knows the bags' weight)
			if (sessionState == SessionState.ADDING_BAGS) {
				// This means that the bags are too heavy. Something should happen here. Perhaps
				// instead we need another call that notifies bags too heavyS
				return;
			}

			// signal attendant(s)
			notifyAttendant(Requests.WEIGHT_DISCREPANCY);

			// signal a discrepancy

			block();
		}

		/**
		 * Upon resolution of a weightDiscrepancy, session should resume
		 */
		@Override
		public void notifyDiscrepancyFixed() {
			itemAdded = false;
			itemRemoved = false;
			resume();
			for (SessionListener l : listeners) {
				l.discrepancyResolved(outerSession);
			}
		}

		@Override
		public void notifyBagsTooHeavy() {
			// tell attendant
			notifyAttendant(Requests.BAGS_TOO_HEAVY);
			block();

		}

	}

	private class PayListener implements FundsListener {
		private Session outerSession;

		private PayListener(Session s) {
			outerSession = s;
		}
		
		
		/**
		 * Signals to the system that the customer has payed the full amount. Ends the
		 * session.
		 */
		@Override
		public void notifyPaid() {
			end();
		}

		/**
		 * Called when there is not enough change (of any kind) avalaiable to handle
		 * payment
		 */
		@Override
		public void notifyInsufficentChange() {
			// notify attendant
			notifyAttendant(Requests.CANT_MAKE_CHANGE);
			block();

		}

		@Override
		public void notifyUpdateAmountDue(BigDecimal amount) {
			for (SessionListener l : listeners) {
				l.pricePaidUpdated(outerSession, amount);
			}
		}

	}

	private class PrinterListener implements ReceiptListener {

		@Override
		public void notifiyOutOfPaper() {
			notifyAttendant(Requests.CANT_PRINT_RECEIPT);
			if(sessionState != SessionState.PRE_SESSION)
			block();
		}

		@Override
		public void notifiyOutOfInk() {
			notifyAttendant(Requests.CANT_PRINT_RECEIPT);
			if(sessionState != SessionState.PRE_SESSION)
			block();
		}

		@Override
		public void notifiyPaperRefilled() {
			if(sessionState != SessionState.PRE_SESSION)
			resume();
		}

		@Override
		public void notifiyInkRefilled() {
			if(sessionState != SessionState.PRE_SESSION)
			resume();
		}

		@Override
		public void notifiyReceiptPrinted(int linesPrinted, int charsPrinted) {
			// Should notifyPaid() not wait until receipt is successfully printed to change
			// to PRE_SESSION?
			notifySessionEnd();
		}

	}

	/**
	 * Constructor for the session method. Requires to be installed on self-checkout
	 * system
	 * with logic to function
	 */
	public Session() {
		sessionState = SessionState.PRE_SESSION;
	}

	/**
	 * Setup method for the session used in installing logic on the system
	 * Initializes private variables to the ones passed. Initially has the session
	 * off, session unfrozen, and pay not enabled.
	 * 
	 * @param BarcodedItems
	 *                       A hashMap of barcoded products and their associated
	 *                       quantity in shopping cart
	 * @param funds
	 *                       The funds used in the session
	 * @param weight
	 *                       The weight of the items and actual weight on the scale
	 *                       during the session
	 * 
	 * 
	 * @param receiptPrinter
	 *                       The PrintReceipt behavior
	 * 
	 * @param IremManager
	 *                       The software for managing adding and removing items
	 */
	public void setup(ItemManager manager, Funds funds, Weight weight, Receipt receiptPrinter, Membership membership,
			AbstractSelfCheckoutStation scs) {
		this.manager = manager;
		this.funds = funds;
		this.weight = weight;
		this.weight.register(new WeightDiscrepancyListener(this));
		this.funds.register(new PayListener(this));
		this.manager.register(new ItemManagerListener(this));
		this.receipt = receiptPrinter;
		this.receipt.register(new PrinterListener());
		this.membership = membership;
		membership.register((membershipNumber) -> {
			Session.this.membershipNumber = membershipNumber;
			Session.this.hasMembership = true;
		});
		this.scs = scs;
	}

	/**
	 * Sets the session to have started, allowing customer to interact with station
	 */
	public void start() {
		// signal about to start + wait for prediction to finish?

		sessionState = SessionState.IN_SESSION;
		stateChanged();
		manager.setAddItems(true);
		hasMembership = false;
		membershipNumber = null;
		manager.clear();
		funds.clear();
		weight.clear();
	}

	/**
	 * Cancels the current session and resets the current session
	 */
	public void cancel() {
		if (sessionState == SessionState.IN_SESSION) {
			sessionState = SessionState.PRE_SESSION;
			stateChanged();
			manager.setAddItems(false);
		} else if(sessionState == SessionState.BULKY_ITEM) {
			sessionState = SessionState.BLOCKED;
		}
		else if (sessionState != SessionState.BLOCKED) {
			sessionState = SessionState.IN_SESSION;
			stateChanged();
			weight.cancelAddBags();
			manager.setAddItems(true);
		}
	}

	/**
	 * Blocks the current session, preventing further action from the customer
	 */
	private void block() {
		prevState = sessionState;
		sessionState = SessionState.BLOCKED;
		stateChanged();
		manager.setAddItems(false);
	}

	private void end() {
		prevState = sessionState;
		sessionState = SessionState.PRE_SESSION;
		stateChanged();
		receipt.printReceipt(getItems());

		
		for(SessionListener l:listeners) {
			l.sessionEnded(this);
		}
		// if the session is slated to be disabled, do that
		if (disableSelf) {
			disable();
		}
	}

	/**
	 * Resumes the session, allowing the customer to continue interaction
	 */
	private void resume() {
		if (funds.isPay()) {
			sessionState = prevState;
		} else {
			sessionState = SessionState.IN_SESSION;
			stateChanged();
			manager.setAddItems(true);
		}
	}

	/**
	 * Enters the adding membership mode for the customer.
	 *
	 * @throws InvalidActionException
	 */
	public void enteringMembership() {
		if (sessionState == SessionState.IN_SESSION) {
			membership.setAddingItems(true);
		} else {
			throw new InvalidActionException("Cannot enter membership if session is not in adding items state");
		}
	}

	/**
	 * Places a previously disabled session into the PRE_SESSION state
	 * For use after clearing hardware issues that left the station disabled.
	 */
	public void enable() {
		// sets the session's state to PRE_SESSION
		if (this.sessionState == SessionState.DISABLED) {
			this.sessionState = SessionState.PRE_SESSION;
			stateChanged();
			disableSelf = false;
		}
	}

	/**
	 * Places this session into the DISABLED state. While in the DISABLED state no
	 * functions
	 * should be able to occur.
	 * 
	 * If the session is currently running/active than the session cannot be
	 * disabled until it
	 * has finished running
	 * 
	 */
	public void disable() {
		// sets the session's state to DISABLED
		if (this.sessionState == SessionState.PRE_SESSION) {
			this.sessionState = SessionState.DISABLED;
			stateChanged();
		} else {
			disableSelf = true;
		}
	}

	/**
	 * Enters the cash payment mode for the customer. Prevents customer from adding
	 * further
	 * items by freezing session.
	 */
	public void payByCash() {
		if (sessionState == SessionState.IN_SESSION) {
			if (!manager.getItems().isEmpty()) {
				sessionState = SessionState.PAY_BY_CASH;
				stateChanged();
				funds.setPay(true);
				funds.enableCash();
				manager.setAddItems(false);
			} else {
				throw new CartEmptyException("Cannot pay for an empty order");
			}
		}
	}

	/**
	 * Enters the card payment mode for the customer. Prevents customer from adding
	 * further
	 * items by freezing session. Can also enter after paying some cash.
	 */
	public void payByCard() {
		if (sessionState == SessionState.IN_SESSION || sessionState == SessionState.PAY_BY_CASH) {
			if (!manager.getItems().isEmpty()) {
				sessionState = SessionState.PAY_BY_CARD;
				stateChanged();
				funds.setPay(true);
				manager.setAddItems(false);
			} else {
				throw new CartEmptyException("Cannot pay for an empty order");
			}
		}
	}

	/**
	 * The customer indicates they want to add a bag by calling addBags
	 * Changes the state of the Session to "ADDING_BAGS"
	 * System is now waiting for bags to be added to the bagging area.
	 * 
	 */
	public void addBags() {
		if (sessionState == SessionState.IN_SESSION) {
			sessionState = SessionState.ADDING_BAGS;
			stateChanged();
			weight.addBags();
		}
		// else: nothing changes about the Session's state
	}

	public void cancelAddBags() {
		if (sessionState == SessionState.ADDING_BAGS) {
			weight.cancelAddBags();
		}
	}
	
	/**
	 * The customer indicates they wish to purchase reusable bags as a part of their interaction.
	 * Customer must indicate the number of bags they want to purchase. 
	 * System only supports one bag type.
	 * 
	 * @param num
	 * 				the number of bags the customer wants to buy
	 * 
	 * 
	 */
	public void purchasebags(int num) {
		if (sessionState == SessionState.IN_SESSION) {
			// signal item manager somehow		
			// enter the addBags() state
			addBags();
		}	
	}
	
	// Move to receiptPrinter class (possible rename of receiptPrinter to just reciept
	public void printReceipt() {
		receipt.printReceipt(manager.getItems());
	}

	/**
	 * Subtracts the weight of the bulky item from the total expected weight
	 * of the system
	 * notifies that the event has happened
	 */
	public void addBulkyItem() {
		// Only able to add when in a discrepancy after adding bags
		if (sessionState == SessionState.BLOCKED && !requestApproved) {
			sessionState = SessionState.BULKY_ITEM;
			stateChanged();
			notifyAttendant(Requests.BULKY_ITEM);
		} else if (sessionState == SessionState.BULKY_ITEM) {
			if (requestApproved) {
				requestApproved = false;
				// subtract the bulky item weight from total weight if assistant has approved
				Mass bulkyItemWeight = this.weight.getLastWeightAdded();
				this.weight.subtract(bulkyItemWeight);
				manager.addBulkyItem();
			} else {
				// assistant has not approved the request. Do nothing
				return;
			}

			// resume session
			this.resume();
		}
	}

	/**
	 * method to allow assistant to approve customer requests
	 */
	public void attendantApprove(Requests request) {
		requestApproved = true;
		if (request == Requests.BULKY_ITEM) {
			addBulkyItem();
		}
	}

	/**
	 * Abstract notification method that tells any registered listeners about the
	 * request of Session.
	 * This is done to reduce redundancy, as there are many possible requests that
	 * could be made of the attendant
	 * 
	 * @param request specific instance of the Requests ennum related to the current
	 *                issues within Session
	 */
	public void notifyAttendant(Requests request) {
		for (SessionListener l : listeners) {
			//l.getRequest(this, request);
		}
	}
	
	private void notifySessionEnd() {
		for (SessionListener l : listeners) {
			l.sessionEnded(this);
		}
	}
	
	public void stateChanged() {
		for (SessionListener l : listeners) {
			l.sessionStateChanged();
		}
	}

	/**
	 * Called when hardware for the session is opened
	 */
	public void openHardware() {
		for (HardwareListener l: hardwareListeners) {
			l.aStationHasBeenOpened();
		}
	}

	/**
	 * Called when hardware for the session is closed
	 */
	public void closeHardware() {
		for (HardwareListener l : hardwareListeners) {
			l.aStationHasBeenClosed();
		}
	}
	
	/**
	 * User demonstrates they wish to ask the attendent for help
	 */
	public void askForHelp() {
		notifyAttendant(Requests.HELP_REQUESTED);
	}
	/**
	 * getter methods
	 */
	public boolean getRequestApproved() {
		return this.requestApproved;
	}

	public HashMap<Product, BigInteger> getItems() {
		return manager.getItems();
	}

	public HashMap<BarcodedProduct, Integer> getBulkyItems() {
		return manager.getBulkyItems();
	}

	public Funds getFunds() {
		return funds;
	}

	public Weight getWeight() {
		return weight;
	}
	

	public ItemManager getManager() {
		return manager;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public Membership getMembership() {
		return membership;
	}

	public String getMembershipNumber() {
		return membershipNumber;
	}

	public boolean membershipEntered() {
		return hasMembership;
	}

	public AbstractSelfCheckoutStation getStation() {
		return scs;
	}



	/**
	 * getter for session state
	 *
	 * @return
	 *         Session State
	 */
	public SessionState getState() {
		return sessionState;
	}
	
	public SessionState getPrevState() {
		return prevState;
	}

	// register listeners
	public final synchronized void register(SessionListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");
		listeners.add(listener);
	}

	// de-register listeners
	public final synchronized void deRegister(SessionListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");
		listeners.remove(listener);
	}
	
	public ArrayList<SessionListener> getListeners(){
		return listeners;
	}

	public final synchronized void registerHardwareListener(HardwareListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");
			hardwareListeners.add(listener);
	}

	public final synchronized void deRegisterHardwareListener(HardwareListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");
			hardwareListeners.remove(listener);
	}

}
