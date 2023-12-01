package com.thelocalmarketplace.software;

import java.util.HashMap;
import java.util.Map;

import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCard;
import com.thelocalmarketplace.software.funds.PayByCash;
import com.thelocalmarketplace.software.items.ItemAddedRule;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.weight.Weight;

/**
 * A facade for the logic, supporting its installation on a self checkout
 * station.
 * Creates and associates Attendants and Sessions.
 * 
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

	private static Attendant attendant;
	private Session session;
	
	public static void installAttendantStation(AttendantStation as) {
		attendant = new Attendant(as);
	}
	
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
	public static SelfCheckoutStationLogic installOn(AbstractSelfCheckoutStation scs) {
		return new SelfCheckoutStationLogic(scs);
	}

	/**
	 * Constructors for the instance of logic
	 * 
	 * @param scs
	 *                The self-checkout station that the logic is installed on
	 * @param session
	 *                The session that the logic shall be installed on
	 */
	private SelfCheckoutStationLogic(AbstractSelfCheckoutStation scs) {
		// creates a new Session
		session = new Session();
		
		// Registers the attendant with the session
		attendant.registerOn(session);
		
		// create Funds, Weight, Receipt, and ItemManger classes to associate w/ Session
		Funds funds = new Funds(scs);
		new PayByCash(scs.getCoinValidator(), scs.getBanknoteValidator(), funds);
		new PayByCard(scs.getCardReader(), funds);
		Weight weight = new Weight(scs.getBaggingArea());
		Receipt receipt = new Receipt(scs.getPrinter());
		ItemManager itemManager = new ItemManager(session); 
		session.setup(itemManager, funds, weight, receipt, scs); 

		// Will also need the touch screen/ keyboard for GUI interaction
		new ItemAddedRule(scs.getMainScanner(), scs.getHandheldScanner(), itemManager);

		// Register IssuePredictor with Session
		IssuePredictor predictor = new IssuePredictor(session, scs); 
		// tell the Attendant about the Predictor
		attendant.addIssuePrediction(predictor); 
	
	}
	
	public static Attendant getAttendant() {
		return attendant;
	}
	
	
	public Session getSession() {
		return session;
	}
	
	
	/**
	 * populates the database with a barcode and barcoded product into the inventory
	 * @param barcode
	 * @param product
	 */
	public static void populateDatabase(Barcode barcode, BarcodedProduct product, int amount) {
		Map<Product, Integer> inventory = ProductDatabases.INVENTORY;
		Map<Barcode, BarcodedProduct> barcodedProducts = ProductDatabases.BARCODED_PRODUCT_DATABASE;
		inventory.put(product, amount);
		barcodedProducts.put(barcode, product);	
	}
	
	public static void populateDatabase(PriceLookUpCode plu, PLUCodedProduct product, int amount) {
		Map<Product, Integer> inventory = ProductDatabases.INVENTORY;
		Map<PriceLookUpCode, PLUCodedProduct> pluProducts = ProductDatabases.PLU_PRODUCT_DATABASE;
		inventory.put(product, amount);
		pluProducts.put(plu, product);	
	}
}
