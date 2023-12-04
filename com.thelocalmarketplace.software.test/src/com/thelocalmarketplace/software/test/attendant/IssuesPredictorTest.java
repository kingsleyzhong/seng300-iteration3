package com.thelocalmarketplace.software.test.attendant;

import java.math.BigDecimal;

import java.util.*;

import org.junit.*;
import com.jjjwelectronics.*;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.printer.ReceiptPrinterSilver;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.Barcode;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserBronze;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.*;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.exceptions.ClosedHardwareException;
import com.thelocalmarketplace.software.exceptions.IncorrectDenominationException;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import com.thelocalmarketplace.software.weight.Weight;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

/**
 * Test class for IssuesPredictor
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

public class IssuesPredictorTest extends AbstractSessionTest{
	
	private CoinDispenserBronze cdb;
	private Coin coin;
	private Banknote banknote;
	
	private BarcodedProduct product;
    private BarcodedProduct product2;
    byte num;
    private Numeral numeral;
    private Barcode barcode;
    private Barcode barcode2;
    private Numeral[] digits;
    
    //private Receipt receiptPrinter;
    
    private AttendantStation station;
    private Attendant attendant;
    private MaintenanceManager maintenanceManager;
    private IssuePredictor predictor;
	
	public IssuesPredictorTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
	}

	@Before
	public void setUp() {
		basicDefaultSetup();
		
		station = new AttendantStation();
        station.plugIn(powerGrid);
        station.turnOn();
        attendant = new Attendant(station);
        attendant.registerOn(session,scs);
        
        maintenanceManager = new MaintenanceManager();
        attendant.addIssuePrediction(session);
        predictor = attendant.getIssuePredictor(session);

		num = 1;
		numeral = Numeral.valueOf(num);
		digits = new Numeral[] { numeral, numeral, numeral };
		barcode = new Barcode(digits);
		barcode2 = new Barcode(new Numeral[] { numeral });
		product = new BarcodedProduct(barcode, "Sample Product", 10, 100.0);
		product2 = new BarcodedProduct(barcode2, "Sample Product 2", 15, 20.0);
    	
		// Create coin & banknote
		coin = new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal ("2.00"));		
		banknote = new Banknote(Currency.getInstance(Locale.CANADA), new BigDecimal ("5.00"));		

       
	}

	@Test
	public void testCheckLowInk() throws OverloadedDevice {
		// Bronze
		predictor.checkLowInk(session, scs.getPrinter());
		Assert.assertEquals(SessionState.DISABLED, session.getState());

	}

	@Test
	public void testCheckLowInkWhenFull() throws OverloadedDevice {
		// Bronze
		scs.getPrinter().addInk(ReceiptPrinterBronze.MAXIMUM_INK);
		predictor.checkLowInk(session, scs.getPrinter());
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
	}

	@Test
	public void testCheckLowPaper() throws OverloadedDevice {
		// Bronze
		predictor.checkLowPaper(session, scs.getPrinter());
		Assert.assertEquals(SessionState.DISABLED, session.getState());

	}

	@Test
	public void testCheckLowPaperWhenFull() throws OverloadedDevice {
		scs.getPrinter().addPaper(ReceiptPrinterBronze.MAXIMUM_PAPER);
		predictor.checkLowPaper(session, scs.getPrinter());
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());

	}

	@Test
	public void testCheckLowCoins() throws OverloadedDevice, IncorrectDenominationException, ClosedHardwareException, CashOverloadException, NotDisabledSessionException {	
		// No coins added
		predictor.checkLowCoins(session, scs.getCoinDispensers());
		// Should be disabled
		Assert.assertEquals(SessionState.DISABLED, session.getState());
		
		// When coins are added
		for (ICoinDispenser dispenser : scs.getCoinDispensers().values()) {
			for (int i = 0; i < 50; i++) { // Add 50 coins
				dispenser.load(coin);
	       }
		}
		
		predictor.checkLowCoins(session, scs.getCoinDispensers());
		// Should be in PRE_SESSION (no issues)
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
	}
	
	
	@Test
	public void testCheckLowBanknotes() throws OverloadedDevice, CashOverloadException {		
		// No banknotes added
		predictor.checkLowBanknotes(session, scs.getBanknoteDispensers());
		// Should be disabled 
		Assert.assertEquals(SessionState.DISABLED, session.getState());
		
		// When banknotes added
		for (IBanknoteDispenser dispenser : scs.getBanknoteDispensers().values()) {
			for (int i = 0; i < 50; i++) { // Add 50 banknotes
				dispenser.load(banknote);
	       }
		}
		
		predictor.checkLowBanknotes(session, scs.getBanknoteDispensers());
		// Should be in PRE_SESSION (no issues)
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
		

	}

	@Test
	public void testCheckCoinsFull() throws OverloadedDevice, SimulationException, CashOverloadException {
		for (int i = 0; i < 500; i++) { 
	        scs.getCoinStorage().load(new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal ("2.00"))); // Add $2.00 500 times
	        }
		predictor.checkCoinsFull(session, scs.getCoinStorage());
		// Should not be Disabled (no issue)
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
		
		// Add 500 more coins (1000 is max)
		for (int i = 0; i < 500; i++) { 
	        scs.getCoinStorage().load(coin); // Add $2.00 500 times
	        }
		predictor.checkCoinsFull(session, scs.getCoinStorage());
		// Should be Disabled
		Assert.assertEquals(SessionState.DISABLED, session.getState());
	}

	
	@Test
	public void testCheckBanknotesFull() throws OverloadedDevice, SimulationException, CashOverloadException {
		 for (int i = 0; i < 500; i++) { 
		        scs.getBanknoteStorage().load(new Banknote(Currency.getInstance(Locale.CANADA), new BigDecimal ("5.00"))); // Add $5.00 500 times
		        }
		 predictor.checkBanknotesFull(session, scs.getBanknoteStorage());
		// Should not be Disabled (no issues)
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
		
		// Add 500 more Banknotes (1000 is max)
		 for (int i = 0; i < 500; i++) { 
		        scs.getBanknoteStorage().load(banknote); // Add $5.00 500 times
		        }
		 predictor.checkBanknotesFull(session, scs.getBanknoteStorage());
		// Should be Disabled
		Assert.assertEquals(SessionState.DISABLED, session.getState());
	}

	@Test
	public void testNotPreSession() {
		session.start();
		predictor.checkLowInk(session, scs.getPrinter());
		predictor.checkLowPaper(session, scs.getPrinter());
		predictor.checkLowCoins(session, scs.getCoinDispensers());
		predictor.checkLowBanknotes(session, scs.getBanknoteDispensers());
		predictor.checkCoinsFull(session, scs.getCoinStorage());
		predictor.checkBanknotesFull(session, scs.getBanknoteStorage());
		Assert.assertEquals(SessionState.IN_SESSION, session.getState());
	}
	
	
	@Test
	public void testPredictionCheckSessionEnded() throws NotDisabledSessionException {
		session.printReceipt();
		Assert.assertEquals(SessionState.DISABLED, session.getState()); // Disabled since there may be prediction issues
	}
	
	@Test
	public void testPredictionCheckHadwareClosed() throws NotDisabledSessionException {
		session.disable();
		maintenanceManager.openHardware(session);
		maintenanceManager.closeHardware();
		Assert.assertEquals(SessionState.DISABLED, session.getState()); // Disabled since there may be prediction issues
	}
	
	@After
	public void tearDown() {
		predictor = null;
	}
}