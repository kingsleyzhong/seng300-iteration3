package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.weight.Weight;

import StubClasses.BagStub;
import powerutility.PowerGrid;

public abstract class AbstractAddBagsTest {
	private AbstractSelfCheckoutStation scs;
	private Funds funds;
	private PowerGrid powergrid;
	private Mass bagMass;
	private Mass overweightBagMass;
	private Mass weightLimitBagMass;
	private Mass notBagMass;
	private BagStub bag;
	private BagStub overweightBag;
	private BagStub weightLimitBag;
	private BagStub notBag;
	private Session session;
	private Weight weight;
	private ItemManager itemManager;
	private Receipt receipt;
	private double BAG_MASS_LIMIT = 250.00;
	
	// This is done this way for JUnit best practices -StackOverflow
	protected abstract AbstractSelfCheckoutStation createInstance();
	
	//TO DO: 
	//attendantApprove does not handle bags too heavy requests. Only bulky item. 
	//need a function like addBulkyItem to resolve weight discrepancies caused by bags inside Session. 
	
	@Before
	public void setup() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
		scs = createInstance();	
		scs.resetConfigurationToDefaults();
   
    	// power on the self checkout station
    	powergrid = PowerGrid.instance();
    	powergrid.engageUninterruptiblePowerSource();
		
    	scs.plugIn(powergrid);
    	// turn on the self checkout station
		scs.turnOn();

        // create "bags"
        
        bagMass = new Mass(10 * Mass.MICROGRAMS_PER_GRAM);// bag of mass 100g < BAG MASS LIMIT
        overweightBagMass = new Mass((BAG_MASS_LIMIT + 60.0) );// mass > BAG MASS LIMIT
        weightLimitBagMass = new Mass(BAG_MASS_LIMIT );// mass equal to the limited size of a bag in the session software
		notBagMass = new Mass((30 * Mass.MICROGRAMS_PER_GRAM));

        bag = new BagStub(bagMass);
        overweightBag = new BagStub(overweightBagMass);
        weightLimitBag = new BagStub(weightLimitBagMass);
		notBag = new BagStub(notBagMass);

        
		// create a session with a known bag limit
		session = new Session(BAG_MASS_LIMIT);
		
        funds = new Funds(scs);
        weight = new Weight(scs.getBaggingArea());
        itemManager = new ItemManager(session);
        receipt = new Receipt(scs.getPrinter());
		// Tell Session about the rest of the system
        session.setup(itemManager, funds, weight, receipt, scs);

