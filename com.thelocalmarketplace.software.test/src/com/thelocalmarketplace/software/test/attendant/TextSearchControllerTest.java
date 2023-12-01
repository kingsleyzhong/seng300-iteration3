package com.thelocalmarketplace.software.test.attendant;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.keyboard.USKeyboardQWERTY;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

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
    }

    @Test
    public void populateSeachFieldTest() throws DisabledDevice {
        // Test to see if typing in the search field works
        String expectedSearch = "MARLBORO GOLD CIGARETTES";
        for (int i = 0; i < expectedSearch.length(); i++) {
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).press();
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).release();
            // Somehow retrieve the searchField
            assertEquals(expectedSearch, a.getTextSearchController().getSearchField());
        }
    }

    @Test
    public void successfulSearchTest() throws DisabledDevice {
        String expectedSearch = "ICKEN";
        for (int i = 0; i < expectedSearch.length(); i++) {
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).press();
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).release();
//            assertEquals(expectedResults, searchResults);
        }
    }

    @Test
    public void failedSearchTest() throws DisabledDevice {
        String expectedSearch = "ALBERTA PURE";
        for (int i = 0; i < expectedSearch.length(); i++) {
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).press();
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).release();
//            assertEquals(null, searchResults);
        }
    }

    @Test
    public void textSearchAndAdd() throws DisabledDevice {
        String expectedSearch = "ICKEN";
        for (int i = 0; i < expectedSearch.length(); i++) {
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).press();
            a.getStation().keyboard.getKey(String.valueOf(expectedSearch.charAt(i))).release();
            // TBD how the rest of this is done
        }
    }

    @Test
    public void disabledTest() {
        // Finish this
    }

}