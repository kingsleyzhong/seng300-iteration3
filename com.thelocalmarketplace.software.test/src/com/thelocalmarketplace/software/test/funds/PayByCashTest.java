package com.thelocalmarketplace.software.test.funds;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.IComponent;
import com.tdc.IComponentObserver;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.BanknoteValidatorObserver;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.CoinValidatorObserver;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCash;
import com.thelocalmarketplace.software.test.AbstractTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

public class PayByCashTest extends AbstractTest {

	public PayByCashTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
		// TODO Auto-generated constructor stub
	}

	private CoinValidator validator;
	private PayByCash cashController;
	private BigDecimal value;
	private BigDecimal price;
	private Funds funds;
	private stubCVListener cvListener;
	private stubBVListener bvListener;

	private CoinValidator coinValidator;
	private BanknoteValidator banknoteValidator;

	/***
	 * setting up
	 */

	@Before
	public void setUp() {
		basicDefaultSetup();

		funds = new Funds(scs);

		coinValidator = scs.getCoinValidator();
		banknoteValidator = scs.getBanknoteValidator();

		cashController = new PayByCash(coinValidator, banknoteValidator, funds);

		// register listeners
		cvListener = new stubCVListener();
		coinValidator.attach(cvListener);

		bvListener = new stubBVListener();
		banknoteValidator.attach(bvListener);
		this.price = BigDecimal.TEN;

		funds.update(price);
	}

	@After
	public void tearDown() {
		// clear all listeners
		coinValidator.detachAll();
		banknoteValidator.detachAll();
	}

	/***
	 * Insert a valid coin of value 1
	 * expected for the total cash paid to update to 1
	 * 
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	@Test

	public void validCoinObserved() throws DisabledException, CashOverloadException {
		scs.getCoinSlot().enable();
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);

		Coin coin = new Coin(currency, value);

		
		while (!cvListener.coinIsValid) {
			scs.getCoinSlot().receive(coin);
		}
		Assert.assertEquals(BigDecimal.ONE, cashController.getCashPaid());
	}

	/***
	 * Insert a invalid coin of value 2
	 * expected for the total cash paid to not update
	 * 
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	@Test

	public void invalidCoinObserved() throws DisabledException, CashOverloadException {
		scs.getCoinSlot().enable();
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(2);

		Coin coin = new Coin(currency, value);


		scs.getCoinSlot().receive(coin);
		Assert.assertEquals(BigDecimal.ZERO, cashController.getCashPaid());
	}

	/***
	 * Insert a valid coin of value 1, but pay is not active
	 * expected for InvalidActionException to occur
	 * 
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	@Test(expected = DisabledException.class)
	
	public void payInactive() throws DisabledException, CashOverloadException {
		scs.getCoinSlot().disable();
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);


		Coin coin = new Coin(currency, value);

		while (!cvListener.coinIsValid) {
			scs.getCoinSlot().receive(coin);
		}

	}

	/***
	 * Insert a valid banknote of value 1
	 * expected for the total cash paid to update to 1
	 * 
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	@Test

	public void validBanknoteObserved() throws DisabledException, CashOverloadException {
		scs.getBanknoteInput().enable();
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);

		Banknote note = new Banknote(currency, value);


		while (!bvListener.cashIsValid) {
			if (scs.getBanknoteInput().hasDanglingBanknotes()) {
				scs.getBanknoteInput().removeDanglingBanknote();
			}
			scs.getBanknoteInput().receive(note);
		}
		Assert.assertEquals(BigDecimal.ONE, cashController.getCashPaid());
	}

	/***
	 * Insert a invalid banknote of value 2
	 * expected for the total cash paid to not update
	 * 
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	@Test

	public void invalidBanknoteObserved() throws DisabledException, CashOverloadException {
		scs.getBanknoteInput().enable();
		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(2);

		Banknote note = new Banknote(currency, value);

		scs.getBanknoteInput().receive(note);
		Assert.assertEquals(BigDecimal.ZERO, cashController.getCashPaid());
	}

	/***
	 * Insert a valid banknote of value 1, but pay is not active
	 * expected for InvalidActionException to occur
	 * 
	 * @throws DisabledException
	 * @throws CashOverloadException
	 */
	@Test(expected = DisabledException.class)

	public void payBanknoteInactive() throws DisabledException, CashOverloadException {

		Currency currency = Currency.getInstance(Locale.CANADA);
		value = BigDecimal.valueOf(1);

		Banknote note = new Banknote(currency, value);


		while (!bvListener.cashIsValid) {
			if (scs.getBanknoteInput().hasDanglingBanknotes()) {
				scs.getBanknoteInput().removeDanglingBanknote();
			}
			scs.getBanknoteInput().receive(note);
		}

	}


	/**
	 * a Stub of the coin validator listener to check if coins have been "seen" or
	 * not
	 */
	public class stubCVListener implements CoinValidatorObserver {
		public boolean coinIsValid = false;

		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {

		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {

		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void validCoinDetected(CoinValidator validator, BigDecimal value) {
			coinIsValid = true;
		}

		@Override
		public void invalidCoinDetected(CoinValidator validator) {
			coinIsValid = false;

		}
	}

	/**
	 * a stub that listens to see if a valid banknote has been detected or not
	 */
	public class stubBVListener implements BanknoteValidatorObserver {
		public boolean cashIsValid = false;

		@Override
		public void enabled(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void disabled(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void turnedOn(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void turnedOff(IComponent<? extends IComponentObserver> component) {
		}

		@Override
		public void goodBanknote(BanknoteValidator validator, Currency currency, BigDecimal denomination) {
			cashIsValid = true;
		}

		@Override
		public void badBanknote(BanknoteValidator validator) {
			cashIsValid = false;
		}
	}
}
