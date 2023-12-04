package StubClasses;

import java.math.BigInteger;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.bag.ReusableBag;

public class ReusableBagStub extends ReusableBag {
	public Mass getMass() {
		return new Mass(BigInteger.valueOf(5_000_000 * 3)); 
	}

}
