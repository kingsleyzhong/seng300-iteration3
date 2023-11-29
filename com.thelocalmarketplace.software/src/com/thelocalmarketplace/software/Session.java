package com.thelocalmarketplace.software;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jjjwelectronics.Mass;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.coin.CoinSlot;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
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
	private AbstractSelfCheckoutStation scs;
	private SessionState sessionState;
	private SessionState prevState;
	private BarcodedProduct lastProduct;
	private Funds funds;
	private Weight weight;
	private ItemManager manager;
	private Receipt receiptPrinter;
	private Membership membership;
	private String membershipNumber;
	private boolean hasMembership = false;
	private Requests request = Requests.NO_REQUEST;
	private boolean requestApproved = false;

	private class ItemManagerListener implements ItemListener {

		@Override
		public void anItemHasBeenAdded(Mass mass, BigDecimal price) {
			weight.update(mass);
			funds.update(price);
		}

		@Override
		public void anItemHasBeenRemoved(Mass mass, BigDecimal price) {
			weight.removeItemWeightUpdate(mass);
			funds.removeItemPrice(price);
		}

	}

	private class WeightDiscrepancyListener implements WeightListener {

		/**
		 * Upon a weightDiscrepancy, session should freeze
		 * 
		 * If the Customer has declared their intention to add bags to the scale, then
		 * checks
		 * the bags instead.
		 */
		@Override
		public void notifyDiscrepancy() {
			// Only needed when the customer wants to add their own bags (this is how
			// Session knows the bags' weight)
			if (sessionState == SessionState.ADDING_BAGS) {
				// This means that the bags are too heavy. Something should happen here. Perhaps
				// instead we need another call that notifies bags too heavyS
				return;
			}
			block();
		}

		/**
		 * Upon resolution of a weightDiscrepancy, session should resume
		 */
		@Override
		public void notifyDiscrepancyFixed() {
			resume();
		}

	}

	private class PayListener implements FundsListener {

		/**
		 * Signals to the system that the customer has payed the full amount. Ends the
		 * session.
		 */
		@Override
		public void notifyPaid() {
			sessionState = SessionState.PRE_SESSION;
		}

	}

	private class PrinterListener implements ReceiptListener {

		@Override
		public void notifiyOutOfPaper() {
			block();
		}

		@Override
		public void notifiyOutOfInk() {
			block();
		}

		@Override
		public void notifiyPaperRefilled() {
			resume();
		}

		@Override
		public void notifiyInkRefilled() {
			resume();
		}

		@Override
		public void notifiyReceiptPrinted() {
			// Should notifyPaid() not wait until receipt is successfully printed to change
			// to PRE_SESSION?
			sessionState = SessionState.PRE_SESSION;
		}

	}

	private class MemberListener implements MembershipListener {
		/** Sets the membership number for the session.*/
		@Override
		public void membershipEntered(String membershipNumber) {
			Session.this.membershipNumber = membershipNumber;
			Session.this.hasMembership = true;
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
	 * Constructor for session that also allows the MAX BAG WEIGHT to be defined
	 * 
	 * @params maxBagWeight
	 *         double representing the expected weight of a bag (in grams)
	 */
	public Session(double maxBagWeight) {
		weight.configureMAXBAGWEIGHT(maxBagWeight);
		sessionState = SessionState.PRE_SESSION;

	}

	/**
	 * Constructor for session that also allows the MAX BAG WEIGHT to be defined
	 * 
	 * @param maxBagWeight
	 *                     long representing the expected weight of a bag (in
	 *                     micrograms)
	 */
	public Session(long maxBagWeight) {
		weight.configureMAXBAGWEIGHT(maxBagWeight);
		sessionState = SessionState.PRE_SESSION;
	}

	/**
	 * Setup method for the session used in installing logic on the system
	 * Initializes private variables to the ones passed. Initially has the session
	 * off, session unfrozen, and pay not enabled.
	 * 
	 * @param BarcodedItems
	 *                      A hashMap of barcoded products and their associated
	 *                      quantity in shopping cart
	 * @param funds
	 *                      The funds used in the session
	 * @param weight
	 *                      The weight of the items and actual weight on the scale
	 *                      during the session
	 * 
	 * @param Receipt
	 *                      The PrintReceipt behavior
	 */
	public void setup(ItemManager manager, Funds funds, Weight weight, Receipt receiptPrinter, Membership membership,
			AbstractSelfCheckoutStation scs) {
		this.manager = manager;
		this.funds = funds;
		this.weight = weight;
		this.weight.register(new WeightDiscrepancyListener());
		this.funds.register(new PayListener());
		this.manager.register(new ItemManagerListener());
		this.receiptPrinter = receiptPrinter;
		this.receiptPrinter.register(new PrinterListener());
		this.membership = membership;
		membership.register(new MemberListener());
		this.scs = scs;
	}

	/**
	 * Sets the session to have started, allowing customer to interact with station
	 */
	public void start() {
		sessionState = SessionState.IN_SESSION;
		manager.setAddItems(true);
		hasMembership = false;
		membershipNumber = null;
		// manager.clear();
		// funds.clear();
		// weight.clear();
	}

	/**
	 * Cancels the current session and resets the current session
	 */
	public void cancel() {
		if (sessionState == SessionState.IN_SESSION) {
			sessionState = SessionState.PRE_SESSION;
		} else if (sessionState != SessionState.BLOCKED) {
			sessionState = SessionState.IN_SESSION;
			weight.cancel();
		}
	}

	/**
	 * Blocks the current session, preventing further action from the customer
	 */
	private void block() {
		prevState = sessionState;
		sessionState = SessionState.BLOCKED;
		manager.setAddItems(false);
	}

	/**
	 * Resumes the session, allowing the customer to continue interaction
	 */
	private void resume() {
		if (funds.isPay()) {
			sessionState = prevState;
		} else {
			sessionState = SessionState.IN_SESSION;
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
	 * Enters the cash payment mode for the customer. Prevents customer from adding
	 * further
	 * items by freezing session.
	 */
	public void payByCash() {
		if (sessionState == SessionState.IN_SESSION) {
			if (!manager.getItems().isEmpty()) {
				sessionState = SessionState.PAY_BY_CASH;
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
	 * items by freezing session.
	 * 
	 * @throws DisabledException
	 * @throws NoCashAvailableException
	 * @throws CashOverloadException
	 */
	public void payByCard() throws CashOverloadException, NoCashAvailableException, DisabledException {
		if (sessionState == SessionState.IN_SESSION) {
			if (!manager.getItems().isEmpty()) {
				sessionState = SessionState.PAY_BY_CARD;
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
			weight.addBags();
		}
		// else: nothing changes about the Session's state
	}

	// Move to receiptPrinter class (possible rename of receiptPrinter to just
	// reciept
	public void printReceipt() {
		receiptPrinter.printReceipt(manager.getItems());
	}

	/**
	 * Subtracts the weight of the bulky item from the total expected weight
	 * of the system
	 * notifies that the event has happened
	 */
	public void addBulkyItem() {
		// Only able to add when in a discrepancy after adding bags
		if (sessionState == SessionState.BLOCKED) {
			sessionState = SessionState.BULKY_ITEM;
			request = Requests.BULKY_ITEM;
			notifyAttendant();
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

	public void notifyAttendant() {
		// attendant.getRequest(request);
		attendantApprove(request);
	}

	/**
	 * getter methods
	 */
	public boolean getRequestApproved() {
		return this.requestApproved;
	}

	public HashMap<BarcodedProduct, Integer> getBarcodedItems() {
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
}
