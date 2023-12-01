package com.thelocalmarketplace.software.test.attendant;

import ca.ucalgary.seng300.simulation.SimulationException;
import com.jjjwelectronics.OverloadedDevice;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.ICoinDispenser;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.exceptions.ClosedHardwareException;
import com.thelocalmarketplace.software.exceptions.IncorrectDenominationException;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test class for MaintenanceManager
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

public class MaintenanceManagerTest extends AbstractSessionTest {
    public MaintenanceManagerTest(String testName, AbstractSelfCheckoutStation scs) {
        super(testName, scs);
    }

    private SelfCheckoutStationLogic logic;
    private AttendantStation station;
    private Attendant attendant;
    private MaintenanceManager maintenanceManager;

    private Coin nickel;
    private Coin dime;
    private Coin quarter;
    private Coin dollar;

    private Banknote five;
    private Banknote ten;
    private Banknote twenty;

    private Currency cad = Currency.getInstance(Locale.CANADA);

    @Before
    public void setup() {
        basicDefaultSetup();
        Coin.DEFAULT_CURRENCY = Currency.getInstance(Locale.CANADA);
        nickel = new Coin(new BigDecimal(0.05));
        dime = new Coin(new BigDecimal(0.1));
        five = new Banknote(cad, new BigDecimal(5));
        ten = new Banknote(cad, new BigDecimal(10));

        station = new AttendantStation();
        attendant = new Attendant(station);
        maintenanceManager = new MaintenanceManager();
    }

    @Test
    public void testConstructor() {
        // Attendant tempAttendant = new Attendant();
        Attendant tempAttendant = new Attendant(station);
        maintenanceManager = new MaintenanceManager();
        assertNotNull(maintenanceManager);
    }

    @Test
    public void testOpenHardwareWhenNotDisabled() {

    }

    @Test
    public void testAddInkWhenLow() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // low on ink
        session.getStation().getPrinter().addInk(10);

        // add ink when it is low
        // need to disable
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk(100);
        maintenanceManager.closeHardware();

        int expected = 110;
        // how to get actual
        int actual = session.getStation().getPrinter().inkRemaining();
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testAddInkWhenEmpty() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // no ink
        session.getStation().getPrinter().addInk(0);

        // add ink when it is low
        // need to disable
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk(100);
        maintenanceManager.closeHardware();

