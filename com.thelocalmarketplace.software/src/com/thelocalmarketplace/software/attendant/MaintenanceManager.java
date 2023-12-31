package com.thelocalmarketplace.software.attendant;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.ClosedHardwareException;
import com.thelocalmarketplace.software.exceptions.IncorrectDenominationException;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Simulation of an attendant performing minuteness and preventative care on self checkout stations
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
public class MaintenanceManager {
	private ArrayList<MaintenanceManagerListener> listeners = new ArrayList<>();
    private boolean isOpen = false;
    private int amountOfInkRefilled = 0;
    private int amountOfPaperRefilled = 0;
    private SessionState state;
    private IReceiptPrinter receiptPrinter;
    private Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
    private Map<BigDecimal, ICoinDispenser> coinDispensers;
    private Session session;
    private BigDecimal[] banknoteDenominations;
    private List<BigDecimal> coinDenominations;
    private BanknoteStorageUnit banknoteStorage;
    private CoinStorageUnit coinStorage;

    /**
     * Constructor for MaintenanceManager
     */
    public MaintenanceManager() {
    }
    
    /**
     * Simulates the act of opening the hardware
     * @param session passed in as the specific machine being opened
     * @throws NotDisabledSessionException if the session is not disabled
     */
    public void openHardware(Session session) throws NotDisabledSessionException {
    	this.session = session;
        state = session.getState();
        if (state == SessionState.DISABLED) {
            AbstractSelfCheckoutStation scs = session.getStation();
            session.notifyOpenHardware();
            banknoteDenominations = scs.getBanknoteDenominations();
            coinDenominations = scs.getCoinDenominations();
            receiptPrinter = scs.getPrinter();
            banknoteDispensers = scs.getBanknoteDispensers();
            coinDispensers = scs.getCoinDispensers();
            banknoteStorage = scs.getBanknoteStorage();
            coinStorage = scs.getCoinStorage();
            isOpen = true;
            
            // signal listeners
            notifyHardwareOpened(session);
        }
        else {
            throw new NotDisabledSessionException("Session is not disabled!");
        }
    }

