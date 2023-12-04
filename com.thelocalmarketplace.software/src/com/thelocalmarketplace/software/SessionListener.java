package com.thelocalmarketplace.software;

import com.thelocalmarketplace.software.attendant.Requests;
import java.math.BigDecimal;
import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.attendant.Requests;

/**
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
 * Kingsley Zhong 		   	: 30197260 
 */

public interface SessionListener {
	/**
	 * Allows for the attendant station to be notified of a request from a session it is tracking and what the request is.
	 * @param session
	 * @param request
	 */
	void getRequest(Session session, Requests request);

	void itemAdded(Session session, Product product, Mass ofProduct, Mass currentExpectedWeight, BigDecimal currentExpectedPrice);
	
	void itemRemoved(Session session, Product product, Mass ofProduct, Mass currentExpectedMass, BigDecimal currentExpectedPrice);
	
	void pluCodeEntered(PLUCodedProduct product);
	
	void addItemToScaleDiscrepancy(Session session);
	
	void removeItemFromScaleDiscrepancy(Session session);
	
	void discrepancy(Session session, String message);
	
	void discrepancyResolved(Session session);
	
	void pricePaidUpdated(Session session, BigDecimal amountDue);
	
	void sessionAboutToStart(Session session);
	
	void sessionEnded(Session session);
	
	void sessionStateChanged();
}
