package com.thelocalmarketplace.software.test.attendant;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Parameterized;

import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.jjjwelectronics.printer.ReceiptPrinterSilver;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.test.AbstractTest;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

public class IssuesPredictorTest extends AbstractTest{
	
	private Session session;
	byte num;
	private Numeral numeral;
	private Numeral[] digits;
	private Barcode barcode;
	private Barcode barcode2;
	private BarcodedProduct product;
	private BarcodedProduct product2;
	private Funds funds;
	private ItemManager itemManager;
	private Weight weight;
	private Receipt receiptPrinter;
	private IssuePredictor issuePredictor;
	private SelfCheckoutStationSilver scss;
	private PowerGrid powerGrid;
	private SelfCheckoutStationGold scsg;
	private IReceiptPrinter printer;
	private ReceiptPrinterSilver silverPrinter;
	private ReceiptPrinterBronze bronzePrinter;
	private ReceiptPrinterGold goldPrinter;

	public IssuesPredictorTest(String testName, AbstractSelfCheckoutStation scs) {
		super(testName, scs);
	}
	
	
	@Before
	public void setUp() {
		basicDefaultSetup();
		
		// Create power source
		PowerGrid.engageUninterruptiblePowerSource();
    	powerGrid = PowerGrid.instance();
    	
    	// Set up session
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
        
        printer = scs.getPrinter();
        receiptPrinter = new Receipt(printer);
        issuePredictor = new IssuePredictor();
       
        // Bronze Printer
        bronzePrinter = new ReceiptPrinterBronze();
        bronzePrinter.plugIn(powerGrid);
		bronzePrinter.turnOn();
		
        // Silver Printer
		silverPrinter = new ReceiptPrinterSilver();
        silverPrinter.plugIn(powerGrid);
		silverPrinter.turnOn();
		
		// Gold Printer
		goldPrinter = new ReceiptPrinterGold();
        goldPrinter.plugIn(powerGrid);
		goldPrinter.turnOn();
	}
	
	@Test 
	public void testCheckLowInk() throws OverloadedDevice {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		// Bronze
		issuePredictor.checkLowInk(session, scs.getPrinter());
		// Silver
		silverPrinter.addInk(100000);
		issuePredictor.checkLowInk(session, silverPrinter);
		// Gold
		goldPrinter.addInk(100000);
		issuePredictor.checkLowInk(session, goldPrinter);
	}
	
	@Test
	public void testCheckLowInkWhenFull() throws OverloadedDevice {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		// Bronze
		issuePredictor.checkLowInk(session, scs.getPrinter());
		// Silver
		silverPrinter.addInk(1 << 20);
		issuePredictor.checkLowInk(session, silverPrinter);
		// Gold
		goldPrinter.addInk(1 << 20);
		issuePredictor.checkLowInk(session, goldPrinter);
	}
	
	@Test
	public void testCheckLowPaper() throws OverloadedDevice {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		// Bronze
		issuePredictor.checkLowPaper(session, scs.getPrinter());
		// Silver
		silverPrinter.addPaper(100);
		issuePredictor.checkLowPaper(session, silverPrinter);
		// Gold
		goldPrinter.addPaper(100);
		issuePredictor.checkLowPaper(session, goldPrinter);
	}
	
	@Test
	public void testCheckLowPaperWhenFull() throws OverloadedDevice {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		// Bronze
		issuePredictor.checkLowPaper(session, scs.getPrinter());
		// Silver
		silverPrinter.addPaper(1 << 10);
		issuePredictor.checkLowPaper(session, silverPrinter);
		// Gold
		goldPrinter.addPaper(1 << 10);
		issuePredictor.checkLowPaper(session, goldPrinter);
	}
}