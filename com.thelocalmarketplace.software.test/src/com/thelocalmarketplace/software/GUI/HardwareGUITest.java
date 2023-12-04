package com.thelocalmarketplace.software.GUI;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.GUI.hardware.ButtonPanel;
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
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.funds.Funds;
import com.thelocalmarketplace.software.funds.PayByCash;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import powerutility.PowerGrid;

import java.awt.*;

public class HardwareGUITest {
    private AbstractSelfCheckoutStation scs;
    private Attendant attendant;
    private AttendantStation as;
    private Session session;
    private SoftwareGUI softwareGUI;
    private HardwareGUI hardwareGUI;
    private ButtonPanel buttonPanel;

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
        hardwareGUI = new HardwareGUI(scs, as);

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

    @Test
    public void testStart() {
        Assert.assertTrue(HardwareGUI.hardwareFrame.isVisible());
        Container tempComponent = (Container) HardwareGUI.hardwareFrame.getContentPane().getComponent(0);
        Assert.assertEquals(tempComponent.getComponent(0), hardwareGUI.buttonPanel.startButton);
        Assert.assertEquals(session.getState(), SessionState.PRE_SESSION);
    }

    @Test
    public void testStartButton() {
        hardwareGUI.buttonPanel.startButton.doClick();
        Container tempComponent = (Container) HardwareGUI.hardwareFrame.getContentPane().getComponent(0);
        Assert.assertNotEquals(tempComponent.getComponent(0), hardwareGUI.buttonPanel.startButton);
        Assert.assertEquals(session.getState(), SessionState.PRE_SESSION);
    }

    @Test
    public void navigateToAttendantGUI() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.buttonPanel.attendantScreen.doClick();
        Assert.assertTrue(hardwareGUI.getSupervisor().screen.getFrame().isShowing());
    }

    @Test
    public void navigateToSoftwareGUI() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.buttonPanel.sessionScreen.doClick();
        Assert.assertTrue(hardwareGUI.getStation().getScreen().getFrame().isShowing());
    }

    @Test
    public void dragItemFromCartToBagging() {

    }

    @Test
    public void dragItemFromCartToScanning() {

    }

    @Test
    public void dragItemFromBaggingToCart() {

    }

    @Test
    public void dragItemFromScanningToCart() {

    }

    @Test
    public void dragItemFromBaggingToScanning() {

    }

    @Test
    public void dragItemFromScanningToBagging() {

    }

    @Test
    public void scanWithMainButton() {

    }

    @Test
    public void scanWithHandheldButton() {

    }

    @Test
    public void collectReceiptButton() {

    }

    @Test
    public void dispenseBagsButton() {

    }

    @Test
    public void removeBagsButton() {

    }

    @Test
    public void inputBill() {

    }

    @Test
    public void inputMultipleBills() {

    }

    @Test
    public void inputNonBill() {

    }

    @Test
    public void removeInputBills() {

    }

    @Test
    public void removeChangeBills() {

    }

    @Test
    public void inputCoin() {

    }

    @Test
    public void inputMultipleCoins() {

    }

    @Test
    public void inputNonCoin() {

    }

    @Test
    public void removeCoinTray() {

    }

    @Test
    public void selectCredit() {

    }

    @Test
    public void selectDebit() {

    }

    @Test
    public void selectInvalidCard() {

    }

    @Test
    public void selectMembershipCard() {

    }

    @Test
    public void cardSwipe() {

    }

    @Test
    public void cardInsert() {

    }

    @Test
    public void cardTap() {

    }

}
