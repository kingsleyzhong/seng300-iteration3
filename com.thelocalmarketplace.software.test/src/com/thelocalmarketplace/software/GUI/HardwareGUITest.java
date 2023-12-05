package com.thelocalmarketplace.software.GUI;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinValidator;
import com.thelocalmarketplace.GUI.hardware.CashPanel;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.hardware.ItemObject;
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

import powerutility.PowerGrid;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.awt.event.InputEvent;

import java.awt.AWTException;
import java.awt.Robot;

/**
 * Isolated test cases for Hardware GUI
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
 */

public class HardwareGUITest {
    private AbstractSelfCheckoutStation scs;
    private AttendantStation as;
    private Session session;
    private SoftwareGUI softwareGUI;
    private HardwareGUI hardwareGUI;
    private CardListenerStub cardListenerStub;
    private BarcodeScannerListenerStub barcodeListenerStub;

    private CashPanel cashpanel;
    private CoinValidator coinValidator;
    private BanknoteValidator banknoteValidator;
    private PayByCash cashController;
    private Funds funds;

    //items
    private BarcodedProduct product;
    private Barcode barcode;
    private BarcodedItem item;
    private BarcodedItem item2;
    private BarcodedItem expensive_item;
    
    private DragRobot dragRobot;
    private Robot panelRobot;
    private ItemObject testObject;
    private BarcodedItem testItem;
    Timer timer;
    int runs = 0;

    public class DragRobot extends Robot {

        private Robot robot;

        public DragRobot() throws AWTException {
            robot = new Robot();
        }

        /**
         * Drags an element from the start point to the end point.
         *
         * @param startPoint The starting point of the drag.
         * @param endPoint   The end point of the drag.
         * @throws InterruptedException 
         */
        public void drag(Point startPoint, Point endPoint) throws InterruptedException {
        	Thread.sleep(500);
            // Move the mouse to the start point
            robot.mouseMove(startPoint.x, startPoint.y);

            // Press the left mouse button
            robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

            // Delay to simulate hold time
            robot.delay(200);

            // Move to the end point
            robot.mouseMove(endPoint.x, endPoint.y);

            // Release the left mouse button
            robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        }

        public void keyPress(int vkEnter) {
        }
    }

