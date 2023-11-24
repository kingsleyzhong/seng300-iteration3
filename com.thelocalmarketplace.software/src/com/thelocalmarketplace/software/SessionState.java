package com.thelocalmarketplace.software;

/*
 * All possible states that a Session can be in
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

public enum SessionState {
    PRE_SESSION(false),// Session is not currently running
    IN_SESSION(false),// Session is currently running
    BLOCKED(false),// The Session has been blocked and cannot progress
    ADDING_BAGS(false), // User signaled they want to add bags to the bagging area
    BULKY_ITEM(false), // User signaled they don't want to bag an item
    PAY_BY_CASH(true), // User signaled they want to pay using cash (one of the pay states)
    PAY_BY_CARD(true);// User signaled they want to pay by card (one of the pay states)
    
	
	
    // This is to simplify checking if the state is a pay state
    private final boolean payState;
	private SessionState(final boolean payState) {
		this.payState = payState;
	}
	
	/*
	 * Returns true if the SessionState is a pay state (eg: pay by card, pay by cash)
	 */
	public boolean inPay() {
		return this.payState;
	}
}
