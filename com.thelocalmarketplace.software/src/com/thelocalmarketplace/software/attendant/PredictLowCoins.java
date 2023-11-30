package com.thelocalmarketplace.software.attendant;

import com.tdc.coin.CoinStorageUnit;
import com.thelocalmarketplace.software.*;

public class PredictLowCoins extends AbstractPredictIssue {
	private CoinStorageUnit coinStorageUnit;
	private Session s;
	
	public void checkLowCoins() {
		if (s.getState() == SessionState.PRE_SESSION) {
			int currentCoins = coinStorageUnit.getCoinCount();
			int maxCoins = coinStorageUnit.getCapacity();
			
			if (currentCoins <= maxCoins * 0.1) { // if current coins is less than 10% of its maxCoins
				signalAttendant("Low on Coins");
			}
		}
	}

}
