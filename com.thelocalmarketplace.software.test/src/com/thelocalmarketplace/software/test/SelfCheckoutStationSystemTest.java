package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.MagneticStripeFailureException;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.CardIssuerDatabase;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.SupportedCardIssuers;
import com.thelocalmarketplace.software.weight.Weight;

import powerutility.PowerGrid;

/**
 * Unit test for the integration of software for selfCheckoutStation with
 * corresponding hardware
 * Contains tests for:
 * Start session
 * Scan item
 * Pay by coin
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

public class SelfCheckoutStationSystemTest extends AbstractTest {
	private SelfCheckoutStationLogic logic;
	private AttendantStation station;

	private Session session;

	private BarcodedProduct product;
	private Barcode barcode;
	private BarcodedItem item;
	private BarcodedItem item2;

	private Currency cad = Currency.getInstance(Locale.CANADA);

	private Coin nickel;
	private Coin dime;
	private Coin quarter;
	private Coin dollar;

	private Banknote five;
	private Banknote ten;
	private Banknote twenty;

	public SelfCheckoutStationSystemTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
	}

	@Before
	public void setup() {
		basicDefaultSetup();

		AbstractSelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] { new BigDecimal(20),
				new BigDecimal(10), new BigDecimal(5) });
		AbstractSelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { BigDecimal.ONE, new BigDecimal(0.25),
				new BigDecimal(0.10), new BigDecimal(0.05) });

		station = new AttendantStation();
		SelfCheckoutStationLogic.installAttendantStation(station);
		logic = SelfCheckoutStationLogic.installOn(scs);
		session = logic.getSession();

		// Populate database
		barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
		product = new BarcodedProduct(barcode, "Some product", 10, 20.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);

		item = new BarcodedItem(barcode, new Mass(20.0));
		item2 = new BarcodedItem(barcode, new Mass(20.0));

		Coin.DEFAULT_CURRENCY = Currency.getInstance(Locale.CANADA);
		dollar = new Coin(BigDecimal.ONE);
		quarter = new Coin(new BigDecimal(0.25));
		dime = new Coin(new BigDecimal(0.10));
		nickel = new Coin(new BigDecimal(0.05));

		five = new Banknote(cad, new BigDecimal(5));
		ten = new Banknote(cad, new BigDecimal(10));
		twenty = new Banknote(cad, new BigDecimal(20));
	}

	// Tests for start Session requirement use case
	@Test
	public void testInitialConfiguration() {
		assertEquals(session.getState(), SessionState.PRE_SESSION);
		assertFalse(session.getState().inPay());
	}

	@Test
	public void testStartSession() {
		session.start();
		assertEquals(session.getState(), SessionState.IN_SESSION);
		assertFalse(session.getState().inPay());
	}

	// Tests for scan an Item requirement use case

	@Test
	public void testScanAnItem() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		HashMap<BarcodedProduct, Integer> items = session.getBarcodedItems();
		assertTrue("The barcoded product associated with the barcode has been added", items.containsKey(product));
	}

	@Test
	public void testScanAnItemFunds() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		Funds funds = session.getFunds();
		BigDecimal expected = new BigDecimal(10);
		BigDecimal actual = funds.getItemsPrice();
		assertEquals("Funds has correct amount", expected, actual);
	}

	@Test
	public void testScanAnItemWeight() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		Weight weight = session.getWeight();
		Mass expected = new Mass(20.0);
		Mass actual = weight.getExpectedWeight();
		assertEquals("Weight has correct amount", expected, actual);
	}

	// Tests for add Item via handheldScan
	@Test
	public void testHandheldScanAnItem() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getHandheldScanner().scan(item);
		}
		HashMap<BarcodedProduct, Integer> items = session.getBarcodedItems();
		assertTrue("The barcoded product associated with the barcode has been added", items.containsKey(product));
	}

	@Test
	public void testHandheldScanAnItemFunds() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getHandheldScanner().scan(item);
		}
		Funds funds = session.getFunds();
		BigDecimal expected = new BigDecimal(10);
		BigDecimal actual = funds.getItemsPrice();
		assertEquals("Funds has correct amount", expected, actual);
	}

	@Test
	public void testHandheldScanAnItemWeight() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getHandheldScanner().scan(item);
		}
		Weight weight = session.getWeight();
		Mass expected = new Mass(20.0);
		Mass actual = weight.getExpectedWeight();
		assertEquals("Weight has correct amount", expected, actual);
	}

	// Tests for pay via coin

	@Test(expected = InvalidActionException.class)
	public void enterPayWhenCartEmpty() {
		session.start();
		session.payByCash();
	}

	@Test(expected = InvalidActionException.class)
	public void addCoinWhenNotInPay() throws DisabledException, CashOverloadException {
		session.start();
		scs.getCoinSlot().receive(dollar);
	}

	@Test
	public void payForItemViaCash() throws DisabledException, CashOverloadException {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		scs.getBaggingArea().addAnItem(item);
		session.payByCash();
		int count = 0;
		while (count < 10) {
			scs.getCoinSlot().receive(dollar);
			if (scs.getCoinTray().collectCoins().isEmpty()) {
				count = count + 1;
			}
		}
		Funds funds = session.getFunds();
		assertEquals("Session is fully paid for", BigDecimal.ZERO, funds.getAmountDue());
		assertEquals("Session has been notified of full payment", session.getState(), SessionState.PRE_SESSION);
	}

	@Test
	public void testPayItemWithCashGetChange() throws DisabledException, CashOverloadException {
		scs.getBanknoteDispensers().get(BigDecimal.TEN).load(ten, ten, ten);

		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		scs.getBaggingArea().addAnItem(item);
		session.payByCash();
		boolean inserted = false;
		while (!inserted) {
			scs.getBanknoteInput().receive(twenty);
			if (!scs.getBanknoteInput().hasDanglingBanknotes()) {
				inserted = true;
			} else {
				scs.getBanknoteInput().removeDanglingBanknote();
			}
		}
		Funds funds = session.getFunds();
		assertEquals("Paid $20", new BigDecimal(20), funds.getPaid());
		assertTrue("Session is fully paid for", funds.getAmountDue().compareTo(BigDecimal.ZERO) < 0);
		assertEquals("Session has been notified of full payment", SessionState.PRE_SESSION, session.getState());

		assertTrue(scs.getBanknoteOutput().hasDanglingBanknotes());
		List<Banknote> change = scs.getBanknoteOutput().removeDanglingBanknotes();

		assertEquals("Dispensed $10", ten, change.get(0));
	}

	@Test
	public void testPayItemGetCoinChange() throws DisabledException, CashOverloadException {
		scs.getBanknoteDispensers().get(new BigDecimal(5)).load(five, five, five);
		scs.getCoinDispensers().get(new BigDecimal(1)).load(dollar, dollar, dollar, dollar, dollar);

		Barcode barcode2 = new Barcode(new Numeral[] { Numeral.valueOf((byte) 2) });
		BarcodedProduct product2 = new BarcodedProduct(barcode2, "Some product", 12, 20.0);
		ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode2, product2);

		BarcodedItem item2 = new BarcodedItem(barcode2, new Mass(20.0));

		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item2);
		}
		scs.getBaggingArea().addAnItem(item2);
		session.payByCash();
		boolean inserted = false;
		while (!inserted) {
			scs.getBanknoteInput().receive(twenty);
			if (!scs.getBanknoteInput().hasDanglingBanknotes()) {
				inserted = true;
			} else {
				scs.getBanknoteInput().removeDanglingBanknote();
			}
		}
		Funds funds = session.getFunds();
		assertEquals("Paid $20", new BigDecimal(20), funds.getPaid());
		assertTrue("Session is fully paid for", funds.getAmountDue().compareTo(BigDecimal.ZERO) < 0);
		assertEquals("Session has been notified of full payment", SessionState.PRE_SESSION, session.getState());

		List<Coin> coinchange = scs.getCoinTray().collectCoins();
		List<Coin> expected = new ArrayList<>();
		expected.add(dollar);
		expected.add(dollar);
		expected.add(dollar);
		assertEquals("Dispensed $3", expected, coinchange);

		List<Banknote> banknotechange = scs.getBanknoteOutput().removeDanglingBanknotes();
		assertEquals("Dispensed $5", five, banknotechange.get(0));
	}

	// Tests for paying Credit via swipe
	/*
	 * @Test
	 * public void testPayWithCredit() throws CashOverloadException,
	 * NoCashAvailableException, DisabledException, IOException {
	 * CardIssuer ci1 = new CardIssuer(SupportedCardIssuers.ONE.getIssuer(), 1);
	 * CardIssuerDatabase.CARD_ISSUER_DATABASE.put(SupportedCardIssuers.ONE.
	 * getIssuer(), ci1);
	 * Card creditCard = new Card(SupportedCardIssuers.ONE.getIssuer(),
	 * "5299334598001547", "Brandon Chan", "666");
	 * 
	 * Calendar exp = Calendar.getInstance();
	 * exp.set(Calendar.YEAR, 2099);
	 * exp.set(Calendar.MONTH, 12);
	 * 
	 * ci1.addCardData(creditCard.number, creditCard.cardholder, exp,
	 * creditCard.cvv, 10000);
	 * 
	 * session.start();
	 * for(int i =0; i < 100; i++) {
	 * scs.getMainScanner().scan(item);
	 * }
	 * scs.getBaggingArea().addAnItem(item);
	 * session.payByCard();
	 * 
	 * Funds funds = session.getFunds();
	 * 
	 * assertEquals(SessionState.PAY_BY_CARD, session.getState());
	 * 
	 * boolean read = false;
	 * while (!read) {
	 * try {
	 * scs.getCardReader().swipe(creditCard);
	 * read = true;
	 * } catch (MagneticStripeFailureException e) {
	 * }
	 * }
	 * assertEquals("Session is fully paid for", BigDecimal.ZERO,
	 * funds.getAmountDue());
	 * assertEquals("Session has been notified of full payment", session.getState(),
	 * SessionState.PRE_SESSION);
	 * }
	 * 
	 * @Test
	 * public void testPayWithDebit() throws CashOverloadException,
	 * NoCashAvailableException, DisabledException, IOException {
	 * CardIssuer ci1 = new CardIssuer(SupportedCardIssuers.ONE.getIssuer(), 1);
	 * CardIssuerDatabase.CARD_ISSUER_DATABASE.put(SupportedCardIssuers.ONE.
	 * getIssuer(), ci1);
	 * Card debitCard = new Card(SupportedCardIssuers.ONE.getIssuer(),
	 * "5299334598001547", "Brandon Chan", "666");
	 * 
	 * Calendar exp = Calendar.getInstance();
	 * exp.set(Calendar.YEAR, 2099);
	 * exp.set(Calendar.MONTH, 12);
	 * 
	 * ci1.addCardData(debitCard.number, debitCard.cardholder, exp, debitCard.cvv,
	 * 10000);
	 * 
	 * session.start();
	 * for(int i =0; i < 100; i++) {
	 * scs.getMainScanner().scan(item);
	 * }
	 * scs.getBaggingArea().addAnItem(item);
	 * session.payByCard();
	 * 
	 * Funds funds = session.getFunds();
	 * 
	 * assertEquals(SessionState.PAY_BY_CARD, session.getState());
	 * 
	 * boolean read = false;
	 * while (!read) {
	 * try {
	 * scs.getCardReader().swipe(debitCard);
	 * read = true;
	 * } catch (MagneticStripeFailureException e) {
	 * }
	 * }
	 * assertEquals("Session is fully paid for", BigDecimal.ZERO,
	 * funds.getAmountDue());
	 * assertEquals("Session has been notified of full payment", session.getState(),
	 * SessionState.PRE_SESSION);
	 * }
	 * 
	 * // Tests for removing an item
	 * 
	 * @Test
	 * public void testAddThenRemoveItem() throws DisabledException,
	 * CashOverloadException {
	 * session.start();
	 * for(int i =0; i < 100; i++) {
	 * scs.getMainScanner().scan(item);
	 * }
	 * scs.getBaggingArea().addAnItem(item);
	 * for(int i =0; i < 100; i++) {
	 * scs.getMainScanner().scan(item2);
	 * }
	 * scs.getBaggingArea().addAnItem(item2);
	 * session.removeItem(product);
	 * scs.getBaggingArea().removeAnItem(item);
	 * session.payByCash();
	 * int count = 0;
	 * while(count<10) {
	 * scs.getCoinSlot().receive(dollar);
	 * if(scs.getCoinTray().collectCoins().isEmpty()) {
	 * count = count + 1;
	 * }
	 * }
	 * Funds funds = session.getFunds();
	 * assertEquals("Session is fully paid for", BigDecimal.ZERO,
	 * funds.getAmountDue());
	 * assertEquals("Session has been notified of full payment", session.getState(),
	 * SessionState.PRE_SESSION);
	 * }
	 */
	// Test add bulky item

	@Test
	public void testAddBulkyItem() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		session.addBulkyItem();
		assertEquals("Session is in session", SessionState.IN_SESSION, session.getState());
	}

	@Test
	public void testRemoveBulkyItem() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		session.addBulkyItem();
		session.removeItem(product);
		assertEquals("Session is in session", SessionState.IN_SESSION, session.getState());
	}

	// Tests for weight Discrepancy

	@Test
	public void testDiscrepancy() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		assertEquals("Session is frozen upon discrepancy", session.getState(), SessionState.BLOCKED);
	}

	@Test
	public void testAddItemWhenDiscrepancy() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item2);
		}
		HashMap<BarcodedProduct, Integer> list = session.getBarcodedItems();
		assertFalse("Item is not added to list", list.containsKey(item2));
	}

	@Test
	public void testEnterPayWhenDiscrepancy() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		assertEquals(SessionState.BLOCKED, session.getState());
		session.payByCash();
		assertEquals(SessionState.BLOCKED, session.getState());
	}

	@Test(expected = InvalidActionException.class)
	public void testPayWhenDiscrepancy() throws DisabledException, CashOverloadException {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		scs.getBaggingArea().addAnItem(item);
		session.payByCash();
		scs.getBaggingArea().addAnItem(item2);
		int count = 0;
		while (count < 1) {
			scs.getCoinSlot().receive(dollar);
			if (scs.getCoinTray().collectCoins().isEmpty()) {
				count = count + 1;
			}
		}
	}

	@Test
	public void testDiscrepancyDuringPay() {
		session.start();
		for (int i = 0; i < 100; i++) {
			scs.getMainScanner().scan(item);
		}
		scs.getBaggingArea().addAnItem(item);

		session.payByCash();
		scs.getBaggingArea().addAnItem(item2);
		Funds funds = session.getFunds();
		assertEquals(SessionState.BLOCKED, session.getState());
		assertTrue(funds.isPay());
		scs.getBaggingArea().removeAnItem(item2);
		assertEquals(SessionState.PAY_BY_CASH, session.getState());
		assertTrue(funds.isPay());
	}

}
