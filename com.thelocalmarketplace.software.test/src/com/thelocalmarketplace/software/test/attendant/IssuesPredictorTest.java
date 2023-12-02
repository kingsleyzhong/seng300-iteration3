package com.thelocalmarketplace.software.test.attendant;

import java.math.BigDecimal;

import java.util.*;

import org.junit.*;
import com.jjjwelectronics.*;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterBronze;
import com.jjjwelectronics.printer.ReceiptPrinterGold;
import com.jjjwelectronics.printer.ReceiptPrinterSilver;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.*;
import com.thelocalmarketplace.software.*;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.exceptions.ClosedHardwareException;
import com.thelocalmarketplace.software.exceptions.IncorrectDenominationException;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
import com.thelocalmarketplace.software.test.AbstractSessionTest;

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

public class IssuesPredictorTest extends AbstractSessionTest{
	
	private IssuePredictor issuePredictor;
	private PowerGrid powerGrid;
	private IReceiptPrinter printer;
	private ReceiptPrinterSilver silverPrinter;
	private ReceiptPrinterBronze bronzePrinter;
	private ReceiptPrinterGold goldPrinter;
	private Object mm;

	public IssuesPredictorTest(String testName, AbstractSelfCheckoutStation scs) {
		super(testName, scs);
	}
	
	
	@Before
	public void setUp() {
		basicDefaultSetup();
		
		Attendant as = new Attendant(new AttendantStation());
		// create issuePredictor instance
        issuePredictor = new IssuePredictor(session, scs, receiptPrinter);
        as.addIssuePrediction(issuePredictor);
        
		// Create power source
		PowerGrid.engageUninterruptiblePowerSource();
    	powerGrid = PowerGrid.instance();
    	
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
		
		// Maintenance Manager
		mm = new MaintenanceManager();
       
	}
	
	@Test 
	public void testCheckLowInk() throws OverloadedDevice {
		// Bronze
		issuePredictor.checkLowInk(session, scs.getPrinter());
		Assert.assertEquals(SessionState.DISABLED, session.getState());

		// Silver
		session.enable();
		silverPrinter.addInk(100000);
		issuePredictor.checkLowInk(session, silverPrinter);
		Assert.assertEquals(SessionState.DISABLED, session.getState());

		// Gold
		session.enable();
		goldPrinter.addInk(100000);
		issuePredictor.checkLowInk(session, goldPrinter);
		Assert.assertEquals(SessionState.DISABLED, session.getState());

	}
	
	@Test
	public void testCheckLowInkWhenFull() throws OverloadedDevice {
		// Bronze
		bronzePrinter.addInk(bronzePrinter.MAXIMUM_INK);
		issuePredictor.checkLowInk(session, scs.getPrinter());
		//Assert.assertEquals(SessionState.PRE_SESSION, session.getState());

		// Silver
		session.enable();
		silverPrinter.addInk(silverPrinter.MAXIMUM_INK);
		issuePredictor.checkLowInk(session, silverPrinter);
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());

		// Gold
		session.enable();
		goldPrinter.addInk(goldPrinter.MAXIMUM_INK);
		issuePredictor.checkLowInk(session, goldPrinter);
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
	}
	
	@Test
	public void testCheckLowPaper() throws OverloadedDevice {
		// Bronze
		issuePredictor.checkLowPaper(session, scs.getPrinter());
		Assert.assertEquals(SessionState.DISABLED, session.getState());

		// Silver
		session.enable();
		silverPrinter.addPaper(100);
		issuePredictor.checkLowPaper(session, silverPrinter);
		Assert.assertEquals(SessionState.DISABLED, session.getState());

		// Gold
		session.enable();
		goldPrinter.addPaper(100);
		issuePredictor.checkLowPaper(session, goldPrinter);
		Assert.assertEquals(SessionState.DISABLED, session.getState());

	}
	
	@Test
	public void testCheckLowPaperWhenFull() throws OverloadedDevice {
		// Bronze
		bronzePrinter.addPaper(bronzePrinter.MAXIMUM_PAPER);
		issuePredictor.checkLowPaper(session, scs.getPrinter());
		//Assert.assertEquals(SessionState.PRE_SESSION, session.getState());

		// Silver
		session.enable();
		silverPrinter.addPaper(silverPrinter.MAXIMUM_PAPER);
		issuePredictor.checkLowPaper(session, silverPrinter);
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());

		// Gold
		session.enable();
		goldPrinter.addPaper(goldPrinter.MAXIMUM_PAPER);
		issuePredictor.checkLowPaper(session, goldPrinter);
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());

	}
	
	@Test
	public void testCheckLowCoins() throws OverloadedDevice {		
		issuePredictor.checkLowCoins(session, scs.getCoinDispensers());
		Assert.assertEquals(SessionState.DISABLED, session.getState());

	}
	
	@Test
	public void testCheckLowCoinsNormal() throws IncorrectDenominationException, ClosedHardwareException, CashOverloadException, NotDisabledSessionException {
		ICoinDispenser tempDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
		Coin coin = new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal ("2.00"));
		session.disable();
        ((MaintenanceManager) mm).openHardware(session);
        for (int i = 0; i < 500; i++) { // 1000 is max capacity
        	((MaintenanceManager) mm).addCoins(new BigDecimal(0.05), coin);
        }
        
        ((MaintenanceManager) mm).closeHardware();
		
		issuePredictor.checkLowCoins(session, scs.getCoinDispensers());
		Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
	}
	
	
	@Test
	public void testCheckLowBanknotes() throws OverloadedDevice {		
		issuePredictor.checkLowBanknotes(session, scs.getBanknoteDispensers());
		Assert.assertEquals(SessionState.DISABLED, session.getState());

	}
	
	@Test
	public void testCheckCoinsFull() throws OverloadedDevice, SimulationException, CashOverloadException {
	    for (int i = 0; i < 1000; i++) { // 1000 is max capacity
	        scs.getCoinStorage().load(new Coin(Currency.getInstance(Locale.CANADA), new BigDecimal ("2.00"))); // Add $2.00 1000 times
	        }
		issuePredictor.checkCoinsFull(session, scs.getCoinStorage());
		Assert.assertEquals(SessionState.DISABLED, session.getState());


	}
	
	@Test
	public void testCheckBanknotesFull() throws OverloadedDevice, SimulationException, CashOverloadException {
		 for (int i = 0; i < 1000; i++) { // 1000 is max capacity
		        scs.getBanknoteStorage().load(new Banknote(Currency.getInstance(Locale.CANADA), new BigDecimal ("5.00"))); // Add $5.00 1000 times
		        }
		issuePredictor.checkBanknotesFull(session, scs.getBanknoteStorage());
		Assert.assertEquals(SessionState.DISABLED, session.getState());

	}
	
	@Test
	public void testNotPreSession() {
		session.start();
		issuePredictor.checkLowInk(session, bronzePrinter);
		issuePredictor.checkLowPaper(session, bronzePrinter);
		issuePredictor.checkLowCoins(session, scs.getCoinDispensers());
		issuePredictor.checkLowBanknotes(session, scs.getBanknoteDispensers());
		issuePredictor.checkCoinsFull(session, scs.getCoinStorage());
		issuePredictor.checkBanknotesFull(session, scs.getBanknoteStorage());
		Assert.assertEquals(SessionState.IN_SESSION, session.getState());
	}
	
}