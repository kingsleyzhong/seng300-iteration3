package com.thelocalmarketplace.software.test.attendant;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.keyboard.USKeyboardQWERTY;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import powerutility.NoPowerException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertEquals;

/**
 * <p>A class of unit tests that test specifically the functionality of
 * com.thelocalmarketplace.software.attendant.TextSearchController; it must be able to take key inputs properly into
 * a search field and then execute a successful search either returning all valid search hits or nothing if there
 * aren't any.</p>
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
public class TextSearchControllerTest extends AbstractTest {
    private AttendantStation station;
    private Attendant a;

    private BarcodedProduct product1;
    private BarcodedProduct product2;
    private PLUCodedProduct product3;

    private Barcode barcode1;
    private Barcode barcode2;
    private PriceLookUpCode plu;

    private BarcodedItem item1;
    private BarcodedItem item2;
    private PLUCodedItem item3;

    private ArrayList<String> expectedResults;

    public TextSearchControllerTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }

    @Before
    public void setup() {
        basicDefaultSetup();
        station = new AttendantStation();
        a = new Attendant(station);
        SelfCheckoutStationLogic.installAttendantStation(station);
        station.plugIn(powerGrid);
        station.turnOn();

        expectedResults = new ArrayList<>();
        barcode1 = new Barcode(new Numeral[]{Numeral.valueOf((byte) 1)});
        product1 = new BarcodedProduct(barcode1, "Raisin Bran Cereal", 10, 100.0);
        item1 = new BarcodedItem(barcode1, new Mass(100.0));

        barcode2 = new Barcode(new Numeral[]{Numeral.valueOf((byte) 2)});
        product2 = new BarcodedProduct(barcode2, "Sun-Maid Raisins 6 Pack", 3, 10.0);
        item2 = new BarcodedItem(barcode2, new Mass(100.0));

        plu = new PriceLookUpCode("6666");
        product3 = new PLUCodedProduct(plu, "Bulk Raisins", 5);
        item3 = new PLUCodedItem(plu, new Mass(1.0));

        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode1, product1); // Add a product to the database
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2); // Add a product to the database
        ProductDatabases.PLU_PRODUCT_DATABASE.put(plu, product3); // Add a product to the database

    }

    /**
     * Tests multiple functionalities of the keyboard
     * @throws DisabledDevice
     */
    @Test
    public void populateSearchFieldTest() throws DisabledDevice {
        String expectedSearch = "yo";
        stringToKeyboard(expectedSearch);

        a.getStation().keyboard.getKey("FnLock Esc").press();
        a.getStation().keyboard.getKey("FnLock Esc").release();
        
        expectedSearch = "Thi$ b3tt3r WoRk.k";
        stringToKeyboard(expectedSearch);
        
        a.getStation().keyboard.getKey("Backspace").press();
        a.getStation().keyboard.getKey("Backspace").release();

        expectedSearch = "Thi$ b3tt3r WoRk.";

        assertEquals(expectedSearch, a.getTextSearchController().getSearchField());;
    }

    /**
     * Searches the typed input "6", expecting to find any description, barcode, or PLU code that contains "6"
     * @throws DisabledDevice
     */
    @Test
    public void successfulNumericSearchTest() throws DisabledDevice {
        ProductDatabases.BARCODED_PRODUCT_DATABASE.putIfAbsent(barcode1, product1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.putIfAbsent(barcode2, product2);
        ProductDatabases.PLU_PRODUCT_DATABASE.putIfAbsent(plu, product3);

        expectedResults.add(product2.getDescription());
        expectedResults.add(product3.getDescription());

        stringToKeyboard("6");

        a.getStation().keyboard.getKey("Enter").press();
        a.getStation().keyboard.getKey("Enter").release();

        assertTrue(a.getTextSearchController().getSearchResults().keySet().containsAll(expectedResults));
    }
    
    /**
     * Searches the typed input "1", expecting to find any description, barcode, or PLU code that contains "1"
     * @throws DisabledDevice
     */
    @Test
    public void successfulBarcodeSearchTest() throws DisabledDevice {
        ProductDatabases.BARCODED_PRODUCT_DATABASE.putIfAbsent(barcode1, product1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.putIfAbsent(barcode2, product2);
        ProductDatabases.PLU_PRODUCT_DATABASE.putIfAbsent(plu, product3);

        expectedResults.add(product1.getDescription());

        stringToKeyboard("1");

        a.getStation().keyboard.getKey("Enter").press();
        a.getStation().keyboard.getKey("Enter").release();

        assertTrue(a.getTextSearchController().getSearchResults().keySet().containsAll(expectedResults));
    }

    /**
     * Searches the typed input "aisin", expecting to find any description that contains the substring "aisin"
     * @throws DisabledDevice
     */
    @Test
    public void successfulTextSearchTest() throws DisabledDevice {
        ProductDatabases.BARCODED_PRODUCT_DATABASE.putIfAbsent(barcode1, product1);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.putIfAbsent(barcode2, product2);
        ProductDatabases.PLU_PRODUCT_DATABASE.putIfAbsent(plu, product3);

        expectedResults.add(product1.getDescription());
        expectedResults.add(product2.getDescription());
        expectedResults.add(product3.getDescription());

        stringToKeyboard("aisin");

        a.getStation().keyboard.getKey("Enter").press();
        a.getStation().keyboard.getKey("Enter").release();

        assertTrue(a.getTextSearchController().getSearchResults().keySet().containsAll(expectedResults));
    }

    /**
     * Searches the typed input "Marlboro Gold Cigarettes", expecting to find nothing, we do not sell
     * "Marlboro Gold Cigarettes". Say no to cigarettes kids!
     * @throws DisabledDevice
     */
    @Test
    public void failedSearchTest() throws DisabledDevice{
        stringToKeyboard("Marlboro Gold Cigarettes");
        a.getStation().keyboard.getKey("Enter").press();
        a.getStation().keyboard.getKey("Enter").release();
        assertTrue(a.getTextSearchController().getSearchResults().isEmpty());
    }

    /**
     * Presses a key when the keyboard is disabled
     * @throws DisabledDevice
     */
    @Test(expected = DisabledDevice.class)
    public void disabledPressTest() throws DisabledDevice {
        a.getStation().keyboard.disable();
        a.getStation().keyboard.getKey("Shift (Right)").press();
    }
    
    /**
     * Releases a key when the keyboard is disabled
     * @throws DisabledDevice
     */
    @Test(expected = DisabledDevice.class)
    public void disabledReleaseTest() throws DisabledDevice {
        a.getStation().keyboard.disable();
        a.getStation().keyboard.getKey("Shift (Right)").release();
    }

    /**
     * Presses a key when the keyboard has no power
     * @throws DisabledDevice
     */
    @Test(expected = NoPowerException.class)
    public void noPowerPressTest() throws DisabledDevice {
        a.getStation().unplug();
        a.getStation().keyboard.getKey("Shift (Right)").press();
    }

    /**
     * Releases a key when the keyboard has no power
     * @throws DisabledDevice
     */
    @Test(expected = NoPowerException.class)
    public void noPowerReleaseTest() throws DisabledDevice {
        a.getStation().unplug();
        a.getStation().keyboard.getKey("Shift (Right)").release();
    }
 
    // -------------------- END TESTS --------------------
    
    /**
     * This is a utility method that will convert a string into the associated key presses on the
     * USKeyboardQWERTY, a similar function may be included in the GUI, but that is outside
     * the scope of this test class.
     * @param input
     * @throws DisabledDevice
     */
    public void stringToKeyboard(String input) throws DisabledDevice {
        USKeyboardQWERTY keyboard = a.getStation().keyboard;

        // Generate Mapping for Non Alphabetical Shift Modified Keys
        Map<Character, Boolean> shiftModified = new HashMap<>();
        Map<Character, String> labelLookup = new HashMap<>();

        for (String label : USKeyboardQWERTY.WINDOWS_QWERTY) {
            if(label.length() == 3 && label.charAt(1) == ' ') { // Desired Format
                Character c1 = label.charAt(0);
                Character c2 = label.charAt(2);

                shiftModified.put(c1, false);
                shiftModified.put(c2, true);

                labelLookup.put(c1, label);
                labelLookup.put(c2, label);
            }
        }

        for (int i = 0; i < input.length(); i++) {
            Character c = input.charAt(i);
            boolean shift = false;
            String targetLabel;

            if(c == ' ') {
                targetLabel = "Spacebar";
            }
            else if(Character.isLowerCase(c)) {
                targetLabel = String.valueOf(Character.toUpperCase(c));
            }
            else if(Character.isUpperCase(c)) {
                targetLabel = String.valueOf(c);
                shift = true;
            }
            else if(labelLookup.containsKey(c)) {
                targetLabel = labelLookup.get(c);
                shift = shiftModified.get(c);
            }
            else {
                continue;
            }

            if(shift) {
                keyboard.getKey("Shift (Left)").press();
            }
            keyboard.getKey(targetLabel).press();
            keyboard.getKey(targetLabel).release();
            if(shift) {
                keyboard.getKey("Shift (Left)").release();
            }
        }
    }
}