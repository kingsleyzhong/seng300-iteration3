package com.thelocalmarketplace.software.attendant;

import java.util.ArrayList;

import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionListener;
import com.thelocalmarketplace.software.weight.WeightListener;

/**
 * Simulation of attendant function and interaction with sessions.
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
	private ArrayList<Session> sessions = new ArrayList<>();
	
	/**
	 * default constructor
	 */
	public Attendant(AttendantStation as) {
		this.as = as;
	}

	private class InnerListener implements SessionListener{
		
		/**
		 * Example of how getRequest could be written. It should include the request and the sesssion the request comes from.
		 * Not you will also have to add any of these methods to SessionListener along with the @Override keyword
		 * @param session
		 * @param request
		 */
		@Override
		public void getRequest(Session session, Requests request) {
			
		}
	}
	
	public void registerOn(Session session) {
		session.register(new InnerListener());
		sessions.add(session);
	}

	public AttendantStation getStation() {
		return as;
	}
}
