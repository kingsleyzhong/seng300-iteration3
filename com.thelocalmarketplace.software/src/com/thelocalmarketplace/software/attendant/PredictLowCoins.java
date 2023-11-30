package com.thelocalmarketplace.software.attendant;

import com.thelocalmarketplace.software.*;

public class PredictLowCoins extends AbstractPredictIssue {
	private Session s;
	
	public void checkLowCoins() {
		if (s.getState() == SessionState.PRE_SESSION) {
			int currentCoins = s.getStation().getCoinStorage().getCoinCount();
			int maxCoins = s.getStation().getCoinStorage().getCapacity();
			
			if (currentCoins <= maxCoins * 0.1) { // if current coins is less than 10% of its maxCoins
				signalAttendant("Low on Coins");
			}
		}
	}

}
