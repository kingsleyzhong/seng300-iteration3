package com.thelocalmarketplace.software.attendant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.printer.*;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteDispenserBronze;
import com.tdc.banknote.BanknoteDispenserGold;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.*;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionListener;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.receipt.Receipt;
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

	// the associated instance of Session
	private Session session;
	
	// hardware of the associated session
	private IReceiptPrinter receiptPrinter;
	private CoinStorageUnit coinStorage;
	private BanknoteStorageUnit banknoteStorage;
	private Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
	private Map<BigDecimal, ICoinDispenser> coinDispensers;
	private Receipt receipt;
	private int estimatedInk;
	private int estimatedPaper;

	private boolean lowInk;
	private boolean lowPaper;
	private boolean lowCoins;
	private boolean fullCoins;
	private boolean lowBanknotes;
	private boolean fullBanknotes;
	
	
	public IssuePredictor(Session session, AbstractSelfCheckoutStation scs, Receipt receipt) {
		this.session = session;
		// register IssuePredictor as a listener to the session
		session.register(new InnerSessionListener());
		session.registerHardwareListener(new InnerHardwareListener());

		// save references to the hardware associated with session?`
		receiptPrinter = scs.getPrinter();
		coinStorage = scs.getCoinStorage();
		banknoteStorage = scs.getBanknoteStorage();
		banknoteDispensers = scs.getBanknoteDispensers();
		coinDispensers = scs.getCoinDispensers();
		scs.getPrinter().register(new InnerReceiptPrinterListener());
		this.receipt = receipt;
		this.receipt.registerPrintListener(new PrintTracker());
		estimatedInk = 0;
		estimatedPaper = 0;
	}

	private class InnerHardwareListener implements HardwareListener {
		@Override
		public void aStationHasBeenOpened() {

		}
		@Override
		public void aStationHasBeenClosed() {
			predictionCheck(session);
		}
	}
	
	private class InnerSessionListener implements SessionListener{

		@Override
		public void getRequest(Session session, Requests request) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemAdded(Session session, Product product, Mass ofProduct, Mass currentExpectedWeight,
				BigDecimal currentExpectedPrice) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void itemRemoved(Session session, Product product, Mass ofProduct, Mass currentExpectedMass,
				BigDecimal currentExpectedPrice) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addItemToScaleDiscrepancy(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeItemFromScaleDiscrepancy(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void discrepancy(Session session, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void discrepancyResolved(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pricePaidUpdated(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionAboutToStart(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionEnded(Session session) {
			// run the prediction algorithm
			predictionCheck(session);
		}
		
	}

	private class InnerReceiptPrinterListener implements ReceiptPrinterListener {
		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}
		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}
		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}
		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}
		@Override
		public void thePrinterIsOutOfPaper() {}
		@Override
		public void thePrinterIsOutOfInk() {}
		@Override
		public void thePrinterHasLowInk() {}
		@Override
		public void thePrinterHasLowPaper() {}
		/**
		 * Announces that paper has been added to the printer.
		 */
		@Override
		public void paperHasBeenAddedToThePrinter() {
			lowPaper = false;
			estimatedPaper = ReceiptPrinterBronze.MAXIMUM_PAPER;
		}
		/**
		 * Announces that ink has been added to the printer.
		 */
		@Override
		public void inkHasBeenAddedToThePrinter() {
			lowInk = false;
			estimatedInk = ReceiptPrinterBronze.MAXIMUM_INK;
		}
	}

	private class PrintTracker implements PrintListener {
		@Override
		public void aReceiptHasBeenPrinted(int linesPrinted, int charsPrinted) {
			estimatedPaper -= linesPrinted;
			estimatedInk -= charsPrinted;
		}
	}

	/**
	 * Runs all checks on a given session
	 */
	private void predictionCheck(Session session) {
		checkLowInk(session, receiptPrinter);
		checkLowPaper(session, receiptPrinter);
		checkLowCoins(session,coinDispensers);
		checkLowBanknotes(session, banknoteDispensers);
		checkCoinsFull(session, coinStorage);
		checkBanknotesFull(session, banknoteStorage);
		boolean[] issues = {lowInk, lowPaper, lowCoins, fullCoins, lowBanknotes, fullBanknotes};
		boolean hasIssue = false;
		for (boolean i : issues) {
			if (i) {
				hasIssue = true;
			}
		}
		if (hasIssue == false) {
			notifyNoIssues(session);
			session.enable();
		}
	}
	
	
	/*
	 * Predict if an issue may occur with not enough ink inside the printer
	 * The current amount of ink in the printer should be above 
	 * a threshold = 10% of maximum ink. If an issue is found, announce 
	 * a low ink event. Otherwise, announce a no issues event.
	 */
    public void checkLowInk(Session s, IReceiptPrinter printer) {
    	receiptPrinter = printer;
    	SessionState state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    	
    	int currentInk;
    	int threshold;
    	
    	if (receiptPrinter instanceof ReceiptPrinterBronze) {
			threshold = ReceiptPrinterBronze.MAXIMUM_INK;
			
    		if (estimatedInk <= threshold * 0.1) {
				notifyLowInk(s);
				lowInk = true;
			} else {
				notifyNoIssues(s);
				lowInk = false;
			}
    	} else if (receiptPrinter instanceof ReceiptPrinterSilver) {
    		ReceiptPrinterSilver silver = (ReceiptPrinterSilver) receiptPrinter;
    		
    		currentInk = silver.inkRemaining();
    		threshold = ReceiptPrinterSilver.MAXIMUM_INK;
    		if (currentInk <= threshold * 0.1) {
				notifyLowInk(s);
				lowInk = true;
			} else {
    			notifyNoIssues(s);
    			lowInk = false;
    		}
    	} else {
    		ReceiptPrinterGold gold = (ReceiptPrinterGold) receiptPrinter;
    		
    		currentInk = gold.inkRemaining();
    		threshold = ReceiptPrinterGold.MAXIMUM_INK;
    		
    		if (currentInk <= threshold * 0.1) {
				notifyLowInk(s);
				lowInk = true;
			} else {
    			notifyNoIssues(s);
    			lowInk = false;
    		}
    	}
    }
    
    /*
     * Predict if an issue may occur with not enough paper inside the printer.
     * The current amount of paper in the printer should be 
     * above a threshold = 10% of maximum paper. If an issue is found, announce 
     * a low paper event. Otherwise, announce a no issues event.
     */
    public void checkLowPaper(Session s, IReceiptPrinter printer) {
    	receiptPrinter = printer;
    	SessionState state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    		
    	int currentPaper;
    	int threshold;
    	
		if (receiptPrinter instanceof ReceiptPrinterBronze) {
			threshold = ReceiptPrinterBronze.MAXIMUM_PAPER;
			
			if (estimatedPaper <= threshold * 0.1) {
				notifyLowPaper(s);
				lowPaper = true;
			} else {
				notifyNoIssues(s);
				lowPaper = false;
			}
		} else if (receiptPrinter instanceof ReceiptPrinterSilver) {
			ReceiptPrinterSilver silver = (ReceiptPrinterSilver) receiptPrinter;
			
			currentPaper = silver.paperRemaining();
			threshold = ReceiptPrinterSilver.MAXIMUM_PAPER;
			
			if (currentPaper <= threshold * 0.1) {
				notifyLowPaper(s);
				lowPaper = true;
			} else {
    			notifyNoIssues(s);
    			lowPaper = false;
    		}
		} else {
			ReceiptPrinterGold gold = (ReceiptPrinterGold) receiptPrinter;
			
			currentPaper = gold.paperRemaining();
			threshold = ReceiptPrinterGold.MAXIMUM_PAPER;
			
			if (currentPaper <= threshold * 0.1) {
				notifyLowPaper(s);
				lowPaper = true;
			} else {
    			notifyNoIssues(s);
    			lowPaper = false;
    		}
		}
    }
    
    /*
     * Predict if an issue may occur with not having enough coins to 
     * dispense as change. The current amount of coins in the dispenser 
     * should be above a threshold = 5. If an issue is found, 
     * announce a low coins event. Otherwise, announce a no issues event.
     */
    public void checkLowCoins(Session s, 
    		Map<BigDecimal, ICoinDispenser> dispensers) {
    	coinDispensers = dispensers;
    	SessionState state = s.getState();
    	
		if (!(state == SessionState.PRE_SESSION)) 
			return;
		
    	for (ICoinDispenser dispenser : coinDispensers.values()) {
			int currentCoins = dispenser.size();
			int threshold = 5;
			
			if (currentCoins <= threshold) {
				notifyCoinsLow(s);
				lowCoins = true;
			} else {
    			notifyNoIssues(s);
    			lowCoins = false;
    		} 
    	}
    }
    
    /**
     * Predict if an issue may occur with not having enough bank notes to 
     * dispense as change. The current amount of bank notes in a dispenser
     * should be above a threshold = 5. If an issue is found, announce
     * a low banknotes event. Otherwise, announce a no issues event.
     */
    public void checkLowBanknotes(Session s, 
    		Map<BigDecimal, IBanknoteDispenser> dispensers) {
    	banknoteDispensers = dispensers;
    	SessionState state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION))  
    		return;
    	
    	for (IBanknoteDispenser dispenser : banknoteDispensers.values()) {
			int currentBanknotes = dispenser.size();
    		int threshold = 5;
    		
			if (currentBanknotes <= threshold) {
				notifyBanknotesLow(s);
				lowBanknotes = true;
			}
			else {
    			notifyNoIssues(s);
    			lowBanknotes = false;
			} 
		}
    }
    
    /**
     * Predict if an issue may occur with the coin storage unit being full, 
     * and thus the customer may not be able to insert any coins. The coin 
     * storage unit should have space. If an issue is found, announce a 
     * coins full event. Otherwise, announce a no issues event.
     */
    public void checkCoinsFull(Session s, CoinStorageUnit storage) {
    	coinStorage = storage;
    	SessionState state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    	
		if (!coinStorage.hasSpace()) {
			notifyCoinsFull(s);
			fullCoins = true;
		} else {
			notifyNoIssues(s);
			fullCoins = false;
		}
    }
    
    /**
     * Predict if an issue may occur with the banknote storage unit being full,
     * and thus the customer may not be able to insert any banknotes. The
     * banknote storage unit should have space. If an issue is found, 
     * announce a banknotes full event. Otherwise, announce a no 
     * issues event.
     */
    public void checkBanknotesFull(Session s, BanknoteStorageUnit storage) {
    	banknoteStorage = storage;
    	SessionState state = s.getState();
    	
    	if (!(state == SessionState.PRE_SESSION)) 
    		return;
    	
    	if (!banknoteStorage.hasSpace()) {
			notifyBanknotesFull(s);
			fullBanknotes = true;
		}
    	else {
			notifyNoIssues(s);
			fullBanknotes = false;
		}
    }
    
    private void notifyUnsupportedFeature(Session session, Issues issue) {
    	for (IssuePredictorListener l : listeners)
    		l.notifyPredictUnsupportedFeature(session, issue);
    }
    
    private void notifyLowInk(Session session) {
    	for (IssuePredictorListener l : listeners) 
			l.notifyPredictLowInk(session);
    }
    
    private void notifyLowPaper(Session session) {
    	for (IssuePredictorListener l : listeners) 
			l.notifyPredictLowPaper(session);
    }
    
    private void notifyCoinsLow(Session session) {
    	for (IssuePredictorListener l : listeners)
			l.notifyPredictLowCoins(session);
    }
    
    private void notifyBanknotesLow(Session session) {
    	for (IssuePredictorListener l : listeners)
			l.notifyPredictLowBanknotes(session);
    }
    
    private void notifyCoinsFull(Session session) {
    	for (IssuePredictorListener l : listeners) 
			l.notifyPredictCoinsFull(session);
    }
    
    private void notifyBanknotesFull(Session session) {
    	for (IssuePredictorListener l : listeners)
			l.notifyPredictBanknotesFull(session);
    }

	private void notifyNoIssues(Session session) {
		for (IssuePredictorListener l : listeners)
			l.notifyNoIssues(session);
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