        // make sure the session is not active before you run the tests
        session.cancel();
	}
	
	
	/*
	 * Tests that calling addBag() before the session has started has no impact on the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session state 
	 * 
	 * Expected Behavior: the session remains in the pre-session state
	 */
	@Test
	public void test_addBags_beforeStartSession_stateUnchanged() {		
		// call addBags
		session.addBags();
		
		// a bag is not physically added because that will cause a discrepancy
				
		// the session has not started
		assertTrue(session.getState() == SessionState.PRE_SESSION); 
	}
	
	/*
	 * Tests that calling addBag() before the session has started has no impact on the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session state 
	 * 
	 * Expected Behavior: the expected weight on the scale doesnt change
	 */
	@Test
	public void test_addBags_beforeStartSession_expectedWeightUnchanged() {		
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
	 * Tests that calling addBags() during an active session updates the state of the Session to reflect this selection.
	 * 
	 * Expected behavior: Session.sessionState == ADDING_BAGS
	 */
	@Test
	public void test_addBags_updatesSessionState() {
		// start session:
		session.start();
		
		// call addBags
		session.addBags();
		
		// the system is in the adding bags state
		assertTrue(session.getState() == SessionState.ADDING_BAGS); 
	}


	/*
	 * Tests that calling addBag() during an active session and then adding the bag to the 
	 * bagging area does not result in any issues.
	 * 
	 * Expected Behavior: the session returns to normal runtime state (in session)
	 */
	@Test
	public void test_addBags_addingBagsUnblocksSession() {
		// start session:
		session.start();
		
		// call addBags
		session.addBags();
		
		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(bag);
		
		// the system is unblocked
		assertTrue(session.getState() == SessionState.IN_SESSION); 
	}

	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated
	 */
	@Test
	public void test_addBags_updatesExpectedWeight() {
		// start session:
		session.start();
				
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
	 * Tests that calling addBag() and then adding the bag to the bagging area does not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated to include the weight of the bag
	 */
	@Test
	public void test_addBags_updatesExpectedWeightByBagWeight() {
		// start session:
		session.start();
				
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();
				
		// call addBags
		session.addBags();
		
		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(bag);
						
		Mass expectedMassAfter = weight.getExpectedWeight();
		
		// compare the masses to see they have updated by the expected amount
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore.sum(bagMass)) == 0);
	}

	
	/*
	 * Tests that calling addBag() during an active session and then changing the bagging area by
	 * removing something from the bagging area isnt registered as adding a bag successfully  
	 * 
	 * Expected behavior: the expected weight of session is not updated
	 */
	@Test
	public void test_addBags_unexpectedChange_doesntUpdateExpectedWeight() {
		// start session:
		session.start();
	
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
	 * Tests that calling addBag() during an active session and then changing the bagging area by
	 * removing something from the bagging area isnt registered as adding a bag successfully  
	 * 
	 * Expected behavior: the session is blocked (this is a weight discrepancy)
	 */
	@Test
	public void test_addBags_unexpectedChange_blocksSession() {
		// start session:
		session.start();
		
		// pre-test: add an item to the bagging area 
		weight.update(notBagMass); // sets the expected mass on the scale to already know about the bag

		scs.getBaggingArea().addAnItem(notBag);
					
		// call addBags
		session.addBags();
				
		// remove the not-bag from the bagging area
		scs.getBaggingArea().removeAnItem(notBag);
						
		// check the state
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an item that is heavier than the set MAXBAGWEIGHT to the bagging area results in
	 * the session being blocked (Bags too Heavy use case)
	 * 
	 * Expected behavior: Session is blocked
	 */
	@Test
	public void test_addBags_overweightBag_blockSession() {
		// start session:
		session.start();

		// call addBags
		session.addBags();
		
		// add the heavy bag to the bagging area
		scs.getBaggingArea().addAnItem(overweightBag);
																
		// check the state		
		assertTrue(session.getState() == SessionState.BLOCKED);
	}
	/*
	 * Tests adding an item that is heavier than the set MAXBAGWEIGHT to the bagging area results in
	 * the expected weight not being updated (Bags too Heavy use case)
	 * 
	 * Expected behavior: expected weight is unchanged
	 */
	@Test
	public void test_addBags_overweightBag_doesntUpdateExpectedWeight() {
		// start session:
		session.start();
		
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
	 * Tests adding an item that is as heavy as the set MAXBAGWEIGHT to the bagging area results in
	 * the session being blocked (Bags too Heavy use case)
	 * 
	 * Expected behavior: Session is blocked
	 */
	@Test
	public void test_addBags_weightLimitBag_blockSession() {
		// start session:
		session.start();

		// call addBags
		session.addBags();
		
		// Add the bag to the bagging area
		scs.getBaggingArea().addAnItem(weightLimitBag);
																
		// check the state
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an item that is as heavy as the set MAXBAGWEIGHT to the bagging area results in
	 * the expected weight staying the same
	 * 
	 * Expected behavior: expected weight is unchanged
	 */
	@Test
	public void test_addBags_weightLimitBag_expectedWeightIsNotUpdated() {
		// start session:
		session.start();

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
	
	
	
	
	
	/**
	 * Tests that Session.configureMAXBAGWEIGHT updates the maxBagWeight in the expected way
	 * Tests using a double (representing the desired maximum bag weight in grams)
	 */
	@Test
	public void test_configureMAXBAGWEIGHT_byDouble() {
		double newMAXBAGWEIGHT = 6.78; // 6.79g
		// create a Mass using this
		Mass expectedMaxBagWeight = new Mass(newMAXBAGWEIGHT);
		
		// create a new Session with this as the max bag weight
		Session newSession = new Session(newMAXBAGWEIGHT);
		
		// create a session with a known bag limit
		session = new Session(BAG_MASS_LIMIT);
		
		// Tell Session about the rest of the system
        Funds funds = new Funds(scs);
        Weight weight = new Weight(scs.getBaggingArea());
        ItemManager itemManager = new ItemManager(session);
        Receipt receipt = new Receipt(scs.getPrinter());
        SelfCheckoutStationBronze scs = new SelfCheckoutStationBronze();
        session.setup(itemManager, funds, weight, receipt, scs);
		
		// create a Mass using the max weight weight value from session
		Mass actualMaxBagWeight = new Mass(newSession.getWeight().get_MAXBAGWEIGHT_inGrams());
		
		// compare the two Masses
		assertTrue(actualMaxBagWeight.compareTo(expectedMaxBagWeight) == 0 );		
	}
	
	/**
	 * Tests that Session.configureMAXBAGWEIGHT updates the maxBagWeight in the expected way
	 * Tests using a long (representing the desired maximum bag weight in micrograms)
	 */
	@Test
	public void test_configureMAXBAGWEIGHT_byLong() {
		long newMAXBAGWEIGHT = 678; // 679g
		// create a Mass using this
		Mass expectedMaxBagWeight = new Mass(newMAXBAGWEIGHT);
		
		// create a new Session with this as the max bag weight
		Session newSession = new Session(newMAXBAGWEIGHT);
		
		
		Funds funds = new Funds(scs);
        Weight weight = new Weight(scs.getBaggingArea());
        ItemManager itemManager = new ItemManager(session);
        Receipt receipt = new Receipt(scs.getPrinter());
        SelfCheckoutStationBronze scs = new SelfCheckoutStationBronze();
        session.setup(itemManager, funds, weight, receipt, scs);
		
		// create a Mass using the max weight weight value from session
		Mass actualMaxBagWeight = new Mass(newSession.getWeight().get_MAXBAGWEIGHT_inGrams());
		
		// compare the two Masses
		assertTrue(actualMaxBagWeight.compareTo(expectedMaxBagWeight) == 0 );		
	}
}
