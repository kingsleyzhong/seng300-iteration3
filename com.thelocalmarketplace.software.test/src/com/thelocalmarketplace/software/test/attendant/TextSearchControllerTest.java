package com.thelocalmarketplace.software.test.attendant;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import powerutility.NoPowerException;

import java.util.ArrayList;

import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * <p>A class of unit tests that test specifically the funtionality of
 * com.thelocalmarketplace.software.attendant.TextSearchController; it must be able to take key inputs properly into
 * a search field and then exeute a successful search either returning all valid search hits or nothing if there
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

    private BarcodedProduct product;
    private Barcode barcode;
    private BarcodedItem item;

    private ArrayList<Product> expectedResults;

    public TextSearchControllerTest(String testName, AbstractSelfCheckoutStation scs) {
        super(testName, scs);
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
        barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
        product = new BarcodedProduct(barcode, "chicken wings", 10, 100.0);
        item = new BarcodedItem(barcode, new Mass(100.0));
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database

    }

    /**
     * This is a bit of a crap test for now, it just tests a bunch of typing functions to see what they actually do...
     * it seems to work!
     * @throws DisabledDevice
     */
    @Test
    public void populateSearchFieldTest() throws DisabledDevice {
        String expectedSearch = "THIS";
        for (int i = 0; i < expectedSearch.length(); i++) {
            a.getStation().keyboard.getKey("Shift (Right)").press();
            a.getStation().keyboard.getKey(Character.toString(expectedSearch.charAt(i))).press();
            a.getStation().keyboard.getKey(Character.toString(expectedSearch.charAt(i))).release();
            a.getStation().keyboard.getKey("Shift (Right)").release();
        }
        String expectedSearch2 = "NOTTHAT";
        for (int i = 0; i < expectedSearch2.length(); i++) {
            a.getStation().keyboard.getKey(Character.toString(expectedSearch2.charAt(i))).press();
            a.getStation().keyboard.getKey(Character.toString(expectedSearch2.charAt(i))).release();
        }
        a.getStation().keyboard.getKey("Backspace").press();
        a.getStation().keyboard.getKey("Backspace").release();
        a.getStation().keyboard.getKey("2 @").press();
        a.getStation().keyboard.getKey("2 @").release();
        a.getStation().keyboard.getKey("Shift (Right)").press();
        a.getStation().keyboard.getKey("2 @").press();
        a.getStation().keyboard.getKey("2 @").release();
        a.getStation().keyboard.getKey("Shift (Right)").release();
        expectedSearch2 = "NOTTHA2@";
        expectedSearch = expectedSearch + expectedSearch2.toLowerCase();

        assertEquals(expectedSearch, a.getTextSearchController().getSearchField());;
    }

    @Test
    public void successfulSearchTest() throws DisabledDevice {
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
        expectedResults.add(product);
        String expectedSearch = "ICKEN";
        for (int i = 0; i < expectedSearch.length(); i++) {
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).press();
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).release();
        }
        a.getStation().keyboard.getKey("Enter").press();
        a.getStation().keyboard.getKey("Enter").release();
        assertEquals(expectedResults, a.getTextSearchController().getSearchResults());
    }

    @Test
    public void failedSearchTest() throws DisabledDevice{
        String expectedSearch = "MARLBOROGOLDCIGARETTES";
        for (int i = 0; i < expectedSearch.length(); i++) {
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).press();
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).release();
        }
        a.getStation().keyboard.getKey("Enter").press();
        a.getStation().keyboard.getKey("Enter").release();
        assertTrue(a.getTextSearchController().getSearchResults().isEmpty());
    }
//
//    @Test
//    public void textSearchAndAdd() throws DisabledDevice {
//        String expectedSearch = "ICKEN";
//        for (int i = 0; i < expectedSearch.length(); i++) {
//            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).press();
//            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).release();
//            // TBD how the rest of this is done
//        }
//    }
//
    @Test(expected = DisabledDevice.class)
    public void disabledPressTest() throws DisabledDevice {
        a.getStation().keyboard.disable();
        a.getStation().keyboard.getKey("Shift (Right)").press();
    }

    @Test(expected = DisabledDevice.class)
    public void disabledReleaseTest() throws DisabledDevice {
        a.getStation().keyboard.disable();
        a.getStation().keyboard.getKey("Shift (Right)").release();
    }

    @Test(expected = NoPowerException.class)
    public void noPowerPressTest() throws DisabledDevice {
        a.getStation().unplug();
        a.getStation().keyboard.getKey("Shift (Right)").press();
    }

    @Test(expected = NoPowerException.class)
    public void noPowerReleaseTest() throws DisabledDevice {
        a.getStation().unplug();
        a.getStation().keyboard.getKey("Shift (Right)").release();
    }

}