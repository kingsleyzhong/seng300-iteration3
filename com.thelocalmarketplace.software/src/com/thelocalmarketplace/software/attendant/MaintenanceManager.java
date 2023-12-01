package com.thelocalmarketplace.software.attendant;

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
    private boolean isOpen = false;
    private SessionState state;
    private IReceiptPrinter receiptPrinter;
    private Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
    private Map<BigDecimal, ICoinDispenser> coinDispensers;
    private AbstractSelfCheckoutStation scs;
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
        if (state == SessionState.BLOCKED) { // Change check to SessionState.DISABLED when added
            scs = session.getStation();
            banknoteDenominations = scs.getBanknoteDenominations();
            coinDenominations = scs.getCoinDenominations();
            receiptPrinter = scs.getPrinter();
            banknoteDispensers = scs.getBanknoteDispensers();
            coinDispensers = scs.getCoinDispensers();
            banknoteStorage = scs.getBanknoteStorage();
            coinStorage = scs.getCoinStorage();
            isOpen = true;
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
     */
    public void addCoins(BigDecimal cd, Coin... coins) throws IncorrectDenominationException, ClosedHardwareException {
        if (isOpen) {
            if (coinDenominations.contains(cd)) {
                for (Coin c : coins) {
                    if (c.getValue() != cd) {
                        throw new IncorrectDenominationException("Incorrect coin was input!");
                    }
                }
                try {
                    coinDispensers.get(cd).load(coins);
                } catch (CashOverloadException e) {
                    throw new RuntimeException(e);
                }
            }
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
            if (i == bd) {
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
     */
    public void addBanknotes(BigDecimal bd, Banknote... banknotes) throws IncorrectDenominationException, ClosedHardwareException {
        if (isOpen) {
            if (verifyBanknoteDenomination(bd) && isOpen) {
                for (Banknote b : banknotes) {
                    if (b.getDenomination() != bd) {
                        throw new IncorrectDenominationException("Incorrect banknote was input!");
                    }
                }
                try {
                    banknoteDispensers.get(bd).load(banknotes);
                } catch (CashOverloadException e) {
                    throw new RuntimeException(e);
                }
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
        // still needs code to handle updating session state
        session = null;
        state = null;
        scs = null;
        banknoteDenominations = null;
        coinDenominations = null;
        receiptPrinter = null;
        banknoteDispensers = null;
        coinDispensers = null;
        isOpen = false;
    }


    // Ink
    public void refillInk(int amount) throws ClosedHardwareException {
        int maxAmount = 1 << 20;

        if (isOpen) {

            try {
                this.receiptPrinter.addInk(amount);
            } catch (OverloadedDevice e) {
                throw new RuntimeException(e);
            }

        } else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

    public void refillPaper(int amount) throws ClosedHardwareException {
        int maxAmount = 1 << 10;

        if (isOpen) {

            try {
                this.receiptPrinter.addPaper(amount);
            } catch (OverloadedDevice e) {
                throw new RuntimeException(e);
            }

        } else {
            throw new ClosedHardwareException("Hardware is closed!");
        }
    }

}
