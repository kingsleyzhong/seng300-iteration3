package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.BagDispenserController;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.weight.Weight;

import StubClasses.ItemsListenerStub;
import StubClasses.SessionListenerStub;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

/**
 * Unit Test class for Session and interaction with surrounding classes Weight
 * and Funds
 * Tests for turning session on, turning session off
 * Adding a single item, adding multiple duplicate items, adding different items
 * Session freezing when discrepancy occurs and un-freezing when discrepancy
 * resolved
 * See tests for Weight, Funds to ensure no bugs.
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
 */

public class SessionTest extends AbstractTest {
    private Session session;
    private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;
    private BarcodedItem barcodedItem;

    private Funds funds;
    private Weight weight;
    private ItemManager itemManager;
    private Membership membership;
    private BagDispenserController bagDispenser;

    // Code added
    private Receipt receiptPrinter;

    private AttendantStation station;

    public SessionTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
        // TODO Auto-generated constructor stub
    }

    @Before
    public void setUp() {
        basicDefaultSetup();

        session = new Session();
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        barcodedItem = new BarcodedItem(barcode, new Mass(product.getExpectedWeight()));
        funds = new Funds(scs);
        itemManager = new ItemManager();
        bagDispenser = new BagDispenserController(scs.getReusableBagDispenser(), itemManager);
        

        IElectronicScale baggingArea = scs.getBaggingArea();
        weight = new Weight(baggingArea);

        IReceiptPrinter printer = scs.getPrinter();
        receiptPrinter = new Receipt(printer);
        
        membership = new Membership(scs.getCardReader());
    }

    @After
    public void clearDatabase() {
    	ProductDatabases.BARCODED_PRODUCT_DATABASE.clear();
    	ProductDatabases.PLU_PRODUCT_DATABASE.clear();
    }
    
    @Test
    public void testSessionInitialization() {
        assertEquals(session.getState(), SessionState.PRE_SESSION);
        assertFalse(session.getState().inPay());
    }

    @Test
    public void testStartSession() {
    	session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
        assertEquals(session.getState(), SessionState.IN_SESSION);
        assertFalse(session.getState().inPay());
    }

    @Test
    public void testCancelSession() {
    	session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
        session.cancel();
        assertEquals(session.getState(), SessionState.PRE_SESSION);
        assertFalse(session.getState().inPay());
    }

    @Test(expected = CartEmptyException.class)
    public void payEmpty_payByCash() {
    	session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
        session.payByCash();
    }

    @Test(expected = CartEmptyException.class)
    public void payEmpty_payByCard() throws CashOverloadException, NoCashAvailableException, DisabledException {
    	session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
        session.payByCard();
    }

    @Test
    public void testPaid() throws DisabledException, CashOverloadException {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
        itemManager.addItem(product);
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        scs.getBaggingArea().addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        session.payByCash();
        assertTrue(session.getState().inPay());
        Coin.DEFAULT_CURRENCY = Currency.getInstance(Locale.CANADA);
        Coin coin = new Coin(BigDecimal.ONE);
        int count = 0;
        while (count < 10) {
            scs.getCoinSlot().receive(coin);
            if (scs.getCoinTray().collectCoins().isEmpty()) {
                count++;
            }
        }
        assertEquals(session.getState(), SessionState.PAY_BY_CASH);
    }
    
    @Test(expected = NullPointerSimulationException.class)
    public void addedNullRegister() {
    	session.register(null);
    	
    }
    
    @Test(expected = NullPointerSimulationException.class)
    public void removedNullRegister() {
    	session.deRegister(null);
    	
    }
    
    @Test
    public void addAndRemoveValidListeners() {
    	SessionListenerStub stub = new SessionListenerStub();
    	assertEquals(session.getListeners().size(), 0);
    	session.register(stub);
    	assertEquals(session.getListeners().size(), 1);
    	
    	// Reset
    	session.deRegister(stub);
    	assertEquals(session.getListeners().size(), 0);
    }
    
    @Test
    public void anItemHasBeenAddedListenerNotified() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
     	SessionListenerStub stub = new SessionListenerStub();
        session.register(stub);
        itemManager.addItem(product);
        
        assertEquals(stub.products.get(product), BigInteger.valueOf(1));
        assertTrue(stub.currentExpectedPrice.compareTo(new BigDecimal(10)) == 0);
        
        // Reset
        session.deRegister(stub);
        itemManager.removeItem(product);
    }
    
    @Test
    public void anItemHasBeenRemovedListenerNotified() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
     	SessionListenerStub stub = new SessionListenerStub();
        session.register(stub);
        itemManager.addItem(product);
        
        // Reset
        itemManager.removeItem(product);
        session.deRegister(stub);
        
        assertEquals(stub.products.get(product), null);
        assertTrue(stub.currentExpectedPrice.compareTo(new BigDecimal(0)) == 0);
        
    }
    
    @Test
    public void aPLUCodeWasEnteredListenerNotified() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
     	SessionListenerStub stub = new SessionListenerStub();
        session.register(stub);
        
        PriceLookUpCode pluCode = new PriceLookUpCode("1234");
        PLUCodedProduct pluProduct = new PLUCodedProduct(pluCode, "bread", 500);
        SelfCheckoutStationLogic.populateDatabase(pluCode, pluProduct, num);
        itemManager.addItem(pluCode);
        
        assertTrue(stub.pluProduct.getPLUCode().equals(pluCode));
        
        // Reset
        session.deRegister(stub);
    }
    
    
    @Test
    public void addItemInSessionDiscrepancy() {
    	session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
    	session.start();
     	SessionListenerStub stub = new SessionListenerStub();
     	session.register(stub);
     	
    	scs.getBaggingArea().addAnItem(barcodedItem);
     	
     	assertTrue(stub.request == Requests.WEIGHT_DISCREPANCY);
    	// Reset
     	session.deRegister(stub);
     	scs.getBaggingArea().removeAnItem(barcodedItem);

    }
    
    @Test
    public void cancelSessionInSession() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
        session.cancel();
        assertTrue(session.getState() == SessionState.PRE_SESSION);
    }
    
    @Test(expected = InvalidActionException.class)
    public void membershipEnteredNotInAddingState() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.enterMembership();
    }
    
    @Test
    public void paperRefilledAndLow() throws EmptyDevice, OverloadedDevice {
    	session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
    	session.start();
     	SessionListenerStub stub = new SessionListenerStub();
     	session.register(stub);
     	scs.getPrinter().addInk(1);
    	scs.getPrinter().addPaper(1);
    	assertEquals(session.getState(), SessionState.IN_SESSION);
    	
    	scs.getPrinter().print('\n');

    	assertEquals(stub.request, Requests.CANT_PRINT_RECEIPT);
    	// Reset
     	session.deRegister(stub);
    }
    
    @Test
    public void inkRefilledAndLow() throws OverloadedDevice, EmptyDevice {
    	session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
    	session.start();
     	SessionListenerStub stub = new SessionListenerStub();
     	session.register(stub);
     	scs.getPrinter().addInk(1);
    	scs.getPrinter().addPaper(2);
    	scs.getPrinter().print('9');
    	assertEquals(stub.request, Requests.CANT_PRINT_RECEIPT);
    	
    	// Reset
     	session.deRegister(stub);
    }
    
    	
    /*
     *Sessions can only be disabled when in the pre-session state 
     */
    @Test
    public void disableSession() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
     	
        session.disable();
        assertTrue(session.getState()== SessionState.DISABLED);
    }
    @Test
    public void disableSessionStarted() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
     	session.start();
        session.disable();
        assertTrue(session.getState() != SessionState.DISABLED);
    }
    /*
     * Sessions can only be enabled when they are disabled, no effect otherwise
     */
    @Test
    public void enableSession() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.disable();
        session.enable();
        assertTrue(session.getState() != SessionState.DISABLED);
    }
    @Test
    public void enableSessionNotDisabled() {
        session.setup(itemManager, funds, weight, receiptPrinter, membership, scs, bagDispenser);
        session.start();
        SessionState preState = session.getState();
        session.enable();
        SessionState postState = session.getState();
        assertTrue(preState == postState);
    }
   
}
