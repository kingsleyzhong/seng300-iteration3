package com.thelocalmarketplace.software.GUI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
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

import powerutility.PowerGrid;

public class SoftwareGUITest{

	private AbstractSelfCheckoutStation scs;
	private Attendant attendant;
	private AttendantStation as;
	private Session session;
	private SoftwareGUI softwareGUI;
	
	private BarcodedProduct product;
	private Barcode barcode;
	private BarcodedItem item;
	private BarcodedItem item2;
	
	@Before
	public void setup() {
		scs = new SelfCheckoutStationGold();
		as = new AttendantStation();
		PowerGrid.engageUninterruptiblePowerSource();
		scs.plugIn(PowerGrid.instance());
		scs.turnOn();
		SelfCheckoutStationLogic.installAttendantStation(as);
		SelfCheckoutStationLogic logic = SelfCheckoutStationLogic.installOn(scs);
		session = logic.getSession();
		softwareGUI = new SoftwareGUI(session);
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
