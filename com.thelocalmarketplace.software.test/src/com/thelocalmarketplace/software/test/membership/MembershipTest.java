package com.thelocalmarketplace.software.test.membership;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.MagneticStripeFailureException;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.membership.MembershipDatabase;
import com.thelocalmarketplace.software.membership.MembershipListener;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

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

public class MembershipTest extends AbstractSessionTest {

    private Membership membership;
    private StubListener stubListener;
    private static final String membershipNumber = "1234";
    private static final String memberName = "John Doe";
    private static final Card membershipCard = new Card("membership", membershipNumber, memberName, "", "", false,
            false);

    static {
        MembershipDatabase.registerMember(membershipNumber, memberName);
    }

    public MembershipTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
        // TODO Auto-generated constructor stub
    }

    @Before
    public void setup() {
        basicDefaultSetup();
        membership = new Membership(scs.getCardReader());
        membership.register(stubListener = new StubListener());
    }

    @Test
    public void testNullCardReader() {
        Assert.assertThrows(NullPointerSimulationException.class, () -> new Membership(null));
    }

    @Test
    public void testNullAddListener() {
        Assert.assertThrows(NullPointerSimulationException.class, () -> membership.register(null));
    }

    @Test
    public void testNullRemoveListener() {
        Assert.assertThrows(NullPointerSimulationException.class, () -> membership.deregister(null));
    }

    @Test
    public void testRegisterListener() {
        StubListener listener2 = new StubListener();
        membership.register(listener2);
        membership.setAddingItems(true);
        membership.typeMembership(membershipNumber);
        Assert.assertEquals(listener2.membershipNumber, membershipNumber);
    }

    @Test
    public void testDeregisterListener() {
        membership.deregister(stubListener);
        membership.typeMembership(membershipNumber);
        Assert.assertNull(stubListener.membershipNumber);
    }

    @Test
    public void testDeregisterAllListeners() {
        StubListener stubListener2 = new StubListener();
        membership.register(stubListener2);
        membership.deregisterAll();
        membership.typeMembership(membershipNumber);
        Assert.assertNull(stubListener.membershipNumber);
        Assert.assertNull(stubListener2.membershipNumber);
    }

    @Test
    public void testTypeMembershipNotAddingItems() {
        membership.typeMembership(membershipNumber);
        Assert.assertNull(stubListener.membershipNumber);
    }

    @Test
    public void testTypeNotAMember() {
        membership.setAddingItems(true);
        membership.typeMembership("1");
        Assert.assertNull(stubListener.membershipNumber);
    }

    @Test
    public void testTypeValidMembership() {
        membership.setAddingItems(true);
        membership.typeMembership(membershipNumber);
        Assert.assertEquals(stubListener.membershipNumber, membershipNumber);
    }

    @Test
    public void testSwipeMembershipNotAddingItems() throws IOException {
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.membershipNumber = null;
            try {
                scs.getCardReader().swipe(membershipCard);
                if (stubListener.membershipNumber == null)
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    @Test
    public void testSwipeMembership() throws IOException {
        membership.setAddingItems(true);
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.membershipNumber = null;
            try {
                scs.getCardReader().swipe(membershipCard);
                if (stubListener.membershipNumber != null && stubListener.membershipNumber.equals(membershipNumber))
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    @Test
    public void testSwipeOtherCard() throws IOException {
        Card otherCard = new Card("VISA", "4123456789012345", "John Doe", "111", "1111", true, true);
        membership.setAddingItems(true);
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.membershipNumber = null;
            try {
                scs.getCardReader().swipe(otherCard);
                if (stubListener.membershipNumber == null)
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    @Test
    public void testSwipeNotAMember() throws IOException {
        Card otherCard = new Card("member", "1", "Jane Smith", "", "", false, false);
        membership.setAddingItems(true);
        int success = 0;
        for (int i = 0; i < 1000; i++) { // loop to account for swipe failures in hardware
            stubListener.membershipNumber = null;
            try {
                scs.getCardReader().swipe(otherCard);
                if (stubListener.membershipNumber == null)
                    success++;
            } catch (MagneticStripeFailureException ignored) {
            }
        }
        Assert.assertTrue(success > 450);
    }

    static class StubListener implements MembershipListener {
        String membershipNumber;

        @Override
        public void membershipEntered(String membershipNumber) {
            this.membershipNumber = membershipNumber;
        }
    }
}
