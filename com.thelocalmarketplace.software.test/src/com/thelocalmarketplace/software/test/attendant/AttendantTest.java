package com.thelocalmarketplace.software.test.attendant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.exceptions.SessionNotRegisteredException;
import com.thelocalmarketplace.software.test.AbstractTest;

import powerutility.PowerGrid;

/**
 * Unit Testing for SelfCheckoutStation logic
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
 * 
 */

public class AttendantTest extends AbstractTest {

    public AttendantTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }

    private SelfCheckoutStationLogic logic;
    private AttendantStation station;

    private Attendant attendant;
    private Session session;

    @Before
    public void setup() {
        basicDefaultSetup();
        station = new AttendantStation();
        attendant = new Attendant(station);
        session = new Session();
        attendant.registerOn(session, scs);
        //attendant.addIssuePrediction(session);
    }

    @Test
    public void testConstructor() {
        Attendant tempAttendant = new Attendant(station);
        assertNotNull(tempAttendant);
    }

    @Test
    public void testDisableSession() {
    	attendant.disableStation(session);
    	
    	assertEquals(SessionState.DISABLED, session.getState());
    }
    
    @Test
    public void testEnableStation() {
    	attendant.enableStation(session);
    	
    	assertEquals(SessionState.PRE_SESSION, session.getState());
    }
    
    @Test 
    public void testGetStation() {
    	AttendantStation expected = station;
    	AttendantStation result = attendant.getStation();
    	
    	assertEquals(expected, result);
    }
    
    @Test 
    public void testGetCustomerStation() {
    	AbstractSelfCheckoutStation expected = attendant.getCustomerStation(session);
    	
    	if (session.getStation() instanceof SelfCheckoutStationBronze) 
    		assertEquals(SelfCheckoutStationBronze.class, expected);
    	else if (session.getStation() instanceof SelfCheckoutStationSilver) 
    		assertEquals(SelfCheckoutStationSilver.class, expected);
    	else if (session.getStation() instanceof SelfCheckoutStationGold)
    		assertEquals(SelfCheckoutStationGold.class, expected);
    }
    
    @Test(expected = SessionNotRegisteredException.class)
    public void testGetCustomerStationUnregistered() {
    	attendant = new Attendant(station);
    	attendant.getCustomerStation(session);
    }
     
}
