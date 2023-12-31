package com.thelocalmarketplace.software.test;

import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import powerutility.PowerGrid;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/*
 * 
* Project Iteration 3 Group 1
*
* Derek Atabayev 			: 30177060 
* Enioluwafe Balogun 		: 30174298 
* Subeg Chahal 				: 30196531 
* Jun Heo 					: 30173430 
* Emily Kiddle 				: 30122331 
* Anthony Kostal-Vazquez 	: 30048301 
* Jessica Li 				: 30180801 
* Sua Lim 					: 30177039 
* Savitur Maharaj 			: 30152888 
* Nick McCamis 				: 30192610 
* Ethan McCorquodale 		: 30125353 
* Katelan Ng 				: 30144672 
* Arcleah Pascual 			: 30056034 
* Dvij Raval 				: 30024340 
* Chloe Robitaille 			: 30022887 
* Danissa Sandykbayeva 		: 30200531 
* Emily Stein 				: 30149842 
* Thi My Tuyen Tran 		: 30193980 
* Aoi Ueki 					: 30179305 
* Ethan Woo 				: 30172855 
* Kingsley Zhong 			: 30197260 
* 
 */

@RunWith(Parameterized.class)
public abstract class AbstractTest {
    protected AbstractSelfCheckoutStation scs;
    protected PowerGrid powerGrid;
    private final Class<? extends AbstractSelfCheckoutStation> scsClass;

    public AbstractTest(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
        this.scsClass = scsClass;
    }

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "Bronze", SelfCheckoutStationBronze.class },
                { "Silver", SelfCheckoutStationSilver.class },
                { "Gold", SelfCheckoutStationGold.class }
        });
    }

    protected void basicDefaultSetup() {
        try {
        	scs = scsClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// This should never happen
			e.printStackTrace();
		}

        AbstractSelfCheckoutStation.resetConfigurationToDefaults();
        PowerGrid.engageUninterruptiblePowerSource();
        powerGrid = PowerGrid.instance();
        scs.plugIn(powerGrid);
        scs.turnOn();
    }

}
