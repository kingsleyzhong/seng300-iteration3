package com.thelocalmarketplace.software.test.membership;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.MagneticStripeFailureException;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.membership.MembershipDatabase;
import com.thelocalmarketplace.software.membership.MembershipListener;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/* Tests related to the Membership facade, its ability to take membership numbers and announce events, and
 * its ability to register/deregister MembershipListeners.
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

public class MembershipTest extends AbstractSessionTest {

    private StubListener stubListener;
    private static final String membershipNumber = "1234";
    private static final String memberName = "John Doe";
    private static final Card membershipCard = new Card("membership", membershipNumber, memberName, "", "", false,
            false);

    static {
        MembershipDatabase.registerMember(membershipNumber, memberName); // add test member to database
    }

    public MembershipTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }

    @Before
    public void setup() {
        basicDefaultSetup();

        // reregister the membership number
        membership.deregisterAll();
        membership.register(stubListener = new StubListener());
    }

    /** Creating a new membership facade with a null card reader should throw a NullPointerSimulationException. */
    @Test
    public void testNullCardReader() {
        Assert.assertThrows(NullPointerSimulationException.class, () -> new Membership(null));
    }

    /** Registering a null listener should throw a NullPointerSimulationException. */
    @Test
    public void testNullAddListener() {
        Assert.assertThrows(NullPointerSimulationException.class, () -> membership.register(null));
    }

    /** Removing a null listener should throw a NullPointerSimulationException. */
    @Test
    public void testNullRemoveListener() {
        Assert.assertThrows(NullPointerSimulationException.class, () -> membership.deregister(null));
    }

    /** A listener that is registered should be able to receive events. */
    @Test
    public void testRegisterListener() {
        StubListener listener2 = new StubListener();
        membership.register(listener2);
        membership.setAddingItems(true);
        membership.typeMembership(membershipNumber);
        Assert.assertEquals(listener2.enteredMembershipNumber, membershipNumber);
    }

    /** Deregistering a listener should make it so that it no longer receives events. */
    @Test
    public void testDeregisterListener() {
        membership.setAddingItems(true);
        membership.deregister(stubListener);
        membership.typeMembership(membershipNumber);
        Assert.assertNull(stubListener.enteredMembershipNumber);
    }

    /** Deregistering all listeners should make none of them receive events. */
    @Test
    public void testDeregisterAllListeners() {
        membership.setAddingItems(true);
        StubListener stubListener2 = new StubListener();
        membership.register(stubListener2);
        membership.deregisterAll();
        membership.typeMembership(membershipNumber);
        Assert.assertNull(stubListener.enteredMembershipNumber);
        Assert.assertNull(stubListener2.enteredMembershipNumber);
    }

    /** Calling typeMembership during any phase other than adding items should throw an InvalidActionException. */
    @Test
    public void testTypeMembershipNotAddingItems() {
        Assert.assertThrows(InvalidActionException.class, () -> membership.typeMembership(membershipNumber));
    }

    /** Typing in a membership number that is not in the database should result in no membership number being stored. */
    @Test
    public void testTypeNotAMember() {
        membership.setAddingItems(true);
        Assert.assertThrows(InvalidActionException.class, () -> membership.typeMembership("1"));
        Assert.assertNull(stubListener.enteredMembershipNumber);
    }

    /** Typing in a valid membership number should store it. */
    @Test
    public void testTypeValidMembership() {
        membership.setAddingItems(true);
        membership.typeMembership(membershipNumber);
        Assert.assertEquals(stubListener.enteredMembershipNumber, membershipNumber);
    }

    /** Swiping a membership card while not in the add items phase should result in no membership number being stored.*/
    @Test
    public void testSwipeMembershipNotAddingItems() throws IOException {
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.enteredMembershipNumber = null;
            try {
                scs.getCardReader().swipe(membershipCard);
                if (stubListener.enteredMembershipNumber == null)
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    /** Swiping a valid membership card should result in the membership number being stored. */
    @Test
    public void testSwipeMembership() throws IOException {
        membership.setAddingItems(true);
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.enteredMembershipNumber = null;
            try {
                scs.getCardReader().swipe(membershipCard);
                if (stubListener.enteredMembershipNumber != null && stubListener.enteredMembershipNumber.equals(membershipNumber))
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    /** Swiping a card that is not a membership card should result in no number being stored. */
    @Test
    public void testSwipeOtherCard() throws IOException {
        Card otherCard = new Card("VISA", "4123456789012345", "John Doe", "111", "1111", true, true);
        membership.setAddingItems(true);
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.enteredMembershipNumber = null;
            try {
                scs.getCardReader().swipe(otherCard);
                if (stubListener.enteredMembershipNumber == null)
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    /** Swiping a membership card with a number not in the database should result in no membership number being set. */
    @Test
    public void testSwipeNotAMember() throws IOException {
        Card otherCard = new Card("member", "1", "Jane Smith", "", "", false, false);
        membership.setAddingItems(true);
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.enteredMembershipNumber = null;
            try {
                scs.getCardReader().swipe(otherCard);
                if (stubListener.enteredMembershipNumber == null)
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    /** A stub MembershipListener, that receives membership number events and stores that number. */
    static class StubListener implements MembershipListener {
        String enteredMembershipNumber; // string that can be used

        /** Sets the enteredMembershipNumber to the entered membershipNumber to indicate a successful event call.
         * @param membershipNumber The entered membership number. */
        @Override
        public void membershipEntered(String membershipNumber) {
            enteredMembershipNumber = membershipNumber;
        }
    }
}
