package com.thelocalmarketplace.software.GUI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

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
import com.thelocalmarketplace.GUI.session.SoftwareGUI;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCash;

import powerutility.PowerGrid;

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
	
	@Before
	public void setup() {
		fuck
		scs = new SelfCheckoutStationGold();
		as = new AttendantStation();
		PowerGrid.engageUninterruptiblePowerSource();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		SelfCheckoutStationLogic.installAttendantStation(as);
		SelfCheckoutStationLogic logic = SelfCheckoutStationLogic.installOn(scs);
		session = logic.getSession();
		softwareGUI = new SoftwareGUI(session);
		hardwaregui = new HardwareGUI(scs);
		
		//cash panel stuff
		cashpanel = new CashPanel(scs);
		
		funds = new Funds(scs);

		coinValidator = scs.getCoinValidator();
		banknoteValidator = scs.getBanknoteValidator();

		cashController = new PayByCash(coinValidator, banknoteValidator, funds);
		
		
		
		
		
		scs.getScreen().setVisible(true);
		
		barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		product = new BarcodedProduct(barcode, "Some product", 10, 20.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);

		item = new BarcodedItem(barcode, new Mass(20.0));
		item2 = new BarcodedItem(barcode, new Mass(20.0));
	}
	
	@After
	public void teardown() {
		scs.getScreen().getFrame().dispose();
	}
	
	
	@Test
	public void testPayWithBill() {
		softwareGUI.btnStart.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		
		scs.getMainScanner().scan(item2);
		scs.getBaggingArea().addAnItem(item2);
		
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		
		cashpanel.FiveBillBtn.doClick();
		cashpanel.FiveBillBtn.doClick();
		
		Assert.assertEquals(BigDecimal.TEN , cashController.getCashPaid());
		
	}
	
	@Test
	public void testPayWithCoin() {
		softwareGUI.btnStart.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		cashpanel.button_one_coin.doClick();
		
		//IDK WHY IT WONT TAKE MY MONEY HERE!!!!!!!!!!!!!!
		Assert.assertEquals(BigDecimal.ONE , cashController.getCashPaid());
		
		
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
	
	

	
	
	public void testAddItemDoPayCard() {
		softwareGUI.btnStart.doClick();
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCardButton().doClick();
		assertTrue(session.getState() == SessionState.PAY_BY_CARD);
	}
}
