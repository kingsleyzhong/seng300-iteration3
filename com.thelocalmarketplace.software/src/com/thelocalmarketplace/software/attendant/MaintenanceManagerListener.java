package com.thelocalmarketplace.software.attendant;

import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.coin.CoinStorageUnit;
import com.thelocalmarketplace.software.Session;

/**
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
public interface MaintenanceManagerListener {

    /**
     * Signals when ink has been added
     */
    void notifyInkAdded(Session session);

    /**
     * Signals when paper has been added
     */
    void notifyPaperAdded(Session session);

    /**
     * Signals when coins have been added
     */
    void notifyCoinAdded(Session session);

    /**
     * Signals when banknotes have been added
     */
    void notifyBanknoteAdded(Session session);

    /**
     * Signals when coins have been removed
     */
    void notifyCoinRemoved(Session session, CoinStorageUnit coinStorage);

    /**
     * Signals when banknotes have been removed
     */
    void notifyBanknoteRemoved(Session session, BanknoteStorageUnit banknoteStorage);
    
    /**
     * Signals when the hardware has been opened
     */
    void notifyHardwareOpened(Session session);
    
    /**
     * Signals when the hardware has been closed
     */
    void notifyHardwareClosed(Session session);
}
