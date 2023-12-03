package com.thelocalmarketplace.software.items;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;

public class PLUItemAddedRule {
		private ItemManager itemManager;
		
		public PLUItemAddedRule(IElectronicScale scannerScale, ItemManager i) {
			itemManager = i;
			scannerScale.register(new innerListener());
		}
		
		public class innerListener implements ElectronicScaleListener {

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				// TODO Auto-generated method stub
				
				//Only trigger when in ADD_PLU_ITEM state to avoid customer accidentally put something on the scanner scale
				if(itemManager.isAddPLUItemState()) {
					PLUCodedProduct product = itemManager.getPluProduct();
					itemManager.addItem(product, mass);
					
				}
			}
			
			@Override
			public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void theMassOnTheScaleHasExceededItsLimit(IElectronicScale scale) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void theMassOnTheScaleNoLongerExceedsItsLimit(IElectronicScale scale) {
				// TODO Auto-generated method stub
				
			}

			
		}
}
