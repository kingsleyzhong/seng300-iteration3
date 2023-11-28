package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.weight.Weight;

/*
 * This file contains an abstract class for session related testing
 * 
* Project Iteration 3 Group 1
*
* Derek Atabayev 			: 30177060 
* Enioluwafe Balogun 		: 30174298 
* Subeg Chahal 				: 30196531 
* Jun Heo 					: 30173430 
* Emily Kiddle 				: 30122331 
* Anthony Kostal-Vazquez 	: 30048301 
* Jessica Li 				: 30180801 
* Sua Lim 					: 30177039 
* Savitur Maharaj 			: 30152888 
* Nick McCamis 				: 30192610 
* Ethan McCorquodale 		: 30125353 
* Katelan Ng 				: 30144672 
* Arcleah Pascual 			: 30056034 
* Dvij Raval 				: 30024340 
* Chloe Robitaille 			: 30022887 
* Danissa Sandykbayeva 		: 30200531 
* Emily Stein 				: 30149842 
* Thi My Tuyen Tran 		: 30193980 
* Aoi Ueki 					: 30179305 
* Ethan Woo 				: 30172855 
* Kingsley Zhong 			: 30197260 
* 
 */

abstract public class AbstractSessionTest extends AbstractTest {
	protected Session session;
	protected ItemManager itemManager;
	protected Funds funds;
	protected Weight weight;
	protected Receipt receiptPrinter;


	public AbstractSessionTest(String testName, AbstractSelfCheckoutStation scs) {
		super(testName, scs);
	}
	
	
	public void basicDefaultSetup() {
		super.basicDefaultSetup();
		
		session = new Session();
		itemManager = new ItemManager(session);
		
		weight = new Weight(scs.getBaggingArea());
		funds = new Funds(scs);
		receiptPrinter = new Receipt(scs.getPrinter());
		
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
	}

}
