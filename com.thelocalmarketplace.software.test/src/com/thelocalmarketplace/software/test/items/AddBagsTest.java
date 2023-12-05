package com.thelocalmarketplace.software.test.items;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.math.BigInteger;
import org.junit.Before;
import org.junit.Test;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.items.ReusableBagProduct;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import StubClasses.BagStub;
import StubClasses.ReusableBagStub;

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
	
	
	ReusableBagStub overweightPurchasedBag; 
	ReusableBag purchasedBag;
	ReusableBag purchasedBag2;
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
		ReusableBagProduct.setPrice(10L);
		purchasedBag = new ReusableBag();
		purchasedBag2 = new ReusableBag();
		overweightPurchasedBag = new ReusableBagStub();
		bag = new BagStub(bagMass);
		overweightBag = new BagStub(overweightBagMass);
		weightLimitBag = new BagStub(weightLimitBagMass);
		notBag = new BagStub(notBagMass);
		session.getWeight().configureMAXBAGWEIGHT(BAG_MASS_LIMIT);
		try {
			scs.getReusableBagDispenser().load(new ReusableBag(), new ReusableBag(), new ReusableBag());
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			System.out.println("Bag dispenser is max capacity");
		}
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
	public void addBags_beforeStartSession_stateUnchanged() {
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
	 * Expected Behavior: the expected weight on the scale doesnt change and the session remains in pre-session
	 * state
	 */
	@Test
	public void testAddBagsBeforeStartSession() {
		session.cancel();
		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(bag);
		
		// expected that the session has not started
		assertTrue(session.getState() == SessionState.PRE_SESSION);

		// check the expected weight has been updated (?)
		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}


	/*
	 * Tests that calling addBags() during an active session updates the state of
	 * the Session to ADDING_BAGS and add bag in bagging area updates the state of session to IN_SESSION
	 * 
	 * Expected Behavior: add bag updates the session ADDING_BAGS and adding bag to bagging area 
	 * updates the session to return to normal runtime state (in session)
	 * 
	 */
	@Test
	public void testAddBagSessionState() {

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
	public void addBags_addingBagsUnblocksSession() {
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
	public void testAddBagsUpdatesExpectedWeight() {
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
	 * Expected behavior: the expected weight of session is not updated and 
	 * the session is blocked (this is a weight discrepancy)
	 */
	@Test
	public void testAddBagsUnexpectedChange() {
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
		
		// check the state
		assertNotEquals(SessionState.IN_SESSION, session.getState());
		assertTrue("Weight Discrepancy", weight.isDiscrepancy());

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
	public void addBags_unexpectedChange_blocksSession() {
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
	public void addBags_overweightBag_blockSession() {
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
	public void addBags_overweightBag_doesntUpdateExpectedWeight() {
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
	public void addBags_weightLimitBag_blockSession() {
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
	public void addBags_weightLimitBag_expectedWeightIsNotUpdated() {
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
	 * Tests that calling purchaseBag() before the session has started has no impact on
	 * the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session
	 * state
	 * 
	 * Expected Behavior: the session remains in the pre-session state
	 */
	@Test
	public void purchaseBags_beforeStartSession_stateUnchanged() {

		
	}


	/*
	 * Tests adding an item that is heavier than or as heavy as the set MAXBAGWEIGHT to the bagging
	 * area results in
	 * the expected weight not being updated (Bags too Heavy use case) and the session blocked
	 * 
	 * Expected behavior: expected weight is unchanged and Session is blocked
	 */
	@Test
	public void testAddOverweightBag() {
		// start session:

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.addBags();	

		// add the heavy bag that is heavier than the set MAXBAGWEIGHT to the bagging area
		scs.getBaggingArea().addAnItem(overweightBag);	
		// add the heavy bag that is as heavy as the set MAXBAGWEIGHT to the bagging area
		scs.getBaggingArea().addAnItem(weightLimitBag);
		
		// check the state
		assertTrue(session.getState() == SessionState.BLOCKED);	
		// check the expected weight after the interaction
		Mass expectedMassAfter = weight.getExpectedWeight();
		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);

	}

	/*
	 * Tests that calling purchaseBag() before the session has started has no impact on
	 * the state of session
	 * ie: the expected weight doesnt change and the session remains in pre-session
	 * state
	 * 
	 * Expected Behavior: the expected weight on the scale doesnt change and the session remains in pre-session
	 * state
	 */
	@Test
	public void testPurchaseBagsBeforeStartSession() {
		// save the expected Mass before adding the bag
		session.cancel();
		Mass expectedMassBefore = weight.getExpectedWeight();
		session.purchaseBags(1);
		
		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(purchasedBag);
		
		//check that the session has not started
		assertTrue(session.getState() == SessionState.PRE_SESSION);

		// check the expected weight has been updated (?)

		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) == 0);
	}

	/*
	 * Tests that calling purchaseBag() before the session has started has no impact on
	 * the number of bags dispensed
	 * ie: the number of bags the dispenser has doesnt change
	 * 
	 * Expected Behavior: the number of bags in the dispenser has doesnt change
	 * UnsupportedOperationException will be thrown for the Bronze self checkout station in getReusableBagDispenser()
	 */
	
	@Test
	public void testNoBagsDispensedBeforeStartSession() {
		// save the expected Mass before adding the bag
		session.cancel();
		try {
			int expectedTotalBags = scs.getReusableBagDispenser().getQuantityRemaining();
			session.purchaseBags(1);
			
			// add the bags to the bagging area
			scs.getBaggingArea().addAnItem(purchasedBag);


			int actualTotalBags = scs.getReusableBagDispenser().getQuantityRemaining();

			// compare the masses to see they have updated
			assertEquals(expectedTotalBags, actualTotalBags);
		}
		catch (UnsupportedOperationException e) {
			 e.printStackTrace();//this exception will be thrown for the Bronze self checkout station in getReusableBagDispenser()
		}
	}
	

	/*
	 * Tests that calling purchaseBag() during an active session and then adding the bag
	 * to the bagging area does not result in any issues.
	 * 
	 * Expected Behavior: the session returns to normal runtime state (in session)
	 * 
	 */
	@Test
	public void testAddPurchasedBagsUnblocksSession() {
		// start session:

		// call addBags
		session.purchaseBags(1);

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(purchasedBag);
		
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
	public void testAddPurchasedBagUpdatesWeight() {
		// start session:

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.purchaseBags(1);

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(purchasedBag);
		

		// check the expected weight has been updated (?)

		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) != 0);
	}
	
	
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does
	 * not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated
	 */
	@Test
	public void testAddPurchasedTwoBagsUpdatesWeight() {
		
=======
	public void purchaseMultipleBags_updatesExpectedWeight() {

>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
		Mass massBefore = weight.getExpectedWeight();
		double weightBefore = massBefore.inGrams().doubleValue();

		session.purchaseBags(2);

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(purchasedBag);

		// check the expected weight 
		Mass massAfter = weight.getExpectedWeight();
		double weightAfter = massAfter.inGrams().doubleValue();
		
		//get weight of bags 
		Mass mass = purchasedBag.getMass();
		double weight = mass.inGrams().doubleValue() * 2;
		
		//calculate expected weight 
		weight += weightBefore;
		
		// compare the masses to see they have updated
		assertTrue(weight == weightAfter);	
		
	}
	
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does
	 * not result in
	 * any issues.
	 * 
	 * Expected behavior: the total number of bags in dispenser is updated
	 * UnsupportedOperationException will be thrown for the Bronze self checkout station in getReusableBagDispenser()
	 */
	@Test
<<<<<<< HEAD
	public void testAddPurchasedBagsUpdatesDispenser() {
		// start session:

		// save the number of bags in dispenser before adding the bag
		try {
			int previousBagsTotal = scs.getReusableBagDispenser().getQuantityRemaining();

			// call addBags
			session.purchaseBags(1);

			// add the bags to the bagging area
			scs.getBaggingArea().addAnItem(purchasedBag);

			// check the number of bags in dispenser
			int currentBagsTotal = scs.getReusableBagDispenser().getQuantityRemaining();

			// compare the number of bags to see if they have updated
			assertTrue(currentBagsTotal == previousBagsTotal   - 1);
		} catch (UnsupportedOperationException e) {
			 e.printStackTrace();//this exception will throw for the Bronze self checkout station for getReusableBagDispenser()
		}
	}
	
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does
	 * not result in
	 * any issues.
	 * 
	 * Expected behavior: the total number of bags in dispenser is updated
	 * UnsupportedOperationException will be thrown for the Bronze self checkout station in getReusableBagDispenser()
	 */
	@Test
	public void testAddPurchasedTwoBagsUpdatesDispenser() {
=======
	public void purchaseBags_updatesBagsLeft() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
		// start session:

		// save the expected Mass before adding the bag
		try {
			int previousBagsTotal = scs.getReusableBagDispenser().getQuantityRemaining();

			// call addBags
			session.purchaseBags(2);

			// add the bags to the bagging area
			scs.getBaggingArea().addAnItem(purchasedBag);
			scs.getBaggingArea().addAnItem(purchasedBag2);

			// check the expected weight has been updated (?)

			int currentBagsTotal = scs.getReusableBagDispenser().getQuantityRemaining();

			// compare the masses to see they have updated
			assertTrue(currentBagsTotal == previousBagsTotal - 2);
			
			//make sure that the number of bags in item manager is updated
			BigInteger totalBags = itemManager.getItems().get(ReusableBagProduct.getBag());
			assertTrue(totalBags.intValue() == 2 );
		} catch (UnsupportedOperationException e) {
			 e.printStackTrace();//this exception will throw for the Bronze self checkout station for getReusableBagDispenser()
		}
		
	}
	
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does
	 * not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated
	 */
	@Test
<<<<<<< HEAD
	public void testPurchaseMultipleBagsOrderUpdatedCorrectly() {
=======
	public void purchaseMultipleBags_updatesBagsLeft() {
		// start session:

		// save the expected Mass before adding the bag
		int previousBagsTotal = scs.getReusableBagDispenser().getQuantityRemaining();

		// call addBags
		session.purchaseBags(2);

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(purchasedBag);
		scs.getBaggingArea().addAnItem(purchasedBag2);

		// check the expected weight has been updated (?)

		int currentBagsTotal = scs.getReusableBagDispenser().getQuantityRemaining();

		// compare the masses to see they have updated
		assertTrue(currentBagsTotal == previousBagsTotal - 2);
	}
	
	/*
	 * Tests that calling addBag() and then adding the bag to the bagging area does
	 * not result in
	 * any issues.
	 * 
	 * Expected behavior: the expected weight of session is updated
	 */
	@Test
	public void purchaseMultipleBags_orderUpdatedCorrectly() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
	
		session.purchaseBags(2);

		// add the bags to the bagging area
		scs.getBaggingArea().addAnItem(purchasedBag);
		scs.getBaggingArea().addAnItem(purchasedBag2);
		BigInteger totalBags = itemManager.getItems().get(ReusableBagProduct.getBag());
		// compare the masses to see they have updated
		assertTrue(totalBags.intValue() == 2 );
	}



	/*
	 *  Tests adding an item that is heavier than the set MAXBAGWEIGHT to the bagging
	 * area results in
	 * the session being blocked ()
	 * 
	 * Expected behavior:the session is blocked and expected weight is unchanged
	 */
	@Test
<<<<<<< HEAD
	public void testPurchaseOverweightBags() {
=======
	public void purchaseBags_overweightBag_blockSession() {
		// start session:

		// call addBags
		session.purchaseBags(1);

		// add the heavy bag to the bagging area
		scs.getBaggingArea().addAnItem(overweightPurchasedBag);

		// check the state
		assertTrue(session.getState() == SessionState.BLOCKED);
	}

	/*
	 * Tests adding an item that is heavier that the expected weight is updated 
	 * 
	 * Expected behavior: expected weight is unchanged
	 */
	@Test
	public void purchaseBags_overweightBag_UpdatesExpectedWeight() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
		// start session:

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.purchaseBags(1);

		// add the heavy bag to the bagging area
		scs.getBaggingArea().addAnItem(overweightPurchasedBag);
		
		// check the state
		assertTrue(session.getState() == SessionState.BLOCKED);

		// check the expected weight after the interaction
		Mass expectedMassAfter = weight.getExpectedWeight();

		// compare the masses to see they have not updated
		assertTrue(expectedMassAfter.compareTo(expectedMassBefore) != 0);
		

	}


	/*
	 * Tests calling removeItem() should remove bag
	 * and return the system to the normal runtime state (IN_SESSION)
	 * weight does not change
	 * Expected behavior: system to the normal runtime state (IN_SESSION) and Expected Weight of the session is not updated
	 */
	@Test
<<<<<<< HEAD
	public void testRemovePurchasedBags() {
=======
	public void removePurchasedBags_updatesState() {
		// start session:

		// call addBags
		session.purchaseBags(1);

		// call cancelAddBags()
		scs.getBaggingArea().addAnItem(purchasedBag);
	

		itemManager.removeItem(ReusableBagProduct.getBag());
		// check state to make sure the system has updates
		scs.getBaggingArea().removeAnItem(purchasedBag);
		assertTrue(session.getState() == SessionState.IN_SESSION);
	}

	/*
	 * Tests calling removeItem() any changes 
	 * weight changes
	 * Expected behavior: Expected Weight of the session is not updated
	 */
	@Test
	public void removePurchasedBags_UpdateExpectedWeight() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
		// start session:

		// save the expected Mass before adding the bag
		Mass expectedMassBefore = weight.getExpectedWeight();

		// call addBags
		session.purchaseBags(1);


		scs.getBaggingArea().addAnItem(purchasedBag);

		// check the expected weight after the interaction
		itemManager.removeItem( ReusableBagProduct.getBag());
		
		scs.getBaggingArea().removeAnItem(purchasedBag);
		
		//checks if system has returned to the normal runtime state (IN_SESSION) 
		assertTrue(session.getState() == SessionState.IN_SESSION);
			

		// compare the masses to see they have not updated
		Mass expectedMassAfter = weight.getExpectedWeight();
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
<<<<<<< HEAD
	public void testCancelAddBagsUpdatesState() {
=======
	public void cancelAddBags_updatesState() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
		// call addBags
		session.addBags();

		// call cancelAddBags()
		session.cancelAddBags();

		// check state to make sure the system has updates
		assertFalse("Not Discrepancy", weight.isDiscrepancy());
		assertEquals(SessionState.IN_SESSION, session.getState());
	}

	/*
	 * Tests calling cancelAddBags(). When in the addBags state, this method should cancel the interaction meaning
	 * any changes in the bagging area result in a weight discrepancy.
	 *
	 * Expected behavior:Expected Weight of the session is not updated
	 */
	@Test
<<<<<<< HEAD
	public void testCancelAddBagsNoWeightUpdates() {
=======
	public void cancelAddBags_doesntUpdateExpectedWeight() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
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
<<<<<<< HEAD
	public void testCancelAddBagsBeforeSessionStart() {
=======
	public void cancelAddBags_doesntUpdatesState_PRE_SESSION() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
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
<<<<<<< HEAD
	public void testCancelAddBagsBlocksSystem() {
=======
	public void cancelAddBags_blocksSystem() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
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
<<<<<<< HEAD
	public void testConfigureMaxWeightByDouble() {
=======
	public void configureMAXBAGWEIGHT_byDouble() {
>>>>>>> 271c5bec41681d7b18420befe6d32c1eb079851b
		double newMAXBAGWEIGHT = 6.78; // 6.79g
		// create a Mass using this
		Mass expectedMaxBagWeight = new Mass(newMAXBAGWEIGHT);

		// create a new Session with this as the max bag weight
		
		session.getWeight().configureMAXBAGWEIGHT(newMAXBAGWEIGHT);

		// create a Mass using the max weight weight value from session
		Mass actualMaxBagWeight = new Mass(session.getWeight().get_MAXBAGWEIGHT_inGrams());

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
	public void testConfigureMaxWeightByLong() {
		long newMAXBAGWEIGHT = 678; // 679g
		// create a Mass using this
		Mass expectedMaxBagWeight = new Mass(newMAXBAGWEIGHT);
		
		session.getWeight().configureMAXBAGWEIGHT(newMAXBAGWEIGHT);

		// create a Mass using the max weight weight value from session
		Mass actualMaxBagWeight = new Mass(session.getWeight().get_MAXBAGWEIGHT_inMicrograms());

		// compare the two Masses
		assertTrue(actualMaxBagWeight.compareTo(expectedMaxBagWeight) == 0);
	}


}
