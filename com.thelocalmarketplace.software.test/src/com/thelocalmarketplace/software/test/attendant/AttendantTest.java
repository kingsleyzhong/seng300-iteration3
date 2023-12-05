package com.thelocalmarketplace.software.test.attendant;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.keyboard.USKeyboardQWERTY;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.exceptions.SessionNotRegisteredException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.BagDispenserController;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.test.AbstractTest;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

/**
 * Unit Testing for SelfCheckoutStation logic
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
 * 
 */

public class AttendantTest extends AbstractTest {

    public AttendantTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }

    private SelfCheckoutStationLogic logic;
    private AttendantStation station;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private BarcodedProduct product;
    private BarcodedProduct product2;
    private Barcode barcode;
    private Barcode barcode2;
    private Attendant attendant;
    private Session session;
    private Funds funds;
    private Weight weight;
    private ItemManager itemManager;
    private Membership membership;
    private BagDispenserController bagDispenser;
    private Receipt receiptPrinter;
    
    private PLUCodedProduct pluProduct;
    private PriceLookUpCode pluCode;


    @Before
    public void setup() {
        basicDefaultSetup();
        station = new AttendantStation();
        attendant = new Attendant(station);
        session = new Session();
        
        
        attendant.registerOn(session, scs);

        station.plugIn(powerGrid);
        station.turnOn();

        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        
      
        pluCode = new PriceLookUpCode("1234");
        pluProduct = new PLUCodedProduct(pluCode, "bread", 500); 

        
        funds = new Funds(scs);
        itemManager = new ItemManager();
        bagDispenser = new BagDispenserController(scs.getReusableBagDispenser(), itemManager);
        IElectronicScale baggingArea = scs.getBaggingArea();
        weight = new Weight(baggingArea);
        IReceiptPrinter printer = scs.getPrinter();
        receiptPrinter = new Receipt(printer);
        
        membership = new Membership(scs.getCardReader());
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        
	    
	    ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);
		ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode, pluProduct);
    }

    @Test
    public void testConstructor() {
        Attendant tempAttendant = new Attendant(station);
        assertNotNull(tempAttendant);
    }

    @Test
    public void testDisableSession() {
    
    	attendant.disableStation(session);
    	
    	assertEquals(SessionState.DISABLED, session.getState());
    }
    
    @Test
    public void testEnableStation() {
    	attendant.enableStation(session);
    	
    	assertEquals(SessionState.PRE_SESSION, session.getState());
    }
    
    @Test
    public void testGetAllSessions() {
    	assertEquals(attendant.getSessions().size(),1);
    }
    
    @Test 
    public void testGetStation() {
    	AttendantStation expected = station;
    	AttendantStation result = attendant.getStation();
    	
    	assertEquals(expected, result);
    }
    
    @Test 
    public void testGetCustomerStation() {
    	AbstractSelfCheckoutStation expected = attendant.getCustomerStation(session);
    	
    	if (session.getStation() instanceof SelfCheckoutStationBronze) 
    		assertEquals(SelfCheckoutStationBronze.class.isInstance(expected), true);
    	else if (session.getStation() instanceof SelfCheckoutStationSilver) 
    		assertEquals(SelfCheckoutStationSilver.class.isInstance(expected), true);
    	else if (session.getStation() instanceof SelfCheckoutStationGold)
    		assertEquals(SelfCheckoutStationGold.class.isInstance(expected), true);
    }
    
   
    
    @Test
    public void testApproveValidRequestRequest() {
    	session.start();
    	session.notifyAttendant(Requests.HELP_REQUESTED);
    	attendant.approveRequest(session);
    	
    }
    
    @Test(expected = SessionNotRegisteredException.class)
    public void testApproveRequestWithInvalidSession() {
    	session.start();
    	Session anotherSession = new Session();
    	attendant.approveRequest(anotherSession);
    }
    
    
    @Test(expected = SessionNotRegisteredException.class)
    public void testAddIssuePredictionWithInvalidSession() {
    	session.start();
    	Session anotherSession = new Session();
    	attendant.addIssuePrediction(anotherSession);
    }
    
    @Test(expected = SessionNotRegisteredException.class)
    public void testGetCurrentRequestWithInvalidSession() {
    	session.start();
    	Session anotherSession = new Session();
    	attendant.getCurrentRequest(anotherSession);
    }
    
    @Test(expected = SessionNotRegisteredException.class)
    public void testGetIssuePredictorWithInvalidSession() {
    	session.start();
    	Session anotherSession = new Session();
    	attendant.getIssuePredictor(anotherSession);
    }
    
    @Test
    public void testAddSearchedItemWithValidBarcodedItem() throws DisabledDevice{
    	session.start();
    	session.notifyAttendant(Requests.HELP_REQUESTED);
        
    	 attendant.stringToKeyboard("Sample Product");
         attendant.getStation().keyboard.getKey("Enter").press();
         attendant.getStation().keyboard.getKey("Enter").release();
         
         attendant.addSearchedItem("Sample Product", session);
         assertEquals(session.getItems().size(), 1);
         assertEquals(attendant.getCurrentRequest(session), Requests.NO_REQUEST);
    }
    
    @Test
    public void testAddSearchedItemWithValidPLUCodedItem() throws DisabledDevice{
    	session.start();
    	session.notifyAttendant(Requests.HELP_REQUESTED);
        
    	 attendant.stringToKeyboard("bread");
         attendant.getStation().keyboard.getKey("Enter").press();
         attendant.getStation().keyboard.getKey("Enter").release();
         
         attendant.addSearchedItem("bread", session);
         assertEquals(session.getState(), SessionState.ADD_PLU_ITEM);
         
    }
    

    
    @Test(expected = SessionNotRegisteredException.class)
    public void testGetCustomerStationUnregistered() {
    	attendant = new Attendant(station);
    	attendant.getCustomerStation(session);
    }
    
    
    
    
}
