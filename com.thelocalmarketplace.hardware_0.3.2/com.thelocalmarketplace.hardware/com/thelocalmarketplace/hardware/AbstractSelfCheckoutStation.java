package com.thelocalmarketplace.hardware;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.jjjwelectronics.bag.IReusableBagDispenser;
import com.jjjwelectronics.card.ICardReader;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.scale.IElectronicScale;
import com.jjjwelectronics.scanner.IBarcodeScanner;
import com.jjjwelectronics.screen.ITouchScreen;
import com.jjjwelectronics.screen.TouchScreenBronze;
import com.tdc.Sink;
import com.tdc.banknote.AbstractBanknoteDispenser;
import com.tdc.banknote.Banknote;
import com.tdc.banknote.BanknoteDispensationSlot;
import com.tdc.banknote.BanknoteInsertionSlot;
import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.banknote.BanknoteValidator;
import com.tdc.banknote.IBanknoteDispenser;
import com.tdc.coin.AbstractCoinDispenser;
import com.tdc.coin.Coin;
import com.tdc.coin.CoinDispenserGold;
import com.tdc.coin.CoinSlot;
import com.tdc.coin.CoinStorageUnit;
import com.tdc.coin.CoinValidator;
import com.tdc.coin.ICoinDispenser;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
import powerutility.PowerGrid;

/**
 * Abstract base class for self-checkout stations.
 * 
 * @author TheLocalMarketplace
 */
public abstract class AbstractSelfCheckoutStation implements ISelfCheckoutStation  {
	static {
		resetConfigurationToDefaults();
	}

