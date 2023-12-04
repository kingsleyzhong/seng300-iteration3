package com.thelocalmarketplace.software.test.funds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.IllegalDigitException;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.exceptions.NotEnoughChangeException;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.test.AbstractTest;

import StubClasses.FundsListenerStub;
import StubClasses.SessionFundsSimulationStub;
import ca.ucalgary.seng300.simulation.SimulationException;

/**
 * Testing for the Funds class
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

public class FundsTest extends AbstractTest {
	public FundsTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
	}

	private Funds funds;

	private BigDecimal amountPaid;
	private BigDecimal price;

	@Before
	public void setUp() {
		basicDefaultSetup();

		funds = new Funds(scs);
		funds.setPay(true);

		price = BigDecimal.valueOf(1);
		amountPaid = BigDecimal.valueOf(1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNullSelfCheckoutStation() {
		scs = null;
		funds = new Funds(scs);
	}

	@Test
	public void testUpdateValidPrice() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(price);
		assertEquals(price, funds.getItemsPrice());	
	}

	@Test(expected = IllegalDigitException.class)
	public void testUpdateInvalidPriceZero() throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(BigDecimal.valueOf(0.00));
	}

	@Test(expected = IllegalDigitException.class)
	public void testUpdateInvalidPriceNegative()
			throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(BigDecimal.valueOf(-1.00));
	}

	@Test
	public void testRemoveValidItemPrice() throws CashOverloadException, NoCashAvailableException, DisabledException {
		assertEquals(BigDecimal.valueOf(0), funds.getItemsPrice());
		funds.update(price);
		assertEquals(BigDecimal.valueOf(1), funds.getItemsPrice());
		funds.removeItemPrice(price);
		assertEquals(BigDecimal.valueOf(0), funds.getItemsPrice());
	}

	@Test(expected = IllegalDigitException.class)
	public void testRemoveInvalidItemPriceZero()
			throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(price);
		funds.removeItemPrice(BigDecimal.valueOf(0.00));
	}

	@Test(expected = IllegalDigitException.class)
	public void testRemoveInvalidItemPriceNegative()
			throws CashOverloadException, NoCashAvailableException, DisabledException {
		funds.update(price);
		funds.removeItemPrice(BigDecimal.valueOf(-1.00));
	}

	@Test
	public void testTurnOnPay() {
		funds.setPay(true);
		assertTrue(funds.isPay());
	}

	@Test
	public void testTurnOffPay() {
		funds.setPay(false);
		assertFalse(funds.isPay());
	}

	@Test
	public void testAmountPaidFullCash()
			throws DisabledException, CashOverloadException, NoCashAvailableException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		funds.register(stub);
		funds.update(price);
		funds.removeItemPrice(price);

		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		scs.getCoinSlot().enable();

		scs.getCoinSlot().enable();
		scs.getCoinSlot().receive(coinAmountPaid);

		assertTrue("Paid event called", stub.getEvents().contains("Paid"));
	}

	@Test
	public void testAmountPaidPartialCash() throws DisabledException, CashOverloadException {
		price = BigDecimal.valueOf(2);

		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		funds.register(stub);
		funds.update(price);

		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		scs.getCoinSlot().enable();

		scs.getCoinSlot().receive(coinAmountPaid);

		assertFalse("Paid event not called", stub.getEvents().contains("Paid"));
	}

	@Test(expected = SimulationException.class)
	public void testRegisterInvalidListener() {
		FundsListenerStub stub = null;
		funds.register(stub);
	}

	@Test(expected = SimulationException.class)
	public void testDeregisterInvalidListener() {
		FundsListenerStub stub = null;
		funds.deregister(stub);
	}

	@Test
	public void testUnregisterListener() throws DisabledException, CashOverloadException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		funds.register(stub);
		funds.deregister(stub);
		funds.update(price);

		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();

		scs.getCoinSlot().enable();
		scs.getCoinSlot().receive(coinAmountPaid);

		assertFalse("Paid event should not be called", stub.getEvents().contains("Paid"));
	}

	@Test
	public void testUnregisterAllListeners() throws DisabledException, CashOverloadException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		Coin coinAmountPaid = new Coin(currency, amountPaid);

		FundsListenerStub stub = new FundsListenerStub();
		FundsListenerStub stub2 = new FundsListenerStub();

		funds.register(stub);
		funds.register(stub2);

		funds.deregisterAll();
		funds.update(price);

		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		scs.getCoinSlot().enable();

		scs.getCoinSlot().enable();
		scs.getCoinSlot().receive(coinAmountPaid);

		assertFalse("Paid event should not be called", stub.getEvents().contains("Paid"));
		assertFalse("Paid event should not be called", stub2.getEvents().contains("Paid"));
	}

	@Test
	public void testEnableDisable() {
		scs.getCoinValidator().disable();
		scs.getCoinValidator().enable();
		scs.getCoinValidator().disactivate();
		scs.getCoinValidator().activate();
	}

	@Test(expected = NotEnoughChangeException.class)
	public void testNotEnoughChange() throws DisabledException, CashOverloadException {
		Currency currency = Currency.getInstance(Locale.CANADA);
		FundsListenerStub stub = new FundsListenerStub();
		price = BigDecimal.valueOf(2);
		
		funds.register(stub);
		funds.setPay(true);
		funds.update(price);

		SessionFundsSimulationStub sampleSimulation = new SessionFundsSimulationStub();
		sampleSimulation.setPayByCash();
		scs.getBanknoteInput().enable();
		//scs.getBanknoteInput().receive(new Banknote(currency, new BigDecimal(1)));
		funds.updatePaidCash(BigDecimal.TEN);
	}
	
	@Test
	public void attemptPayByCashNotInPay() {
		// Cleanup from other methods
		funds.setPay(false);
		
		Currency currency = Currency.getInstance(Locale.CANADA);
		FundsListenerStub stub = new FundsListenerStub();
		price = BigDecimal.valueOf(1);
		funds.register(stub);
		funds.update(price);
		funds.updatePaidCash(BigDecimal.ONE);
		
		assertTrue(funds.getAmountDue().equals(BigDecimal.valueOf(1)));
		
	}
	
}
