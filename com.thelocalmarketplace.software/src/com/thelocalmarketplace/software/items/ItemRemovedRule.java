package com.thelocalmarketplace.software.items;

import java.util.Map;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.items.ItemAddedRule.innerListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

public class ItemRemovedRule {
	private ItemManager itemManager;

	/**
	 * Basic constructor for ItemRemovedRule. Registers a listener to scanners being
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
	public ItemRemovedRule( ItemManager itemManager) {
		//this.GUI.register(this);
		this.itemManager = itemManager;
	}

	/**
	 * An innerListener class that will listen to events from the GUI.
	 * If an item has been removed by a customer, remove item from the session
	 */
	public class innerListener  {
		public void deleteItem(BarcodedProduct product) {
			if ( itemManager.getBulkyItems().containsKey(product) || itemManager.getItems().containsKey(product))
				itemManager.removeItem(product);
		}

		

	}

}
