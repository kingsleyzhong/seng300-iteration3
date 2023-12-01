package com.thelocalmarketplace.GUI;

import java.math.BigDecimal;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.GUI.attendant.AttendantGUI;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.SoftwareGUI;
import com.thelocalmarketplace.GUI.startscreen.StartScreenGUI;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.SelfCheckoutStationBronze;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.items.ItemManager;

import powerutility.PowerGrid;

public class Simulation {
	private AbstractSelfCheckoutStation scs;
	private AttendantStation as;
	private Session session;
	private ItemManager itemManager;
	
	private HardwareGUI hardwareGUI;
	private AttendantGUI attendantGUI;
	private SoftwareGUI softwareGUI;
	
	public Simulation() {
		setupData();
		setupLogic();
	}
	
	/**
	 * Sets up the logic of the simulation
	 */
	public void setupLogic() {
		scs = new SelfCheckoutStationBronze();
		as = new AttendantStation();
		
		PowerGrid.engageUninterruptiblePowerSource();
		scs.plugIn(PowerGrid.instance());
		as.plugIn(PowerGrid.instance());
		scs.turnOn();
		as.turnOn();
		
		SelfCheckoutStationLogic.installAttendantStation(as);
		SelfCheckoutStationLogic logic = SelfCheckoutStationLogic.installOn(scs);
		session = logic.getSession();
		session.getStation().setSupervisor(as);
		
		hardwareGUI = new HardwareGUI(scs, as);
		attendantGUI = new AttendantGUI(as);
		softwareGUI = new SoftwareGUI(session);
		
		// hidden by default
		//HardwareGUI.setVisibility(false);
		//softwareGUI.hide();
		
		//Pop visibility to top
		HardwareGUI.setVisibility(true);
	}
	
	/**
	 * Populates the databases, generates coin/banknote denominations,
	 */
	public void setupData() {
		Barcode barcode1 = new Barcode(new Numeral[] { Numeral.one});
		BarcodedProduct product1 = new BarcodedProduct(barcode1, "baaakini", 15, 300.0);
		SelfCheckoutStationLogic.populateDatabase(barcode1, product1, 10);
		
		Barcode barcode2 = new Barcode(new Numeral[] {Numeral.two});
		BarcodedProduct product2 = new BarcodedProduct(barcode2, "wooly warm blanket", 60, 1000.0);
		SelfCheckoutStationLogic.populateDatabase(barcode2, product2, 5);
		
		Barcode barcode3 = new Barcode(new Numeral[] {Numeral.three});
		BarcodedProduct product3 = new BarcodedProduct(barcode3, "baaanana bread bites", 5, 125.0);
		SelfCheckoutStationLogic.populateDatabase(barcode3, product3, 20);
		
		PriceLookUpCode plu1 = new PriceLookUpCode(new String("0000"));
		PLUCodedProduct pluProduct1 = new PLUCodedProduct(plu1, "baaananas", 10);
		SelfCheckoutStationLogic.populateDatabase(plu1, pluProduct1, 10);
		
		PriceLookUpCode plu2 = new PriceLookUpCode(new String("0001"));
		PLUCodedProduct pluProduct2 = new PLUCodedProduct(plu2, "baaakliva", 20);
		SelfCheckoutStationLogic.populateDatabase(plu2, pluProduct2, 10);
		
		//Denominations of the SCS
		AbstractSelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] {new BigDecimal(100), 
				new BigDecimal(50), new BigDecimal(20), new BigDecimal(10), new BigDecimal(5) });
		AbstractSelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { new BigDecimal(2), 
				BigDecimal.ONE, new BigDecimal(0.25), new BigDecimal(0.10), new BigDecimal(0.05)});
	}
	
	public void unhide() {
		HardwareGUI.setVisibility(true);
		softwareGUI.unhide();
	}
}