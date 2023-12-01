package com.thelocalmarketplace.software.test.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemAddedRule;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.test.AbstractTest;
import com.thelocalmarketplace.software.weight.Weight;

/**
 * Unit Test class for RemoveItemMethod and interaction with surrounding classes
 * Weight
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

public class RemoveItemTests extends AbstractTest {
    public RemoveItemTests(String testName, AbstractSelfCheckoutStation scs) {
        super(testName, scs);
        // TODO Auto-generated constructor stub
    }

    private Session session;

    private BarcodedProduct product;
    private BarcodedProduct product2;
    private Barcode barcode;
    private BarcodedItem item;
    private ItemManager itemManager;
    private Receipt receipt;

    private Funds funds;

    private Weight weight;

    // sets up the test cases
    @Before
    public void setup() {
        basicDefaultSetup();

        session = new Session();
        
        itemManager = new ItemManager(session);
        
        receipt = new Receipt(scs.getPrinter());

        new ItemAddedRule(scs.getMainScanner(),scs.getHandheldScanner(), itemManager);

        barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
        product = new BarcodedProduct(barcode, "Product 1", 10, 100.0);
        product2 = new BarcodedProduct(barcode, "Product 2", 10, 120.0);

        item = new BarcodedItem(barcode, new Mass(100.0));

        weight = new Weight(scs.getBaggingArea());
        funds = new Funds(scs);

    }

    // Successfully remove item (update weight and price) , Silver, Gold
    @Test
    public void testRemoveItemInDatabase() {

        // start the session
    	session.setup(itemManager, funds, weight, receipt, scs);
        session.start();
        // add item
        itemManager.addItem(product);

        // Check that the product was added
        HashMap<Product, BigInteger> productList = session.getItems();
        assertTrue(productList.containsKey(product));

        // Remove item
        itemManager.removeItem(product);

        // check that the weights and values
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.ZERO, session.getFunds().getAmountDue());

        // Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(0);
        assertEquals(expected, actual);

    }

    // remove item that hasn't been scanned , Silver, Gold
    @Test(expected = ProductNotFoundException.class)
    public void testRemoveItemNotInDatabase() {
        // start the session
    	session.setup(itemManager, funds, weight, receipt, scs);
        session.start();

        // Remove item
        itemManager.removeItem(product);
    }

    // remove duplicate item (update weight and price) , Silver, Gold
    @Test
    public void testRemoveDupliacateItemInDatabase() {
        // start the session
    	session.setup(itemManager, funds, weight, receipt, scs);
        session.start();

        // add item twice
        itemManager.addItem(product);
        itemManager.addItem(product);

        // Check that the product was added
        HashMap<Product, BigInteger> productList = session.getItems();
        assertTrue(productList.containsKey(product));

        // Remove item once
        itemManager.removeItem(product);

        // check that the weights and values
        assertTrue(productList.containsKey(product));
        assertEquals(BigDecimal.TEN, session.getFunds().getAmountDue());

        // Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(100.0);
        assertEquals(expected, actual);
    }

    // remove item twice
    @Test(expected = ProductNotFoundException.class)
    public void testRemoveSameItemTwice() {
    	session.setup(itemManager, funds, weight, receipt, scs);
        session.start();
        itemManager.addItem(product);
        HashMap<Product, BigInteger> productList = session.getItems();
        itemManager.removeItem(product);
        itemManager.removeItem(product);
    }

    // remove item that was not the last added
    @Test
    public void testRemoveItemThatsNotLastAdded() {
        // start the session
    	session.setup(itemManager, funds, weight, receipt, scs);
        session.start();

        // add two different items
        itemManager.addItem(product);
        itemManager.addItem(product2);

        // Check that the product was added
        HashMap<Product, BigInteger> productList = session.getItems();
        assertTrue(productList.containsKey(product));

        // Remove item once
        itemManager.removeItem(product);

        // check that the weights and values
        assertFalse(productList.containsKey(product));
        assertEquals(BigDecimal.TEN, session.getFunds().getAmountDue());

        // Initializing weights to test for adjustment
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(120.0);
        assertEquals(expected, actual);
    }

}