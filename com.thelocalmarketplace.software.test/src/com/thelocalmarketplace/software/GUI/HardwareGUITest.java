package com.thelocalmarketplace.software.GUI;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.GUI.hardware.CashPanel;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.SoftwareGUI;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.SelfCheckoutStationGold;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCash;
import org.junit.After;
import org.junit.Before;
import powerutility.PowerGrid;

public class HardwareGUITest {
    private AbstractSelfCheckoutStation scs;
    private Attendant attendant;
    private AttendantStation as;
    private Session session;
    private SoftwareGUI softwareGUI;
    private HardwareGUI hardwaregui;

    private CashPanel cashpanel;
    private CoinValidator coinValidator;
    private BanknoteValidator banknoteValidator;
    private CoinValidator validator;
    private PayByCash cashController;
    private Funds funds;


    private BarcodedProduct product;
    private Barcode barcode;
    private BarcodedItem item;
    private BarcodedItem item2;

    @Before
    public void setup() {
        scs = new SelfCheckoutStationGold();
        as = new AttendantStation();
        PowerGrid.engageUninterruptiblePowerSource();
        scs.plugIn(PowerGrid.instance());
        scs.turnOn();
        SelfCheckoutStationLogic.installAttendantStation(as);
        SelfCheckoutStationLogic logic = SelfCheckoutStationLogic.installOn(scs);
        session = logic.getSession();
        softwareGUI = new SoftwareGUI(session);
        hardwaregui = new HardwareGUI(scs);

        //cash panel stuff
        cashpanel = new CashPanel(scs);

        funds = new Funds(scs);

        coinValidator = scs.getCoinValidator();
        banknoteValidator = scs.getBanknoteValidator();

        cashController = new PayByCash(coinValidator, banknoteValidator, funds);





        scs.getScreen().setVisible(true);

        barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
        product = new BarcodedProduct(barcode, "Some product", 10, 20.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);

        item = new BarcodedItem(barcode, new Mass(20.0));
        item2 = new BarcodedItem(barcode, new Mass(20.0));
    }

    @After
    public void teardown() {
        scs.getScreen().getFrame().dispose();
    }
}
