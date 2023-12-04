package com.thelocalmarketplace.software.attendant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionListener;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;
import com.thelocalmarketplace.software.weight.WeightListener;

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
	
	/**
	 * default constructor
	 */
	public Attendant(AttendantStation as) {
		this.as = as;
		ts = new TextSearchController(as.keyboard);
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
		

		@Override
		public void sessionAboutToStart(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionEnded(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pluCodeEntered(PLUCodedProduct product) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionStateChanged() {
			// TODO Auto-generated method stub
			
		}
	}
	

	/**
	 * Receives notifications when an issue (eg. low ink, paper, low coins) is likely to occur for a given Session
	 * Used to let Attendant know when a customer station might have issues before they happen
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
	public void registerOn(Session session) {
		session.register(new InnerSessionListener());
		//sessions.add(session);
		//as.supervisedStations();
		sessions.put(session, Requests.NO_REQUEST);
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
		} else {
			// Product not found in the visual catalogue
			throw new ProductNotFoundException("Item not found");
		}
	}
	
	/**
	 * Method for associating the Attendant with an instance of IssuePredictor.
	 * @param predictor
	 * 						an instance of IssuePredictor
	 */
	public void addIssuePrediction(IssuePredictor predictor) {
		// associates the inner issue predictor class with the predictor
		predictor.register(new InnerPredictionListener());
	}
	public AttendantStation getStation() {
		return as;
	}
	public HashMap<Session, Requests> getSessions(){
		return sessions;
	}

	public TextSearchController getTextSearchController() {
		return ts;
	}
}