        int expected = 100;
        // how to get actual?
        int actual = session.getStation().getPrinter().inkRemaining();
        Assert.assertEquals(expected, actual);
    }

    @Test (expected = OverloadedDevice.class)
    public void testAddInkWhenFull() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // full on ink
        session.getStation().getPrinter().addInk(1 << 20);

        // add ink when it is low
        // need to disable
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk(100);
    }

    @Test (expected = OverloadedDevice.class)
    public void testAddInkMoreThanMax() throws OverloadedDevice, NotDisabledSessionException, ClosedHardwareException {
        // add ink so that amount + remaining > max
        session.getStation().getPrinter().addInk(10);

        // add ink
        // need to disable
        maintenanceManager.openHardware(session);
        maintenanceManager.refillInk(1 << 20);
    }

    @Test (expected = NotDisabledSessionException.class)
    public void testAddInkPrinterNotOpened() throws OverloadedDevice, ClosedHardwareException {
        // add ink when printer is not initialized
        session.getStation().getPrinter().addInk(10);

        maintenanceManager.refillInk(100);
    }

    @Test (expected = NotDisabledSessionException.class)
    public void testAddInkWhenHardwareNotDisabled() throws OverloadedDevice, NotDisabledSessionException {
        session.getStation().getPrinter().addInk(10);

        // add ink
        // DON'T disable
        maintenanceManager.openHardware(session);
    }
    
    @Test
    public void testAddCoin() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, IncorrectDenominationException {
        ICoinDispenser tempDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        tempDispenser.load(nickel);
        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.05), nickel, nickel, nickel);
        maintenanceManager.closeHardware();
        assertEquals(tempDispenser.size(), 4);
    }

    @Test (expected = IncorrectDenominationException.class)
    public void testAddCoinIncorrectDenomination() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, IncorrectDenominationException {
        ICoinDispenser tempDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        tempDispenser.load(nickel, nickel, nickel);
        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.05), dime);
        maintenanceManager.closeHardware();
    }

    @Test (expected = CashOverloadException.class)
    public void testAddCoinOverflow() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, IncorrectDenominationException {
        ICoinDispenser tempDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        Coin coinList[] = new Coin[tempDispenser.getCapacity()];
        for (Coin i : coinList) {i = new Coin(new BigDecimal(0.05));} // Fills Coin Dispenser to full capacity
        tempDispenser.load(coinList);
        maintenanceManager.openHardware(session);
        maintenanceManager.addCoins(new BigDecimal(0.05), nickel);
        maintenanceManager.closeHardware();
    }

    @Test (expected = ClosedHardwareException.class)
    public void testAddCoinClosedHardware() throws CashOverloadException, ClosedHardwareException, IncorrectDenominationException {
        ICoinDispenser tempDispenser = session.getStation().getCoinDispensers().get(new BigDecimal(0.05));
        tempDispenser.load(nickel);
        maintenanceManager.addCoins(new BigDecimal(0.05), nickel, nickel, nickel);
    }

    @Test
    public void emptyCoinStorageUnit() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException {
        CoinStorageUnit tempStorage = session.getStation().getCoinStorage();
        tempStorage.load(nickel, nickel, nickel);
        maintenanceManager.openHardware(session);
        List<Coin> removedCoins = maintenanceManager.removeCoins();
        maintenanceManager.closeHardware();
        assertEquals(tempStorage.getCoinCount(), 0);
        assertEquals(removedCoins.size(), 3);
    }

    @Test
    public void testAddBanknote() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, IncorrectDenominationException {
        IBanknoteDispenser tempDispenser = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));
        tempDispenser.load(five);
        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(5), five, five, five);
        maintenanceManager.closeHardware();
        assertEquals(tempDispenser.size(), 4);
    }

    @Test (expected = IncorrectDenominationException.class)
    public void testAddBanknoteIncorrectDenomination() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, IncorrectDenominationException {
        IBanknoteDispenser tempDispenser = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));
        tempDispenser.load(five);
        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(5), five, five, ten);
        maintenanceManager.closeHardware();
    }

    @Test (expected = CashOverloadException.class)
    public void testAddBanknoteOverflow() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException, IncorrectDenominationException {
        IBanknoteDispenser tempDispenser = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));
        Banknote banknoteList[] = new Banknote[tempDispenser.getCapacity()];
        for (Banknote i : banknoteList) {i = new Banknote(cad, new BigDecimal(5));} // Fills Coin Dispenser to full capacity
        tempDispenser.load(banknoteList);
        maintenanceManager.openHardware(session);
        maintenanceManager.addBanknotes(new BigDecimal(5), five);
        maintenanceManager.closeHardware();
    }

    @Test (expected = ClosedHardwareException.class)
    public void testAddBanknoteClosedHardware() throws CashOverloadException, ClosedHardwareException, IncorrectDenominationException {
        IBanknoteDispenser tempDispenser = session.getStation().getBanknoteDispensers().get(new BigDecimal(5));
        maintenanceManager.addBanknotes(new BigDecimal(5), five);
    }

    @Test
    public void emptyBanknoteStorage() throws CashOverloadException, NotDisabledSessionException, ClosedHardwareException {
        BanknoteStorageUnit tempStorage = session.getStation().getBanknoteStorage();
        tempStorage.load(five, five, five);
        maintenanceManager.openHardware(session);
        List<Banknote> removedBanknotes = maintenanceManager.removeBanknotes();
        maintenanceManager.closeHardware();
        assertEquals(tempStorage.getBanknoteCount(), 0);
        assertEquals(removedBanknotes.size(), 3);
    }

}
