package com.thelocalmarketplace.software.test.funds;

import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.ChipFailureException;
import com.jjjwelectronics.card.InvalidPINException;
import com.jjjwelectronics.card.MagneticStripeFailureException;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.funds.CardIssuerDatabase;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCard;
import com.thelocalmarketplace.software.funds.SupportedCardIssuers;
import com.thelocalmarketplace.software.test.AbstractTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import powerutility.NoPowerException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * Testing for the PayByCard class with Bronze hardware
 * </p>
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

public class PayByCardTest extends AbstractTest {

	public PayByCardTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
		// TODO Auto-generated constructor stub
	}

	private ArrayList<CardIssuer> supportedCardsClasses = new ArrayList<CardIssuer>();
	private CardIssuer ci1;
	private CardIssuer ci2;
	private CardIssuer ci3;
	private CardIssuer ci4;
	private Card disCard;
	private Card viva;
	private Card cdnDep;
	private Card debit;
	private Funds funds;
	private PayByCard pbc;

	@Before
	public void setup() {
		basicDefaultSetup();

		funds = new Funds(scs);

		pbc = new PayByCard(scs.getCardReader(), funds);

		ci1 = new CardIssuer(SupportedCardIssuers.ONE.getIssuer(), 1);
		ci2 = new CardIssuer(SupportedCardIssuers.TWO.getIssuer(), 5);
		ci3 = new CardIssuer(SupportedCardIssuers.THREE.getIssuer(), 99);
		ci4 = new CardIssuer(SupportedCardIssuers.FOUR.getIssuer(), 2);

		supportedCardsClasses.add(ci1);
		supportedCardsClasses.add(ci2);
		supportedCardsClasses.add(ci3);
		supportedCardsClasses.add(ci4);

		int index = 0;
		for (SupportedCardIssuers supportedCards : SupportedCardIssuers.values()) {
			CardIssuerDatabase.CARD_ISSUER_DATABASE.put(supportedCards.getIssuer(), supportedCardsClasses.get(index));
			index++;
		}

		disCard = new Card(SupportedCardIssuers.ONE.getIssuer(), "5299334598001547", "Brandon Chan", "666", "1234",
				true, true);
		viva = new Card(SupportedCardIssuers.TWO.getIssuer(), "4504389022574000", "Doris Giles", "343", "1234", true,
				true);
		cdnDep = new Card(SupportedCardIssuers.THREE.getIssuer(), "1111111111111111", "Not A Real Person", "420",
				"1234", true, true);
		debit = new Card(SupportedCardIssuers.FOUR.getIssuer(), "5160617843321186", "Robehrt Lazar", "111", "1234",
				true, true);

		Calendar exp = Calendar.getInstance();
		exp.set(Calendar.YEAR, 2099);
		exp.set(Calendar.MONTH, 12);

		ci1.addCardData(disCard.number, disCard.cardholder, exp, disCard.cvv, 10000);
		ci2.addCardData(viva.number, viva.cardholder, exp, viva.cvv, 7500);
		ci3.addCardData("0", cdnDep.cardholder, exp, cdnDep.cvv, 1000);
		ci4.addCardData(debit.number, debit.cardholder, exp, debit.cvv, 2000);
	}

	@After
	public void tearDown() {
		// Clear the CARD_ISSUER_DATABASE after each test
		CardIssuerDatabase.CARD_ISSUER_DATABASE.clear();
		scs.getCardReader().deregisterAll();
	}

	@Test(expected = NoPowerException.class)
	public void powerOffSwipe() throws IOException {
		scs.turnOff();
		scs.getCardReader().swipe(debit);
		scs.turnOn();
	}

	@Test(expected = InvalidActionException.class)
	public void swipeIncorrectState() throws IOException {
		funds.setPay(false);
		while (!funds.successfulSwipe) {
			try {
				scs.getCardReader().swipe(debit);
			} catch (MagneticStripeFailureException e) {
			}
		}
	}

	@Test(expected = InvalidActionException.class)
	public void invalidCardNumber() throws IOException {
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);
		while (!funds.successfulSwipe) {
			System.out.print("I ran");
			try {
				scs.getCardReader().swipe(cdnDep);
			} catch (MagneticStripeFailureException e) {
			}
		}
	}

	@Test(expected = InvalidActionException.class)
	public void blockedCard() throws IOException {
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);
		ci4.block(debit.number);
		while (!funds.successfulSwipe) {
			try {
				scs.getCardReader().swipe(debit);

			} catch (MagneticStripeFailureException e) {
			}
		}
	}

	@Test(expected = InvalidActionException.class)
	public void holdCountDecline() throws IOException {
		ci1.authorizeHold(disCard.number, 1);
		long price = 100;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);
		ci1.authorizeHold(disCard.number, 1);
		assertEquals(-1, ci1.authorizeHold(disCard.number, 1));
		ci1.releaseHold(disCard.number, 1);
		while (!funds.successfulSwipe) {
			try {
				scs.getCardReader().swipe(disCard);
			} catch (MagneticStripeFailureException e) {
			}
		}
	}

	@Test(expected = InvalidActionException.class)
	public void availableBalanceDecline() throws IOException {
		long price = 1000000;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);
		while (!funds.successfulSwipe) {
			try {
				scs.getCardReader().swipe(viva);
			} catch (MagneticStripeFailureException e) {
			}
		}
	}

	@Test
	public void successfulSwipe() throws IOException {
		long price = 10;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);

		while (!funds.payed) {
			try {
				scs.getCardReader().swipe(debit);

			} catch (MagneticStripeFailureException e) {
			}
		}
		assertTrue(funds.payed);
	}

	@Test
	public void successfulTap() throws IOException {
		long price = 2;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);

		while (!funds.payed) {
			try {
				scs.getCardReader().tap(disCard);

			} catch (ChipFailureException e) {
			}
		}
		assertTrue(funds.payed);
	}

	@Test(expected = InvalidPINException.class)
	public void incorrectPin() throws IOException {
		long price = 10;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);

		try {
			scs.getCardReader().insert(viva, "4321");
		} finally {
			scs.getCardReader().remove();
		}
	}

	@Test
	public void successfulInsert() throws IOException {
		long price = 10;
		BigDecimal itemPrice = new BigDecimal(price);
		funds.update(itemPrice);
		funds.setPay(true);
		while (!funds.payed) {
			try {
				scs.getCardReader().insert(debit, "1234");
				scs.getCardReader().remove();

			} catch (InvalidPINException | ChipFailureException e) {
			}
		}
		assertTrue(funds.payed);
	}

}
