package com.thelocalmarketplace.GUI;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.scanner.Barcode;
import com.tdc.CashOverloadException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
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
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.receipt.Receipt;

import ca.ucalgary.seng300.simulation.SimulationException;
import powerutility.PowerGrid;

public class Simulation {
	private AbstractSelfCheckoutStation scs;
	private AttendantStation as;
	private Attendant attendant;
	private Session session;
	private Receipt receipt;
	private IssuePredictor predictor;
	private MaintenanceManager manager;
	
	private HardwareGUI hardwareGUI;
	private AttendantGUI attendantGUI;
	private SoftwareGUI softwareGUI;
	
	public Simulation() {
		setupData();
		setupLogic();
		setupLoading();
	}

	/**
	 * Sets up the logic of the simulation
	 * @throws NotDisabledSessionException 
	 */
	public void setupLogic() throws NotDisabledSessionException {
		scs = new SelfCheckoutStationBronze();
		as = new AttendantStation();
		
		PowerGrid.engageUninterruptiblePowerSource();
		scs.plugIn(PowerGrid.instance());
		as.plugIn(PowerGrid.instance());
		scs.turnOn();
		as.turnOn();
		
		SelfCheckoutStationLogic.installAttendantStation(as);
		attendant = SelfCheckoutStationLogic.getAttendant();
		receipt = new Receipt(scs.getPrinter());	
		
		manager = new MaintenanceManager();
		manager.openHardware(session);

		predictor = new IssuePredictor(session, scs);
		SelfCheckoutStationLogic logic = SelfCheckoutStationLogic.installOn(scs);
		session = logic.getSession();
		session.getStation().setSupervisor(as);
		
		hardwareGUI = new HardwareGUI(scs, as);
		attendantGUI = new AttendantGUI(attendant, manager);
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
		
		Barcode barcode4 = new Barcode(new Numeral[] {Numeral.four});
		BarcodedProduct product4 = new BarcodedProduct(barcode4, "flock of socks", 7, 50.0);
		SelfCheckoutStationLogic.populateDatabase(barcode4, product4, 20);

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
	
	public void setupLoading() {
		Currency currency = Currency.getInstance(Locale.CANADA);

		Coin nickel = new Coin(currency, new BigDecimal(0.05));
		Coin dime  = new Coin(currency, new BigDecimal(0.10));
		Coin quarter  = new Coin(currency, BigDecimal.valueOf(0.25));
		Coin loonie  = new Coin(currency, new BigDecimal(1));
		Coin toonie  = new Coin(currency, new BigDecimal(2));
		Coin[] coins = new Coin[] {nickel, dime, quarter, loonie, toonie};
		
		for(int i = 0; i<coins.length; i++) {
			Coin[] coinList = new Coin[] {coins[i], coins[i], coins[i], coins[i], 
					coins[i], coins[i], coins[i], coins[i], coins[i], coins[i]};
			BigDecimal denomination = coins[i].getValue();
			try {
				scs.getCoinDispensers().get(denomination).load(coinList);
			} catch (SimulationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CashOverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//bills
		Banknote five = new Banknote(currency, new BigDecimal(5));
		Banknote ten = new Banknote(currency, new BigDecimal(10));
		Banknote twenty = new Banknote(currency, new BigDecimal(20));
		Banknote fifty = new Banknote(currency, new BigDecimal(50));
		Banknote hundred = new Banknote(currency, new BigDecimal(100));
		Banknote[] banknotes = new Banknote[] {five,ten,twenty,fifty,hundred};
		
		for(int i = 0; i<banknotes.length; i++) {
			Banknote[] banknoteList = new Banknote[] {banknotes[i], banknotes[i], banknotes[i], banknotes[i], 
					banknotes[i], banknotes[i], banknotes[i], banknotes[i], banknotes[i], banknotes[i]};
			BigDecimal denomination = banknotes[i].getDenomination();
			try {
				scs.getBanknoteDispensers().get(denomination).load(banknoteList);
			} catch (CashOverloadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			scs.getPrinter().addPaper(512);
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			scs.getPrinter().addInk(1024);
		} catch (OverloadedDevice e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void unhide() {
		HardwareGUI.setVisibility(true);
		softwareGUI.unhide();
	}
}