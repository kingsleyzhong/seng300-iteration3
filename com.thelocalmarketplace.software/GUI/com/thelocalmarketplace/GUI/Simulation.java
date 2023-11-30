package com.thelocalmarketplace.GUI;

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
	private Attendant attendant;
	
	private HardwareGUI hardwareGUI;
	private AttendantGUI attendantGUI;
	private SoftwareGUI softwareGUI;
	
	public Simulation() {
		setupLogic();
		setupData();
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
		attendant = SelfCheckoutStationLogic.getAttendant();
		
		hardwareGUI = new HardwareGUI(scs);
		//attendantGUI = new AttendantGUI(attendant, as.screen);
		//softwareGUI = new SoftwareGUI();
		
		// hidden by default
		hardwareGUI.hide();
	}
	
	/**
	 * Populates the databases, generates coin/banknote denominations,
	 */
	public void setupData() {
		Barcode barcode1 = new Barcode(new Numeral[] { Numeral.one});
		BarcodedProduct product1 = new BarcodedProduct(barcode1, "baaakini", 15, 300.0);
		SelfCheckoutStationLogic.populateDatabase(barcode1, product1, 10);
		
		PriceLookUpCode plu1 = new PriceLookUpCode(new String("0000"));
		PLUCodedProduct pluProduct1 = new PLUCodedProduct(plu1, "baaananas", 10);
		SelfCheckoutStationLogic.populateDatabase(plu1, pluProduct1, 10);
	}
	
	public void unhide() {
		hardwareGUI.unhide();
	}
}