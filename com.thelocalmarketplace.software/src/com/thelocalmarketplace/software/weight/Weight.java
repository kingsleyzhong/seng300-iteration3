package com.thelocalmarketplace.software.weight;

import java.math.BigInteger;
import java.util.ArrayList;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Tracks the weight of the system. Contains an expected weight, which contains
 * the weight of all the
 * products in the session. And contains an actual weight, which is the weight
 * on the scale.
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

public class Weight {

	public ArrayList<WeightListener> listeners = new ArrayList<>();
	private Mass actualWeight = Mass.ZERO; // Actual Current weight on scale,set to zero in default
	private Mass expectedWeight = Mass.ZERO; // Expected weight according to added items, set to zero in default
	private boolean isDiscrepancy = false; // Flag of weight Discrepancy, set to false in default
	private boolean bagCheck = false;
	private Mass lastWeightAdded = Mass.ZERO;
	private Mass personalBagsWeight;
	private Mass purchasedBagsWeight;
	
	private Mass MAXBAGWEIGHT = new Mass(500 * Mass.MICROGRAMS_PER_GRAM);

	/**
	 * Basic constructors for weight class
	 *
	 * @param scs
	 *            The self-checkout station in which the weight shall be registered
	 *            to
	 */
	public Weight(IElectronicScale baggingArea) {
		baggingArea.register(new innerListener());
	}

	public class innerListener implements ElectronicScaleListener {

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
		public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
			actualWeight = mass;
			if(bagCheck) {
				checkBags();
			}
			else{ // check if a weight discrepancy has occurred
				checkDiscrepancy();
			}

		}

