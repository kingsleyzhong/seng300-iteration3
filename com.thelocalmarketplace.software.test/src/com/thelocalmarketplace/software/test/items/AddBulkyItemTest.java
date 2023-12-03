package com.thelocalmarketplace.software.test.items;

import static org.junit.Assert.*;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.test.AbstractTest;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Unit Test class for Add Bulky Item Use Case
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
public class AddBulkyItemTest extends AbstractTest {
    public AddBulkyItemTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
        // TODO Auto-generated constructor stub
    }

    private Session session;
    private ItemManager itemManager;
    private BarcodedProduct product;
    private BarcodedProduct product2;
    private BarcodedItem item;
    private ScannerListenerStub listener;
    private Receipt receipt;

    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;

    private Funds funds;
    private Weight weight;

    @Before
    public void setUp() {
        basicDefaultSetup();

        listener = new ScannerListenerStub();
        scs.getHandheldScanner().register(listener);

        session = new Session();
        itemManager = new ItemManager(session);
        receipt = new Receipt(scs.getPrinter());

        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        funds = new Funds(scs);
        item = new BarcodedItem(barcode, new Mass(100.0));

        weight = new Weight(scs.getBaggingArea());
    }

    /**
     * test case for adding bulky item successfully
     * Scenario: add an item, customer call addBulkyItem, assistant approves
     */
    @Test
    public void testAddBulkyItem() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
        assertEquals(session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for handling bulky item
     * tests if the system properly signals the attendant that a no bagging request
     * is in progress
     * and if the attendant signals to the system that request is approved
     */
    @Test
    public void testAddBulkyItemAttendantCall() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);
        assertTrue("Bulky Item request has been approved.", session.getBulkyItem().containsKey(product));
    }

    /**
     * test case for adding two items and call addBulkyItem
     * Scenario: add items, customer call addBulkyItem, attendant approves
     */
    @Test
    public void testAddTwoBulkyItemAndInSession() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        itemManager.addItem(product2);
        session.bulkyItemCalled();
        session.addBulkyItem(product2);

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    /**
     * test case for adding two same items and call addBulkyItem
     * Scenario: add items, customer call addBulkyItem, attendant approves
     */
    @Test
    public void testAddTwoSameBulkyItem() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    /**
     * test case for adding bulky item by handheld scan bronze
     * Scenario: scan an item with handheld scanner, call addBulkyItem
     */
    @Test
    public void testAddBulkyItemHandheldScanner() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);

        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scs.getHandheldScanner().scan(item);
        }

        session.bulkyItemCalled();
        session.addBulkyItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case for causing weight discrepancy 1 using bronze station
     * scenario: add item, customer call addBulkyItem, attendant approves, then
     * place the bulky item in the bagging area anyways
     *
     * This will cause a weight discrepancy, which can have 3 options for fixing the
     * issue
     * 1. customer removes the item
     * 2. customer signals the system for AddBulkyItem
     * 3. attendant signals the system of weight discrepancy approval
     */
    @Test
    public void testBulkyItemWeightDiscrepancy() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);
        scs.getBaggingArea().addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
        assertEquals("Discrepancy must have occurred", session.getState(), SessionState.BLOCKED);
    }

    /**
     * test case for fixing weight discrepancy 1 using bronze station
     * scenario: add item, customer call addBulkyItem, attendant approves, then
     * place the bulky item in the bagging area anyways,
     * but then remove the item from the bagging area to fix weight discrepancy
     *
     * weight discrepancy fixed using option 1
     */
    @Test
    public void testBulkyItemWeightDiscrepancyResolved() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.getBaggingArea().addAnItem(item);
        scs.getBaggingArea().removeAnItem(item);
        assertEquals("Discrepancy resolved", session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case for weight discrepancy 2
     * scenario: add item, do not call addBulkyItem(), and not place the item in the
     * bagging area
     *
     * This will cause a weight discrepancy, and the system will signal the customer
     * if they want to call AddBulkyItem
     * 3 options for fixing the issue
     * 1. customer removes the item
     * 2. customer signals the system for AddBulkyItem
     * 3. attendant signals the system of weight discrepancy approval
     */
    @Test
    public void testAddItemButNotCallBulkyItem() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        itemManager.addItem(product);
        assertEquals("Discrepancy must have occurred", session.getState(), SessionState.BLOCKED);
    }

    /**
     * test case for fixing weight discrepancy 2
     * scenario: add item, do not call addBulkyItem(), and not place the item in the
     * bagging area,
     * then call addBulkyItem() to fix weight discrepancy
     *
     * weight discrepancy fixed using option 2
     */
    @Test
    public void testAddItemButNotCallBulkyItemFixed() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);
        assertEquals("Discrepancy is fixed", session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case to check bulky item cancelled by customer
     */
    @Test
    public void testCancelBulkyItem() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);
        session.cancelBulkyItem(product);
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.getBaggingArea().addAnItem(item);
        assertEquals("Bulky item is cancelled", session.getWeight().getExpectedWeight(), item.getMass());
    }

    /**
     * test case to check bulky item cancelled by customer
     */
    @Test
    public void testCancelWithTwoBulkyItem() {
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        session.cancelBulkyItem(product);
        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        scs.getBaggingArea().addAnItem(item);
        assertEquals("Bulky item is cancelled", session.getWeight().getExpectedWeight(), item.getMass());
    }

    /**
     * test case for cancelling add bulky item when it was not called
     */
    @Test
    public void testBulkyItemNotCalled() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        itemManager.addItem(product);
        // cancel bulky item when bulky item was not called
        session.cancelBulkyItem(product);
        assertTrue("Bulky item was not called.", session.getBulkyItem().isEmpty());
        // assertFalse("Bulky item was not called.", session.getBulkyItemCalled());
    }

    /**
     * test case for no weight discrepancy (attendant is not called)
     */
    @Test
    public void testNoWeightDiscrepancy() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        // session.assistantApprove();
        session.addBulkyItem(product);
        assertFalse("There is no weight discrepancy.", session.getWeight().isDiscrepancy());
    }

    /**
     * test case to remove a bulky item
     */
    @Test
    public void testRemoveBulkyItem() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);
        session.removeItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case to remove a bulky item (with 2 bulky items scanned)
     */
    @Test
    public void testRemoveBulkyItemTwoItems() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());

        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        session.removeItem(product);
        assertEquals("Price is 10", BigDecimal.valueOf(10), session.getFunds().getItemsPrice());
    }

    /**
     * test case to remove a bulky item (with 3 bulky items scanned)
     */
    @Test
    public void testRemoveBulkyItemThreeItems() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.addBulkyItem(product);

        session.removeItem(product);
        assertEquals("Price is 20", BigDecimal.valueOf(20), session.getFunds().getItemsPrice());
    }

    /**
     * test case for removing non existent bulky item
     */
    @Test
    public void testRemoveBulkyItemNotFound() {
        // need to have bulky item in the hashmap but not have any quantity
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.bulkyItemCalled();
        session.removeItem(product);
        Assert.assertTrue("There is no bulky item to remove.", session.getBulkyItem().isEmpty());
    }

    /**
     * test case for calling add bulky item when not allowed
     */
    @Test
    public void testAddBulkyItemNotAllowed() {
        session.start();
        session.setup(itemManager, funds, weight, receipt, scs);
        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
        itemManager.addItem(product);
        session.addBulkyItem(product);
        // Assert.assertFalse("You cannot handle bulky item.",
        // session.getBulkyItemCalled());
        Assert.assertTrue("You cannot handle bulky item.", session.getBulkyItem().isEmpty());
    }

    public class ScannerListenerStub implements BarcodeScannerListener {
        public ArrayList<Barcode> barcodesScanned;

        ScannerListenerStub() {
            barcodesScanned = new ArrayList<Barcode>();
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

        @Override
        public void aBarcodeHasBeenScanned(IBarcodeScanner barcodeScanner, Barcode barcode) {
            barcodesScanned.add(barcode);
        }

    }
}
