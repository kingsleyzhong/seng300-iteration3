package StubClasses;

import com.thelocalmarketplace.software.weight.WeightListener;

public class WeightListenerStub implements WeightListener {
	private boolean discrepancy = false;
	private boolean bagsTooHeavy = false;
	
	@Override
	public void notifyDiscrepancy() {
		discrepancy = true;
		
	}

	@Override
	public void notifyDiscrepancyFixed() {
		discrepancy = false;
		
	}

	@Override
	public void notifyBagsTooHeavy() {
		bagsTooHeavy = true;
		
	}

	public boolean getDiscrepancy() {
		return discrepancy;
	}
	
	public boolean getBagsTooHeavy() {
		return bagsTooHeavy;
	}
	
}
