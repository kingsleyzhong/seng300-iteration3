package com.thelocalmarketplace.software.test.membership;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.membership.Member;
import com.thelocalmarketplace.software.membership.MembershipDatabase;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MemberTest extends AbstractSessionTest {
    private static final String memberName = "John Doe";
    private static final String membershipNumber = "1234";

    public MemberTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }

    @Before
    public void setup() {
        MembershipDatabase.MEMBERSHIP_DATABASE.clear();
        MembershipDatabase.registerMember(membershipNumber, memberName);
    }

    @Test
    public void testRegisterMember() {
        String membershipNumber2 = "123";
        String memberName2 = "Jane Smith";
        MembershipDatabase.registerMember(membershipNumber2, memberName2);
        Assert.assertTrue(MembershipDatabase.MEMBERSHIP_DATABASE.containsKey(membershipNumber2));
    }

    @Test
    public void testMemberName() {
        Assert.assertEquals(memberName, MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber).getName());
    }

    @Test
    public void testAddPoints() {
        Member member = MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber);
        member.changePoints(10);
        Assert.assertEquals(10, member.getPoints());
    }

    @Test
    public void testSubtractPoints() {
        Member member = MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber);
        member.changePoints(10);
        member.changePoints(-5);
        Assert.assertEquals(5, member.getPoints());
    }

    @Test
    public void testNegativePoints() {
        Member member = MembershipDatabase.MEMBERSHIP_DATABASE.get(membershipNumber);
        member.changePoints(-5);
        Assert.assertEquals(0, member.getPoints());
    }

}