    @Before
    public void setup() throws AWTException, CashOverloadException, InterruptedException {
    	Thread.sleep(300);
        //telling it what banknotes it can take
        AbstractSelfCheckoutStation.configureBanknoteDenominations(new BigDecimal[] {new BigDecimal(100),
                new BigDecimal(50), new BigDecimal(20), new BigDecimal(10), new BigDecimal(5) });
        AbstractSelfCheckoutStation.configureCoinDenominations(new BigDecimal[] { new BigDecimal(2),
                BigDecimal.ONE, new BigDecimal(0.25), new BigDecimal(0.10), new BigDecimal(0.05)});

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

        testItem = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one}), new Mass(300.0));
        testObject = new ItemObject(testItem, "testItem");

        //cash panel stuff
        cashpanel = new CashPanel(scs);
        funds = new Funds(scs);
        
		//adding change to the machine
		Banknote ten = new Banknote(Currency.getInstance(Locale.CANADA), new BigDecimal(10));
		scs.getBanknoteDispensers().get(ten.getDenomination()).load(ten);
		
		

        coinValidator = scs.getCoinValidator();
        banknoteValidator = scs.getBanknoteValidator();
        cardListenerStub = new CardListenerStub();
        scs.getCardReader().register(cardListenerStub);
        barcodeListenerStub = new BarcodeScannerListenerStub();
        scs.getMainScanner().register(barcodeListenerStub);
        scs.getHandheldScanner().register(barcodeListenerStub);

        cashController = new PayByCash(coinValidator, banknoteValidator, funds);

        



        scs.getScreen().setVisible(true);

        barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) });
        product = new BarcodedProduct(barcode, "Some product", 10, 20.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(barcode, product);

        item = new BarcodedItem(barcode, new Mass(20.0));
        item2 = new BarcodedItem(barcode, new Mass(20.0));
        
        
        //expensive item
        Barcode expensive_barcode = new Barcode(new Numeral[] { Numeral.valueOf((byte) 2) });
        BarcodedProduct expensive_product = new BarcodedProduct(barcode, "Some product1", 1000, 20.0);
        ProductDatabases.BARCODED_PRODUCT_DATABASE.put(expensive_barcode, expensive_product);
        expensive_item = new BarcodedItem(expensive_barcode, new Mass(20.0));
        
        dragRobot = new DragRobot();
    }
    
    private void panelRobotSetup() throws AWTException {
    	panelRobot = new Robot();
        runs=0;
        timer = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                panelRobot.keyPress(KeyEvent.VK_1);
                panelRobot.keyRelease(KeyEvent.VK_1);
                panelRobot.keyPress(KeyEvent.VK_2);
                panelRobot.keyRelease(KeyEvent.VK_2);
                panelRobot.keyPress(KeyEvent.VK_3);
                panelRobot.keyRelease(KeyEvent.VK_3);
                panelRobot.keyPress(KeyEvent.VK_4);
                panelRobot.keyRelease(KeyEvent.VK_4);
                panelRobot.keyPress(KeyEvent.VK_ENTER);
                panelRobot.keyRelease(KeyEvent.VK_ENTER);
                runs +=1;
                if(runs>20) {
                    timer.stop();
                }
            }

        });
        timer.start();
    }
    
    @After
    public void teardown() throws InterruptedException {
    	Thread.sleep(300);
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
    public void testStartButton() throws InterruptedException {
    	Thread.sleep(500);
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
    public void dragItemFromCartToBagging() throws InterruptedException {
        hardwareGUI.buttonPanel.startButton.doClick();
        HardwareGUI.setVisibility(true);
    	Thread.sleep(500);
        dragRobot.drag(new Point(100, 100), new Point(500, 400));
        Thread.sleep(1000);
        Assert.assertEquals(1, hardwareGUI.getItemsInBaggingArea().size());
    }

    @Test
    public void dragItemFromCartToScanning() throws InterruptedException {
        hardwareGUI.buttonPanel.startButton.doClick();
        HardwareGUI.setVisibility(true);
    	Thread.sleep(500);
        dragRobot.drag(new Point(100, 100), new Point(500, 200));
        Thread.sleep(1000);
        Assert.assertEquals(1, hardwareGUI.getItemsInScanningArea().size());
    }
    
    @Test
    public void dragItemFromBaggingToCart() throws InterruptedException {
        hardwareGUI.buttonPanel.startButton.doClick();
        HardwareGUI.setVisibility(true);
        hardwareGUI.getItemsInBaggingArea().addElement(testObject);
        Assert.assertEquals(1, hardwareGUI.getItemsInBaggingArea().size());
        Thread.sleep(500);
        dragRobot.drag(new Point(380, 320), new Point(100, 300));
        Thread.sleep(1000);
        Assert.assertEquals(0, hardwareGUI.getItemsInBaggingArea().size());
    }

    @Test
    public void dragItemFromScanningToBagging() throws InterruptedException {
        hardwareGUI.buttonPanel.startButton.doClick();
        HardwareGUI.setVisibility(true);
        hardwareGUI.getItemsInScanningArea().addElement(testObject);
        Assert.assertEquals(1, hardwareGUI.getItemsInScanningArea().size());
        dragRobot.drag(new Point(380, 80), new Point(400, 400));
        Thread.sleep(1000);
        Assert.assertEquals(0, hardwareGUI.getItemsInScanningArea().size());
        Assert.assertEquals(1, hardwareGUI.getItemsInBaggingArea().size());
    }

    @Test
    public void scanWithMainButton() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.setLastItem(testObject);
        hardwareGUI.buttonPanel.mainScanner.doClick();
        Assert.assertTrue(barcodeListenerStub.barcodeScanned);
    }

    @Test
    public void scanWithHandheldButton() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.setLastItem(testObject);
        hardwareGUI.buttonPanel.handheldScanner.doClick();
        Assert.assertTrue(barcodeListenerStub.barcodeScanned);
    }

    @Test
    public void collectReceiptButton() throws AWTException {
    	panelRobotSetup();
        softwareGUI.btnStart.doClick();
        hardwareGUI.buttonPanel.startButton.doClick();

        scs.getMainScanner().scan(item2);
        scs.getBaggingArea().addAnItem(item2);

        softwareGUI.pay.doClick();
        softwareGUI.paymentScreen.getCashButton().doClick();


        cashpanel.TenBillBtn.doClick();
        hardwareGUI.buttonPanel.receiptPrinter.doClick();
        Assert.assertNotEquals("", hardwareGUI.buttonPanel.getReceipt());
    }

    @Test
    public void dispenseBagsButton() {

    }

    @Test
    public void removeBagsButton() {

    }


    @Test
    public void inputMultipleBills() throws AWTException {
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(expensive_item);
		scs.getBaggingArea().addAnItem(expensive_item);
		
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		
		cashpanel.FiveBillBtn.doClick();
		cashpanel.TenBillBtn.doClick();
		cashpanel.TwentyBillBtn.doClick();
		cashpanel.FiftyBillBtn.doClick();
		cashpanel.HundredBillButton.doClick();
		
		Assert.assertEquals(new BigDecimal(185) , cashController.getCashPaid());
    }

    @Test
    public void inputNonBill() throws AWTException {
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		cashpanel.NonBillBtn.doClick();

		
		Assert.assertEquals(BigDecimal.ZERO, cashController.getCashPaid());
    }

    @Test
    public void removeInputBills() throws AWTException {
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		//add non bill
		cashpanel.NonBillBtn.doClick();
		
		//check there is a dangling bill
		Assert.assertTrue(scs.getBanknoteInput().hasDanglingBanknotes());
		
		//remove it 
		cashpanel.RemoveInputBill.doClick();

		//check that its been removed
		Assert.assertFalse(scs.getBanknoteInput().hasDanglingBanknotes());
    }

    @Test
    public void removeChangeBills() throws AWTException {
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		//this is an overpayment by 10 so ill get 10 change
		cashpanel.TwentyBillBtn.doClick();
		
		cashpanel.RemoveChangeBillBtn.doClick();
		
		//check that there is no change bills
	
		Assert.assertFalse(scs.getBanknoteOutput().hasDanglingBanknotes());
    }
    

    @Test
    public void inputBill() throws AWTException {
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item2);
		scs.getBaggingArea().addAnItem(item2);
		
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		
		cashpanel.TenBillBtn.doClick();

		
		Assert.assertEquals(BigDecimal.TEN , cashController.getCashPaid());
    }
    
    @Test
    public void inputCoin() throws AWTException {
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		cashpanel.button_one_coin.doClick();

		
		Assert.assertEquals(BigDecimal.ONE , cashController.getCashPaid());
		
    }

    //CURSED CURSED CURSED
    @Test
    public void inputMultipleCoins() throws AWTException {
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		cashpanel.button_five_cent.doClick();
		cashpanel.button_ten_cent.doClick();
		cashpanel.button_twentyfive_cent.doClick();
		cashpanel.button_one_coin.doClick();
		cashpanel.btn_two_coin.doClick();

		Assert.assertEquals(BigDecimal.valueOf(3.4) , cashController.getCashPaid().setScale(1, RoundingMode.HALF_UP));
		
    }


    @Test
    public void inputNonCoin() throws DisabledException, CashOverloadException, AWTException{
    	panelRobotSetup();
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		cashpanel.btnNoncoin.doClick();

		
		Assert.assertEquals(BigDecimal.ZERO, cashController.getCashPaid());
    }
    
    @Test
    public void removeCoinTray() throws AWTException {
    	panelRobotSetup();
    	
		softwareGUI.btnStart.doClick();
		hardwareGUI.buttonPanel.startButton.doClick();
		
		scs.getMainScanner().scan(item);
		scs.getBaggingArea().addAnItem(item);
		softwareGUI.pay.doClick();
		softwareGUI.paymentScreen.getCashButton().doClick();
		
		
		cashpanel.btnNoncoin.doClick();
		
		//check has 1 coin
		//Assert.assertEquals(1, coins.size());
		
		cashpanel.btn_remove_coins.doClick();
		
		List<Coin> coinchange = scs.getCoinTray().collectCoins();
		Assert.assertEquals(0, coinchange.size());
    }

    @Test
    public void selectCredit() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.creditCardButton.doClick();
        Assert.assertEquals("Credit Card", hardwareGUI.card.cardSelectedString);
    }

    @Test
    public void selectDebit() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.debitCardButton.doClick();
        Assert.assertEquals("Debit Card", hardwareGUI.card.cardSelectedString);
    }

    @Test
    public void selectInvalidCard() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.invalidCardButton.doClick();
        Assert.assertEquals("Invalid Card", hardwareGUI.card.cardSelectedString);
    }

    @Test
    public void selectMembershipCard() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.membershipCardButton.doClick();
        Assert.assertEquals("Membership Card", hardwareGUI.card.cardSelectedString);
    }

    @Test
    public void creditCardSwipe() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.creditCardButton.doClick();
        try {
            hardwareGUI.card.swipeButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardSwiped);
    }

    @Test
    public void creditCardInsert() throws AWTException {
    	panelRobotSetup();
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.creditCardButton.doClick();
        try {
            hardwareGUI.card.insertButton.doClick();
        }
        catch (Exception e) {}
        panelRobot.delay(1000);
        Assert.assertTrue(cardListenerStub.cardInserted);
    }

    @Test
    public void creditCardTap() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.creditCardButton.doClick();
        try {
            hardwareGUI.card.tapButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardTapped);
    }
    
    @Test
    public void debitCardSwipe() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.debitCardButton.doClick();
        try {
            hardwareGUI.card.swipeButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardSwiped);
    }

    @Test
    public void debitCardInsert() throws AWTException {
    	panelRobotSetup();
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.creditCardButton.doClick();
        try {
            hardwareGUI.card.insertButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardInserted);
    }

    @Test
    public void debitCardTap() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.creditCardButton.doClick();
        try {
            hardwareGUI.card.tapButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardTapped);
    }

    @Test
    public void invalidCardSwipe() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.invalidCardButton.doClick();
        try {
            hardwareGUI.card.swipeButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardSwiped);
    }

    @Test
    public void invalidCardInsert() throws AWTException {
    	panelRobotSetup();
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.invalidCardButton.doClick();
        try {
            hardwareGUI.card.tapButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardTapped);
    }

    @Test
    public void invalidCardTap() {
        hardwareGUI.buttonPanel.startButton.doClick();
        hardwareGUI.card.invalidCardButton.doClick();
        try {
            hardwareGUI.card.tapButton.doClick();
        }
        catch (Exception e) {}
        Assert.assertTrue(cardListenerStub.cardTapped);
    }



}
