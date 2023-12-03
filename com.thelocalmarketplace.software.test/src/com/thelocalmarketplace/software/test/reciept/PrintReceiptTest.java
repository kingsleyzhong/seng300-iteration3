package com.thelocalmarketplace.software.test.reciept;

/*
 * Testing for print receipt on the  receipt printer
 * 
* Project Iteration 3 Group 1
*
* Derek Atabayev 			: 30177060 
* Enioluwafe Balogun 		: 30174298 
* Subeg Chahal 				: 30196531 
* Jun Heo 					: 30173430 
* Emily Kiddle 				: 30122331 
* Anthony Kostal-Vazquez 	: 30048301 
* Jessica Li 				: 30180801 
* Sua Lim 					: 30177039 
* Savitur Maharaj 			: 30152888 
* Nick McCamis 				: 30192610 
* Ethan McCorquodale 		: 30125353 
* Katelan Ng 				: 30144672 
* Arcleah Pascual 			: 30056034 
* Dvij Raval 				: 30024340 
* Chloe Robitaille 			: 30022887 
* Danissa Sandykbayeva 		: 30200531 
* Emily Stein 				: 30149842 
* Thi My Tuyen Tran 		: 30193980 
* Aoi Ueki 					: 30179305 
* Ethan Woo 				: 30172855 
* Kingsley Zhong 			: 30197260 
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.receipt.ReceiptListener;
import com.thelocalmarketplace.software.test.AbstractSessionTest;

public class PrintReceiptTest extends AbstractSessionTest {

    public PrintReceiptTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
        // TODO Auto-generated constructor stub
    }

    private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Numeral[] digits;
    private Barcode barcode;
    private Barcode barcode2;

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

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

        System.setErr(new PrintStream(errContent));
    }

    @After
    public void restoreStreams() {
        System.setErr(originalErr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullStation() {
        receiptPrinter = new Receipt(null);
    }

    @Test
    public void testOneItemPrintReceipt() throws OverloadedDevice {
        scs.getPrinter().addPaper(512);
        scs.getPrinter().addInk(1024);
        session.start();
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        System.out.println(testReceipt);
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10.0\n"));
    }

    @Test
    public void testPrintReceiptOnlyAddPaper() throws OverloadedDevice {
        session.start();
        itemManager.addItem(product);
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        assertTrue(testReceipt.contains(""));
        scs.getPrinter().addPaper(512);
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt2 = scs.getPrinter().removeReceipt();
        assertTrue(testReceipt2.contains(""));
    }

    @Test
    public void testPrintReceiptOnlyAddInk() throws OverloadedDevice {
        session.start();
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        assertTrue(testReceipt.contains(""));
        scs.getPrinter().addInk(1024);
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt2 = scs.getPrinter().removeReceipt();
        assertTrue(testReceipt2.contains(""));
    }

    @Test
    public void testTwoItemPrintReceipt() throws OverloadedDevice {
        scs.getPrinter().addPaper(512);
        scs.getPrinter().addInk(1024);
        session.start();
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        itemManager.addItem(product2);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item2Mass = new Mass(product2.getExpectedWeight());
        BarcodedItem item2 = new BarcodedItem(product2.getBarcode(), item2Mass);
        scs.getBaggingArea().addAnItem(item2);
        
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10.0\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15.0\n"));
    }

    @Test
    public void testPrintReceiptOutOffPaper() throws OverloadedDevice {
        scs.getPrinter().addPaper(2);
        scs.getPrinter().addInk(1024);
        session.start();
        assertTrue(session.getState() == SessionState.IN_SESSION);
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        itemManager.addItem(product2);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item2Mass = new Mass(product2.getExpectedWeight());
        BarcodedItem item2 = new BarcodedItem(product2.getBarcode(), item2Mass);
        scs.getBaggingArea().addAnItem(item2);
        
        session.printReceipt();
        assertTrue(session.getState() == SessionState.BLOCKED);
    }

    @Test
    public void testPrintReceiptOutOffInk() throws OverloadedDevice {
        scs.getPrinter().addPaper(512);
        scs.getPrinter().addInk(20);
        session.start();
        assertTrue(session.getState() == SessionState.IN_SESSION);
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        assertEquals(testReceipt, "\nItem: Sample Product Am");
        assertTrue(session.getState() == SessionState.BLOCKED);
    }

    @Test
    public void testPrintReceiptReloadPaper() throws OverloadedDevice {
        scs.getPrinter().addPaper(1);
        scs.getPrinter().addInk(1024);
        session.start();
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        itemManager.addItem(product2);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item2Mass = new Mass(product2.getExpectedWeight());
        BarcodedItem item2 = new BarcodedItem(product2.getBarcode(), item2Mass);
        scs.getBaggingArea().addAnItem(item2);
        
        session.printReceipt();
        scs.getPrinter().cutPaper();
        scs.getPrinter().removeReceipt();
        scs.getPrinter().addPaper(512);
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        System.out.println(testReceipt);
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10.0\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15.0\n"));
    }

    @Test
    public void testPrintReceiptReloadInk() throws OverloadedDevice {
        scs.getPrinter().addPaper(512);
        scs.getPrinter().addInk(20);
        session.start();
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        itemManager.addItem(product2);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item2Mass = new Mass(product2.getExpectedWeight());
        BarcodedItem item2 = new BarcodedItem(product2.getBarcode(), item2Mass);
        scs.getBaggingArea().addAnItem(item2);
        
        session.printReceipt();
        scs.getPrinter().cutPaper();
        scs.getPrinter().removeReceipt();
        scs.getPrinter().addInk(1024);
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10.0\n"));
        assertTrue(testReceipt.contains("Item: Sample Product 2 Amount: 1 Price: 15.0\n"));
    }

    @Test
    public void testRegisterListener() throws OverloadedDevice {
        PrinterListener stub = new PrinterListener();
        receiptPrinter.register(stub);
        scs.getPrinter().addPaper(512);
        scs.getPrinter().addInk(1024);
        session.start();
        
        itemManager.addItem(product);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item1Mass = new Mass(product.getExpectedWeight());
        BarcodedItem item1 = new BarcodedItem(product.getBarcode(), item1Mass);
        scs.getBaggingArea().addAnItem(item1);
        
        session.printReceipt();
        scs.getPrinter().cutPaper();
        String testReceipt = scs.getPrinter().removeReceipt();
        assertTrue(testReceipt.contains("Item: Sample Product Amount: 1 Price: 10.0\n"));
        assertTrue(stub.success);
    }

    @Test
    public void testDeregisterListener() throws OverloadedDevice {
        PrinterListener stub = new PrinterListener();
        receiptPrinter.register(stub);
        assertTrue(receiptPrinter.deregister(stub));
    }

    @Test
    public void testDeregisterAllListeners() throws OverloadedDevice {
        PrinterListener stub = new PrinterListener();
        receiptPrinter.register(stub);
        receiptPrinter.deregisterAll();
        assertTrue(receiptPrinter.listeners.isEmpty());
    }

    @Test
    public void testPrintingLineLongerThenCharLimit() throws OverloadedDevice {
        BarcodedProduct product3 = new BarcodedProduct(barcode2,
                "A very long long long long long long long product name", 15, 20.0);
        scs.getPrinter().addPaper(512);
        scs.getPrinter().addInk(1024);
        session.start();
        
        itemManager.addItem(product3);
        // Have to add product as an item to the bagging area for the session to not be blocked (corrects weight discrepancy)
        Mass item3Mass = new Mass(product3.getExpectedWeight());
        BarcodedItem item3 = new BarcodedItem(product3.getBarcode(), item3Mass);
        scs.getBaggingArea().addAnItem(item3);
        
        session.printReceipt();
        String errorMessage = errContent.toString();
        assertTrue(errorMessage.contains("The line is too long. Add a newline"));
    }

    // Stub listener
    private class PrinterListener implements ReceiptListener {
        public boolean block = false; // Flag for running our of ink, set to false in default
        public boolean success = false; // Flag for successful printing.

        @Override
        public void notifiyOutOfPaper() {
            block = true;
        }

        @Override
        public void notifiyOutOfInk() {
            block = true;
        }

        @Override
        public void notifiyPaperRefilled() {
            block = false;
        }

        @Override
        public void notifiyInkRefilled() {
            block = false;
        }

        @Override
        public void notifiyReceiptPrinted() {
            success = true;
        }
    }
}