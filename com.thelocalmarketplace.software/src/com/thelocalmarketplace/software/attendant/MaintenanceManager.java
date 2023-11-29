package com.thelocalmarketplace.software.attendant;

import com.jjjwelectronics.printer.IReceiptPrinter;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;

import java.math.BigDecimal;
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
    public class NotDisabledSessionException extends Exception {
        public NotDisabledSessionException(String str) {super(str);}
    }
    public MaintenanceManager() {
    }
    public void openHardware(Session session) throws NotDisabledSessionException {
        this.session = session;
        state = session.getState();
        if (state == SessionState.BLOCKED) { // Change check to SessionState.DISABLED
            scs = session.getStation();
            receiptPrinter = scs.getPrinter();
            banknoteDispensers = scs.getBanknoteDispensers();
            coinDispensers = scs.getCoinDispensers();
        }
        else {
            throw new NotDisabledSessionException("Session is not disabled!");
        }
    }
    
}
