package com.thelocalmarketplace.software.funds;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.*;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.card.Card.CardData;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.jjjwelectronics.card.*;

/**
 * <p> This class facilitates communication between com.jjjwelectronics.card.CardReaderListener and com.thelocalmarketplace.software.funds.Funds</p> 
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
public class PayByCard {
	
	private Card card;
	private double amountDue;
	boolean paidBool;
	boolean posted;
	private Funds funds;
	
	public PayByCard(ICardReader cardReader, Funds funds) {
		InnerListener cardListener = new InnerListener();
		cardReader.register(cardListener);
		
		amountDue = funds.getAmountDue().doubleValue();
		this.funds = funds;
	}
	
	private class InnerListener implements CardReaderListener {

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
		public void aCardHasBeenSwiped(){
			paidBool = false;
			// TODO Auto-generated method stub
			
		}

		@Override
		public void theDataFromACardHasBeenRead(CardData data) {	
			//card = new Card(data.getType(), data.getNumber(), data.getCardholder(), null);
			getTransactionFromBank(card);
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
	}

	
    /**
     * Facilitates all communication with CardIssuer(s) required for billing/posting 
     * @throws DisabledException 
     * @throws NoCashAvailableException 
     * @throws CashOverloadException 
     */
	public void getTransactionFromBank(Card card){
		// Determine the type of card, check the database for validity, then attempt to process a transaction
		// All failures to process/post will be handled simply as "Declined" as that is all a self-checkout POS would typically report
		if (card.kind == SupportedCardIssuers.ONE.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.ONE.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {	
				// Posting may fail for some reason such as insufficient available Credit limit
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.ONE.getIssuer()).postTransaction(card.number, holdNumber, amountDue);

				// This can fail and return -1 but this should not ever happen as this cannot fail if the transaction gets this far
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.ONE.getIssuer()).releaseHold(card.number, 1);
				}
				paidBool = true;
				funds.updatePaidCard(paidBool);
				return;
							
		} else if (card.kind == SupportedCardIssuers.TWO.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.TWO.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {	
				// Posting may fail for some reason such as insufficient available Credit limit
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.TWO.getIssuer()).postTransaction(card.number, holdNumber, amountDue);

				// This can fail and return -1 but this should not ever happen as this cannot fail if the transaction gets this far
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.TWO.getIssuer()).releaseHold(card.number, 1);
				}
				paidBool = true;
				funds.updatePaidCard(paidBool);
				return;
							
		} else if (card.kind == SupportedCardIssuers.THREE.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.THREE.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {	
				// Posting may fail for some reason such as insufficient available Credit limit
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.THREE.getIssuer()).postTransaction(card.number, holdNumber, amountDue);

				// This can fail and return -1 but this should not ever happen as this cannot fail if the transaction gets this far
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.THREE.getIssuer()).releaseHold(card.number, 1);
				}
				paidBool = true;
				funds.updatePaidCard(paidBool);		
				return;
			
		} else if (card.kind == SupportedCardIssuers.FOUR.getIssuer()) {
			long holdNumber = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.FOUR.getIssuer()).authorizeHold(card.number, 1);
			
			if (holdNumber == -1L) {
				// There are not enough available holds
				// Invalid card
				// Blocked card
				// Maxed holds
				return;
			} else {
				// Posting may fail for some reason such as insufficient available Credit limit
				boolean post = CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.FOUR.getIssuer()).postTransaction(card.number, holdNumber, amountDue);
				
				// This can fail and return -1 but this should not ever happen as this cannot fail if the transaction gets this far
				CardIssuerDatabase.CARD_ISSUER_DATABASE.get(SupportedCardIssuers.FOUR.getIssuer()).releaseHold(card.number, 1);

				paidBool = true;
				funds.updatePaidCard(paidBool);
				return;
			}
		} else {
			throw new InvalidActionException("Declined");
		}
	}	
}


