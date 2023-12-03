package com.thelocalmarketplace.software.test.items;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

public class ItemManagerTest extends AbstractSessionTest {
	byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private BarcodedProduct product;
    private BarcodedProduct product2;
    private Barcode barcode;
    private Barcode barcode2;
    private BarcodedItem barcodedItem;
    private BarcodedItem barcodedItem2;
    
    

	@Before
	public void setUp() {
		basicDefaultSetup();
		num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
		product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
	    product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        barcodedItem = new BarcodedItem(barcode, new Mass(product.getExpectedWeight()));
        barcodedItem2 = new BarcodedItem(barcode2, new Mass(product2.getExpectedWeight()));
	}
	
	
	public ItemManagerTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }
	
    @Test
    public void testAddItem() {
        session.start();
        itemManager.addItem(product);
        HashMap<Product, BigInteger> list = session.getItems();
        assertTrue("Contains product in list", list.containsKey(product));
        BigInteger expected = BigInteger.valueOf(1);
        assertEquals("Has 1", expected, list.get(product));
        
        // Reset 
        itemManager.removeItem(product);
    }

    @Test
    public void testAddItemQuantity() {
    	BarcodedItem barcodedItemDuplicate = new BarcodedItem(barcode, new Mass(product.getExpectedWeight()));
        session.start();
        // Add multiple quantities of the same product
        itemManager.addItem(product);
        scs.getBaggingArea().addAnItem(barcodedItem);
        itemManager.addItem(product);
        scs.getBaggingArea().addAnItem(barcodedItemDuplicate);
        HashMap<Product, BigInteger> list = session.getItems();
        assertTrue("Contains product in list", list.containsKey(product));
        BigInteger expected = BigInteger.valueOf(2);
        assertEquals("Has 2 products", expected, list.get(product));
        
        // Reset
        itemManager.removeItem(product);
        scs.getBaggingArea().removeAnItem(barcodedItem);
        itemManager.removeItem(product);
        scs.getBaggingArea().removeAnItem(barcodedItemDuplicate);
    }
    
    @Test
    public void addTwoDifItems() {
        session.start();
        itemManager.addItem(product);
        scs.getBaggingArea().addAnItem(barcodedItem);
        itemManager.addItem(product2);
        scs.getBaggingArea().addAnItem(barcodedItem2);
        HashMap<Product, BigInteger> list = session.getItems();
        BigInteger expected = BigInteger.valueOf(1);
        assertTrue("Contains product in list", list.containsKey(product));
        assertEquals("Contains 1 product", expected, list.get(product));
        assertTrue("Contains product2 in list", list.containsKey(product2));
        assertEquals("Contains 1 product2", expected, list.get(product2));
        
        
        // Reset
        itemManager.removeItem(product);
        scs.getBaggingArea().removeAnItem(barcodedItem);
        itemManager.removeItem(product2);
        scs.getBaggingArea().removeAnItem(barcodedItem2);
    }

    @Test
    public void addItemFundUpdate() {
        session.start();
        itemManager.addItem(product);
        Funds fund = session.getFunds();
        BigDecimal actual = fund.getItemsPrice();
        BigDecimal expected = new BigDecimal(10);
        assertEquals("Value of 10 added", expected, actual);
    }

    @Test
    public void addTwoItemsFundUpdate() {  	
        session.start();
        itemManager.addItem(product);
        scs.getBaggingArea().addAnItem(barcodedItem);
        itemManager.addItem(product2);
        scs.getBaggingArea().addAnItem(barcodedItem2);
        Funds fund = session.getFunds();
        BigDecimal actual = fund.getItemsPrice();
        BigDecimal expected = new BigDecimal(25);
        assertEquals("Value of 25 added", expected, actual);
        
        // Reset
        itemManager.removeItem(product2);
        scs.getBaggingArea().removeAnItem(barcodedItem);
        itemManager.removeItem(product);
        scs.getBaggingArea().removeAnItem(barcodedItem2);
    }
    
    @Test
    public void addItemWeightUpdate() {
        session.start();
        itemManager.addItem(product);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(100.0);
        assertEquals("Mass is 100.0", expected, actual);
    } 

    @Test
    public void addTwoItemsWeightUpdate() {
        session.start();
        itemManager.addItem(product);
        scs.getBaggingArea().addAnItem(barcodedItem);
        itemManager.addItem(product2);
        Weight itemWeight = session.getWeight();
        Mass actual = itemWeight.getExpectedWeight();
        Mass expected = new Mass(120.0);
        assertEquals("Mass is 120.0", expected, actual);
        
        // Reset
        scs.getBaggingArea().removeAnItem(barcodedItem);
        itemManager.removeItem(product2);
        itemManager.removeItem(product);
    }

    @Test
    public void testWeightDiscrepancy() {
        session.start();
        itemManager.addItem(product);
        assertEquals("Discrepancy must have occured", session.getState(), SessionState.BLOCKED);
    }
    
    @Test
    public void testWeightDiscrepancyResolved() {
    	// Create random item 
    	BarcodedItem item = new BarcodedItem(barcode, new Mass(100.0));
    	
        session.start();
        itemManager.addItem(product);
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        scs.getBaggingArea().addAnItem(item);
        assertEquals("Discrepancy resolved", session.getState(), SessionState.IN_SESSION);

        // Reset
        itemManager.removeItem(product);
        scs.getBaggingArea().removeAnItem(item);

    }
}
