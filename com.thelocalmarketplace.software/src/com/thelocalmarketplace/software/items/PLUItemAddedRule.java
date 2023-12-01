package com.thelocalmarketplace.software.items;

import java.util.Map;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleListener;
import com.jjjwelectronics.scale.IElectronicScale;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

public class PLUItemAddedRule {
		private ItemManager itemManager;
		private Session session;
		
		public PLUItemAddedRule(IElectronicScale scannerScale, ItemManager i, Session session) {
			itemManager = i;
			this.session = session;
			scannerScale.register(new innerListener());
		}
		
		public class innerListener implements ElectronicScaleListener {

			@Override
			public void theMassOnTheScaleHasChanged(IElectronicScale scale, Mass mass) {
				// TODO Auto-generated method stub
				
				//Only trigger when in ADD_PLU_ITEM state to avoid customer accidentally put something on the scanner scale
				if(session.getState() == SessionState.ADD_PLU_ITEM) {
					PriceLookUpCode code = session.getLastPLUcode();
					Map<PriceLookUpCode, PLUCodedProduct> database = ProductDatabases.PLU_PRODUCT_DATABASE;
					// Checks if product is in database. Throws exception if not in database.
					if (database.containsKey(code)) {
						PLUCodedProduct product = database.get(code);
						itemManager.addItem(product, mass);
					} else {
						throw new InvalidArgumentSimulationException("Not in database");
					}
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