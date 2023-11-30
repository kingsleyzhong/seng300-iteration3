package com.thelocalmarketplace.software.test.attendant;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
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

public class IssuesPredictorTest{

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

//	public IssuesPredictorTest(String testName, AbstractSelfCheckoutStation scs) {
//		super(testName, scs);
//	}
	
	@Before
	public void setUp() {
		AbstractSelfCheckoutStation.resetConfigurationToDefaults();
    	PowerGrid.engageUninterruptiblePowerSource();
    	powerGrid = PowerGrid.instance();
    	scss = new SelfCheckoutStationSilver();
    	
    	scss.plugIn(powerGrid);
		scss.turnOn();
		
		
		session = new Session();
        num = 1;
        numeral = Numeral.valueOf(num);
        digits = new Numeral[] { numeral, numeral, numeral };
        barcode = new Barcode(digits);
        barcode2 = new Barcode(new Numeral[] { numeral });
        product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
        product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
        funds = new Funds(scss);
        itemManager = new ItemManager(session);

        IElectronicScale baggingArea = scss.getBaggingArea();
        weight = new Weight(baggingArea);

        IReceiptPrinter printer = scss.getPrinter();
        receiptPrinter = new Receipt(printer);
        issuePredictor = new IssuePredictor();
	}
	
	@Test 
	public void testCheckLowInk() {
		session.setup(issuePredictor, itemManager, funds, weight, receiptPrinter, scss);
		issuePredictor.checkLowInk(session);
		System.out.println(scss.getPrinter());
		
		
	}
	
}
