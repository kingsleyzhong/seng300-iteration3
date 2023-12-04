package com.thelocalmarketplace.software.test.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.views.AbstractView;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemAddedRule;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import com.thelocalmarketplace.software.test.AbstractTest;

import com.thelocalmarketplace.software.weight.Weight;

import StubClasses.BagStub;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import junit.framework.Assert;
import powerutility.PowerGrid;

/*
 * Testing for methods related to the AddBags use case
 * 	- method Session.addBags()
 * 	- method Session.checkBags()
 * 	- method Session.bagsTooHeavy()
 * 	- method Session.cancelAddBags()
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

public class AddBagsTest extends AbstractSessionTest {

	public AddBagsTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
		// TODO Auto-generated constructor stub
	}

	private double BAG_MASS_LIMIT = 250.00;// 250g, well above what a bag probably weighs
											// above the sensativity limit for even a bronze scale (5g)
	// bag
	BagStub bag;
	BagStub overweightBag;
	BagStub weightLimitBag;
	BagStub notBag;
	Mass bagMass;
	Mass overweightBagMass;
	Mass weightLimitBagMass;
	Mass notBagMass;

	IElectronicScale baggingArea;

	// time to wait before adding item to the bagging area

	@Before
	public void setup() {
		basicDefaultSetup();
		// create "bags"

		bagMass = new Mass(10 * Mass.MICROGRAMS_PER_GRAM);// bag of mass 100g < BAG MASS LIMIT
		overweightBagMass = new Mass((BAG_MASS_LIMIT + 60.0));// mass > BAG MASS LIMIT
		weightLimitBagMass = new Mass(BAG_MASS_LIMIT);// mass equal to the limited size of a bag in the session software
		notBagMass = new Mass((30 * Mass.MICROGRAMS_PER_GRAM));

		bag = new BagStub(bagMass);
		overweightBag = new BagStub(overweightBagMass);
		weightLimitBag = new BagStub(weightLimitBagMass);
		notBag = new BagStub(notBagMass);
		session.getWeight().configureMAXBAGWEIGHT(BAG_MASS_LIMIT);
		session.start();

		baggingArea = scs.getBaggingArea();
	}

	/*
	 * Tests that calling addBag() before the session has started has no impact on
	 * the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session
	 * state
	 * 
	 * Expected Behavior: the session remains in the pre-session state
	 */
	@Test
	public void test_addBags_beforeStartSession_stateUnchanged() {
		// call addBags
		session.cancel();
		session.addBags();

		// a bag is not physically added because that will cause a discrepancy

		// the session has not started
		assertTrue(session.getState() == SessionState.PRE_SESSION);
	}

	/*
	 * Tests that calling addBag() before the session has started has no impact on
	 * the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session
	 * state
	 * 
	 * Expected Behavior: the expected weight on the scale doesnt change
	 */
	@Test
	public void test_addBags_beforeStartSession_expectedWeightUnchanged() {
		session.cancel();
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(bag);

		// check the expected weight has been updated (?)

		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	/*
	 * Tests that calling addBags() during an active session updates the state of
	 * the Session to reflect this selection.
	 * 
	 * Expected behavior: Session.sessionState == ADDING_BAGS
	 */
	@Test
	public void test_addBags_updatesSessionState() {
		// start session:

		// call addBags
		session.addBags();

		// the system is in the adding bags state
		assertTrue(session.getState() == SessionState.ADDING_BAGS);
	}

	/*
	 * Tests that calling addBag() during an active session and then adding the bag
	 * to the
	 * bagging area does not result in any issues.
	 * 
	 * Expected Behavior: the session returns to normal runtime state (in session)
	 * 
	 */
	// bug found
	@Test
	public void test_addBags_addingBagsUnblocksSession() {
		// start session:

		// call addBags
		session.addBags();

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(bag);

		// the system is unblocked
		assertTrue(session.getState() == SessionState.IN_SESSION);
	}

	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does
	 * not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated
	 */
	@Test
	public void test_addBags_updatesExpectedWeight() {
		// start session:

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(bag);

		// check the expected weight has been updated (?)

		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have updated
		assertFalse(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	/*
	 * Tests that calling addBag() during an active session and then changing the
	 * bagging area by
	 * removing something from the bagging area isnt registered as adding a bag
	 * successfully
	 * 
	 * Expected behavior: the expected weight of session is not updated
	 */
	@Test
	public void test_addBags_unexpectedChange_doesntUpdateExpectedWeight() {
		// start session:

		// pre-test: add an item to the bagging area
		weight.update(notBagMass); // sets the expected mass on the scale to already know about the bag
		scs.getBaggingArea().addAnItem(notBag);

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();

		// remove the not-bag from the bagging area
		scs.getBaggingArea().removeAnItem(notBag);

		// check the expected weight after the interaction
		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	/*
	 * Tests that calling addBag() during an active session and then changing the
	 * bagging area by
	 * removing something from the bagging area isnt registered as adding a bag
	 * successfully
	 * 
	 * Expected behavior: the session is blocked (this is a weight discrepancy)
	 */
	@Test
	public void test_addBags_unexpectedChange_blocksSession() {
		// pre-test: add an item to the bagging area
		weight.update(notBagMass); // sets the expected mass on the scale to already know about the bag

		baggingArea.addAnItem(notBag);

		// call addBags
		session.addBags();

		// remove the not-bag from the bagging area
		baggingArea.removeAnItem(notBag);

		// check the state
		assertNotEquals(SessionState.IN_SESSION, session.getState());
		assertTrue("Weight Discrepancy", weight.isDiscrepancy());
	}

	/*
	 * Tests adding an item that is heavier than the set MAXBAGWEIGHT to the bagging
	 * area results in
	 * the session being blocked (Bags too Heavy use case)
	 * 
	 * Expected behavior: Session is blocked
	 */
	@Test
	public void test_addBags_overweightBag_blockSession() {
		// start session:

		// call addBags
		session.addBags();

		// add the heavy bag to the bagging area
		scs.getBaggingArea().addAnItem(overweightBag);

		// check the state
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an item that is heavier than the set MAXBAGWEIGHT to the bagging
	 * area results in
	 * the expected weight not being updated (Bags too Heavy use case)
	 * 
	 * Expected behavior: expected weight is unchanged
	 */
	@Test
	public void test_addBags_overweightBag_doesntUpdateExpectedWeight() {
		// start session:

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();

		// add the heavy bag to the bagging area
		scs.getBaggingArea().addAnItem(overweightBag);

		// check the expected weight after the interaction
		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);

	}

	/*
	 * Tests adding an item that is as heavy as the set MAXBAGWEIGHT to the bagging
	 * area results in
	 * the session being blocked (Bags too Heavy use case)
	 * 
	 * Expected behavior: Session is blocked
	 */
	@Test
	public void test_addBags_weightLimitBag_blockSession() {
		// start session:

		// call addBags
		session.addBags();

		// Add the bag to the bagging area
		scs.getBaggingArea().addAnItem(weightLimitBag);

		// check the state
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an item that is as heavy as the set MAXBAGWEIGHT to the bagging
	 * area results in
	 * the expected weight staying the same
	 * 
	 * Expected behavior: expected weight is unchanged
	 */
	@Test
	public void test_addBags_weightLimitBag_expectedWeightIsNotUpdated() {
		// start session:

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();

		// add the heavy bag to the bagging area
		scs.getBaggingArea().addAnItem(weightLimitBag);

		// check the expected weight after the interaction
		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	/*
	 * Tests calling cancelAddBags(). When in the addBags state, this method should
	 * cancel the interaction
	 * and return the system to the normal runtime state (IN_SESSION)
	 * 
	 * Expected behavior: State is changed to SessionState.In_SESSION
	 */
	@Test
	public void test_cancelAddBags_updatesState() {
		// call addBags
		session.addBags();

		// call cancelAddBags()
		session.cancelAddBags();

		// check state to make sure the system has updates
		assertFalse("Not Discrepancy", weight.isDiscrepancy());
		assertEquals(SessionState.IN_SESSION, session.getState());
	}

	/*
	 * Tests calling cancelAddBags(). When in the addBags state, this method should
	 * cancel the interaction, meaning
	 * any changes in the bagging area result in a weight discrepancy.
	 *
	 * Expected behavior: Expected Weight of the session is not updated
	 */
	@Test
	public void test_cancelAddBags_doesntUpdateExpectedWeight() {
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();

		// call cancelAddBags()
		session.cancelAddBags();

		// add the heavy bag to the bagging area
		baggingArea.addAnItem(bag);

		// check the expected weight after the interaction
		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	/*
	 * Tests calling cancelAddBags(). When not in the addBags state, this method
	 * should do nothing
	 * 
	 * Expected behavior: State is unchanged and remains in the PRE_SESSION state
	 */
	@Test
	public void test_cancelAddBags_doesntUpdatesState_PRE_SESSION() {
		// dont start session
		session.cancel();

		// call cancelAddBags
		session.cancelAddBags();

		// check the state
		assertTrue(session.getState() == SessionState.PRE_SESSION);
	}

	/*
	 * Tests calling cancelAddBags(). If the method is called and then a bag is
	 * added to the bagging area,
	 * the session should enter a blocked state (caused by a weight discrepancy)
	 * 
	 * Expected behavior: State is SessionState.BLOCKED
	 */
	@Test
	public void test_cancelAddBags_blocksSystem() {
		// start session:

		// call addBags
		session.addBags();

		// call cancelAddBags()
		session.cancelAddBags();

		// add the heavy bag to the bagging area
		baggingArea.addAnItem(bag);

		// check that the session is blocked
		assertTrue("Weight Discrepancy", weight.isDiscrepancy());
		assertEquals("Check that the session is blocked", SessionState.BLOCKED, session.getState());
	}

	/**
	 * Tests that Session.configureMAXBAGWEIGHT updates the maxBagWeight in the
	 * expected way
	 * Tests using a double (representing the desired maximum bag weight in grams)
	 */
	@Test
	public void test_configureMAXBAGWEIGHT_byDouble() {
		double newMAXBAGWEIGHT = 6.78; // 6.79g
		// create a Mass using this
		Mass expectedMaxBagWeight = new Mass(newMAXBAGWEIGHT);

		// create a new Session with this as the max bag weight
		Session newSession = new Session();
		Funds funds = new Funds(scs);
		Weight weight = new Weight(scs.getBaggingArea());
		ItemManager itemManager = new ItemManager();
		Receipt receipt = new Receipt(scs.getPrinter());
		SelfCheckoutStationBronze scs = new SelfCheckoutStationBronze();
		newSession.setup(itemManager, funds, weight, receipt, membership, scs);
		newSession.getWeight().configureMAXBAGWEIGHT(newMAXBAGWEIGHT);

		// create a Mass using the max weight weight value from session
		Mass actualMaxBagWeight = new Mass(newSession.getWeight().get_MAXBAGWEIGHT_inGrams());

		// compare the two Masses
		assertTrue(actualMaxBagWeight.compareTo(expectedMaxBagWeight) == 0);
	}

	/**
	 * Tests that Session.configureMAXBAGWEIGHT updates the maxBagWeight in the
	 * expected way
	 * Tests using a long (representing the desired maximum bag weight in
	 * micrograms)
	 */
	@Test
	public void test_configureMAXBAGWEIGHT_byLong() {
		long newMAXBAGWEIGHT = 678; // 679g
		// create a Mass using this
		Mass expectedMaxBagWeight = new Mass(newMAXBAGWEIGHT);

		// create a new Session with this as the max bag weight
		Session newSession = new Session();
		Funds funds = new Funds(scs);
		Weight weight = new Weight(scs.getBaggingArea());
		ItemManager itemManager = new ItemManager();
		Receipt receipt = new Receipt(scs.getPrinter());
		SelfCheckoutStationBronze scs = new SelfCheckoutStationBronze();
		newSession.setup(itemManager, funds, weight, receipt, membership, scs);
		newSession.getWeight().configureMAXBAGWEIGHT(newMAXBAGWEIGHT);

		// create a Mass using the max weight weight value from session
		Mass actualMaxBagWeight = new Mass(newSession.getWeight().get_MAXBAGWEIGHT_inMicrograms());

		// compare the two Masses
		assertTrue(actualMaxBagWeight.compareTo(expectedMaxBagWeight) == 0);
	}

}
