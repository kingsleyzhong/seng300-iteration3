package com.thelocalmarketplace.software.attendant;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.keyboard.IKeyboard;
import com.jjjwelectronics.keyboard.KeyboardListener;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.screen.TouchScreenListener;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>Simulation of an attendant searching for items using a physical keyboard via keyword search.
 * It is capable of searching item descriptions, PLU codes, and Barcodes.</p>
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
	private boolean shift;
	private String searchField;
	private StringBuilder searchFieldSB;
	private HashMap<String, Product> searchResults;

	public TextSearchController(IKeyboard keyboard) {
		searchResults = new HashMap<>();
		searchField = new String("");
		shift = false;
		TextSearchController.InnerListener textListener = new TextSearchController.InnerListener();
		keyboard.register(textListener);
	}

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

			if (label.equals("Shift (Right)") || label.equals("Shift (Left)")) {
				shift = true;
			} else if (label.length() == 1 && !shift) {
				searchField = searchField + label.toLowerCase();

			} else if (label.length() == 1 && shift) {
				searchField = searchField + label;

			} else if (label.length() == 3 && !shift) {
				searchField += label.charAt(0);  // Does this work?

			} else if (label.length() == 3 && shift) {
				searchField += label.charAt(2);  // Does this work?

			} else if (label.equals("Spacebar")) {
				searchField += " ";

			} else if (label.equals("FnLock Esc")) {
				searchField = "";

			} else if (label.equals("Backspace")) {
				searchFieldSB = new StringBuilder(searchField);
				searchFieldSB.deleteCharAt(searchField.length() - 1);
				searchField = searchFieldSB.toString();

			} else {
				//Do nothing? Unfortunately we can't make this realistic so every key actions on release
			}
		}

		@Override
		public void aKeyHasBeenReleased(String label) {

			if (label.equals("Shift (Right)") || label.equals("Shift (Left)")) {
				shift = false;

			} else if (label.equals("Enter")) {
				if (searchField != "") textSearchProduct(searchField);

			} else {
				//Do nothing? These are keys that are not characters and do not have bindings in the software
			}
		}
	}

	/**
	 * This is a search engine utilizing a regex search of the product databases capable of both searching item descriptions, PLU codes, and Barcodes (UPC)
	 * It is generally activated upon pressing the enter key when the attendant station is in search mode
	 * @param searchField
	 */
	public void textSearchProduct(String searchField) {

		Map<Barcode, BarcodedProduct> databaseBC = ProductDatabases.BARCODED_PRODUCT_DATABASE;
		Map<PriceLookUpCode, PLUCodedProduct> databasePLU = ProductDatabases.PLU_PRODUCT_DATABASE;

		Pattern regexPattern = Pattern.compile(searchField, Pattern.CASE_INSENSITIVE);
		
		searchResults.clear();

		String itemQuery;
		Matcher regexMatcher;

		// Barcoded Products by description
		for (HashMap.Entry<Barcode, BarcodedProduct> entry : databaseBC.entrySet()) {
			itemQuery = entry.getValue().getDescription();
			regexMatcher = regexPattern.matcher(itemQuery);
			if (regexMatcher.find()) {
				searchResults.put(entry.getValue().getDescription(), entry.getValue());
			}
		}

		// Barcoded Products by Barcode (i.e. UPC number)
		for (HashMap.Entry<Barcode, BarcodedProduct> entry : databaseBC.entrySet()) {
			itemQuery = String.valueOf(entry.getKey());
			regexMatcher = regexPattern.matcher(itemQuery);
			if (regexMatcher.find()) {
				searchResults.put(entry.getValue().getDescription(), entry.getValue());
			}
		}

		// Products with PLU by description
		for (HashMap.Entry<PriceLookUpCode, PLUCodedProduct> entry : databasePLU.entrySet()) {
			itemQuery = entry.getValue().getDescription();
			regexMatcher = regexPattern.matcher(itemQuery);
			if (regexMatcher.find()) {
				searchResults.put(entry.getValue().getDescription(), entry.getValue());
			}
		}

		// Products with PLU by PLU
		for (HashMap.Entry<PriceLookUpCode, PLUCodedProduct> entry : databasePLU.entrySet()) {
			itemQuery = String.valueOf(entry.getKey());
			regexMatcher = regexPattern.matcher(itemQuery);
			if (regexMatcher.find()) {
				searchResults.put(entry.getValue().getDescription(), entry.getValue());
			}
		}
	}

	public String getSearchField() {
		return searchField;
	}

	public HashMap<String, Product> getSearchResults() {
		return searchResults;
	}
}
