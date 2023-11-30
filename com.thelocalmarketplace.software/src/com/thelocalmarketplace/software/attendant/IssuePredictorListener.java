package com.thelocalmarketplace.software.attendant;
/*
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
public interface IssuePredictorListener {
	 
	void notifyPredictUnsupportedFeature(Requests request);
	
	/**
	 * Signals an event that a low ink issue will shortly occur
	 */
	void notifyPredictLowInk();
	
	
	/**
	 * Signals an event that a low paper issue will shortly occur
	 */
	void notifyPredictLowPaper();
	
 
	/**
	 * Signals an event that a coins full issue will shortly occur
	 */
	void notifyPredictCoinsFull();
	
	
	/**
	 * Signals an event that a bank notes full issue will shortly occur
	 */
	void notifyPredictBanknotesFull();
	
	/**
	 * Signals an event that a low coins issue will shortly occur
	 */
	void notifyPredictLowCoins();
	
	/**
	 * Signals an event that a low bank notes issue will shortly occur
	 */
	void notifyPredictLowBanknotes();

}
