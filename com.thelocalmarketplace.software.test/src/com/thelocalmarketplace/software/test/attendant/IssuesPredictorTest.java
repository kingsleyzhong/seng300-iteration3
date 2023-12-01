package com.thelocalmarketplace.software.test.attendant;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import org.junit.Assert;
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
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.test.AbstractTest;
import com.thelocalmarketplace.software.weight.Weight;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * Test class for IssuesPredictor
 *
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev 			: 30177060
 * Enioluwafe Balogun 		: 30174298
 * Subeg Chahal 			: 30196531
 * Jun Heo 					: 30173430
 * Emily Kiddle 			: 30122331
 * Anthony Kostal-Vazquez 	: 30048301
 * Jessica Li 				: 30180801
 * Sua Lim 					: 30177039
 * Savitur Maharaj 			: 30152888
 * Nick McCamis 			: 30192610
 * Ethan McCorquodale 		: 30125353
 * Katelan Ng 				: 30144672
 * Arcleah Pascual 			: 30056034
 * Dvij Raval 				: 30024340
 * Chloe Robitaille 		: 30022887
 * Danissa Sandykbayeva 	: 30200531
 * Emily Stein 				: 30149842
 * Thi My Tuyen Tran 		: 30193980
 * Aoi Ueki 				: 30179305
 * Ethan Woo 				: 30172855
 * Kingsley Zhong 			: 30197260
 */

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
	private PowerGrid powerGrid;
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
		scs.getCoinDispensers().values();
		// Bronze
		issuePredictor.checkLowInk(session, scs.getPrinter());
		// Silver
		silverPrinter.addInk(100000);
		issuePredictor.checkLowInk(session, silverPrinter);
		// Gold
		goldPrinter.addInk(100000);
		issuePredictor.checkLowInk(session, goldPrinter);
		//session.predictionCheck();

		//Assert.assertEquals(SessionState.BLOCKED, session.getState());

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

		//Assert.assertEquals(SessionState.BLOCKED, session.getState());
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
	
	@Test
	public void testCheckLowCoins() throws OverloadedDevice {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		
		issuePredictor.checkLowCoins(session, scs.getCoinDispensers());
	}
	
	@Test
	public void testCheckLowBanknotes() throws OverloadedDevice {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		
		issuePredictor.checkLowBanknotes(session, scs.getBanknoteDispensers());
		//System.out.println(scs.getBanknoteDispensers());

	}
	
	@Test
	public void testCheckCoinsFull() throws OverloadedDevice, SimulationException, CashOverloadException {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
	    for (int i = 0; i < 1000; i++) { // 1000 is max capacity
	        scs.getCoinStorage().load(new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal ("2.00"))); // Add $2.00 1000 times
	        }
		issuePredictor.checkCoinsFull(session, scs.getCoinStorage());

	}
	
	@Test
	public void testCheckBanknotesFull() throws OverloadedDevice, SimulationException, CashOverloadException {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		 for (int i = 0; i < 1000; i++) { // 1000 is max capacity
		        scs.getBanknoteStorage().load(new Banknote(Currency.getInstance(Locale.CANADA), new BigDecimal ("5.00"))); // Add $5.00 1000 times
		        }
		issuePredictor.checkBanknotesFull(session, scs.getBanknoteStorage());

	}
	
	@Test
	public void testSessionBlock() {
		session.setup(itemManager, funds, weight, receiptPrinter, scs);
		session.start();
		issuePredictor.checkLowInk(session, bronzePrinter);
		issuePredictor.checkLowPaper(session, bronzePrinter);
		issuePredictor.checkLowCoins(session, scs.getCoinDispensers());
		issuePredictor.checkLowBanknotes(session, scs.getBanknoteDispensers());
		issuePredictor.checkCoinsFull(session, scs.getCoinStorage());
		issuePredictor.checkBanknotesFull(session, scs.getBanknoteStorage());
	}
}