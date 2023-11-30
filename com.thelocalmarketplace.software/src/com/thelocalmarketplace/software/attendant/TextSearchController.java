package com.thelocalmarketplace.software.attendant;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.keyboard.KeyboardListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.screen.TouchScreenListener;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Simulation of an attendant searching for items using a physical keyboard via keyword search.</p>
 * <p></p>
 * <p>Project Iteration 3 Group 1:</p>
 * <p></p>
 * <p> Derek Atabayev 				: 30177060 </p>
 * <p> Enioluwafe Balogun 			: 30174298 </p>
 * <p> Subeg Chahal 				: 30196531 </p>
 * <p> Jun Heo 						: 30173430 </p>
 * <p> Emily Kiddle 				: 30122331 </p>
 * <p> Anthony Kostal-Vazquez 		: 30048301 </p>
 * <p> Jessica Li 					: 30180801 </p>
 * <p> Sua Lim 						: 30177039 </p>
 * <p> Savitur Maharaj 				: 30152888 </p>
 * <p> Nick McCamis 				: 30192610 </p>
 * <p> Ethan McCorquodale 			: 30125353 </p>
 * <p> Katelan Ng 					: 30144672 </p>
 * <p> Arcleah Pascual 				: 30056034 </p>
 * <p> Dvij Raval 					: 30024340 </p>
 * <p> Chloe Robitaille 			: 30022887 </p>
 * <p> Danissa Sandykbayeva 		: 30200531 </p>
 * <p> Emily Stein 					: 30149842 </p>
 * <p> Thi My Tuyen Tran 			: 30193980 </p>
 * <p> Aoi Ueki 					: 30179305 </p>
 * <p> Ethan Woo 					: 30172855 </p>
 * <p> Kingsley Zhong 				: 30197260 </p>
 */
public class TextSearchController {
	private String searchField;
	private ArrayList<Product> searchResults = new ArrayList<>();
	private boolean shift;

	private class InnerListener implements TouchScreenListener, KeyboardListener {

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
		public void aKeyHasBeenPressed(String label) {
			// TODO This software must decide what is done with the key press here
			// This will only do something for meta keys like shift
			// Only meta keys trigger something in this model
			if (label.equals("Shift (Right)") || label.equals("Shift (Left)")) {
				shift = true;
			} else {
				//Do nothing? Unfortunately we can't make this realistic so every key actions on release
			}
			// Note this is very rough/inaccurate implementation
		}

		@Override
		public void aKeyHasBeenReleased(String label) {
			// TODO This software must decide what is done when the key is released here
			// We could have something like:
			if (label.equals("WHAT GOES HERE?") && !shift){
				// This is a character key with shift not pressed

				searchField += label;
			} else if (label.equals("WHAT GOES HERE?") && shift){
				// This is a character key with shift pressed

				searchField += label.charAt(label.length() - 1); // Does this work?
			} else if (label.equals("Escape")) {
				searchField = "";
				// Cancel the search event? These kinds of key press events usually are actioned on release
			} else if (label.equals("Shift (Right)") || label.equals("Shift (Left)")) {
				shift = false;
			} else if (label.equals("Enter")) {
				textSearchProduct(label);
			} else {
				//Do nothing? These are keys that are not characters and do not have bindings in the software
			}
			// Note this is very rough/inaccurate implementation
		}
	}

	private ArrayList<Product> textSearchProduct(String label) {
		// This is some kind of method that may action the search
		// WIP!!

		ArrayList<Product> searchResults = new ArrayList<>();
//		Map<Barcode, BarcodedProduct> database = ProductDatabases.INVENTORY;
		Map<Barcode, BarcodedProduct> databaseBC = ProductDatabases.BARCODED_PRODUCT_DATABASE;
		Map<PriceLookUpCode, PLUCodedProduct> databasePLU = ProductDatabases.PLU_PRODUCT_DATABASE;

		Pattern regexPattern = Pattern.compile(label, Pattern.CASE_INSENSITIVE);

		// Products in INVENTORY?
		// Now why may this not work the same way?
//		for (HashMap.Entry<Product, Integer> entry: database.entrySet()) {
//			String itemQuery = database.get(entry.getValue()).getDescription();
//			Matcher regexMatcher = regexPattern.matcher(itemQuery);
//			if (regexMatcher.find()) {
//				searchResults.add(entry.getValue());
//			}
//		}

		// Barcoded Products?
		for (HashMap.Entry<Barcode, BarcodedProduct> entry: databaseBC.entrySet()) {
			String itemQuery = databaseBC.get(entry.getValue()).getDescription();
			Matcher regexMatcher = regexPattern.matcher(itemQuery);
			if (regexMatcher.find()) {
				searchResults.add(entry.getValue());
			}
		}

  		// Products with PLU?
		for (HashMap.Entry<PriceLookUpCode, PLUCodedProduct> entry: databasePLU.entrySet()) {
			String itemQuery = databasePLU.get(entry.getValue()).getDescription();
			Matcher regexMatcher = regexPattern.matcher(itemQuery);
			if (regexMatcher.find()) {
				searchResults.add(entry.getValue());
			}
		}

		if (searchResults == null){
			throw new InvalidActionException("No Results");
		} else {
			return searchResults;
		}

	}
}
