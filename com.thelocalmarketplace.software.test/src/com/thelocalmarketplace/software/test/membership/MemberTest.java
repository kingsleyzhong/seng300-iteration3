package com.thelocalmarketplace.software.test.membership;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.membership.Member;
import com.thelocalmarketplace.software.membership.MembershipDatabase;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/* Tests related to the Member class and the MembershipDatabase
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

public class MemberTest extends AbstractSessionTest {
    private static final String memberName = "John Doe";
    private static final String membershipNumber = "1234";

    public MemberTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }

    /** Clear the Membership database and re-register the test member */
    @Before
    public void setup() {
        MembershipDatabase.MEMBERSHIP_DATABASE.clear();
        MembershipDatabase.registerMember(membershipNumber, memberName);
    }

    /** A newly registered member should be present in the database. */
    @Test
    public void registerMember() {
        String membershipNumber2 = "123";
        String memberName2 = "Jane Smith";
        MembershipDatabase.registerMember(membershipNumber2, memberName2);
        Assert.assertTrue(MembershipDatabase.MEMBERSHIP_DATABASE.containsKey(membershipNumber2));
    }

    /**
     * A member object in the database should return the correct corresponding name.
     */
    @Test
    public void memberName() {
        Assert.assertEquals(memberName, MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber).getName());
    }

    /** Points should go up with a positive delta. */
    @Test
    public void addPoints() {
        Member member = MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber);
        member.changePoints(10);
        Assert.assertEquals(10, member.getPoints());
    }

    /** Points should go down with a negative delta. */
    @Test
    public void subtractPoints() {
        Member member = MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber);
        member.changePoints(10);
        member.changePoints(-5);
        Assert.assertEquals(5, member.getPoints());
    }

    /** Negative points should not be possible. */
    @Test
    public void negativePoints() {
        Member member = MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber);
        member.changePoints(-5);
        Assert.assertEquals(0, member.getPoints());
    }

}
