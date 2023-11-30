package com.thelocalmarketplace.software.attendant;

import java.util.ArrayList;

import com.jjjwelectronics.printer.IReceiptPrinter;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinStorageUnit;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.receipt.ReceiptListener;
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
public abstract class PredictionManager  {
	public ArrayList<PredictionListener> listeners = new ArrayList<>();

	private Session s;
	private IReceiptPrinter receiptPrinter;
	private CoinSlot coinSlot;
	private BanknoteDispensationSlot banknoteSlot;
	private BanknoteStorageUnit banknoteStorage;
	private CoinStorageUnit coinStorage;
	
	private SessionState state;
	
	
	public PredictionManager(Session s) {
		this.s = s;
		receiptPrinter = s.getStation().getPrinter();
		coinSlot = s.getStation().getCoinSlot();
		banknoteSlot = s.getStation().getBanknoteOutput();
		banknoteStorage = s.getStation().getBanknoteStorage();
		coinStorage = s.getStation().getCoinStorage();
	}
	
	public void signalAttendant(Requests request) {
		// temporary thing i guess 

		System.out.println(request);
		
	}
	
    public void checkLowInk() {
    	state = s.getState();
    	if (state == SessionState.PRE_SESSION) {
	    	int currentInk = receiptPrinter.inkRemaining();
	    	
	    	int threshhold = 0; // how i get minimum from AbstractReceiptPaper??
	    	
	    	if (currentInk <= threshhold * 0.1) {
	    		for (PredictionListener l : listeners) 
	    			l.notifyPredictLowInk();
	    		
	    	}
	    
    	} else {
    		// announce session is already started 
    	}
    }
    
    public void checkLowPaper() {
    	int currentPaper = receiptPrinter.paperRemaining();
    	int threshhold = 0;
    	
    	if (currentPaper == threshhold) {
    		for (PredictionListener l : listeners) 
    			l.notifyPredictLowPaper();
    	}
    	
    }
    
    public void checkLowCoins() {
    	
    }
    
    public void checkLowBanknotes() {
    	
    }
    
    public void checkCoinsFull() {
    	
    }
    
    public void checkBanknotesFull() {
    	
    }
    
	public synchronized boolean deregister(PredictionListener listener) {
		return listeners.remove(listener);
	}

	public synchronized void deregisterAll() {
		listeners.clear();
	}

	public final synchronized void register(PredictionListener listener) {
		listeners.add(listener);
	}
	
	
}
