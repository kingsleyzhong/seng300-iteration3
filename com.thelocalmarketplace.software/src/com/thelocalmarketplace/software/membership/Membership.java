package com.thelocalmarketplace.software.membership;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;
import com.jjjwelectronics.card.CardReaderListener;
import com.jjjwelectronics.card.ICardReader;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

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

/** Facade class responsible for facilitating transfer of membership numbers between hardware and the business logic.
 * Provides functionality for entering membership numbers as a String (through the user interface), or from the card
 * reader. Membership cards should have "membership" as their type. */
public class Membership {
    private final List<MembershipListener> listeners = new ArrayList<>();
    private boolean addingItems = false;


	/** Initializes a new instance of a Membership facade that provides the checkout station logic with a
     * user-inputted membership number.
     * @param cardReader The card reader to read membership cards. Cannot be null. */
    public Membership(ICardReader cardReader) {
    	if (cardReader == null)
            throw new NullPointerSimulationException("card reader");
        cardReader.register(new InnerListener());
    }
    
    /** Checks to see if the provided card number is contained within the membership database. If the
     * membership number is present, then listeners are notified. For use with the virtual GUI keyboard.
     * @param memberCardNumber The card number which will be checked */
    public void typeMembership(String memberCardNumber) {
    	if (addingItems && MembershipDatabase.MEMBERSHIP_DATABASE.containsKey(memberCardNumber))
    		notifyMembershipEntered(memberCardNumber);
    	else
    		throw new InvalidActionException("Membership not in database");
    }
    
    /** Checks to see if the provided card data has a card number contained in the membership database.
     * If the membership number is present, then listeners are notified.
     * @param memberCard The member card to be examined for its card number */
    private void swipeMembership(CardData memberCard) {
    	String memberCardNumber = memberCard.getNumber();
    	if (addingItems && MembershipDatabase.MEMBERSHIP_DATABASE.containsKey(memberCardNumber)) {
    		notifyMembershipEntered(memberCardNumber);
    	}
    }

    private class InnerListener implements CardReaderListener {

		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {}

		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {}

		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {}

		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {}

		@Override
		public void aCardHasBeenInserted() {}

		@Override
		public void theCardHasBeenRemoved() {}

		@Override
		public void aCardHasBeenTapped() {}

		@Override
		public void aCardHasBeenSwiped() {}

		// Listens for card data which has been successfully read by the card reader
		@Override
		public void theDataFromACardHasBeenRead(CardData data) {
			if (data.getType().equalsIgnoreCase("membership"))
				swipeMembership(data);
		}
    }
    
    /** Enables the Membership facade to listen for membership entry. */
    public void setAddingItems(boolean addingItems) {
		this.addingItems = addingItems;
	}

    /** Registers a MembershipListener on this Membership facade.
     * @param listener The MembershipListener to register. Cannot be null. */
    public void register(MembershipListener listener) {
        if (listener == null)
            throw new NullPointerSimulationException("membership listener");
        listeners.add(listener);
    }

    /** Deregisters a MembershipListener on this Membership facade.
     * @param listener The MembershipListener to Deregister. Cannot be null. */
    public boolean deregister(MembershipListener listener) {
        if (listener == null)
            throw new NullPointerSimulationException("membership listener");
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
