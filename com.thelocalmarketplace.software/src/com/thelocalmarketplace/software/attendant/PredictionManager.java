package com.thelocalmarketplace.software.attendant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.jjjwelectronics.printer.IReceiptPrinter;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.ICoinDispenser;
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
	private CoinStorageUnit coinStorage;
	private BanknoteStorageUnit banknoteStorage;
	private Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
	private Map<BigDecimal, ICoinDispenser> coinDispensers;
	
	private SessionState state;
	
	
	public PredictionManager(Session s) {
		this.s = s;
		receiptPrinter = s.getStation().getPrinter();
		coinStorage = s.getStation().getCoinStorage();
		banknoteStorage = s.getStation().getBanknoteStorage();
		banknoteDispensers = s.getStation().getBanknoteDispensers();
		coinDispensers = s.getStation().getCoinDispensers();
	}
	
    public void checkLowInk() {
    	state = s.getState();
    	
    	if (state == SessionState.PRE_SESSION) {
	    	int currentInk = receiptPrinter.inkRemaining();
	    	int threshold = 0; // how i get minimum from AbstractReceiptPaper??
	    	
	    	if (currentInk <= threshold * 0.1) {
	    		notifyLowInk();
	    	}
    	} 
    }
    
    public void checkLowPaper() {
    	state = s.getState();
    	
    	if (state == SessionState.PRE_SESSION) {
	    	int currentPaper = receiptPrinter.paperRemaining();
	    	int threshold = 0;
	    	
	    	if (currentPaper == threshold) {
	    		notifyLowPaper();
	    	}
    	}
    	
    }
    
    public void checkLowCoins() {
    	state = s.getState();
    	
    	if (state == SessionState.PRE_SESSION) {		
	    	for (ICoinDispenser dispenser : coinDispensers.values()) {
	    		int currentCoins = dispenser.size();
	    		int threshold = 0;
	    		
	    		if (currentCoins == threshold) {
	    			notifyCoinsLow();
	    		}
	    	}
    	}
    }
    
    public void checkLowBanknotes() {
    	state = s.getState();
    	
    	if (state == SessionState.PRE_SESSION) {    		
	    	for (IBanknoteDispenser dispenser : banknoteDispensers.values()) {
	    		int currentBanknotes = dispenser.size();
	    		int threshold = 0;
	    		
	    		if (currentBanknotes == threshold) {
	    			notifyBanknotesLow();
	    		}
	    	}
    	}
    }
    
    public void checkCoinsFull() {
    	state = s.getState();
    	
    	if (state == SessionState.PRE_SESSION) {    		
	    	int currentCoins = coinStorage.getCoinCount();
	    	int threshold = coinStorage.getCapacity();
	    	
	    	if (currentCoins == threshold) {
	    		notifyCoinsFull();
	    	}
    	}
    }
    
    public void checkBanknotesFull() {
    	state = s.getState();
    	
    	if (state == SessionState.PRE_SESSION) {    		
	    	int currentCoins = banknoteStorage.getBanknoteCount();
	    	int threshold = banknoteStorage.getCapacity();
	    	
	    	if (currentCoins == threshold) {
	    		notifyBanknotesFull();
	    	}
    	}
    }
    
    private void notifyLowInk() {
    	for (PredictionListener l : listeners) 
			l.notifyPredictLowInk();
    }
    
    private void notifyLowPaper() {
    	for (PredictionListener l : listeners) 
			l.notifyPredictLowPaper();
    }
    
    private void notifyCoinsLow() {
    	for (PredictionListener l : listeners)
			l.notifyPredictLowCoins();
    }
    
    private void notifyBanknotesLow() {
    	for (PredictionListener l : listeners)
			l.notifyPredictLowBanknotes();
    }
    
    private void notifyCoinsFull() {
    	for (PredictionListener l : listeners) 
			l.notifyPredictCoinsFull();
    }
    
    private void notifyBanknotesFull() {
    	for (PredictionListener l : listeners)
			l.notifyPredictBanknotesFull();
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
