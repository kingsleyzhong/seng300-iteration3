package com.thelocalmarketplace.software.test.items;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodeScannerListener;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;

import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.items.ItemAddedRule;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

/**
 * Testing for the AddItemRule class
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

public class ItemAddedRuleTest extends AbstractSessionTest {

    public ItemAddedRuleTest(String testName, AbstractSelfCheckoutStation scs) {
        super(testName, scs);
        // TODO Auto-generated constructor stub
    }

    private BarcodedProduct product;
    private Barcode barcode;
    private BarcodedItem item;


    private ScannerListenerStub listener;

    @Before
    public void setup() {
    	basicDefaultSetup();
        new ItemAddedRule(scs.getMainScanner(), scs.getHandheldScanner(), itemManager);

        barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
        product = new BarcodedProduct(barcode, "Product 1", 10, 100.0);
        item = new BarcodedItem(barcode, new Mass(100.0));
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product); // Add a product to the database


        listener = new ScannerListenerStub();

        scs.getMainScanner().register(listener);
        scs.getHandheldScanner().register(listener);
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testNullSCS() {
        ItemManager itemManagerNull  = null;
        new ItemAddedRule(scs.getMainScanner(), scs.getHandheldScanner(), itemManagerNull);
    }

    @Test
    public void testAddItemInDatabase() {
        session.start();

        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scs.getMainScanner().scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test
    public void testAddItemInDatabaseHandheldScanner() {
        session.start();
        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scs.getHandheldScanner().scan(item);
        }
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
    }

    @Test(expected = InvalidArgumentSimulationException.class)
    public void testAddItemNotInDatabase() {
        session.start();

        Barcode barcodeNotInDatabase = new Barcode(new Numeral[] { Numeral.five, Numeral.five, Numeral.eight });
        BarcodedItem itemNotInDatabase = new BarcodedItem(barcodeNotInDatabase, new Mass(100.0));

        for (int i = 0; i < 100; i++) {
            scs.getMainScanner().scan(itemNotInDatabase);
        }
    }

    @Test
    public void testSessionNotOn() {
        scs.getMainScanner().scan(item);
        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertFalse(productList.containsKey(product));
    }

    @Test
    public void testSessionFrozen() {
        session.start();

        while (!listener.barcodesScanned.contains(item.getBarcode())) {
            scs.getMainScanner().scan(item);
        }
        Barcode newBarcode = new Barcode(new Numeral[] { Numeral.six });
        BarcodedItem newItem = new BarcodedItem(newBarcode, new Mass(100.0));
        BarcodedProduct newProduct = new BarcodedProduct(newBarcode, "New Product", 10, 100.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(newBarcode, newProduct);

        scs.getMainScanner().scan(newItem);

        HashMap<BarcodedProduct, Integer> productList = session.getBarcodedItems();
        assertTrue(productList.containsKey(product));
        assertFalse(productList.containsKey(newProduct));
    }

    @Test
    public void testNoExceptionsOccur() {
        scs.getMainScanner().turnOff();
        scs.getMainScanner().turnOn();
        scs.getMainScanner().disable();
        scs.getMainScanner().enable();

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
