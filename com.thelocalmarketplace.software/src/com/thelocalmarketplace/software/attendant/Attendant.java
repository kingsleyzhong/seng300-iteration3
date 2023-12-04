package com.thelocalmarketplace.software.attendant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.keyboard.USKeyboardQWERTY;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionListener;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;
import com.thelocalmarketplace.software.exceptions.SessionNotRegisteredException;

/**
 * Simulation of attendant station, its functions, and its interactions with customer stations/session
 * 
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev 			: 30177060 
 * Enioluwafe Balogun 		: 30174298 
 * Subeg Chahal 			: 30196531 
 * Jun Heo 					: 30173430 
 * Emily Kiddle 			: 30122331 
 * Anthony Kostal-Vazquez 	: 30048301 
 * Jessica Li 				: 30180801 
 * Sua Lim 					: 30177039 
 * Savitur Maharaj 			: 30152888 
 * Nick McCamis 			: 30192610 
 * Ethan McCorquodale 		: 30125353 
 * Katelan Ng 				: 30144672 
 * Arcleah Pascual 			: 30056034 
 * Dvij Raval 				: 30024340 
 * Chloe Robitaille 		: 30022887 
 * Danissa Sandykbayeva 	: 30200531 
 * Emily Stein 				: 30149842 
 * Thi My Tuyen Tran 		: 30193980 
 * Aoi Ueki 				: 30179305 
 * Ethan Woo 				: 30172855 
 * Kingsley Zhong 			: 30197260
 */

public class Attendant {
	private AttendantStation as;
	private TextSearchController ts;
	private HashMap<Session, Requests> sessions = new HashMap<>();
	private HashMap<Session, IssuePredictor> predictors = new HashMap<>();
	private HashMap<Session, AbstractSelfCheckoutStation> stations = new HashMap<>();
	public ArrayList<AttendantListener> listeners = new ArrayList<>();
	
	/**
	 * Creates an instance of class Attendant given an instance of hardware AttendantStation.
	 * Associates the Attendant with the keyboard.
	 * 
	 * @param as
	 * 				an instance of type AttendantStation
	 */
	public Attendant(AttendantStation as) {
		this.as = as;
		ts = new TextSearchController(as.keyboard);
	}

	/**
	 * Registers an instance of Session with this instance of Attendant.
	 * Attendant will be added as a listener to the given instance of Session.
	 * 
	 * @param session
	 * 					an instance of Session
	 */
	public void registerOn(Session session, AbstractSelfCheckoutStation scs) {
		// register the Attendant as a listener
		session.register(new InnerSessionListener());
		
		//add the customer station hardware to the hardware tracker
		as.add(scs);
		// associate the hardware with the Session
		stations.put(session, scs);
		
		// start tracking the session and its requests
		sessions.put(session,Requests.NO_REQUEST);// by default a Session has no request
	}
	
	/**
	 * Method for associating the Attendant with an instance of IssuePredictor.
	 * @param session
	 * 						an instance of Session
	 */
	public void addIssuePrediction(Session session) {
		// checks if this Attendant is tracking this Session
		if(sessions.containsKey(session)) {
			//// creates an instance of IssuePredictor, associates it with session
			// get the hardware associated with this Session
			IssuePredictor predictor = new IssuePredictor(session, stations.get(session));

			// associates the inner issue predictor listener with the predictor
			predictor.register(new InnerPredictionListener());
			
			// adds the issuer predictor to the HashMap
			predictors.put(session, predictor);			
		}
		else {
			throw new SessionNotRegisteredException();
		}
	}
	
	/**
	 * Receives updates about changes in instances of Session
	 * Used to get messages from Customers (eg. weight discrepancy, help request)
	 */
	private class InnerSessionListener implements SessionListener{

		@Override
		public void itemAdded(Session session, Product product, Mass ofProduct, Mass currentExpectedWeight,
				BigDecimal currentExpectedPrice) {
			
		}

		@Override
		public void itemRemoved(Session session, Product product, Mass ofProduct, Mass currentExpectedMass,
				BigDecimal currentExpectedPrice) {
			
		}

