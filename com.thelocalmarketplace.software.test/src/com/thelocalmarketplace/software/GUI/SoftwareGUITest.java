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
import java.util.Currency;
import java.util.Locale;

import javax.swing.JDialog;
import javax.swing.Timer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.GUI.attendant.AttendantGUI;
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
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
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
	private MaintenanceManager manager;
	private IssuePredictor predictor;
	private HardwareGUI hardwareGUI;
	private AttendantGUI attendantGUI;
	
	
	private CoinValidator coinValidator;
	private BanknoteValidator banknoteValidator;
	private CoinValidator validator;
	private Funds funds;
	private Coin coin;
	
	private BarcodedProduct product;
	private Barcode barcode;
	private BarcodedItem item;
	private BarcodedItem item2;	
	
	private PriceLookUpCode plu1;
	private PLUCodedProduct pluProduct1;
	
	
	Robot robot;
	Timer timer;
	int runs = 0;



	
	@Before
	public void setup() throws NotDisabledSessionException {
		barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		product = new BarcodedProduct(barcode, "Some product", 1, 20.0);
		SelfCheckoutStationLogic.populateDatabase(barcode, product, 20);
		
		plu1 = new PriceLookUpCode(new String("0000"));
		pluProduct1 = new PLUCodedProduct(plu1, "Another product", 1);
		SelfCheckoutStationLogic.populateDatabase(plu1, pluProduct1, 1);
		
		item = new BarcodedItem(barcode, new Mass(20.0));
		item2 = new BarcodedItem(barcode, new Mass(20.0));
		scs = new SelfCheckoutStationBronze();
		as = new AttendantStation();
		
		PowerGrid.engageUninterruptiblePowerSource();
		scs.plugIn(PowerGrid.instance());
		as.plugIn(PowerGrid.instance());
		scs.turnOn();
		as.turnOn();
		
		SelfCheckoutStationLogic.installAttendantStation(as);
		attendant = SelfCheckoutStationLogic.getAttendant();
		
		SelfCheckoutStationLogic logic = SelfCheckoutStationLogic.installOn(scs);
		session = logic.getSession();
		session.getStation().setSupervisor(as);
		
		session.disable();
		manager = new MaintenanceManager();
		manager.openHardware(session);
		
		predictor = attendant.getIssuePredictor(session);
		
		hardwareGUI = new HardwareGUI(scs, as);
		attendantGUI = new AttendantGUI(attendant, manager, predictor);
		softwareGUI = new SoftwareGUI(session);
		
		funds = new Funds(scs);

		coinValidator = scs.getCoinValidator();
		banknoteValidator = scs.getBanknoteValidator();
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		coin = new Coin(currency, new BigDecimal(1));
		
		scs.getScreen().setVisible(true);
		
		session.enable();
		hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.buttonPanel.sessionScreen.doClick();

		
		
/***
 * Robot made for pressing the key "enter" for cases of pop ups		
 */
		runs=0;
		timer = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Window[] frames = JDialog.getWindows();
			    for (Window frame : frames)
			      if(frame.getClass() == JDialog.class) {
			    	  frame.dispose();
			      }

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
		
		while (Window.getWindows().length == 0) {
			for(Window window: windows) {
				window.dispose();
			}
			timer.stop();
		}
	}
	

	@Test
	public void preSession() {
		assertTrue(softwareGUI.frame.isVisible());
		assertTrue(softwareGUI.frame.getContentPane().getComponent(0) == softwareGUI.startPane);
		assertTrue(session.getState() == SessionState.PRE_SESSION);
	}
	
	@Test
	public void startButton() {
		softwareGUI.btnStart.doClick();
		assertFalse(softwareGUI.frame.getContentPane().getComponent(0) == softwareGUI.startPane);
		assertTrue(softwareGUI.frame.getContentPane().getComponent(0) == softwareGUI.mainPane);
		assertTrue(softwareGUI.displayingStart);
	}
	
	@Test 
	public void openHardwareScreen() {
		softwareGUI.btnStart.doClick();
		softwareGUI.hardwareButton.doClick();
		assertTrue(HardwareGUI.hardwareFrame.isVisible());
	}
	
	
	@Test
	public void openAttendantScreen() {
		softwareGUI.btnStart.doClick();
		softwareGUI.attendantButton.doClick();
		assertTrue(attendantGUI.isVisibile());
	}
	
	@Test
	public void cancelSession() {
		softwareGUI.btnStart.doClick();
		softwareGUI.cancel.doClick();
		assertTrue(session.getState() == SessionState.PRE_SESSION);
	}
	
	@Test 
	public void displayDisabled() {
		session.disable();
		softwareGUI.btnStart.doClick();
		assertTrue(softwareGUI.displayingDisabled);	
		
	}
	
	@Test
	public void callAttendantButton() {
		softwareGUI.btnStart.doClick();
		softwareGUI.callAttendant.doClick();
		assertTrue(attendant.getCurrentRequest(session) == Requests.HELP_REQUESTED);	
	}
	
	@Test
	public void payButton() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		assertTrue(softwareGUI.paymentScreen.isVisible());
	}
	
	@Test
	public void addingItem() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		assertTrue(softwareGUI.cartItemsPanel.contains(product));
		assertEquals("$1.00", softwareGUI.cartTotalInDollars.getText());
		assertEquals("20.00g", softwareGUI.infoWeightNumber.getText());
	}
	
	@Test
	public void adding2Items() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		scs.getMainScanner().scan(item2);
		assertTrue(softwareGUI.cartItemsPanel.contains(product));
		assertEquals("$2.00", softwareGUI.cartTotalInDollars.getText());
		assertEquals("40.00g", softwareGUI.infoWeightNumber.getText());
	}
	
	@Test
	public void addItemDoPayCash() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		assertTrue(session.getState() == SessionState.PAY_BY_CASH);
	}
	
	@Test
	public void addItemDoPayCard() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCardButton().doClick();
		assertTrue(session.getState() == SessionState.PAY_BY_CARD);
	}
	
	@Test
	public void openCatalog() {
		softwareGUI.btnStart.doClick();
		softwareGUI.searchCatalogue.doClick();
		assertTrue(softwareGUI.catalogue.isVisible());
	}
	
	@Test 
	public void searchCatalogueAddItem() {
		softwareGUI.btnStart.doClick();
		softwareGUI.searchCatalogue.doClick();
		softwareGUI.catalogue.getHashMapForButtons().keySet();
		
		for (Product product: softwareGUI.catalogue.getHashMapForButtons().keySet()) {
			
			ProductPanel panel = softwareGUI.catalogue.getHashMapForButtons().get(product);
			
			panel.getAddButton().doClick();
			
		assertEquals("1", softwareGUI.itemAmount.getText());
		}
		
	}
	
	@Test
	public void openAddBags() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		assertTrue(softwareGUI.addBagsScreen.isVisible());
	}
	
	@Test
	public void addPersonalBags() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddPersonalBagButton().doClick();
		assertTrue(session.getState() == SessionState.ADDING_BAGS);
	}
	
	@Test
	public void openAddStoreBag() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().isVisible());

	}
	
	@Test
	public void addStoreBagOne() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getOne().doClick();
		Assert.assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("1"));

	}
	
	@Test
	public void addStoreBagTwo() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getTwo().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("2"));

	}
	
	@Test
	public void addStoreBagThree() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getThree().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("3"));

	}
	
	@Test
	public void addStoreBagFour() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getFour().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("4"));

	}
	
	@Test
	public void addStoreBagFive() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getFive().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("5"));

	}
	
	@Test
	public void addStoreBagSix() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getSix().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("6"));

	}
	
	@Test
	public void addStoreBagSeven() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getSeven().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("7"));

	}
	
	@Test
	public void addStoreBagEight() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getEight().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("8"));

	}
	
	@Test
	public void addStoreBagNine() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getNine().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("9"));

	}
	
	@Test
	public void addStoreBagZero() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getZero().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals("0"));

	}
	
	@Test
	public void addStoreBagDelete() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getOne().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getDelete().doClick();
		assertTrue(softwareGUI.addBagsScreen.getNumOfBagsScreen().getNumber().equals(""));

	}
	
	@Test 
	public void addStoreBagDone() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getAddStoreBagButton().doClick();
		softwareGUI.addBagsScreen.getNumOfBagsScreen().getDone().doClick();
		//Where to check for bags 
		assertTrue(false);

	}
	
	@Test
	public void openAddBagsClose() {
		softwareGUI.btnStart.doClick();
		softwareGUI.addBags.doClick();
		softwareGUI.addBagsScreen.getCancelButton().doClick();
		assertFalse(softwareGUI.addBagsScreen.getNumOfBagsScreen().isVisible());

	}
	
	@Test
	public void addPLUOpen() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		assertTrue(softwareGUI.pluNumPad.isVisible());
		
	}
	
	@Test
	public void addPLUCodeOne() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getOne().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("1"));
		
	}
	
	@Test
	public void addPLUCodeTwo() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getTwo().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("2"));

	}
	
	@Test
	public void addPLUCodeThree() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getThree().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("3"));

	}
	
	@Test
	public void addPLUCodeFour() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getFour().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("4"));

	}
	
	@Test
	public void addPLUCodeFive() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getFive().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("5"));

	}
	
	@Test
	public void addPLUCodeSix() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getSix().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("6"));

	}
	
	@Test
	public void addPLUCodeSeven() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getSeven().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("7"));

	}
	
	@Test
	public void addPLUCodeEight() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getEight().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("8"));

	}
	
	@Test
	public void addPLUCodeNine() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getNine().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("9"));

	}
	
	@Test
	public void addPLUCodeZero() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals("0"));


	}
	
	@Test
	public void addPLUCodeDelete() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getOne().doClick();
		softwareGUI.pluNumPad.getDelete().doClick();
		assertTrue(softwareGUI.pluNumPad.getPlu().equals(""));

	}
	
	@Test
	public void addPLUCodeDone() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getOne().doClick();
		softwareGUI.pluNumPad.getDone().doClick();
		assertTrue(session.getState() != SessionState.ADD_PLU_ITEM);
	}
	
