package com.thelocalmarketplace.software.attendant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.printer.ReceiptPrinterSilver;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.BanknoteDispenserGold;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.CoinDispenserBronze;
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
public class IssuePredictor  {
	public ArrayList<IssuePredictorListener> listeners = new ArrayList<>();

	private Session s;
	private IReceiptPrinter receiptPrinter;
	private CoinStorageUnit coinStorage;
	private BanknoteStorageUnit banknoteStorage;
	private Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
	private Map<BigDecimal, ICoinDispenser> coinDispensers;
	
	private SessionState state;
	
	
	public IssuePredictor(Session s) {
		this.s = s;
		receiptPrinter = s.getStation().getPrinter();
		coinStorage = s.getStation().getCoinStorage();
		banknoteStorage = s.getStation().getBanknoteStorage();
		banknoteDispensers = s.getStation().getBanknoteDispensers();
		coinDispensers = s.getStation().getCoinDispensers();
	}
	
	/*
	 * Predict if an issue may occur with not enough ink inside the printer
	 * The current amount of ink in the printer should be above 
	 * a threshold = N/A. If an issue is found, announce a low ink event.
	 */
    public void checkLowInk() {
    	state = s.getState();
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    	
    	int currentInk;
    	int threshold;
    	
    	if (receiptPrinter instanceof ReceiptPrinterBronze) {
    		notifyUnsupportedFeature(Requests.LOW_INK_CHECK_UNSUPPORTED);
    	} else if (receiptPrinter instanceof ReceiptPrinterSilver) {
    		ReceiptPrinterSilver silver = (ReceiptPrinterSilver) receiptPrinter;
    		
    		currentInk = silver.inkRemaining();
    		threshold = silver.MAXIMUM_INK;
    		
    		if (currentInk <= threshold * 0.1)
    			notifyLowInk();
    	} else {
    		ReceiptPrinterGold gold = (ReceiptPrinterGold) receiptPrinter;
    		
    		currentInk = gold.inkRemaining();
    		threshold = gold.MAXIMUM_INK;
    		
    		if (currentInk <= threshold * 0.1)
    			notifyLowInk();
    	}
    }
    
    /*
     * Predict if an issue may occur with not enough paper inside the printer.
     * The current amount of paper in the printer should be 
     * above a threshold = N/A. If an issue is found, announce 
     * a low paper event.
     */
    public void checkLowPaper() {
    	state = s.getState();
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    		
    	int currentPaper;
    	int threshold;
    	
		if (receiptPrinter instanceof ReceiptPrinterBronze) 
			notifyUnsupportedFeature(Requests.LOW_PAPER_CHECK_UNSUPPORTED);
		else if (receiptPrinter instanceof ReceiptPrinterSilver) {
			ReceiptPrinterSilver silver = (ReceiptPrinterSilver) receiptPrinter;
			
			currentPaper = silver.paperRemaining();
			threshold = silver.MAXIMUM_PAPER;
			
			if (currentPaper <= threshold * 0.1) 
				notifyLowPaper();
		} else {
			ReceiptPrinterGold gold = (ReceiptPrinterGold) receiptPrinter;
			
			currentPaper = gold.paperRemaining();
			threshold = gold.MAXIMUM_PAPER;
			
			if (currentPaper <= threshold * 0.1)
				notifyLowPaper();
		}
    }
    
    /*
     * Predict if an issue may occur with not having enough coins to 
     * dispense as change. The current amount of coins in the dispenser 
     * should be above a threshold = N/A. If an issue is found, 
     * announce a low coins event
     */
    public void checkLowCoins() {
    	state = s.getState();
    	
		if (!(state == SessionState.PRE_SESSION)) 
			return;
		
    	for (ICoinDispenser dispenser : coinDispensers.values()) {
    		if (!dispenser.hasSpace()) 
    			notifyCoinsLow();
    	}
    }
    
    /**
     * Predict if an issue may occur with not having enough bank notes to 
     * dispense as change. The current amount of bank notes in the dispenser
     * should be above a threshold = N/A. If an issue is found, announce
     * a low banknotes event.
     */
    public void checkLowBanknotes() {
    	state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION))  
    		return;
    	
    	for (IBanknoteDispenser dispenser : banknoteDispensers.values()) {
    		if (dispenser instanceof BanknoteDispenserBronze) {
    			int currentBanknotes = dispenser.size();
    			int threshold = 0;
    			
    			if (currentBanknotes == threshold) 
    				notifyBanknotesLow();
    		} else if (dispenser instanceof BanknoteDispenserGold){
				if (((BanknoteDispenserGold) dispenser).hasSpace()) 
					notifyBanknotesLow();
			} else 
				notifyUnsupportedFeature(Requests.LOW_BANKNOTE_CHECK_UNSUPPORTED);
		}
    }
    
    /**
     * Predict if an issue may occur with the coin storage unit being full, 
     * and thus the customer may not be able to insert any coins. The current 
     * amount of coins in the storage unit should be below a threshold = N/A.
     * If an issue is found, announce a coins full event.
     */
    public void checkCoinsFull() {
    	state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    	
		if (!coinStorage.hasSpace()) 
			notifyCoinsFull();
    }
    
    /**
     * Predict if an issue may occur with the banknote storage unit being full,
     * and thus the customer may not be able to insert any banknotes. The
     * current amount of banknotes in the storage unit should be below a 
     * threshold = N/A. If an issue is found, announce a banknotes full event.
     */
    public void checkBanknotesFull() {
    	state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    	
    	if (!banknoteStorage.hasSpace()) 
    		notifyBanknotesFull();   	
    }
    
    private void notifyUnsupportedFeature(Requests request) {
    	for (IssuePredictorListener l : listeners)
    		l.notifyPredictUnsupportedFeature(request);
    }
    
    private void notifyLowInk() {
    	for (IssuePredictorListener l : listeners) 
			l.notifyPredictLowInk();
    }
    
    private void notifyLowPaper() {
    	for (IssuePredictorListener l : listeners) 
			l.notifyPredictLowPaper();
    }
    
    private void notifyCoinsLow() {
    	for (IssuePredictorListener l : listeners)
			l.notifyPredictLowCoins();
    }
    
    private void notifyBanknotesLow() {
    	for (IssuePredictorListener l : listeners)
			l.notifyPredictLowBanknotes();
    }
    
    private void notifyCoinsFull() {
    	for (IssuePredictorListener l : listeners) 
			l.notifyPredictCoinsFull();
    }
    
    private void notifyBanknotesFull() {
    	for (IssuePredictorListener l : listeners)
			l.notifyPredictBanknotesFull();
    }
    
	public synchronized boolean deregister(IssuePredictorListener listener) {
		return listeners.remove(listener);
	}

	public synchronized void deregisterAll() {
		listeners.clear();
	}

	public final synchronized void register(IssuePredictorListener listener) {
		listeners.add(listener);
	}
	
	
}