		@Override
		public void addItemToScaleDiscrepancy(Session session) {
			
		}

		@Override
		public void removeItemFromScaleDiscrepancy(Session session) {
			
		}

		@Override
		public void discrepancy(Session session, String message) {
		 
		}

		@Override
		public void discrepancyResolved(Session session) {
			
		}

		@Override
		public void pricePaidUpdated(Session session, BigDecimal amountDue) {
			
		}

		/**
		 * Allows for the attendant station to be notified of a request from a session it is tracking and what the request is.
		 * @param session
		 * @param request
		 */
		@Override
		public void getRequest(Session session, Requests request) {
			sessions.put(session, request);// update the request for this Session
		}

		@Override
		public void sessionAboutToStart(Session session) {
			
		}

		@Override
		public void sessionEnded(Session session) {
			sessions.put(session,  Requests.NO_REQUEST); // resets the current Request to be NO_REQUEST
			
		}

		@Override
		public void pluCodeEntered(PLUCodedProduct product) {
			
		}

		@Override
		public void sessionStateChanged() {
			
		}
	}
	

	/**
	 * Receives notifications when an issue (eg. low ink, paper, low coins) is likely to occur for a given Session
	 * Used to let Attendant know when a customer station might have issues before they happen.
	 * 
	 */
	private class InnerPredictionListener implements IssuePredictorListener{

		@Override
		public void notifyPredictLowInk(Session session) {
			session.disable();
		}

		@Override
		public void notifyPredictLowPaper(Session session) {
			session.disable();
		}

		@Override
		public void notifyPredictCoinsFull(Session session) {
			session.disable();
		}

		@Override
		public void notifyPredictBanknotesFull(Session session) {
			session.disable();
		}

		@Override
		public void notifyPredictLowCoins(Session session) {
			session.disable();
		}

		@Override
		public void notifyPredictLowBanknotes(Session session) {
			session.disable();
		}

		@Override
		public void notifyNoIssues(Session session) {
			session.enable();
		}

	}


	
	/**
	 * Handles all possible ways an attendant could "approve" a customer's request
	 * @param session
	 * 					an instance of Session
	 */
	public void approveRequest(Session session) {
		if(sessions.containsKey(session)) {
			Requests request = sessions.get(session);
			session.attendantApprove(request);// session will have to handle the rest
		}
		else {
			throw new SessionNotRegisteredException();
		}
		
	}
	
	
	/**
	 * Method for enabling a Customer SelfCheckoutStation associated with a given session, unlocking the 
	 * Session and putting it into the PRE_SESSION state
	 * @param session
	 * 					a currently disabled session
	 */
	public void enableStation(Session session) {
		session.enable();
	}
	
	/**
	 * Method for disabling a Customer SelfCheckoutStation associated with a given session.
	 * Places the session into the DISABLED state, preventing further actions.
	 * If the session is currently active than waits until the session is finished before disabling it
	 * 
	 * @param session
	 * 						a session that is either running or in the pre-session state
	 */
	public void disableStation(Session session) {
			session.disable();
	}

	/**
	 * Adds an item found in the search results. Assumes that only instances of PLUCodedProduct and BarcodedProduct are possible.
	 *
	 * @param description
	 * @param session
	 *                The text from the GUI representing the product in the search results.
	 */
	public void addSearchedItem(String description, Session session) {
		if (sessions.get(session).equals(Requests.HELP_REQUESTED)) { // Replace with ADD_ITEM_SEARCH ?
			if (ts.getSearchResults().containsKey(description)) {
				Product selectedProduct = ts.getSearchResults().get(description);

				// The product could be a Barcoded Product
				if (selectedProduct instanceof BarcodedProduct) {
					session.getManager().addItem((BarcodedProduct) selectedProduct);
				}

				// The product could be a PLU Coded Product
				else if (selectedProduct instanceof PLUCodedProduct) {
					PLUCodedProduct pluProduct = (PLUCodedProduct) selectedProduct;
					session.getManager().addItem(pluProduct.getPLUCode());
				}

				// Cancel search/request?
				else if (description.equals("CANCEL SEARCH")){
					sessions.put(session, Requests.NO_REQUEST);
					return;
				}
			} else {
				// Product not found for some reason - is this needed?
				throw new ProductNotFoundException("Item not found");
			}
		} else {
			// Session does not have this request 
			//throw new ProductNotFoundException("Item not found");
		}
		sessions.put(session, Requests.NO_REQUEST); // Replace with a general request cancellation method?
	}