    /**
     * Simulates placing coins within a Coin Dispenser
     * @param cd the denomination of the Coin Dispenser
     * @param coins the coins to be placed inside
     * @throws IncorrectDenominationException when the denomination of an input coin does not match the denomination of the Dispenser
     * @throws ClosedHardwareException if hardware is not open
     * @throws CashOverloadException if too many coins are added
     */
    public void addCoins(BigDecimal cd, Coin... coins) throws IncorrectDenominationException, ClosedHardwareException, CashOverloadException {
        if (isOpen) {
            if (coinDenominations.contains(cd)) {
                for (Coin c : coins) {
                    if (c.getValue().doubleValue() != cd.doubleValue()) {
                        throw new IncorrectDenominationException("Incorrect coin was input!");
                    }
                }
                coinDispensers.get(cd).load(coins);
            }
            notifyCoinAdded(session);
        }
        else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

    /**
     * Simulates removing coins from Coin Storage
     * @return the coins that are removed
     * @throws ClosedHardwareException if hardware is not open
     */
    public List<Coin> removeCoins() throws ClosedHardwareException {
        if (isOpen) {
            notifyCoinRemoved(session, coinStorage);
            return coinStorage.unload();
        }
        else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

    /**
     * Method that checks if the specified denomination exists
     * @param bd specified denomination
     * @return boolean - true if it exists, false if not
     */
    private boolean verifyBanknoteDenomination(BigDecimal bd) {
        for (BigDecimal i : banknoteDenominations) {
            if (i.doubleValue() == bd.doubleValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Simulates adding a banknote to a specified Banknote Dispenser
     * @param bd denomination of the specified Banknote Dispenser
     * @param banknotes banknotes to be added to the dispenser
     * @throws IncorrectDenominationException if an incorrect banknote denomination is attempted to be input
     * @throws ClosedHardwareException if station is not open
     * @throws CashOverloadException if too many banknotes are added
     */
    public void addBanknotes(BigDecimal bd, Banknote... banknotes) throws IncorrectDenominationException, ClosedHardwareException, CashOverloadException {
        if (isOpen) {
            if (verifyBanknoteDenomination(bd) && isOpen) {
                for (Banknote b : banknotes) {
                    if (b.getDenomination().doubleValue() != bd.doubleValue()) {
                        throw new IncorrectDenominationException("Incorrect banknote was input!");
                    }
                }
                banknoteDispensers.get(bd).load(banknotes);
                notifyBanknoteAdded(session);
            }
        }
        else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

    /**
     * Simulates removing banknotes from Banknote Storage
     * @return the removed banknotes
     * @throws ClosedHardwareException if the hardware is not open
     */
    public List<Banknote> removeBanknotes() throws ClosedHardwareException {
        if (isOpen) {
        	notifyBanknoteRemoved(session, banknoteStorage);
            return banknoteStorage.unload();
        }
        else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

    /**
     * Simulates closing the hardware
     */
    public void closeHardware() {
        session.notifyCloseHardware();
        session = null;
        state = null;
        banknoteDenominations = null;
        coinDenominations = null;
        receiptPrinter = null;
        banknoteDispensers = null;
        coinDispensers = null;
        isOpen = false;
        notifyHardwareClosed(session);
    }


    /**
     * Simulates the act of refilling ink to printer
     * @param amount amount of ink to be refilled
     * @throws ClosedHardwareException if hardware is not opened
     */
    public void refillInk(int amount) throws ClosedHardwareException, OverloadedDevice {
        if (isOpen) {

            this.receiptPrinter.addInk(amount);
            this.amountOfInkRefilled = 0;
            this.amountOfInkRefilled += amount;
            
            notifyInkAdded(session);

        } else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

    /**
     * Simulates the act of refilling paper to printer
     * @param amount amount of paper to be refilled
     * @throws ClosedHardwareException if hardware is not opened
     */
    public void refillPaper(int amount) throws ClosedHardwareException, OverloadedDevice {
        if (isOpen) {

            this.receiptPrinter.addPaper(amount);
            this.amountOfPaperRefilled = 0;
            this.amountOfPaperRefilled += amount;
            
            notifyPaperAdded(session);
            
        } else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

    public int getCurrentAmountOfInk() { return this.amountOfInkRefilled; }

    public int getCurrentAmountOfPaper() { return this.amountOfPaperRefilled; }

    // notifications of events
    
    /**
     * Signals listeners when ink has been added
     */
    protected void notifyInkAdded(Session session) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyInkAdded(session);
    }

    /**
     * Signals listeners when paper has been added
     */
    protected void notifyPaperAdded(Session session) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyPaperAdded(session);
    }

    /**
     * Signals listeners when coins have been added
     */
    protected void notifyCoinAdded(Session session) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyCoinAdded(session);
    }

    /**
     * Signals listeners when banknotes have been added
     */
    protected void notifyBanknoteAdded(Session session) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyBanknoteAdded(session);
    }

    /**
     * Signals when coins have been removed
     */
    protected void notifyCoinRemoved(Session session, CoinStorageUnit coinStorage) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyCoinRemoved(session, coinStorage);
    }

    /**
     * Signals listeners when banknotes have been removed
     */
    protected void notifyBanknoteRemoved(Session session, BanknoteStorageUnit banknoteStorage) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyBanknoteRemoved(session, banknoteStorage);
    }
    /**
     * Signals when the hardware has been opened
     * @param session 
     */
    protected void notifyHardwareOpened(Session session) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyHardwareOpened(session);
    }
    
    /**
     * Signals when the hardware has been closed
     */
    protected void notifyHardwareClosed(Session session) {
		for(MaintenanceManagerListener listener : listeners)
			listener.notifyHardwareClosed(session);
    }

    // listener stuff
    // register listeners
 	public final synchronized void register(MaintenanceManagerListener listener) {
 		if (listener == null)
 			throw new NullPointerSimulationException("listener");
 		listeners.add(listener);
 	}

 	// de-register listeners
 	public final synchronized void deRegister(MaintenanceManagerListener listener) {
 		if (listener == null)
 			throw new NullPointerSimulationException("listener");
 		listeners.remove(listener);
 	}
 	
 	public ArrayList<MaintenanceManagerListener> getListeners(){
 		return listeners;
 	}
}
