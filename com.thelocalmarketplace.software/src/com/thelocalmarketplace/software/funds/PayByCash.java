package com.thelocalmarketplace.software.funds;

import java.math.BigDecimal;
import java.util.Currency;

import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/***
 * This class contains the observers for pay by cash events 
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


public class PayByCash {
	
	private BigDecimal cashPaid; //amount of cash that has been paid
	private Funds fund;
	
/***
 * Constructor that begins the total paid in cash at 0
 * @param scs
 * @param funds 
 * @param paid 
 */
	public PayByCash(CoinValidator coinValidator, BanknoteValidator banknoteValidator, Funds funds) {
		
		this.cashPaid = BigDecimal.ZERO;
				
		InnerCoinListener coinListener = new InnerCoinListener();
		InnerBankNoteListener banknoteListener = new InnerBankNoteListener();
		coinValidator.attach(coinListener);
		banknoteValidator.attach(banknoteListener);
		this.fund = funds;
		
		
				
	}
	
/***
 * InnerListener that observes for a valid coin to be received
 */
		 private class InnerCoinListener implements CoinValidatorObserver {

			@Override
			public void enabled(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void validCoinDetected(CoinValidator validator, BigDecimal value) {
				if (value.compareTo(BigDecimal.ZERO) <= 0) {
	                throw new IllegalArgumentException("Coin value should be positive.");
	            }
	                updateCoin(value); 
	            
				
			}

			public void invalidCoinDetected(CoinValidator validator) {
				//Dealt with in the hardware
			}
			
		 }
 /***
  * InnerListener that observes for a valid banknote to be received
  */
		private class InnerBankNoteListener implements BanknoteValidatorObserver {

			@Override
			public void enabled(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void disabled(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void turnedOn(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void turnedOff(IComponent<? extends IComponentObserver> component) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
				if (currency == null) {
					throw new NullPointerSimulationException("Null is not a valid currency.");
				}
				
				if (denomination.compareTo(BigDecimal.ZERO) <= 0) {
	                throw new IllegalArgumentException("Coin value should be positive.");
	            }
	            	updateBankNote(denomination); 
			}

			@Override
			public void badBanknote(BanknoteValidator validator) {
				//Dealt with in the hardware
			}	
	}
		
/***
 * This method will update the cashPaid based on the coin received
 */
	private void updateCoin(BigDecimal value) {
				
		this.cashPaid = this.cashPaid.add(value);
		this.fund.updatePaidCash(value);
				
	}
	
/***
 * This method will update the cashPaid based on the banknote received
 */
	private void updateBankNote(BigDecimal denomination) {
		
		this.cashPaid = this.cashPaid.add(denomination);
		this.fund.updatePaidCash(denomination);
	
	}

/***
 * Getter for cashPaid
 * @return cashPaid
 */
	public BigDecimal getCashPaid() {
				
		return this.cashPaid;
		
		
	}
	
}
