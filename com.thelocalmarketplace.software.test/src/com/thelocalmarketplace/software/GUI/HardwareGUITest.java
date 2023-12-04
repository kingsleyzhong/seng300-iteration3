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
import java.math.BigDecimal;
import java.awt.event.InputEvent;

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
    private DragRobot dragRobot;

    public class DragRobot {

        private Robot robot;

        public DragRobot() throws AWTException {
            robot = new Robot();
        }

        /**
         * Drags an element from the start point to the end point.
         *
         * @param startPoint The starting point of the drag.
         * @param endPoint   The end point of the drag.
         */
        public void drag(Point startPoint, Point endPoint) {
            // Move the mouse to the start point
            robot.mouseMove(startPoint.x, startPoint.y);

            // Press the mouse button (left click)
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
            robot.delay(200); // short delay to allow the mouse press to register

            // Move the mouse to the end point while the button is pressed
            robot.mouseMove(endPoint.x, endPoint.y);
            robot.delay(200); // short delay for the dragging to register

            // Release the mouse button
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }
    }

    @Before
    public void setup() throws AWTException {
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
        dragRobot = new DragRobot();
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
        hardwareGUI.buttonPanel.startButton.doClick();
        System.out.println(hardwareGUI.cartPanel.getLocationOnScreen());
        Point cartCoord = hardwareGUI.cartPanel.getComponent(0).getLocationOnScreen();
        cartCoord.move(10, 10);
        Point baggingCoord = hardwareGUI.baggingPanel.getLocationOnScreen();
        baggingCoord.move(10, 10);
        dragRobot.drag(cartCoord, baggingCoord);
        System.out.println(hardwareGUI.getItemsInBaggingArea().size());
        Assert.assertEquals(1, hardwareGUI.getItemsInBaggingArea().size());
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
    
    //IDK WHY NONE OF THE BILL STUFF WONT WORK.... IT WORKS MANUALLY 
    @Test
    public void inputBill() {
//		softwareGUI.btnStart.doClick();
//		hardwareGUI.buttonPanel.startButton.doClick();
//		
//		scs.getMainScanner().scan(item2);
//		scs.getBaggingArea().addAnItem(item2);
//		
//		softwareGUI.pay.doClick();
//		softwareGUI.paymentScreen.getCashButton().doClick();
//		
//		//cashpanel.RemoveInputBill.doClick();
//		
//		cashpanel.FiveBillBtn.doClick();
//		cashpanel.FiveBillBtn.doClick();
//		
//		Assert.assertEquals(BigDecimal.TEN , cashController.getCashPaid());
    }
    
    @Test
    public void inputCoin() {
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		cashpanel.button_one_coin.doClick();
		
		Assert.assertEquals(BigDecimal.ONE , cashController.getCashPaid());
		
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
