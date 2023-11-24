package com.thelocalmarketplace.software;

import java.util.HashMap;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemAddedRule;
import com.thelocalmarketplace.software.receipt.PrintReceipt;
import com.thelocalmarketplace.software.weight.Weight;

/**
 * A facade for the logic, supporting its installation on a self checkout
 * station.
 * Allows for a database to be constructed
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

public class SelfCheckoutStationLogic {

	/**
	 * Installs an instance of the logic on the selfCheckoutStation and the session
	 * run on the station
	 * 
	 * @param scs
	 *                The self-checkout station that the logic shall be installed
	 * @param session
	 *                The session that the logic shall be installed on
	 * @return
	 *         returns an instance of the SelfCheckoutStationLogic on a
	 *         SelfChekoutStation and Session
	 */
	public static SelfCheckoutStationLogic installOn(AbstractSelfCheckoutStation scs, Session session) {
		return new SelfCheckoutStationLogic(scs, session);
	}

	/**
	 * Constructors for the instance of logic
	 * 
	 * @param scs
	 *                The self-checkout station that the logic is installed on
	 * @param session
	 *                The session that the logic shall be installed on
	 */
	private SelfCheckoutStationLogic(AbstractSelfCheckoutStation scs, Session session) {
		//Session session = new Session();
		//Attendant attendant = new Attendant(as);
		//session.register(attendant);
		Funds funds = new Funds(scs); //Funds needs the coinDispensers and bankNoteDispensers
		// PayByCashController(scs.coinValidator, scs.banknoteValidator, funds);
		// PayByCardController(scs.cardReader, funds);
		Weight weight = new Weight(scs); //Weight need the bagging area
		PrintReceipt receiptPrinter = new PrintReceipt(scs); // Needs printer
		HashMap<BarcodedProduct, Integer> barcodedItems = new HashMap<BarcodedProduct, Integer>();
		// Will also need the touch screen/ keyboard for GUI interaction
		session.setup(barcodedItems, funds, weight, receiptPrinter); 
		// ItemManager itemManager = new ItemManager(session);
		// new ItemRemovedRule(itemManager)
		new ItemAddedRule(scs, session); //Needs access to mainScanner and handheldScanner
	}
}
