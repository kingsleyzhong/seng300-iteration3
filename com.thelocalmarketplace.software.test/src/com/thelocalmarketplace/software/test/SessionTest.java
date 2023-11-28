package com.thelocalmarketplace.software.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
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
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

import java.math.BigDecimal;
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

    private Funds funds;
    private Weight weight;
    private ItemManager itemManager;

    // Code added
    private Receipt receiptPrinter;

    private AttendantStation station;

    public SessionTest(String testName, AbstractSelfCheckoutStation scs) {
        super(testName, scs);
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
        funds = new Funds(scs);
        itemManager = new ItemManager(session);

        IElectronicScale baggingArea = scs.getBaggingArea();
        weight = new Weight(baggingArea);

        IReceiptPrinter printer = scs.getPrinter();
        receiptPrinter = new Receipt(printer);
    }

    @Test
    public void testSessionInitialization() {
        assertEquals(session.getState(), SessionState.PRE_SESSION);
        assertFalse(session.getState().inPay());
    }

    @Test
    public void testStartSession() {
        session.start();
        assertEquals(session.getState(), SessionState.IN_SESSION);
        assertFalse(session.getState().inPay());
    }

    @Test
    public void testCancelSession() {
        session.start();
        session.cancel();
        assertEquals(session.getState(), SessionState.PRE_SESSION);
        assertFalse(session.getState().inPay());
    }

    @Test
    public void testAddItem() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        assertTrue("Contains product in list", list.containsKey(product));
        Integer expected = 1;
        assertEquals("Has 1", expected, list.get(product));
    }

    @Test
    public void testAddItemQuantity() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        // Add multiple quantities of the same product
        itemManager.addItem(product);
        itemManager.addItem(product);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        assertTrue("Contains product in list", list.containsKey(product));
        Integer expected = 2;
        assertEquals("Has 2 products", expected, list.get(product));
    }

    @Test
    public void addTwoDifItems() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        itemManager.addItem(product2);
        HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
        Integer expected = 1;
        assertTrue("Contains product in list", list.containsKey(product));
        assertEquals("Contains 1 product", expected, list.get(product));
        assertTrue("Contains product2 in list", list.containsKey(product2));
        assertEquals("Contains 1 product2", expected, list.get(product2));
    }

    @Test
    public void addItemFundUpdate() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        Funds fund = session.getFunds();
        BigDecimal actual = fund.getItemsPrice();
        BigDecimal expected = new BigDecimal(10);
        assertEquals("Value of 10 added", expected, actual);
    }

    @Test
    public void addTwoItemsFundUpdate() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        itemManager.addItem(product2);
        Funds fund = session.getFunds();
        BigDecimal actual = fund.getItemsPrice();
        BigDecimal expected = new BigDecimal(25);
        assertEquals("Value of 25 added", expected, actual);
    }

    @Test
    public void addItemWeightUpdate() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(100.0);
        assertEquals("Mass is 100.0", expected, actual);
    }

    @Test
    public void addTwoItemsWeightUpdate() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        itemManager.addItem(product2);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(120.0);
        assertEquals("Mass is 120.0", expected, actual);
    }

    @Test
    public void testWeightDiscrepancy() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        assertEquals("Discrepancy must have occured", session.getState(), SessionState.BLOCKED);
    }

    @Test
    public void testWeightDiscrepancyResolved() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        itemManager.addItem(product);
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        scs.getBaggingArea().addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        assertEquals("Discrepancy resolved", session.getState(), SessionState.IN_SESSION);

    }

    @Test(expected = CartEmptyException.class)
    public void payEmpty_payByCash() {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        session.payByCash();
    }

    @Test(expected = CartEmptyException.class)
    public void payEmpty_payByCard() throws CashOverloadException, NoCashAvailableException, DisabledException {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
        session.payByCard();
    }

    @Test
    public void testPaid() throws DisabledException, CashOverloadException {
        session.start();
        session.setup(new HashMap<BarcodedProduct, Integer>(), funds, weight, receiptPrinter);
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
        assertEquals(session.getState(), SessionState.PRE_SESSION);
    }

}