	/**
	 * This is a utility method that will convert a string into the associated key presses on the
	 * USKeyboardQWERTY
	 * @param input
	 * @throws DisabledDevice
	 */
	public void stringToKeyboard(String input) throws DisabledDevice {
		USKeyboardQWERTY keyboard = as.keyboard;

		// Generate Mapping for Non-Alphabetical Shift Modified Keys
		Map<Character, Boolean> shiftModified = new HashMap<>();
		Map<Character, String> labelLookup = new HashMap<>();

		for (String label : USKeyboardQWERTY.WINDOWS_QWERTY) {
			if (label.length() == 3 && label.charAt(1) == ' ') { // Desired Format
				Character c1 = label.charAt(0);
				Character c2 = label.charAt(2);

				shiftModified.put(c1, false);
				shiftModified.put(c2, true);

				labelLookup.put(c1, label);
				labelLookup.put(c2, label);
			}
		}

		for (int i = 0; i < input.length(); i++) {
			Character c = input.charAt(i);
			boolean shift = false;
			String targetLabel;

			if (c == ' ') {
				targetLabel = "Spacebar";
			} else if (Character.isLowerCase(c)) {
				targetLabel = String.valueOf(Character.toUpperCase(c));
			} else if (Character.isUpperCase(c)) {
				targetLabel = String.valueOf(c);
				shift = true;
			} else if (labelLookup.containsKey(c)) {
				targetLabel = labelLookup.get(c);
				shift = shiftModified.get(c);
			} else {
				continue;
			}

			if (shift) {
				keyboard.getKey("Shift (Left)").press();
			}
			keyboard.getKey(targetLabel).press();
			keyboard.getKey(targetLabel).release();
			if (shift) {
				keyboard.getKey("Shift (Left)").release();
			}
		}
	}


	public AttendantStation getStation() {
		return as;
	}
	/**
	 * Returns the customer station hardware associated with a given instance of Session,
	 * if the Session is registered  with this Attendant.
	 * 
	 * @param session
	 * 					an instance of Session
	 * @return
	 * 					AbstractSelfCheckoutStation
	 */
	public AbstractSelfCheckoutStation getCustomerStation(Session session) {
		// check if this Attendant is tracking this session
		if(sessions.containsKey(session)) {
			return stations.get(session);
		}
		else {
			throw new SessionNotRegisteredException();
		}
	}
	
	/**
	 * Returns the prediction software (IssuePredictor) associated with a given instance of Session,
	 * if the Session is registered with this Attendant. 
	 * 
	 * @param session
	 * 					an instance of Session
	 * @return
	 * 					IssuePredictor
	 */
	public IssuePredictor getIssuePredictor(Session session) {
		// check if this Attendant is tracking this session
		if(sessions.containsKey(session)) {
			return predictors.get(session);
		}
		else {
			throw new SessionNotRegisteredException();
		}
	}
	
	/**
	 * Returns the current Request of a given instance of Session, if this Attendant is tracking
	 * the given Session.
	 * 
	 * @param session
	 * 					an instance of Session
	 * @return
	 * 					Requests
	 */
	public Requests getCurrentRequest(Session session) {
		// check if this Attendant is tracking this session
		if(sessions.containsKey(session)) {
			return sessions.get(session);
		}
		else {
			throw new SessionNotRegisteredException();
		}
	}
	
	/**
	 * Returns the set of all sessions being tracked by this Attendant
	 * @return
	 * 			HashMap<Session, Requests>
	 */
	public HashMap<Session, Requests> getSessions(){
		return sessions;
	}

	/**
	 * Returns the instance of TextSearchController associated with this Attendant
	 * @return
	 * 			TextSearchController
	 */
	public TextSearchController getTextSearchController() {
		return ts;
	}
}
