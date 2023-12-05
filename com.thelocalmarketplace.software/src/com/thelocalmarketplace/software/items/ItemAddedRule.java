package com.thelocalmarketplace.software.items;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import java.util.Map;

/**
 * Rule for adding items to the session.
 * Takes the scanned item from the barcode scanner and adds it to the session
 * if the session is on and not frozen.
 * 
 * In the case that a session is frozen or not on, an InvalidActionException
 * will be called.
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
public class ItemAddedRule {
	private ItemManager itemManager;

	/**
	 * An innerListener class that listens to BarcodeScannerListener.
	 * If a barcode has been scanned add item to the session
	 */
	public class innerListener implements BarcodeScannerListener {
		@Override
		public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
			Map<Barcode, BarcodedProduct> database = ProductDatabases.BARCODED_PRODUCT_DATABASE;

			// Checks if product is in database. Throws exception if not in database.
			if (database.containsKey(barcode)) {
				BarcodedProduct product = database.get(barcode);
				itemManager.addItem(product);
			} else {
				throw new InvalidArgumentSimulationException("Not in database");
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
	}

	/**
	 * Basic constructor for ItemAddedRule. Registers a listener to scanners being
	 * used in the
	 * self-checkout station.
	 * 
	 * @param mainScanner
	 *                The main scanner
	 * @param handheldScanner
	 *                The handheld scanner
	 * @param itemManager
	 * 				The item manager is where items are managed
	 */
	public ItemAddedRule(IBarcodeScanner mainScanner, IBarcodeScanner handheldScanner, ItemManager itemManager) {
		if (mainScanner == null || handheldScanner == null || itemManager == null) {
			throw new InvalidArgumentSimulationException("Self Checkout Station cannot be null.");
		}
		mainScanner.register(new innerListener());
		handheldScanner.register(new innerListener());
		this.itemManager = itemManager;
	}
}
