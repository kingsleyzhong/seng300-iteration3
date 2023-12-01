package com.thelocalmarketplace.software.test.attendant;

import com.jjjwelectronics.OverloadedDevice;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.exceptions.ClosedHardwareException;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
import com.thelocalmarketplace.software.test.AbstractSessionTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

    @Before
    public void setup() {
        basicDefaultSetup();


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
}
