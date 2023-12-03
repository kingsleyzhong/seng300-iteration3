package com.thelocalmarketplace.software.test.weight;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.test.AbstractTest;
import com.thelocalmarketplace.software.weight.Weight;
import com.thelocalmarketplace.software.weight.WeightListener;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/*	* Unit Test for Weight Class
 * 
 * 
* Project Iteration 3 Group 1
*
* Derek Atabayev 			: 30177060 
* Enioluwafe Balogun 		: 30174298 
* Subeg Chahal 				: 30196531 
* Jun Heo 					: 30173430 
* Emily Kiddle 				: 30122331 
* Anthony Kostal-Vazquez 	: 30048301 
* Jessica Li 				: 30180801 
* Sua Lim 					: 30177039 
* Savitur Maharaj 			: 30152888 
* Nick McCamis 				: 30192610 
* Ethan McCorquodale 		: 30125353 
* Katelan Ng 				: 30144672 
* Arcleah Pascual 			: 30056034 
* Dvij Raval 				: 30024340 
* Chloe Robitaille 			: 30022887 
* Danissa Sandykbayeva 		: 30200531 
* Emily Stein 				: 30149842 
* Thi My Tuyen Tran 		: 30193980 
* Aoi Ueki 					: 30179305 
* Ethan Woo 				: 30172855 
* Kingsley Zhong 			: 30197260 
 */
public class WeightTest extends AbstractTest {
	public WeightTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
		// TODO Auto-generated constructor stub
	}

	private Weight weight;

	private TestWeightListener weightListener;

	/*
	 * Stub for TestWeightListerner
	 */
	private class TestWeightListener implements WeightListener {
		boolean discrepancyNotified = false;
		boolean discrepancyFixed = false;

		@Override
		public void notifyDiscrepancy() {
			discrepancyNotified = true;
			discrepancyFixed = false;
		}

		@Override
		public void notifyDiscrepancyFixed() {
			discrepancyFixed = true;
			discrepancyNotified = false;
		}

		@Override
		public void notifyBagsTooHeavy() {
		}

	}

	@Before
	public void setUp() {
		basicDefaultSetup();

		weight = new Weight(scs.getBaggingArea());

		weightListener = new TestWeightListener();
		weight.register(weightListener);

	}

	@Test
	public void testUpdateWithOneItem() {
		Mass mass1 = new Mass(2.0);
		weight.update(mass1);
		Mass expected = mass1;
		Mass actual = weight.getExpectedWeight();
		assertEquals("The expected Mass should be updated to 2.0", expected, actual);
	}

	@Test
	public void testUpdateWithTwoItems() {
		Mass mass1 = new Mass(2.0);
		Mass mass2 = new Mass(200.0);
		weight.update(mass1);
		weight.update(mass2);
		Mass expected = mass1.sum(mass2);
		Mass actual = weight.getExpectedWeight();
		assertEquals("The expected Mass should be updated to 202.0", expected, actual);
	}

	@Test
	public void testTheMassOnTheScaleHasChangedUpdatesActualWeight() {
		Mass newMass = new Mass(10.0);
		Barcode barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		Item item = new BarcodedItem(barcode, new Mass(10.0));
		scs.getBaggingArea().addAnItem(item); // Simulate the scale reporting a new mass
		assertEquals("Actual weight should be updated to the new mass.", newMass, weight.getActualWeight());
	}

	@Test
	public void testCheckDiscrepancyWithDifferentWeights() {
		weight.update(new Mass(100.0)); // Set expected weight to 100
		Barcode barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		Item item = new BarcodedItem(barcode, new Mass(150.0));
		scs.getBaggingArea().addAnItem(item); // Simulate the scale reporting a new mass

		assertTrue("Discrepancy should be notified.", weightListener.discrepancyNotified);
		assertTrue("Discrepancy flag should be true.", weight.isDiscrepancy());
	}

	@Test
	public void testCheckDiscrepancyWithSameWeights() {
		weight.update(new Mass(100.0)); // Set expected weight to 100
		Barcode barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		Item item = new BarcodedItem(barcode, new Mass(100.0));
		scs.getBaggingArea().addAnItem(item); // Simulate the scale reporting a new mass
		assertFalse("Discrepancy flag should be false.", weight.isDiscrepancy());
	}

	@Test
	public void testCheckDiscrepancySmallDifference() {
		weight.update(new Mass(100.0)); // Set expected weight to 100
		Barcode barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		Item item = new BarcodedItem(barcode, new Mass(104.0));
		scs.getBaggingArea().addAnItem(item); // Simulate the scale reporting a new mass
		assertFalse("Discrepancy flag should be false.", weight.isDiscrepancy());
	}

	@Test
	public void testCheckDiscrepancyFixed() {
		weight.update(new Mass(100.0)); // Set expected weight to 100
		Barcode barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		Item item = new BarcodedItem(barcode, new Mass(50.0));
		scs.getBaggingArea().addAnItem(item); // Simulate the scale reporting a new mass
		Item item2 = new BarcodedItem(barcode, new Mass(50.0));
		scs.getBaggingArea().addAnItem(item2);
		assertFalse("Discrepancy flag should be false.", weight.isDiscrepancy());
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testRegisterNullListener() {
		weight.register(null);
	}

	@Test(expected = NullPointerSimulationException.class)
	public void testDeRegisterNullListener() {
		weight.deRegister(null);
	}

	@Test
	public void testDeRegisterListener() {
		weight.deRegister(weightListener);
		assertFalse("Listener should be removed.", weight.listeners.contains(weightListener));
	}
}