		@Override
		public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
			// TODO Auto-generated method stub

		}

		@Override
		public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
			// TODO Auto-generated method stub

		}
	}
	
	/**
	 * Method used to signal that a bag will be added to the bagging area
g	 *
	 */
	public void addBags() {
			bagCheck = true;
	}
	
	/*
	 * Occurs when the bags the Customer added to the bagging area are above the
	 * maximum allowed bag weight
	 * (set by MAXBAGWEIGHT, able to be configured).
	 * 
	 * Currently sorta useless without an attendant or any way to contact an
	 * attendant
	 * 
	 * Once blocked this could be overrides the same as any other blocked state
	 */
	private void bagsTooHeavy() {
		isDiscrepancy = true;
		for (WeightListener l : listeners) {
			l.notifyBagsTooHeavy();
		}
	}
	

	/*
	 * Runs when a customer has signaled their desire to add their own bags to the
	 * bagging area,
	 * and then a change in the bagging area was recorded.
	 * 
	 * Compares the weight on the scale to the the weight before adding bags to
	 * determine if bags were added
	 * and that the bags are below the specified maximum bag weight (MAXBAGWEIGHT).
	 * 
	 * If the weight change was negative -> notifies unexpected change in the
	 * bagging area
	 * and blocks the system. Expected weight is not updated.
	 * If the bags are too heavy -> notifies attendant and customer. Blocks the
	 * system
	 * Else: bags were accepted. Expected weight is updated to include the bag
	 * weight. Session returns to normal runtime state.
	 * 
	 */
	private void checkBags() {

		// check that the weight change was caused by adding weight
		if (actualWeight.compareTo(expectedWeight) < 0) {
			return;
		}
		
		personalBagsWeight = actualWeight.difference(expectedWeight).abs();

		// check if the updated weight is to heavy for just a bag (Throw exception??)
		// if weight > expected weight of a bag
		if (personalBagsWeight.compareTo(MAXBAGWEIGHT) >= 0) {
			bagsTooHeavy();
			return;
		} else {
			// else: the bag added is within the allowed weight range
			// update the expected weight on the scale
			update(personalBagsWeight);

		}
		// returns the Session to the normal runtime state
		isDiscrepancy = false;
		bagCheck = false;
		for (WeightListener l : listeners) {
			l.notifyDiscrepancyFixed();
		}
	}
	
	public void cancel() {
		bagCheck = false;
	}
	
	/*
	 * This method will update expected weight by accumulating the weight of scanned
	 * item from parameter
	 * After each time this method been called, it will run checkDiscrepancy()
	 */
	public void update(Mass mass) {
		this.expectedWeight = this.expectedWeight.sum(mass);
		this.lastWeightAdded = mass;
		checkDiscrepancy();
	}
	
	public void subtract(Mass mass) {
		this.expectedWeight = this.expectedWeight.difference(mass).abs();
		checkDiscrepancy();
	}
	
	public Mass getLastWeightAdded() {
		return this.lastWeightAdded;
	}
	
	/*
	 * This method will change the expected weight after the item is removed
	 */
	public void removeItemWeightUpdate(Mass massToSubtract) {
		// Figure this part out
		// first we make mass to subtract a negative
		// BigInteger negative_mass =
		// BigInteger.valueOf(massToSubtract.inMicrograms().multiply(null));
		// BigInteger conversion = BigInteger.valueOf(-1);
	
		BigInteger NewValue = this.expectedWeight.inMicrograms().subtract(massToSubtract.inMicrograms());
	
		// Mass negativemass = new
		// Mass(massToSubtract.inMicrograms().multiply(conversion));
	
		this.expectedWeight = new Mass(NewValue);
		checkDiscrepancy();
	}
	
	/*
	 * This method checks if there is a Discrepancy between expectedWeight and
	 * actualWeight.
	 * if two values are equal and isDiscrepancy is true, then call
	 * DisrepancyFixed() on it's listener and set isDiscrepancy False
	 * if two value are not equal, call notifyDiscrepancy on it's listeners.
	 * Still need to figure out how to set a range of Mass as effective error
	 * Tolerance of +-5% and 5 grams (bronze)
	 */
	public void checkDiscrepancy() {
		double difference = expectedWeight.difference(actualWeight).abs().inGrams().doubleValue();
		if (difference > 5) {
			isDiscrepancy = true;
			for (WeightListener l : listeners) {
				l.notifyDiscrepancy();
			}
		} else {
			if (isDiscrepancy) {
				isDiscrepancy = false;
				for (WeightListener l : listeners) {
					l.notifyDiscrepancyFixed();
				}
			}
		}
	}
	
	/**
	 * Gets the expectedWeight of the system
	 *
	 * @return
	 *         The expected weight as Mass of the system
	 */
	public Mass getExpectedWeight() {
		return expectedWeight;
	}

	/**
	 * Gets the actualWeight of the system
	 *
	 * @return
	 *         The actual weight currently on the scale
	 */
	public Mass getActualWeight() {
		return actualWeight;
	}

	/**
	 * Returns the maximum bag weight for the system in grams (this one is secure)
	 */
	public double get_MAXBAGWEIGHT_inGrams() {
		return this.MAXBAGWEIGHT.inGrams().doubleValue();
	}

	/**
	 * Returns the maximum bag weight for the system in grams (this one is secure)
	 */
	public long get_MAXBAGWEIGHT_inMicrograms() {
		return this.MAXBAGWEIGHT.inMicrograms().longValue();
	}

	
	/**
	 * Gets the value of if there is a discrepancy or not
	 *
	 * @return
	 *         True if there is a discrepancy, false if no discrepancy
	 */
	public boolean isDiscrepancy() {
		return isDiscrepancy;
	}

	/**
	 * Sets the maximum bag weight for this session
	 * 
	 * @params
	 *         maxBagWeight: double representing the maximum weight of a bag (in
	 *         grams)
	 */
	public void configureMAXBAGWEIGHT(double maxBagWeight) {
		MAXBAGWEIGHT = new Mass(maxBagWeight);
	}

	/**
	 * Sets the maximum bag weight for this session
	 * 
	 * @params
	 *         maxBagWeight: long representing the maximum weight of a bag (in
	 *         micrograms)
	 */
	public void configureMAXBAGWEIGHT(long maxBagWeight) {
		MAXBAGWEIGHT = new Mass(maxBagWeight);
	}
	
	// register listeners
	public final synchronized void register(WeightListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}

	// de-register listeners
	public final synchronized void deRegister(WeightListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.remove(listener);
	}

	
}
