
package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.weight.Weight;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

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

public class SelfCheckoutStationLogicTest extends AbstractTest {

    private SelfCheckoutStationLogic logic;
    private AttendantStation station;

    public SelfCheckoutStationLogicTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        super(testName, scsClass);
    }

    @Before
    public void setup() {
        basicDefaultSetup();

        station = new AttendantStation();
    }

    @Test
    public void installation() {
        SelfCheckoutStationLogic.installAttendantStation(station);
        logic = SelfCheckoutStationLogic.installOn(scs);
        assertNotNull(logic);
    }

    @Test
    public void installationComponents() {
        // Check that the logic has installed Funds, Weight, and ItemAddedRule on the
        // session and scs.
        SelfCheckoutStationLogic.installAttendantStation(station);

        logic = SelfCheckoutStationLogic.installOn(scs);
        Session session = logic.getSession();

        assertNotNull(session);

        Funds funds = session.getFunds();
        Weight weight = session.getWeight();

        assertNotNull(funds);
        assertNotNull(weight);
    }
}
