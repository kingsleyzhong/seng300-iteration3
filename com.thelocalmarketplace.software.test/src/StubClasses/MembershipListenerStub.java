package StubClasses;

import com.thelocalmarketplace.software.membership.MembershipListener;

public class MembershipListenerStub implements MembershipListener{
	private String membershipNumber;
	
	@Override
	public void membershipEntered(String membershipNumber) {
		this.membershipNumber = membershipNumber;
		
	}
	
	public String getMembership() {
		return membershipNumber;
		
	}
	
}
