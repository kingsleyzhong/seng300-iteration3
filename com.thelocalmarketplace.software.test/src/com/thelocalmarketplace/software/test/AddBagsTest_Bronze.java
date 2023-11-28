package com.thelocalmarketplace.software.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.views.AbstractView;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.SelfCheckoutStationSilver;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.items.ItemAddedRule;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;
import com.thelocalmarketplace.software.weight.Weight;

import StubClasses.BagStub;
import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import junit.framework.Assert;
import powerutility.PowerGrid;

/*
 * Testing for methods related to the AddBags use case
 * 	- method Session.addBags()
 * 	- method Session.checkBags()
 * 	- method Session.bagsTooHeavy()
 * 	- method Session.cancelAddBags()
 * 
 * Project iteration 2 group members:
 * Aj Sallh : 30023811
 * Anthony Kostal-Vazquez : 30048301
 * Chloe Robitaille : 30022887
 * Dvij Raval : 30024340
 * Emily Kiddle : 30122331
 * Katelan NG : 30144672
 * Kingsley Zhong : 30197260
 * Nick McCamis : 30192610
 * Sua Lim : 30177039
 * Subeg CHAHAL : 30196531
 */

public class AddBagsTest_Bronze extends AbstractAddBagsTest {

	@Override
	protected AbstractSelfCheckoutStation createInstance() {
		// TODO Auto-generated method stub
		return new SelfCheckoutStationBronze();
	}
	
	

}
