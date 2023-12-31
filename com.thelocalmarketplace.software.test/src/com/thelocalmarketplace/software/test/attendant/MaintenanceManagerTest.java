package com.thelocalmarketplace.software.test.attendant;

import com.jjjwelectronics.OverloadedDevice;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.exceptions.ClosedHardwareException;
import com.thelocalmarketplace.software.exceptions.IncorrectDenominationException;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
import com.thelocalmarketplace.software.test.AbstractSessionTest;

import StubClasses.MaintenanceManagerListenerStub;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test class for MaintenanceManager
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

public class MaintenanceManagerTest extends AbstractSessionTest {
    public MaintenanceManagerTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
        // TODO Auto-generated constructor stub
    }

    private AttendantStation station;
    private Attendant attendant;
    private MaintenanceManager maintenanceManager;
    private IssuePredictor predictor;

    private MaintenanceManagerListenerStub listener1;
    private MaintenanceManagerListenerStub listener2;

    private Coin nickel;
    private Coin dime;

    private Banknote five;
    private Banknote ten;
    private Currency cad = Currency.getInstance(Locale.CANADA);

    /**
     * set up for Maintenance Manager Test Cases
     */
    @Before
    public void setup() throws OverloadedDevice, CashOverloadException {
        basicDefaultSetup();

        AbstractSelfCheckoutStation
                .configureBanknoteDenominations(new BigDecimal[] { new BigDecimal(10), new BigDecimal(5) });
        AbstractSelfCheckoutStation
                .configureCoinDenominations(new BigDecimal[] { new BigDecimal(0.10), new BigDecimal(0.05) });

        listener1 = new MaintenanceManagerListenerStub();
        listener2 = new MaintenanceManagerListenerStub();
        station = new AttendantStation();
        station.plugIn(powerGrid);
        station.turnOn();
        attendant = new Attendant(station);
        attendant.registerOn(session, scs);

        maintenanceManager = new MaintenanceManager();
        maintenanceManager.register(listener1);
        maintenanceManager.register(listener2);

        attendant.addIssuePrediction(session);
        predictor = attendant.getIssuePredictor(session);

        Coin.DEFAULT_CURRENCY = Currency.getInstance(Locale.CANADA);
        nickel = new Coin(new BigDecimal(0.05));
        dime = new Coin(new BigDecimal(0.1));
        five = new Banknote(cad, new BigDecimal(5));
        ten = new Banknote(cad, new BigDecimal(10));
    }

    public void setupNoIssueSCS() throws OverloadedDevice, CashOverloadException {
        scs.getPrinter().addInk(1 << 20);
        scs.getPrinter().addPaper(1 << 10);

        scs.getCoinDispensers().get(new BigDecimal(0.05)).load(nickel, nickel, nickel, nickel, nickel, nickel);
        scs.getCoinDispensers().get(new BigDecimal(0.1)).load(dime, dime, dime, dime, dime, dime);
        scs.getBanknoteDispensers().get(new BigDecimal(5)).load(five, five, five, five, five, five);
        scs.getBanknoteDispensers().get(new BigDecimal(10)).load(ten, ten, ten, ten, ten, ten);

    }

    /**
     * test case for the constructor
     */
    @Test
    public void constructor() {
        maintenanceManager = new MaintenanceManager();
        assertNotNull(maintenanceManager);
    }

    /**
     * test case for opening hardware when not disabled
     * 
     * @throws NotDisabledSessionException if session is not disabled
     */
    @Test(expected = NotDisabledSessionException.class)
    public void openHardwareWhenNotDisabled() throws NotDisabledSessionException {
        maintenanceManager.openHardware(session);
    }

    @Test(expected = NullPointerSimulationException.class)
    public void testRegisterNullListener() {
        maintenanceManager.register(null);
    }

    @Test(expected = NullPointerSimulationException.class)
    public void testDeregisterNullListener() {
        maintenanceManager.deRegister(null);
    }

    @Test
    public void testDeregisterListener() {
        maintenanceManager.deRegister(listener1);
        assertEquals(maintenanceManager.getListeners().size(), 1);
    }

    /**
     * test case for refilling ink when printer is low on ink
     * 
     * @throws OverloadedDevice            if too much ink is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test
    public void addInkWhenLow() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // low on ink
        session.getStation().getPrinter().addInk(10);

        // add ink when it is low
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk((1 << 20) - 10);
        maintenanceManager.closeHardware();

        int expected = 1 << 20;
        int actual = maintenanceManager.getCurrentAmountOfInk() + 10;
        Assert.assertEquals(expected, actual);
        assertTrue(listener1.inkAdded);
    }

    /**
     * test case for adding ink when printer is empty on ink
     * 
     * @throws OverloadedDevice            if too much ink is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is not closed
     */
    @Test
    public void addInkWhenEmpty() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // add ink when empty
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk(1 << 20);
        maintenanceManager.closeHardware();

        int expected = 1 << 20;
        int actual = maintenanceManager.getCurrentAmountOfInk();
        Assert.assertEquals(expected, actual);
        assertTrue(listener1.inkAdded);

    }

    /**
     * test case for refiling ink when printer is full of ink
     * 
     * @throws OverloadedDevice            if too much ink is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is not closed
     */
    @Test(expected = OverloadedDevice.class)
    public void addInkWhenFull() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // full on ink
        session.getStation().getPrinter().addInk(1 << 20);

        // add ink when full
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk(100);

    }

    /**
     * test case for refilling ink to more than max capacity
     * 
     * @throws OverloadedDevice            if too much ink is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test(expected = OverloadedDevice.class)
    public void addInkMoreThanMax() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // add ink so that amount + remaining > max
        session.getStation().getPrinter().addInk(10);

        // add ink
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk(1 << 20);
    }

    /**
     * test case for refilling ink when printer is not opened
     * 
     * @throws OverloadedDevice        if too much ink is refilled
     * @throws ClosedHardwareException if hardware is closed
     */
    @Test(expected = ClosedHardwareException.class)
    public void addInkPrinterNotOpened() throws OverloadedDevice, ClosedHardwareException {
        // add ink when printer is not opened

        maintenanceManager.refillInk(1 << 20);
    }

    /**
     * test case for refilling paper when printer is low on paper
     * 
     * @throws OverloadedDevice            if too much paper is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test
    public void addPaperWhenLow()
            throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException, CashOverloadException {
        // low on paper
        session.getStation().getPrinter().addPaper(10);

        // add paper
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillPaper((1 << 10) - 10);
        maintenanceManager.closeHardware();

        int expected = 1 << 10;
        int actual = maintenanceManager.getCurrentAmountOfPaper() + 10;
        Assert.assertEquals(expected, actual);
        assertTrue(listener1.paperAdded);

    }

    /**
     * test case for refilling paper when printer is empty on paper
     * 
     * @throws OverloadedDevice            if too much paper is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test
    public void addPaperWhenEmpty() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // add paper when empty
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillPaper(1 << 10);
        maintenanceManager.closeHardware();

        int expected = 1 << 10;
        int actual = maintenanceManager.getCurrentAmountOfPaper();
        Assert.assertEquals(expected, actual);
        assertTrue(listener1.paperAdded);
    }

    /**
     * test case for refilling paper when printer is full of paper
     * 
     * @throws OverloadedDevice            if too much paper is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test(expected = OverloadedDevice.class)
    public void addPaperWhenFull() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // full on ink
        session.getStation().getPrinter().addPaper(1 << 10);

        // add paper
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillPaper(100);
    }

    /**
     * test case for refilling paper to more than max capacity
     * 
     * @throws OverloadedDevice            if too much paper is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is not closed
     */
    @Test(expected = OverloadedDevice.class)
    public void addPaperMoreThanMax() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // add paper so that amount + remaining > max
        session.getStation().getPrinter().addPaper(10);

        // add paper
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.refillPaper(1 << 10);
    }

    /**
     * test case for refilling paper when hardware is not opened
     * 
     * @throws OverloadedDevice        if too much paper is refilled
     * @throws ClosedHardwareException if hardware is closed
     */
    @Test(expected = ClosedHardwareException.class)
    public void addPaperPrinterNotOpened() throws OverloadedDevice, ClosedHardwareException {
        // add ink when printer is not opened
        maintenanceManager.refillPaper(100);
    }

    /**
     * test case for adding coins to coin dispenser
     * 
     * @throws CashOverloadException          if too much coin is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if the coins don't have the correct
     *                                        denomination
     */
    @Test
    public void addCoin() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException,
            IncorrectDenominationException {
        ICoinDispenser tempCoinDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        tempCoinDispenser.load(nickel);

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.05), nickel, nickel, nickel);
        maintenanceManager.closeHardware();
        assertEquals(tempCoinDispenser.size(), 4);
        assertTrue(listener1.coinAdded);
    }

    /**
     * test case for adding coins with non existent denomination
     * 
     * @throws CashOverloadException          if too much coin are added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if the coins do not have the correct
     *                                        denomination
     */
    @Test
    public void addCoinNoDenomination() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException {
        ICoinDispenser tempCoinDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        tempCoinDispenser.load(nickel);

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.20), nickel, nickel, nickel);
        maintenanceManager.closeHardware();

        assertEquals(1, session.getStation().getCoinDispensers().get(new BigDecimal(0.05)).size());
        assertTrue(listener1.coinAdded);
    }

    /**
     * test case for adding coins with incorrect denomination
     * 
     * @throws CashOverloadException          if too much coin is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is not closed
     * @throws IncorrectDenominationException if the coins do not have the correct
     *                                        denomination
     */
    @Test(expected = IncorrectDenominationException.class)
    public void addCoinIncorrectDenomination() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException {
        ICoinDispenser tempCoinDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        tempCoinDispenser.load(nickel, nickel, nickel);

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.05), dime);
        maintenanceManager.closeHardware();
    }

    /**
     * test case for adding too much coin
     * 
     * @throws CashOverloadException          if too much coin is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if coins do not have correct
     *                                        denomination
     */
    @Test(expected = CashOverloadException.class)
    public void addCoinOverflow() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException,
            IncorrectDenominationException {
        ICoinDispenser tempCoinDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));

        Coin coinList[] = new Coin[tempCoinDispenser.getCapacity()];
        for (Coin i : coinList) {
            Coin.DEFAULT_CURRENCY = Currency.getInstance(Locale.CANADA);
            i = new Coin(new BigDecimal(0.05));
            tempCoinDispenser.load(i);
        } // Fills Coin Dispenser to full capacity

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.05), nickel);
        maintenanceManager.closeHardware();
    }

    /**
     * test case for adding coins when hardware is closed
     * 
     * @throws CashOverloadException          if too much coin is added
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if coins do not have the correct
     *                                        denomination
     */
    @Test(expected = ClosedHardwareException.class)
    public void addCoinClosedHardware()
            throws CashOverloadException, ClosedHardwareException, IncorrectDenominationException {
        ICoinDispenser tempCoinDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        tempCoinDispenser.load(nickel);
        maintenanceManager.addCoins(new BigDecimal(0.05), nickel, nickel, nickel);
    }

    /**
     * test case for removing coins from coin storage unit
     * 
     * @throws CashOverloadException       if too much coin is added
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test
    public void emptyCoinStorageUnit()
            throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException {
        CoinStorageUnit tempStorage = session.getStation().getCoinStorage();
        tempStorage.load(nickel, nickel, nickel);

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        List<Coin> removedCoins = maintenanceManager.removeCoins();
        maintenanceManager.closeHardware();
        assertEquals(tempStorage.getCoinCount(), 0);

        int coinsRemoved = 0;
        for (Coin coin : removedCoins) {
            if (coin != null) {
                coinsRemoved++;
            }
        }
        assertEquals(coinsRemoved, 3);
        assertTrue(listener1.coinRemoved);
    }

    /**
     * test case for removing coins when hardware is not opened
     * 
     * @throws CashOverloadException   if too much coin is added
     * @throws ClosedHardwareException if hardware is closed
     */
    @Test(expected = ClosedHardwareException.class)
    public void removeCoinsHardwareNotOpened() throws CashOverloadException, ClosedHardwareException {
        maintenanceManager.removeCoins();
    }

    /**
     * test case for adding banknote to banknote dispenser
     * 
     * @throws CashOverloadException          if too much banknote is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is not closed
     * @throws IncorrectDenominationException if banknotes do not have the correct
     *                                        denomination
     */
    @Test
    public void addBanknote() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException,
            IncorrectDenominationException {
        IBanknoteDispenser tempBanknoteDispenser = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));
        tempBanknoteDispenser.load(five);

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(5), five, five, five);
        maintenanceManager.closeHardware();
        assertEquals(tempBanknoteDispenser.size(), 4);
        assertTrue(listener1.banknoteAdded);
    }

    /**
     * test case for adding banknotes with non existent denomination
     * 
     * @throws CashOverloadException          if too much banknote is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is not closed
     * @throws IncorrectDenominationException if banknotes do not have correct
     *                                        denomination
     */
    @Test
    public void addBanknoteNoDenomination() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException {
        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(100), five, five, five);
        maintenanceManager.closeHardware();
        assertEquals(0, session.getStation().getBanknoteDispensers().get(new BigDecimal(5)).size());
    }

    /**
     * test case for adding banknotes with incorrect denomination
     * 
     * @throws CashOverloadException          if too much banknote is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is not closed
     * @throws IncorrectDenominationException if banknotes do not have correct
     *                                        denomination
     */
    @Test(expected = IncorrectDenominationException.class)
    public void addBanknoteIncorrectDenomination() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException {
        IBanknoteDispenser tempBanknoteDispenser = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));
        tempBanknoteDispenser.load(five);

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(5), five, five, ten);
        maintenanceManager.closeHardware();
    }

    /**
     * test case for adding too much banknote
     * 
     * @throws CashOverloadException          if too much banknote is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if banknotes do not have correct
     *                                        denomination
     */
    @Test(expected = CashOverloadException.class)
    public void addBanknoteOverflow() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException {
        IBanknoteDispenser tempBanknoteDispenser = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));

        Banknote banknoteList[] = new Banknote[tempBanknoteDispenser.getCapacity()];
        for (Banknote i : banknoteList) {
            i = new Banknote(cad, new BigDecimal(5));
            tempBanknoteDispenser.load(i);
        } // Fills Coin Dispenser to full capacity

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(5), five);
        maintenanceManager.closeHardware();
    }

    /**
     * test case for adding banknotes when hardware is closed
     * 
     * @throws CashOverloadException          if too much banknote is added
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if banknotes do not have correct
     *                                        denomination
     */
    @Test(expected = ClosedHardwareException.class)
    public void addBanknoteClosedHardware()
            throws CashOverloadException, ClosedHardwareException, IncorrectDenominationException {
        maintenanceManager.addBanknotes(new BigDecimal(5), five);
    }

    /**
     * test case for removing banknotes from banknote storage unit
     * 
     * @throws CashOverloadException       if too much banknote is added
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test
    public void emptyBanknoteStorage()
            throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException {
        BanknoteStorageUnit tempStorage = session.getStation().getBanknoteStorage();
        tempStorage.load(five, five, five);

        attendant.disableStation(session);
        maintenanceManager.openHardware(session);
        List<Banknote> removedBanknotes = maintenanceManager.removeBanknotes();
        maintenanceManager.closeHardware();
        assertEquals(tempStorage.getBanknoteCount(), 0);

        int banknotesRemoved = 0;
        for (Banknote banknote : removedBanknotes) {
            if (banknote != null) {
                banknotesRemoved++;
            }
        }
        assertEquals(banknotesRemoved, 3);
        assertTrue(listener1.banknoteRemoved);
    }

    /**
     * test case for removing banknotes when hardware is closed
     * 
     * @throws ClosedHardwareException if hardware is closed
     */
    @Test(expected = ClosedHardwareException.class)
    public void removeBanknotesHardwareNotOpened() throws ClosedHardwareException {
        maintenanceManager.removeBanknotes();
    }

    /**
     * test case for detecting change: ink is refilled to printer and there is no
     * more issue
     * 
     * @throws OverloadedDevice            if too much ink is added
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     * @throws CashOverloadException
     */
    @Test
    public void addInkDetectChange()
            throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException, CashOverloadException {
        scs.getPrinter().addPaper(1 << 10);

        scs.getBanknoteDispensers().get(new BigDecimal(5)).load(five, five, five, five, five, five);
        scs.getBanknoteDispensers().get(new BigDecimal(10)).load(ten, ten, ten, ten, ten, ten);
        scs.getCoinDispensers().get(new BigDecimal(0.05)).load(nickel, nickel, nickel, nickel, nickel, nickel);
        scs.getCoinDispensers().get(new BigDecimal(0.1)).load(dime, dime, dime, dime, dime, dime);

        predictor.checkLowInk(session, session.getStation().getPrinter());
        Assert.assertEquals(SessionState.DISABLED, session.getState());
        // add ink
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk((1 << 20) - 10);
        maintenanceManager.closeHardware();

        Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
    }

    /**
     * test case for detecting change: paper is refilled to printer and there is no
     * more issue
     * 
     * @throws OverloadedDevice            if too much paper is refilled
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     * @throws CashOverloadException
     */
    @Test
    public void addPaperDetectChange()
            throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException, CashOverloadException {
        scs.getPrinter().addInk(1 << 20);

        scs.getBanknoteDispensers().get(new BigDecimal(5)).load(five, five, five, five, five, five);
        scs.getBanknoteDispensers().get(new BigDecimal(10)).load(ten, ten, ten, ten, ten, ten);
        scs.getCoinDispensers().get(new BigDecimal(0.05)).load(nickel, nickel, nickel, nickel, nickel, nickel);
        scs.getCoinDispensers().get(new BigDecimal(0.1)).load(dime, dime, dime, dime, dime, dime);
        predictor.checkLowPaper(session, session.getStation().getPrinter());

        // add paper
        maintenanceManager.openHardware(session);
        maintenanceManager.refillPaper((1 << 10) - 10);
        maintenanceManager.closeHardware();

        Assert.assertEquals(SessionState.PRE_SESSION, session.getState());
    }

    /**
     * test case for detecting change: printer is low on ink but ink is not
     * refilled; issue is not resolved
     * 
     * @throws OverloadedDevice            if too much ink is refilled
     * @throws NotDisabledSessionException if session is not disabled
     */
    @Test
    public void noAddInkDetectChange() throws OverloadedDevice, NotDisabledSessionException {
        predictor.checkLowInk(session, session.getStation().getPrinter());

        // add ink
        maintenanceManager.openHardware(session);
        maintenanceManager.closeHardware();

        Assert.assertEquals(SessionState.DISABLED, session.getState());
    }

    /**
     * test case for detecting change: printer is low on paper but is not refilled;
     * issue is not resolved
     * 
     * @throws OverloadedDevice            if too much paper is refilled
     * @throws NotDisabledSessionException if session is not disabled
     */
    @Test
    public void noAddPaperDetectChange() throws OverloadedDevice, NotDisabledSessionException {
        predictor.checkLowPaper(session, session.getStation().getPrinter());

        // add ink
        maintenanceManager.openHardware(session);
        maintenanceManager.closeHardware();

        Assert.assertEquals(SessionState.DISABLED, session.getState());
    }

    /**
     * test case for detecting change: coins are added to coin dispenser and there
     * is no more issue
     * 
     * @throws CashOverloadException          if too much coin is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if coins do not have correct
     *                                        denomination
     */
    @Test
    public void coinDispenserDetectChange() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException, OverloadedDevice {
        setupNoIssueSCS();
        ICoinDispenser tempCoinDispenser1 = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        ICoinDispenser tempCoinDispenser2 = session.getStation().getCoinDispensers().get(new BigDecimal(0.10));
        tempCoinDispenser1.unload();
        tempCoinDispenser2.unload();
        tempCoinDispenser1.load(nickel);
        tempCoinDispenser2.load(dime);

        predictor.checkLowCoins(session, session.getStation().getCoinDispensers());

        maintenanceManager.openHardware(session);
        Coin nickelList[] = new Coin[tempCoinDispenser1.getCapacity() - tempCoinDispenser1.size()];
        for (Coin i : nickelList) {
            i = new Coin(new BigDecimal(0.05));
            maintenanceManager.addCoins(new BigDecimal(0.05), i);
        }
        Coin dimeList[] = new Coin[tempCoinDispenser2.getCapacity() - tempCoinDispenser2.size()];
        for (Coin i : dimeList) {
            i = new Coin(new BigDecimal(0.10));
            maintenanceManager.addCoins(new BigDecimal(0.10), i);
        }
        maintenanceManager.closeHardware();
        assertEquals(SessionState.PRE_SESSION, session.getState());
    }

    /**
     * test case for detecting change: banknotes are added and there is no more
     * issue
     * 
     * @throws CashOverloadException          if too much banknote is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if banknotes do not have correct
     *                                        denomination
     */
    @Test
    public void banknoteDispenserDetectChange() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException {
        IBanknoteDispenser tempBanknoteDispenser1 = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));
        IBanknoteDispenser tempBanknoteDispenser2 = session.getStation().getBanknoteDispensers()
                .get(new BigDecimal(10));

        predictor.checkLowBanknotes(session, session.getStation().getBanknoteDispensers());

        maintenanceManager.openHardware(session);
        Banknote fiveList[] = new Banknote[tempBanknoteDispenser1.getCapacity() - tempBanknoteDispenser1.size()];
        for (Banknote i : fiveList) {
            i = new Banknote(cad, new BigDecimal(5));
            maintenanceManager.addBanknotes(new BigDecimal(5), i);
        }
        Banknote tenList[] = new Banknote[tempBanknoteDispenser2.getCapacity() - tempBanknoteDispenser2.size()];
        for (Banknote i : tenList) {
            i = new Banknote(cad, new BigDecimal(10));
            maintenanceManager.addBanknotes(new BigDecimal(10), i);
        }
        maintenanceManager.closeHardware();
        assertEquals(SessionState.PRE_SESSION, session.getState());
    }

    /**
     * test case for detecting change: coins are removed from coin storage unit when
     * it's full; there is no more issue
     * 
     * @throws CashOverloadException       if too much coin is added
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is cloed
     * @throws OverloadedDevice
     */
    @Test
    public void coinStorageDetectChange()
            throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, OverloadedDevice {
        CoinStorageUnit tempStorage = session.getStation().getCoinStorage();
        int i = tempStorage.getCapacity();
        while (i != 0) {
            tempStorage.load(nickel);
            i--;
        }

        predictor.checkCoinsFull(session, session.getStation().getCoinStorage());

        maintenanceManager.openHardware(session);
        maintenanceManager.removeCoins();
        maintenanceManager.closeHardware();
        assertEquals(tempStorage.getCoinCount(), 0);
    }

    /**
     * test case for detecting change: banknotes are removed from banknote storage
     * unit when it's full; there is no more issue
     * 
     * @throws CashOverloadException       if too much banknote is added
     * @throws NotDisabledSessionException if session is not disabled
     * @throws ClosedHardwareException     if hardware is closed
     */
    @Test
    public void banknoteStorageDetectChange()
            throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, OverloadedDevice {
        setupNoIssueSCS();
        BanknoteStorageUnit tempStorage = session.getStation().getBanknoteStorage();
        int i = tempStorage.getCapacity();
        while (i != 0) {
            tempStorage.load(five);
            i--;
        }

        predictor.checkBanknotesFull(session, session.getStation().getBanknoteStorage());

        maintenanceManager.openHardware(session);
        maintenanceManager.removeBanknotes();
        maintenanceManager.closeHardware();
        assertEquals(tempStorage.getBanknoteCount(), 0);
        assertEquals(SessionState.PRE_SESSION, session.getState());
    }

    /**
     * test case for detecting change: dispenser is low on coin but is not refilled;
     * issue not resolved
     * 
     * @throws CashOverloadException       if too much coin is added
     * @throws NotDisabledSessionException if session is not disabled
     */
    @Test
    public void noAddCoinDetectChange() throws NotDisabledSessionException {
        predictor.checkLowCoins(session, session.getStation().getCoinDispensers());

        maintenanceManager.openHardware(session);
        maintenanceManager.closeHardware();
        assertEquals(SessionState.DISABLED, session.getState());
    }

    /**
     * test case for detecting change: dispenser is low on banknote but is not
     * refilled; issue not resolved
     * 
     * @throws CashOverloadException       if too much banknote is added
     * @throws NotDisabledSessionException if session is not disabled
     */
    @Test
    public void noAddBanknoteDetectChange() throws NotDisabledSessionException {
        predictor.checkLowBanknotes(session, session.getStation().getBanknoteDispensers());

        maintenanceManager.openHardware(session);
        maintenanceManager.closeHardware();
        assertEquals(SessionState.DISABLED, session.getState());
    }

    /**
     * test case for detecting change: coin storage unit is full of coin but is not
     * removed; issue not resolved
     * 
     * @throws CashOverloadException       if too much coin is added
     * @throws NotDisabledSessionException if session is not disabled
     */
    @Test
    public void noRemoveCoinDetectChange() throws CashOverloadException, NotDisabledSessionException {
        CoinStorageUnit tempStorage = session.getStation().getCoinStorage();
        int i = tempStorage.getCapacity();
        while (i != 0) {
            tempStorage.load(nickel);
            i--;
        }

        predictor.checkCoinsFull(session, session.getStation().getCoinStorage());

        maintenanceManager.openHardware(session);
        maintenanceManager.closeHardware();
        assertEquals(SessionState.DISABLED, session.getState());
    }

    /**
     * test case for detecting change: banknote storage unit is full of banknote but
     * is not removed; issue not resolved
     * 
     * @throws CashOverloadException       if too much banknote is added
     * @throws NotDisabledSessionException if session is not disabled
     */
    @Test
    public void noRemoveBanknoteDetectChange() throws CashOverloadException, NotDisabledSessionException {
        BanknoteStorageUnit tempStorage = session.getStation().getBanknoteStorage();
        int i = tempStorage.getCapacity();
        while (i != 0) {
            tempStorage.load(five);
            i--;
        }

        predictor.checkBanknotesFull(session, session.getStation().getBanknoteStorage());

        maintenanceManager.openHardware(session);
        maintenanceManager.closeHardware();
        assertEquals(SessionState.DISABLED, session.getState());
    }

    /**
     * test case for detecting change: dispenser is low on coin; coin is added but
     * not enough; issue not resolved
     * 
     * @throws CashOverloadException          if too much coin is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if coin does not have correct
     *                                        denomination
     */
    @Test
    public void addCoinButNoChange() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException,
            IncorrectDenominationException {
        predictor.checkLowCoins(session, session.getStation().getCoinDispensers());
        assertEquals(SessionState.DISABLED, session.getState());

        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.05), nickel);
        maintenanceManager.addCoins(new BigDecimal(0.10), dime);
        maintenanceManager.closeHardware();

        assertEquals(SessionState.DISABLED, session.getState());
    }

    /**
     * test case for detecting change: dispenser is low on banknote; banknote is
     * added but not enough; issue not resolved
     * 
     * @throws CashOverloadException          if too much banknote is added
     * @throws NotDisabledSessionException    if session is not disabled
     * @throws ClosedHardwareException        if hardware is closed
     * @throws IncorrectDenominationException if banknote does not have correct
     *                                        denomination
     */
    @Test
    public void addBanknoteButNoChange() throws CashOverloadException, NotDisabledSessionException,
            ClosedHardwareException, IncorrectDenominationException {
        predictor.checkLowBanknotes(session, session.getStation().getBanknoteDispensers());

        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(5), five);
        maintenanceManager.addBanknotes(new BigDecimal(0.10), ten);
        maintenanceManager.closeHardware();
        assertEquals(SessionState.DISABLED, session.getState());
    }
}