//Testing 0000	
	@Test
	public void addPLUValid() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pluCode.doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getZero().doClick();
		softwareGUI.pluNumPad.getDone().doClick();
		
		assertTrue(session.getState() == SessionState.ADD_PLU_ITEM);

		
	}
	
	@Test
	public void addPLUInvalid() {
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
	public void cashCannotPayForEmpty() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton();
		assertTrue(session.getState() == SessionState.IN_SESSION);

	}
	
	@Test
	public void cardCannotPayForEmpty() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCardButton();
		assertTrue(session.getState() == SessionState.IN_SESSION);
	}
	
	@Test 
	public void membershipButton() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().isVisible());
	}
	
	@Test
	public void membershipOne() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getOne().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("1"));
		
	}
	
	@Test
	public void membershipTwo() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getTwo().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("2"));

	}
	
	@Test
	public void membershipThree() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getThree().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("3"));

	}
	
	@Test
	public void membershipFour() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getFour().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("4"));

	}
	
	@Test
	public void membershipFive() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getFive().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("5"));

	}
	
	@Test
	public void membershipSix() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getSix().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("6"));

	}
	
	@Test
	public void membershipSeven() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getSeven().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("7"));

	}
	
	@Test
	public void membershipEight() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getEight().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("8"));

	}
	
	@Test
	public void membershipNine() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getNine().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("9"));

	}
	
	@Test
	public void membershipZero() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getZero().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals("0"));

	}
	
	@Test
	public void membershipDelete() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getZero().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getDelete().doClick();
		assertTrue(softwareGUI.paymentScreen.getMembershipPad().getNumber().equals(""));

	}
	
	@Test
	public void membershipDone() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getZero().doClick();
		softwareGUI.paymentScreen.getMembershipPad().getDone().doClick();
		assertTrue(session.getMembershipNumber().equals("0"));
		assertTrue(false);

	}
	
	@Test
	public void membershipValid() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getMembershipButton().doClick();
		assertTrue(session.getMembershipNumber().equals("0"));

	} 
	
	@Test
	public void paymentNotValid() {
		softwareGUI.btnStart.doClick();
		softwareGUI.pay.doClick();
		assertTrue(session.getState() == SessionState.IN_SESSION);
	}

	@Test 
	public void endScreen() throws DisabledException, CashOverloadException {
		
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);

		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		coinValidator.receive(coin);
		
		assertTrue(softwareGUI.displayingEnd);
	
	}
	
	@Test 
	public void hideScreen() {
		
		softwareGUI.hide();
		assertFalse(softwareGUI.frame.isVisible());
	}
	
	@Test 
	public void unhideScreen() {
		
		softwareGUI.unhide();
		assertTrue(softwareGUI.frame.isVisible());
	}
	
	
	@Test 
	public void removingAProduct() {
		
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		
		softwareGUI.cartItemsPanel.getNewPanel().getMinusButton();
		
		assertTrue(softwareGUI.itemAmount.getText().equals("0"));
		
	}
	
	@Test 
	public void blockedSessionTryAddBags() {
		
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);

		softwareGUI.addBags.doClick();
		assertTrue(session.getState() == SessionState.BLOCKED);
		
	}
	
	@Test 
	public void blockedSessionTrySearchCatalogue() {
		
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);

		softwareGUI.searchCatalogue.doClick();
		assertTrue(session.getState() == SessionState.BLOCKED);
		
	}
	
	@Test 
	public void blockedSessionTryPLU() {
		
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);

		softwareGUI.pluCode.doClick();
		assertTrue(session.getState() == SessionState.BLOCKED);
		
	}
	
	@Test 
	public void blockedSessionTryCancel() {
		
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);

		softwareGUI.cancel.doClick();
		assertTrue(session.getState() == SessionState.BLOCKED);
		
	}
	
	
	
}