	protected static int reusableBagDispenserCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the reusable-bag dispenser.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureReusableBagDispenserCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		reusableBagDispenserCapacityConfiguration = count;
	}

	protected static int coinDispenserCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin dispensers.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinDispenserCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinDispenserCapacityConfiguration = count;
	}

	protected static int banknoteStorageUnitCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the banknote storage unit.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureBanknoteStorageUnitCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		banknoteStorageUnitCapacityConfiguration = count;
	}

	protected static int coinStorageUnitCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin storage unit.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinStorageUnitCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinStorageUnitCapacityConfiguration = count;
	}

	protected static int coinTrayCapacityConfiguration;

	/**
	 * Configures the maximum capacity of the coin tray.
	 * 
	 * @param count
	 *            The maximum capacity.
	 */
	public static void configureCoinTrayCapacity(int count) {
		if(count <= 0)
			throw new InvalidArgumentSimulationException("Count must be positive.");
		coinTrayCapacityConfiguration = count;
	}

	protected static Currency currencyConfiguration;

	/**
	 * Configures the currency to be supported.
	 * 
	 * @param curr
	 *            The currency to be supported.
	 */
	public static void configureCurrency(Currency curr) {
		if(curr == null)
			throw new NullPointerSimulationException("currency");
		currencyConfiguration = curr;
	}

	protected static BigDecimal[] banknoteDenominationsConfiguration;

	/**
	 * Configures the set of banknote denominations.
	 * 
	 * @param denominations
	 *            The denominations to use for banknotes.
	 */
	public static void configureBanknoteDenominations(BigDecimal[] denominations) {
		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		HashSet<BigDecimal> set = new HashSet<>();
		for(BigDecimal denomination : denominations) {
			if(denomination.compareTo(BigDecimal.ZERO) <= 0)
				throw new InvalidArgumentSimulationException("Each denomination must be positive.");

			set.add(denomination);
		}

		if(set.size() != denominations.length)
			throw new InvalidArgumentSimulationException("The denominations must all be unique.");

		// Copy the array to avoid the potential for a security hole
		banknoteDenominationsConfiguration = Arrays.copyOf(denominations, denominations.length);
	}

	protected static List<BigDecimal> coinDenominationsConfiguration;

	/**
	 * Configures the set of coin denominations.
	 * 
	 * @param denominations
	 *            The denominations to use for coins.
	 */
	public static void configureCoinDenominations(BigDecimal[] denominations) {
		if(denominations == null)
			throw new NullPointerSimulationException("denominations");

		if(denominations.length < 1)
			throw new InvalidArgumentSimulationException("There must be at least one denomination.");

		HashSet<BigDecimal> set = new HashSet<>();
		for(BigDecimal denomination : denominations) {
			if(denomination.compareTo(BigDecimal.ZERO) <= 0)
				throw new InvalidArgumentSimulationException("Each denomination must be positive.");

			set.add(denomination);
		}

		if(set.size() != denominations.length)
			throw new InvalidArgumentSimulationException("The denominations must all be unique.");

		// Copy the array to avoid the potential for a security hole
		coinDenominationsConfiguration = new ArrayList<BigDecimal>();
		for(BigDecimal denomination : denominations)
			coinDenominationsConfiguration.add(denomination);
	}

	protected static double scaleMaximumWeightConfiguration;

	/**
	 * Configures the maximum weight permitted for the scales.
	 * 
	 * @param weight
	 *            The maximum weight permitted for the scales.
	 */
	public static void configureScaleMaximumWeight(double weight) {
		if(weight <= 0.0)
			throw new InvalidArgumentSimulationException("The maximum weight must be positive.");

		scaleMaximumWeightConfiguration = weight;
	}

	protected static double scaleSensitivityConfiguration;

	/**
	 * Configures the sensitivity of the scales.
	 * 
	 * @param sensitivity
	 *            The sensitivity of the scales.
	 */
	public static void configureScaleSensitivity(double sensitivity) {
		if(sensitivity <= 0.0)
			throw new InvalidArgumentSimulationException("The sensitivity must be positive.");

		scaleSensitivityConfiguration = sensitivity;
	}

	/**
	 * Resets the configuration to the default values.
	 */
	public static void resetConfigurationToDefaults() {
		banknoteDenominationsConfiguration = new BigDecimal[] { BigDecimal.ONE };
		banknoteStorageUnitCapacityConfiguration = 1000;
		coinDenominationsConfiguration = new ArrayList<>();
		coinDenominationsConfiguration.add(BigDecimal.valueOf(1L));
		coinDispenserCapacityConfiguration = 100;
		coinStorageUnitCapacityConfiguration = 1000;
		coinTrayCapacityConfiguration = 25;
		currencyConfiguration = Currency.getInstance(Locale.CANADA);
		reusableBagDispenserCapacityConfiguration = 100;
		scaleMaximumWeightConfiguration = 5000.0;
		scaleSensitivityConfiguration = 0.5;
	}

	protected IElectronicScale baggingArea;
	protected IElectronicScale scanningArea;
	protected ITouchScreen screen;
	protected IReusableBagDispenser reusableBagDispenser;
	protected IReceiptPrinter printer;
	protected ICardReader cardReader;
	protected IBarcodeScanner mainScanner;
	protected IBarcodeScanner handheldScanner;
	protected BanknoteInsertionSlot banknoteInput;
	protected BanknoteDispensationSlot banknoteOutput;
	protected BanknoteValidator banknoteValidator;
	protected BanknoteStorageUnit banknoteStorage;
	protected BigDecimal[] banknoteDenominations;
	protected Map<BigDecimal, IBanknoteDispenser> banknoteDispensers;
	protected CoinSlot coinSlot;
	protected CoinValidator coinValidator;
	protected CoinStorageUnit coinStorage;
	protected List<BigDecimal> coinDenominations;
	protected Map<BigDecimal, ICoinDispenser> coinDispensers;
	protected CoinTray coinTray;

	protected AbstractSelfCheckoutStation(IElectronicScale baggingArea, IElectronicScale scanningArea,
		ITouchScreen screen, IReusableBagDispenser bagDispenser, IReceiptPrinter printer, ICardReader cardReader,
		IBarcodeScanner mainScanner, IBarcodeScanner handheldScanner, BanknoteInsertionSlot banknoteInput,
		BanknoteDispensationSlot banknoteOutput, BanknoteValidator banknoteValidator,
		BanknoteStorageUnit banknoteStorage, BigDecimal[] banknoteDenominations,
		Map<BigDecimal, IBanknoteDispenser> banknoteDispensers, CoinSlot coinSlot, CoinValidator coinValidator,
		CoinStorageUnit coinStorage, List<BigDecimal> coinDenominations, Map<BigDecimal, ICoinDispenser> coinDispensers,
		CoinTray coinTray) {
		this.baggingArea = baggingArea;
		this.scanningArea = scanningArea;
		this.screen = screen;
		this.reusableBagDispenser = bagDispenser;
		this.printer = printer;
		this.cardReader = cardReader;
		this.mainScanner = mainScanner;
		this.handheldScanner = handheldScanner;
		this.banknoteInput = banknoteInput;
		this.banknoteOutput = banknoteOutput;
		this.banknoteValidator = banknoteValidator;
		this.banknoteStorage = banknoteStorage;
		this.banknoteDenominations = banknoteDenominations;
		this.banknoteDispensers = banknoteDispensers;
		this.coinSlot = coinSlot;
		this.coinValidator = coinValidator;
		this.coinStorage = coinStorage;
		this.coinDenominations = coinDenominations;
		this.coinDispensers = coinDispensers;
		this.coinTray = coinTray;
	}

	/**
	 * Represents the large scale where items are to be placed once they have been
	 * scanned or otherwise entered.
	 * 
	 * @return The bagging area.
	 */
	@Override
	public IElectronicScale getBaggingArea() {
		return baggingArea;
	}

	/**
	 * Represents the small scale used to weigh items that are sold by weight.
	 * 
	 * @return The scanning area scale.
	 */
	@Override
	public IElectronicScale getScanningArea() {
		return scanningArea;
	}

	/**
	 * Represents a touch screen display on which is shown a graphical user
	 * interface.
	 * 
	 * @return The touch screen.
	 */
	@Override
	public ITouchScreen getScreen() {
		return screen;
	}

	/**
	 * Represents the dispenser of reusable-bags.
	 * 
	 * @return The reusable bag dispenser.
	 */
	@Override
	public IReusableBagDispenser getReusableBagDispenser() {
		return reusableBagDispenser;
	}

	/**
	 * Represents a printer for receipts.
	 * 
	 * @return The receipt printer.
	 */
	@Override
	public IReceiptPrinter getPrinter() {
		return printer;
	}

	/**
	 * Represents a device that can read electronic cards, through one or more input
	 * modes according to the setup of the card.
	 * 
	 * @return The card reader.
	 */
	@Override
	public ICardReader getCardReader() {
		return cardReader;
	}

	/**
	 * Represents a large, central barcode scanner.
	 * 
	 * @return The main scanner.
	 */
	@Override
	public IBarcodeScanner getMainScanner() {
		return mainScanner;
	}

	/**
	 * Represents a handheld, secondary barcode scanner.
	 * 
	 * @return The handheld scanner.
	 */
	@Override
	public IBarcodeScanner getHandheldScanner() {
		return handheldScanner;
	}

	/**
	 * Represents a device that permits banknotes to be entered.
	 * 
	 * @return The banknote input slot.
	 */
	@Override
	public BanknoteInsertionSlot getBanknoteInput() {
		return banknoteInput;
	}

	/**
	 * Represents a device that permits banknotes to be given to the customer.
	 * 
	 * @return The banknote output slot.
	 */
	@Override
	public BanknoteDispensationSlot getBanknoteOutput() {
		return banknoteOutput;
	}

	/**
	 * Represents a device that checks the validity of a banknote, and determines
	 * its denomination.
	 * 
	 * @return The banknote validator device.
	 */
	@Override
	public BanknoteValidator getBanknoteValidator() {
		return banknoteValidator;
	}

	/**
	 * Represents a device that stores banknotes.
	 * 
	 * @return The banknote storage unit.
	 */
	@Override
	public BanknoteStorageUnit getBanknoteStorage() {
		return banknoteStorage;
	}

	/**
	 * Represents the set of denominations supported by the self-checkout system.
	 * 
	 * @return The banknote denominations supported by this station.
	 */
	@Override
	public BigDecimal[] getBanknoteDenominations() {
		return banknoteDenominations;
	}

	/**
	 * Represents the set of banknote dispensers, indexed by the denomination that
	 * each contains. Note that nothing prevents banknotes of the wrong denomination
	 * to be loaded into a given dispenser.
	 * 
	 * @return The banknote dispenser devices, indexed by the denomination managed
	 *             by each.
	 */
	@Override
	public Map<BigDecimal, IBanknoteDispenser> getBanknoteDispensers() {
		return banknoteDispensers;
	}

	/**
	 * Represents a device that permits coins to be entered.
	 * 
	 * @return The coin input.
	 */
	@Override
	public CoinSlot getCoinSlot() {
		return coinSlot;
	}

	/**
	 * Represents a device that checks the validity of a coin, and determines its
	 * denomination.
	 * 
	 * @return The coin validator device.
	 */
	@Override
	public CoinValidator getCoinValidator() {
		return coinValidator;
	}

	/**
	 * Represents a device that stores coins that have been entered by customers.
	 * 
	 * @return The coin storage unit.
	 */
	@Override
	public CoinStorageUnit getCoinStorage() {
		return coinStorage;
	}

	/**
	 * Represents the set of denominations of coins supported by this self-checkout
	 * system.
	 * 
	 * @return The set of coin denominations supported by this station.
	 */
	@Override
	public List<BigDecimal> getCoinDenominations() {
		return coinDenominations;
	}

	/**
	 * Represents the set of coin dispensers, indexed by the denomination of coins
	 * contained by each.
	 * 
	 * @return The coin dispenser devices, indexed by the denomination managed by
	 *             each.
	 */
	@Override
	public Map<BigDecimal, ICoinDispenser> getCoinDispensers() {
		return coinDispensers;
	}

	/**
	 * Represents a device that receives coins to return to the customer.
	 * 
	 * @return The coin output.
	 */
	@Override
	public CoinTray getCoinTray() {
		return coinTray;
	}

	private AttendantStation supervisor = null;

	boolean isSupervised() {
		return supervisor != null;
	}

	void setSupervisor(AttendantStation supervisor) {
		this.supervisor = supervisor;
	}

	/**
	 * Plugs in all the devices in the station.
	 * 
	 * @param grid
	 *            The power grid to plug into. Cannot be null.
	 */
	@Override
	public void plugIn(PowerGrid grid) {
		baggingArea.plugIn(grid);
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.connect(grid);
		banknoteInput.connect(grid);
		banknoteOutput.connect(grid);
		banknoteStorage.connect(grid);
		banknoteValidator.connect(grid);
		cardReader.plugIn(grid);
		for(ICoinDispenser cd : coinDispensers.values())
			cd.connect(grid);
		coinSlot.connect(grid);
		coinStorage.connect(grid);
		// Don't turn on the coin tray
		coinValidator.connect(grid);
		handheldScanner.plugIn(grid);
		mainScanner.plugIn(grid);
		printer.plugIn(grid);
		scanningArea.plugIn(grid);
		screen.plugIn(grid);
		reusableBagDispenser.plugIn(grid);
	}

	/**
	 * Unplugs all the devices in the station.
	 */
	@Override
	public void unplug() {
		baggingArea.unplug();
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.disconnect();
		banknoteInput.disconnect();
		banknoteOutput.disconnect();
		banknoteStorage.disconnect();
		banknoteValidator.disconnect();
		cardReader.unplug();
		for(ICoinDispenser cd : coinDispensers.values())
			cd.disconnect();
		coinSlot.disconnect();
		coinStorage.disconnect();
		// Don't turn on the coin tray
		coinValidator.disconnect();
		handheldScanner.unplug();
		mainScanner.unplug();
		printer.unplug();
		scanningArea.unplug();
		screen.unplug();
		reusableBagDispenser.unplug();
	}

	/**
	 * Turns on all the devices in the station.
	 */
	@Override
	public void turnOn() {
		baggingArea.turnOn();
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.activate();
		banknoteInput.activate();
		banknoteOutput.activate();
		banknoteStorage.activate();
		banknoteValidator.activate();
		cardReader.turnOn();
		for(ICoinDispenser cd : coinDispensers.values())
			cd.activate();
		coinSlot.activate();
		coinStorage.activate();
		// Don't turn on the coin tray
		coinValidator.activate();
		handheldScanner.turnOn();
		mainScanner.turnOn();
		printer.turnOn();
		scanningArea.turnOn();
		screen.turnOn();
		reusableBagDispenser.turnOn();
	}

	/**
	 * Turns off all the devices in the station.
	 */
	@Override
	public void turnOff() {
		baggingArea.turnOff();
		for(IBanknoteDispenser bd : banknoteDispensers.values())
			bd.disactivate();
		banknoteInput.disactivate();
		banknoteOutput.disactivate();
		banknoteStorage.disactivate();
		banknoteValidator.disactivate();
		cardReader.turnOff();
		for(ICoinDispenser cd : coinDispensers.values())
			cd.disactivate();
		coinSlot.disactivate();
		coinStorage.disactivate();
		// Don't turn on the coin tray
		coinValidator.disactivate();
		handheldScanner.turnOff();
		mainScanner.turnOff();
		printer.turnOff();
		scanningArea.turnOff();
		screen.turnOff();
		reusableBagDispenser.turnOff();
	}

	protected void interconnect(BanknoteInsertionSlot slot, BanknoteValidator validator) {
		TwoWayChannel<Banknote> channel = new TwoWayChannel<Banknote>(slot, validator);
		slot.sink = channel;
		validator.source = channel;
	}

	protected void interconnect(BanknoteValidator validator, BanknoteStorageUnit storage) {
		OneWayChannel<Banknote> channel = new OneWayChannel<Banknote>(storage);
		validator.sink = channel;
	}

	protected void interconnect(IBanknoteDispenser dispenser, BanknoteDispensationSlot slot) {
		OneWayChannel<Banknote> channel = new OneWayChannel<Banknote>(slot);
		((AbstractBanknoteDispenser)dispenser).sink = channel;
	}

	protected void interconnect(CoinSlot slot, CoinValidator validator) {
		OneWayChannel<Coin> channel = new OneWayChannel<Coin>(validator);
		slot.sink = channel;
	}

	protected void interconnect(ICoinDispenser dispenser, CoinTray tray) {
		OneWayChannel<Coin> channel = new OneWayChannel<Coin>(tray);
		((AbstractCoinDispenser)dispenser).sink = channel;
	}
}