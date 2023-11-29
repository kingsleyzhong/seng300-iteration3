package com.thelocalmarketplace.software.membership;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.scanner.IBarcodeScanner;

import java.util.ArrayList;
import java.util.List;

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
public class Membership {
    protected final List<MembershipListener> listeners;
    private final ICardReader cardReader;
    private final List<IBarcodeScanner> barcodeScanners;
    private boolean addingItems = false;


	/** Initializes a new instance of a Membership facade that provides the checkout station logic with a
     * user-inputted membership number.
     * @param cardReader The card reader to read membership cards.
     * @param barcodeScanners The barcode scanners to scan membership barcodes. */
    public Membership(ICardReader cardReader, IBarcodeScanner... barcodeScanners) {
        listeners = new ArrayList<>();
        this.cardReader = cardReader;
        this.barcodeScanners = List.of(barcodeScanners);
        InnerListener membershipReader = new InnerListener();
        cardReader.register(membershipReader);
    }
    
    public class InnerListener implements CardReaderListener{

		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aCardHasBeenInserted() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void theCardHasBeenRemoved() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aCardHasBeenTapped() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void aCardHasBeenSwiped() {
			// TODO Auto-generated method stub
		}

		@Override
		public void theDataFromACardHasBeenRead(CardData data) {
			// TODO Auto-generated method stub
			if(addingItems == true && data.getType().contains("Membership")) {
				notifyMembershipEntered(data.getNumber());
			}
		}
    	
		
    }
    
    //setter for adding items
    public void setAddingItems(boolean addingItems) {
		this.addingItems = addingItems;
	}

    /** Registers a MembershipListener on this Membership facade.
     * @param listener The MembershipListener to register.*/
    public void register(MembershipListener listener) {
        if (listener == null)
            throw new NullPointerSimulationException("membership");
        listeners.add(listener);
    }

    /** Deregisters a MembershipListener on this Membership facade.
     * @param listener The MembershipListener to Deregister.*/
    public boolean deregister(MembershipListener listener) {
        if (listener == null)
            throw new NullPointerSimulationException("membership");
        return listeners.remove(listener);
    }

    /** Deregisters all MembershipListeners in this Membership facade. */
    public void deregisterAll() {
        listeners.clear();
    }

    protected void notifyMembershipEntered(String membershipNumber) {
        for (MembershipListener listener : listeners)
            listener.membershipEntered(membershipNumber);
    }
}
