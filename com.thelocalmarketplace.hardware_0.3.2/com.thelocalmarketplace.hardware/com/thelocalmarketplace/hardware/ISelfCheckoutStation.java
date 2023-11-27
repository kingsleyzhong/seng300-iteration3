package com.thelocalmarketplace.hardware;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.jjjwelectronics.screen.ITouchScreen;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.ICoinDispenser;

import powerutility.PowerGrid;

/**
 * Base type for self-checkout stations.
 * 
 * @author TheLocalMarketplace
 */
public interface ISelfCheckoutStation {
	/**
	 * Represents the large scale where items are to be placed once they have been
	 * scanned or otherwise entered.
	 * 
	 * @return The bagging area.
	 */
	public IElectronicScale getBaggingArea();

	/**
	 * Represents the small scale used to weigh items that are sold by weight.
	 * 
	 * @return The scanning area scale.
	 */
	public IElectronicScale getScanningArea();

	/**
	 * Represents a touch screen display on which is shown a graphical user
	 * interface.
	 * 
	 * @return The touch screen.
	 */
	public ITouchScreen getScreen();

	/**
	 * Represents the dispenser of reusable-bags.
	 * 
	 * @return The reusable bag dispenser.
	 */
	public IReusableBagDispenser getReusableBagDispenser();

	/**
	 * Represents a printer for receipts.
	 * 
	 * @return The receipt printer.
	 */
	public IReceiptPrinter getPrinter();

	/**
	 * Represents a device that can read electronic cards, through one or more input
	 * modes according to the setup of the card.
	 * 
	 * @return The card reader.
	 */
	public ICardReader getCardReader();

	/**
	 * Represents a large, central barcode scanner.
	 * 
	 * @return The main scanner.
	 */
	public IBarcodeScanner getMainScanner();

	/**
	 * Represents a handheld, secondary barcode scanner.
	 * 
	 * @return The handheld scanner.
	 */
	public IBarcodeScanner getHandheldScanner();

	/**
	 * Represents a device that permits banknotes to be entered.
	 * 
	 * @return The banknote input slot.
	 */
	public BanknoteInsertionSlot getBanknoteInput();

	/**
	 * Represents a device that permits banknotes to be given to the customer.
	 * 
	 * @return The banknote output slot.
	 */
	public BanknoteDispensationSlot getBanknoteOutput();

	/**
	 * Represents a device that checks the validity of a banknote, and determines
	 * its denomination.
	 * 
	 * @return The banknote validator device.
	 */
	public BanknoteValidator getBanknoteValidator();

	/**
	 * Represents a device that stores banknotes.
	 * 
	 * @return The banknote storage unit.
	 */
	public BanknoteStorageUnit getBanknoteStorage();

	/**
	 * Represents the set of denominations supported by the self-checkout system.
	 * 
	 * @return The banknote denominations supported by this station.
	 */
	public BigDecimal[] getBanknoteDenominations();

	/**
	 * Represents the set of banknote dispensers, indexed by the denomination that
	 * each contains. Note that nothing prevents banknotes of the wrong denomination
	 * to be loaded into a given dispenser.
	 * 
	 * @return The banknote dispenser devices, indexed by the denomination managed
	 *             by each.
	 */
	public Map<BigDecimal, IBanknoteDispenser> getBanknoteDispensers();

	/**
	 * Represents a device that permits coins to be entered.
	 * 
	 * @return The coin input.
	 */
	public CoinSlot getCoinSlot();

	/**
	 * Represents a device that checks the validity of a coin, and determines its
	 * denomination.
	 * 
	 * @return The coin validator device.
	 */
	public CoinValidator getCoinValidator();

	/**
	 * Represents a device that stores coins that have been entered by customers.
	 * 
	 * @return The coin storage unit.
	 */
	public CoinStorageUnit getCoinStorage();

	/**
	 * Represents the set of denominations of coins supported by this self-checkout
	 * system.
	 * 
	 * @return The set of coin denominations supported by this station.
	 */
	public List<BigDecimal> getCoinDenominations();

	/**
	 * Represents the set of coin dispensers, indexed by the denomination of coins
	 * contained by each.
	 * 
	 * @return The coin dispenser devices, indexed by the denomination managed by
	 *             each.
	 */
	public Map<BigDecimal, ICoinDispenser> getCoinDispensers();

	/**
	 * Represents a device that receives coins to return to the customer.
	 * 
	 * @return The coin output.
	 */
	public CoinTray getCoinTray();

	/**
	 * Plugs in all the devices in the station.
	 * 
	 * @param grid
	 *            The power grid to plug into. Cannot be null.
	 */
	public void plugIn(PowerGrid grid);

	/**
	 * Unplugs all the devices in the station.
	 */
	public void unplug();

	/**
	 * Turns on all the devices in the station.
	 */
	public void turnOn();

	/**
	 * Turns off all the devices in the station.
	 */
	public void turnOff();
}