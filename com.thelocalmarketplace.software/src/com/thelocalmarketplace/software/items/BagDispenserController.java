package com.thelocalmarketplace.software.items;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.bag.ReusableBag;
import com.jjjwelectronics.bag.ReusableBagDispenserListener;

/**
 * Handles hardware interactions with the ReusableBagDispenser.
 * 
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev : 30177060
 * Enioluwafe Balogun : 30174298
 * Subeg Chahal : 30196531
 * Jun Heo : 30173430
 * Emily Kiddle : 30122331
 * Anthony Kostal-Vazquez : 30048301
 * Jessica Li : 30180801
 * Sua Lim : 30177039
 * Savitur Maharaj : 30152888
 * Nick McCamis : 30192610
 * Ethan McCorquodale : 30125353
 * Katelan Ng : 30144672
 * Arcleah Pascual : 30056034
 * Dvij Raval : 30024340
 * Chloe Robitaille : 30022887
 * Danissa Sandykbayeva : 30200531
 * Emily Stein : 30149842
 * Thi My Tuyen Tran : 30193980
 * Aoi Ueki : 30179305
 * Ethan Woo : 30172855
 * Kingsley Zhong : 30197260
 */
public class BagDispenserController {

	private boolean outOfBags = false;
	private IReusableBagDispenser bagDispenser; // should this instead be IReusableBagDispenser?
	private ItemManager manager;
	
	public BagDispenserController(IReusableBagDispenser dispenser, ItemManager manager) {
		this.bagDispenser = dispenser; 
		this.manager = manager;
		
		// register InnerListener with the hardware
		bagDispenser.register(new InnerListener());
	}

	private class InnerListener implements ReusableBagDispenserListener {

		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {

		}

		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {

		}

		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {

		}

		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {

		}

		@Override
		public void aBagHasBeenDispensedByTheDispenser() {

		}

		@Override
		public void theDispenserIsOutOfBags() {
			outOfBags = true;
		}

		@Override
		public void bagsHaveBeenLoadedIntoTheDispenser(int count) {
			outOfBags = false;
		}
	}

	public void dispenseBag(int num) {
		// signals the hardware to dispense a single bag
		if (!outOfBags) {
			for (int i = 0; i < num; i++) {
				try {
					if ( manager.getAddItems()) {
						ReusableBag bag = bagDispenser.dispense();
					}
				} catch (EmptyDevice e) {
					break;		
				}	
			}
		}
		// else: bag goes to the bagging area, update weight and stuff
	}
}