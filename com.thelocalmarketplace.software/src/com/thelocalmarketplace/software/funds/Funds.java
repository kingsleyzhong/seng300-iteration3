package com.thelocalmarketplace.software.funds;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.jjjwelectronics.IllegalDigitException;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.exceptions.NotEnoughChangeException;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * This class represents the funds associated with a self-checkout session.
 * It manages the total price of items, the amount paid, and the amount due.
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
public class Funds {
	protected ArrayList<FundsListener> listeners = new ArrayList<>();
	private BigDecimal itemsPrice; // Summed price of all items in the session (in cents)
	//private BigDecimal paid; // Amount paid by the customer (in cents)
	private BigDecimal amountDue; // Remaining amount to be paid (in cents)
	private boolean isPay; // Flag indicating if the session is in pay mode
	private final BigDecimal[] banknoteDenominations;
    private final List<BigDecimal> coinDenominations;

  // from old version, delete if unused/ it breaks stuff
	// Testing ONLY
	public boolean payed;
	public boolean successfulSwipe;  // end old version stuff to delete
  
	private AbstractSelfCheckoutStation scs;

	/**
	 * Constructor that initializes the funds and registers an inner listener to the
	 * self-checkout station.
	 * 
	 * @param scs The self-checkout station
	 */
	public Funds(AbstractSelfCheckoutStation scs) {

		if (scs == null) {
			throw new IllegalArgumentException("SelfCheckoutStation should not be null.");
		}
		this.itemsPrice = BigDecimal.ZERO;
		this.amountDue = BigDecimal.ZERO;
		this.isPay = false;
		this.payed = false;
		this.scs = scs;
		scs.getCoinSlot().disable();
		scs.getBanknoteInput().disable();
		// sort denominations in descending order
        banknoteDenominations = scs.getBanknoteDenominations();
        Arrays.sort(banknoteDenominations, Collections.reverseOrder());
        coinDenominations = scs.getCoinDenominations();
        coinDenominations.sort(Collections.reverseOrder());
	}

	/**
	 * Updates the total items price.
	 * 
	 * @param price The price to be added (in cents)
	 */
	public void update(BigDecimal price) {
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalDigitException("Price should be positive.");
		}
		this.itemsPrice = this.itemsPrice.add(price);
		calculateAmountDue(price.negate());
	}

	/**
	 * Updates the total items price after an item has been removed.
	 * 
	 * @param price The price to be removed (in cents)
	 */
	public void removeItemPrice(BigDecimal price) {
		if (price.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalDigitException("Price should be positive.");
		}
		
		this.itemsPrice = this.itemsPrice.subtract(price);
		calculateAmountDue(price);
	}

	/**
	 * Sets the pay mode.
	 * 
	 * @param isPay Flag indicating if the session is in pay mode
	 */
	public void setPay(boolean isPay) {
		this.isPay = isPay;
	}
	
	public void enableCash() {
		scs.getCoinSlot().enable();
		scs.getBanknoteInput().enable();
	}
	
	public void disableCash() {
		scs.getCoinSlot().disable();
		scs.getBanknoteInput().disable();
	}

	public BigDecimal getItemsPrice() {
		return itemsPrice;
	}

	/*public BigDecimal getPaid() {
		return paid;
	}*/

	public BigDecimal getAmountDue() {
		return amountDue;
	}

	public boolean isPay() {
		return isPay;
	}

	/**
	 * Calculates the amount due by subtracting the paid amount from the total items
	 * price.
	 */
	private void calculateAmountDue(BigDecimal amountPaid) {
		
		this.amountDue = this.amountDue.subtract(amountPaid);

		for (FundsListener l : listeners)
			l.notifyUpdateAmountDue(this.amountDue);
		// To account for any rounding errors, checks if less that 0.0005 rather than
		// just 0
		if (amountDue.intValue() <= 0.0005 && isPay) {

			for (FundsListener l : listeners)
				l.notifyPaid();

			// Return change if amount needed to be returned is greater than a cent
			if (amountDue.intValue() <= -1) {
				returnChange();
			}
			payed = true;
		}
	}

	/*** 
	 * Checks the status of a card payment
	 */
	public void updatePaidCard(boolean paidBool) {
		if (isPay) {
			if (paidBool) {
				calculateAmountDue(amountDue.negate());
			}
		} else {
			throw new InvalidActionException("Not in Card Payment state");
		}
	}

	/***
	 * Updates Payment based on the PayByCash Controller
	 */
	public void updatePaidCash(BigDecimal paid) {
		if (isPay) {
			calculateAmountDue(paid.negate());
		}

	}

	/***
	 * Calculates the change needed	 * 
	 */
	private void returnChange() {
		double changeDue = (this.amountDue).abs().doubleValue();
		changeHelper(changeDue);
	}

	/***
	 * Returns the change back to customer
	 * 
	 * @param changeDue
	 * 	 */
	private void changeHelper(double changeDue){
		
		// loop through the denominations (sorted from largest to smallest) 
        // until either the change is fully paid
        // or the system possesses insufficient change
        for (int index = 0; index < banknoteDenominations.length + coinDenominations.size() && changeDue > 0; index++) {
            BigDecimal denomination;
            boolean isBanknote = false;

            // if the current index is within the "banknotes zone"
            if (index < banknoteDenominations.length) {
                denomination = banknoteDenominations[index];
                isBanknote = true;
            }
            // if the current index is within the "coins zone"
            else
                denomination = coinDenominations.get(index - banknoteDenominations.length);

            // dispense the current denomination until it is larger than the remaining amount of change
            while (changeDue >= denomination.doubleValue()) {
                if (isBanknote) {
                    try {
                        scs.getBanknoteDispensers().get(denomination).emit();
                    } catch (NoCashAvailableException e) {
                    	// notifies change not available
                    	for (FundsListener l : listeners)
            				l.notifyInsufficentChange();
                        break; // go to next denomination if this denomination runs out
                    } catch (DisabledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (CashOverloadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                else {
                    try {
                        scs.getCoinDispensers().get(denomination).emit();
                    } catch (NoCashAvailableException e) {
                    	// notifies change not available
                    	for (FundsListener l : listeners)
            				l.notifyInsufficentChange();
                        break; // go to next denomination if this denomination runs out
                    } catch (CashOverloadException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (DisabledException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                changeDue -= denomination.doubleValue();
            }
        }
        scs.getBanknoteOutput().dispense();
        // occurs when all denominations have been cycled through and the change is not yet fully dispensed
        if (changeDue > 0) {
        	// notifies change not available
        	for (FundsListener l : listeners)
				l.notifyInsufficentChange();
        	throw new NotEnoughChangeException("Not enough change in the machine");
        }
	}

	/**
	 * Methods for adding funds listeners to the funds
	 */
	public synchronized boolean deregister(FundsListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

		return listeners.remove(listener);
	}

	public synchronized void deregisterAll() {
		listeners.clear();
	}

	public final synchronized void register(FundsListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}

    public void clear() {
		itemsPrice = new BigDecimal(0);
		amountDue = new BigDecimal(0);
    }
}