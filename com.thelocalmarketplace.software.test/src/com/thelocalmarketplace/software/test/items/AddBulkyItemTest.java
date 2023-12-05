package com.thelocalmarketplace.software.test.items;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;

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
public class AddBulkyItemTest extends AbstractSessionTest {
    public AddBulkyItemTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
        // TODO Auto-generated constructor stub
    }

    private BarcodedProduct product;
    private BarcodedProduct product2;
    private BarcodedItem item;
    private ScannerListenerStub listener;
    private IElectronicScale baggingArea;

    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;

    @Before
    public void setUp() {
        basicDefaultSetup();

        listener = new ScannerListenerStub();
        scs.getHandheldScanner().register(listener);

        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        item = new BarcodedItem(barcode, new Mass(100.0));
        
        baggingArea = scs.getBaggingArea();
        
        session.start();
    }

    /**
     * test case for adding bulky item successfully
     * Scenario: add an item, customer call addBulkyItem, attendant approves
     */
    @Test
    public void testAddBulkyItem() {
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        Mass actual = weight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
        assertEquals(session.getState(), SessionState.IN_SESSION);
        assertFalse("There is no weight discrepancy.", session.getWeight().isDiscrepancy());
    }

    /**
     * test case for handling bulky item
     * Scenario: add items, customer calls addBulkyItem, attendant does not approve
     */
    @Test
    public void testBulkyItemNotApproved() {
        itemManager.addItem(product);
        session.addBulkyItem();
        
        assertEquals("Discrepancy", SessionState.BULKY_ITEM, session.getState());
        assertTrue("Weight Discrepancy", weight.isDiscrepancy());
    }

    /**
     * test case for adding two items and call addBulkyItem
     * Scenario: add items, customer call addBulkyItem, attendant approves
     */
    @Test
    public void testAddTwoBulkyItemAndInSession() {
        itemManager.addItem(product);
        baggingArea.addAnItem(item);
        
        itemManager.addItem(product2);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        Mass actual = weight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    /**
     * test case for adding two same items and call addBulkyItem
     * Scenario: add items, customer call addBulkyItem, attendant approves
     */
    @Test
    public void testAddTwoSameBulkyItem() {
        itemManager.addItem(product);
        baggingArea.addAnItem(item);
        
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        Mass actual = weight.getExpectedWeight();
        Mass expected = new Mass(BigDecimal.valueOf(100));
        assertEquals("Mass is 100", expected, actual);
    }

    /**
     * test case for adding bulky item by handheld scan bronze
     * Scenario: scan an item with handheld scanner, call addBulkyItem
     */
    @Test
    public void testAddBulkyItemHandheldScanner() {
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scs.getHandheldScanner().scan(item);
        }

        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);
        Mass actual = weight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case for causing weight discrepancy
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
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);
        baggingArea.addAnItem(new BarcodedItem(barcode, new Mass(100.0)));
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
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
        baggingArea.addAnItem(item);
        baggingArea.removeAnItem(item);
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
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);
        
        assertEquals("Discrepancy is fixed", session.getState(), SessionState.IN_SESSION);
    }

    /**
     * test case to check bulky item cancelled by customer
     * temp removed: cancel isn't supported
     */
//    @Test
//    public void testCancelBulkyItem() {
//        scs.plugIn(PowerGrid.instance());
//        scs.turnOn();
//        session.start();
//        
//        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
//        itemManager.addItem(product);
//        session.addBulkyItem();
//        session.attendantApprove(Requests.BULKY_ITEM);
//        session.cancelBulkyItem(product);
//        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
//        scs.getBaggingArea().addAnItem(item);
//        assertEquals("Bulky item is cancelled", session.getWeight().getExpectedWeight(), item.getMass());
//    }

    /**
     * test case to check bulky item cancelled by customer
     * Temp removed: this isn't a feature anymore
     */
//    @Test
//    public void testCancelWithTwoBulkyItem() {
//        scs.plugIn(PowerGrid.instance());
//        scs.turnOn();
//        session.start();
//        
//        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
//        itemManager.addItem(product);
//        session.addBulkyItem();
//        session.attendantApprove(Requests.BULKY_ITEM);
//
//        itemManager.addItem(product);
//        session.addBulkyItem();
//        session.attendantApprove(Requests.BULKY_ITEM);
//
//        session.cancelBulkyItem(product);
//        BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
//        scs.getBaggingArea().addAnItem(item);
//        assertEquals("Bulky item is cancelled", session.getWeight().getExpectedWeight(), item.getMass());
//    }

    /**
     * test case for cancelling add bulky item when it was not called
     * temp removed: cancel isn't supported ATM
     */
//    @Test
//    public void testBulkyItemNotCalled() {
//        session.start();
//        
//        itemManager.addItem(product);
//        // cancel bulky item when bulky item was not called
//        session.cancelBulkyItem(product);
//        assertTrue("Bulky item was not called.", session.getBulkyItem().isEmpty());
//        // assertFalse("Bulky item was not called.", session.getBulkyItemCalled());
//    }

    /**
     * test case to remove a bulky item
     */
    @Test
    public void testRemoveBulkyItem() {
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);
        
        itemManager.removeItem(product);
        
        Mass actual = weight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals("Mass is 0", expected, actual);
    }

    /**
     * test case to remove a bulky item (with 2 bulky items scanned)
     */
    @Test
    public void testRemoveBulkyItemTwoItems() {
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        itemManager.removeItem(product);
        assertEquals("Price is 10", BigDecimal.valueOf(10), session.getFunds().getItemsPrice());
    }

    /**
     * test case to remove a bulky item (with 3 bulky items scanned)
     */
    @Test
    public void testRemoveBulkyItemThreeItems() {
        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        itemManager.addItem(product);
        session.addBulkyItem();
        session.attendantApprove(Requests.BULKY_ITEM);

        itemManager.removeItem(product);
        
        assertFalse("No Discrepancy", weight.isDiscrepancy());
        assertEquals("Price is 20", BigDecimal.valueOf(20), session.getFunds().getItemsPrice());
    }

    /**
     * test case for removing non existent bulky item
     * - temp removed, I don't even know what this is trying to do -Kingsley
     */
//    @Test
//    public void testRemoveBulkyItemNotFound() {
//        // need to have bulky item in the hashmap but not have any quantity
//        session.start();
//        
//        itemManager.addItem(product);
//        
//        session.removeItem(product);
//        Assert.assertTrue("There is no bulky item to remove.", session.getBulkyItem().isEmpty());
//    }

    /**
     * test case for calling add bulky item when not allowed
     * I also don't know what this is trying to do.
     */
//    @Test
//    public void testAddBulkyItemNotAllowed() {
//        session.start();
//        
//        // session.bulkyItemMap(new HashMap<BarcodedProduct, Integer>());
//        itemManager.addItem(product);
//        session.addBulkyItem();
//        session.attendantApprove(Requests.BULKY_ITEM);
//        // Assert.assertFalse("You cannot handle bulky item.",
//        // session.getBulkyItemCalled());
//        Assert.assertTrue("You cannot handle bulky item.", session.getBulkyItem().isEmpty());
//    }

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
