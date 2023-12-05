package com.thelocalmarketplace.software.GUI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

import javax.swing.Timer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.GUI.hardware.CashPanel;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.ProductPanel;
import com.thelocalmarketplace.GUI.session.SoftwareGUI;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCash;
import com.thelocalmarketplace.software.membership.Membership;
import com.thelocalmarketplace.software.membership.MembershipDatabase;

import powerutility.PowerGrid;

/**
 * Isolated test cases for Software GUI
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

public class SoftwareGUITest{

	private AbstractSelfCheckoutStation scs;
	private Attendant attendant;
	private AttendantStation as;
	private Session session;
	private SoftwareGUI softwareGUI;
	private HardwareGUI hardwaregui;
	
	private CashPanel cashpanel;
	private CoinValidator coinValidator;
	private BanknoteValidator banknoteValidator;
	private CoinValidator validator;
	private PayByCash cashController;
	private Funds funds;
	
	
	private BarcodedProduct product;
	private Barcode barcode;
	private BarcodedItem item;
	private BarcodedItem item2;
	
	Robot robot;
	Timer timer;
	int runs = 0;
	private PriceLookUpCode plu1;
	private PLUCodedProduct pluProduct1;
	
	@Before
	public void setup() {
		barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		product = new BarcodedProduct(barcode, "Some product", 10, 20.0);
		SelfCheckoutStationLogic.populateDatabase(barcode, product, 20);
		
		plu1 = new PriceLookUpCode(new String("0000"));
		pluProduct1 = new PLUCodedProduct(plu1, "baaananas", 10);
		SelfCheckoutStationLogic.populateDatabase(plu1, pluProduct1, 10);
		
		MembershipDatabase.registerMember("0", "name");
		
		scs = new SelfCheckoutStationGold();
		as = new AttendantStation();
		PowerGrid.engageUninterruptiblePowerSource();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		SelfCheckoutStationLogic.installAttendantStation(as);
		SelfCheckoutStationLogic logic = SelfCheckoutStationLogic.installOn(scs);
		session = logic.getSession();
		softwareGUI = new SoftwareGUI(session);
		hardwaregui = new HardwareGUI(scs, as);
		
		//cash panel stuff
		cashpanel = new CashPanel(scs);
		
		funds = new Funds(scs);

		coinValidator = scs.getCoinValidator();
		banknoteValidator = scs.getBanknoteValidator();

		cashController = new PayByCash(coinValidator, banknoteValidator, funds);
		
		scs.getScreen().setVisible(true);
		

		item = new BarcodedItem(barcode, new Mass(20.0));
		item2 = new BarcodedItem(barcode, new Mass(20.0));
		
		
		
		try {
			robot = new Robot();

		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		runs=0;
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				runs +=1;
				if(runs>20) {
					timer.stop();
				}
			}
			
		});
		timer.start();
	}
	
	@After
	public void teardown() {
		Window[] windows = Window.getWindows();
		for(Window window: windows) {
			window.dispose();
		}
		timer.stop();
	}
	
	
	
	@Test
	public void testStart() {
		assertTrue(softwareGUI.frame.isVisible());
		assertTrue(softwareGUI.frame.getContentPane().getComponent(0) == softwareGUI.startPane);
		assertTrue(session.getState() == SessionState.PRE_SESSION);
	}
	
	@Test
	public void testStartButton() {
		softwareGUI.btnStart.doClick();
		assertFalse(softwareGUI.frame.getContentPane().getComponent(0) == softwareGUI.startPane);
		assertTrue(softwareGUI.frame.getContentPane().getComponent(0) == softwareGUI.mainPane);
		assertTrue(session.getState() == SessionState.IN_SESSION);
	}
	
	@Test
	public void testPayButton() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		assertTrue(softwareGUI.paymentScreen.isVisible());
	}
	
	@Test
	public void testAddItem() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		assertTrue(softwareGUI.cartItemsPanel.contains(product));
		assertEquals("$10.00", softwareGUI.cartTotalInDollars.getText());
		assertEquals("20.00g", softwareGUI.infoWeightNumber.getText());
	}
	
	@Test
	public void testAdd2Items() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		scs.getMainScanner().scan(item2);
		assertTrue(softwareGUI.cartItemsPanel.contains(product));
		assertEquals("$20.00", softwareGUI.cartTotalInDollars.getText());
		assertEquals("40.00g", softwareGUI.infoWeightNumber.getText());
	}
	
	@Test
	
	public void testAddItemDoPayCash() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		assertTrue(session.getState() == SessionState.PAY_BY_CASH);
	}
	
	@Test
	public void testAddItemDoPayCard() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCardButton().doClick();
		assertTrue(session.getState() == SessionState.PAY_BY_CARD);
	}
	
	@Test
	public void testOpenCatalog() {
		softwareGUI.btnStart.doClick();
		softwareGUI.searchCatalogue.doClick();
		assertTrue(softwareGUI.catalogue.isVisible());
	}
	
	@Test
	public void testUpdate() {
		assertTrue(false);
		
	}
	
	@Test
	public void testOpenAddBags() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		assertTrue(softwareGUI.addBagsScreen.isVisible());
	}
	
	@Test
	public void testAddPersonalBags() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddPersonalBagButton().doClick();
		assertTrue(session.getState() == SessionState.ADDING_BAGS);
	}
	
	@Test
	public void testOpenAddStoreBag() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().isVisible());

	}
	
	@Test
	public void testAddStoreBagOne() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getOne().doClick();
		Assert.assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("1"));

	}
	
	@Test
	public void testAddStoreBagTwo() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getTwo().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("2"));

	}
	
	@Test
	public void testAddStoreBagThree() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getThree().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("3"));

	}
	
	@Test
	public void testAddStoreBagFour() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getFour().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("4"));

	}
	
	@Test
	public void testAddStoreBagFive() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getFive().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("5"));

	}
	
	@Test
	public void testAddStoreBagSix() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getSix().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("6"));

	}
	
	@Test
	public void testAddStoreBagSeven() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getSeven().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("7"));

	}
	
	@Test
	public void testAddStoreBagEight() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getEight().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("8"));

	}
	
	@Test
	public void testAddStoreBagNine() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getNine().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("9"));

	}
	
	@Test
	public void testAddStoreBagZero() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getZero().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("0"));

	}
	
	@Test
	public void testAddStoreBagDelete() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getOne().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getDelete().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals(""));

	}
	
	@Test 
	public void testAddStoreBagDone() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getDone().doClick();
		//Where to check for bags 
		assertTrue(false);

	}
	
	@Test
	public void testOpenAddBagsClose() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getCancelButton().doClick();
		assertFalse(softwareGUI.addBagsScreen.getNumOfBagsScreen().isVisible());

	}
	
	@Test
	public void testAddPLUOpen() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		assertTrue(softwareGUI.pluNumPad.isVisible());
		
	}
	
	@Test
	public void testAddPLUCodeOne() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getOne().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("1"));
		
	}
	
	@Test
	public void testAddPLUCodeTwo() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getTwo().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("2"));

	}
	
	@Test
	public void testAddPLUCodeThree() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getThree().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("3"));

	}
	
	@Test
	public void testAddPLUCodeFour() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getFour().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("4"));

	}
	
	@Test
	public void testAddPLUCodeFive() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getFive().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("5"));

	}
	
	@Test
	public void testAddPLUCodeSix() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getSix().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("6"));

	}
	
	@Test
	public void testAddPLUCodeSeven() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getSeven().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("7"));

	}
	
	@Test
	public void testAddPLUCodeEight() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getEight().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("8"));

	}
	
	@Test
	public void testAddPLUCodeNine() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getNine().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("9"));

	}
	
	@Test
	public void testAddPLUCodeZero() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("0"));


	}
	
	@Test
	public void testAddPLUCodeDelete() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getOne().doClick();
		softwareGUI.pluNumPad.getDelete().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals(""));

	}
	
	@Test
	public void testAddPLUCodeDone() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getOne().doClick();
		softwareGUI.pluNumPad.getDone().doClick();
		assertTrue(session.getState() != SessionState.ADD_PLU_ITEM);
	}
	
//Testing 0000	
	@Test
	public void testAddPLUValid() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getDone().doClick();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		
		assertTrue(session.getState() == SessionState.ADD_PLU_ITEM);

		
	}
	
	@Test
	public void testAddPLUInvalid() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getTwo().doClick();
		softwareGUI.pluNumPad.getDone().doClick();
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		assertTrue(session.getState() != SessionState.ADD_PLU_ITEM);

		
	}

	
	@Test
	public void testCashCannotPayForEmpty() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton();
		assertTrue(session.getState() == SessionState.IN_SESSION);

	}
	
	@Test
	public void testCardCannotPayForEmpty() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCardButton();
		assertTrue(session.getState() == SessionState.IN_SESSION);
	}
	
	@Test 
	public void testMembershipButton() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().isVisible());
	}
	
	@Test
	public void testMembershipOne() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getOne().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("1"));
		
	}
	
	@Test
	public void testMembershipTwo() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getTwo().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("2"));

	}
	
	@Test
	public void testMembershipThree() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getThree().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("3"));

	}
	
	@Test
	public void testMembershipFour() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getFour().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("4"));

	}
	
	@Test
	public void testMembershipFive() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getFive().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("5"));

	}
	
	@Test
	public void testMembershipSix() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getSix().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("6"));

	}
	
	@Test
	public void testMembershipSeven() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getSeven().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("7"));

	}
	
	@Test
	public void testMembershipEight() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getEight().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("8"));

	}
	
	@Test
	public void testMembershipNine() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getNine().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("9"));

	}
	
	@Test
	public void testMembershipZero() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getZero().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("0"));

	}
	
	@Test
	public void testMembershipDelete() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getZero().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getDelete().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals(""));

	}
	
	@Test
	public void testMembershipDone() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getZero().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getDone().doClick();
		//assertTrue(session.getMembershipNumber().equals("0"));
		assertTrue(false);

	}
	
	@Test
	public void testMembershipValid() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		//assertTrue(session.getMembershipNumber().equals("0"));

	} 
	
	@Test 
	public void testSearchCatalogueAddItem() {
		softwareGUI.btnStart.doClick();
		softwareGUI.searchCatalogue.doClick();
		softwareGUI.catalogue.getHashMapForButtons().keySet();
		
		for (Product product: softwareGUI.catalogue.getHashMapForButtons().keySet()) {
			
			ProductPanel panel = softwareGUI.catalogue.getHashMapForButtons().get(product);
			
			panel.getAddButton().doClick();
			
		assertEquals("1", softwareGUI.itemAmount.getText());
		}
		
	}
	
}
