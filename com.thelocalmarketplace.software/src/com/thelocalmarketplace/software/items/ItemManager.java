package com.thelocalmarketplace.software.items;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Manages aspects to adding items
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

public class ItemManager {
	protected ArrayList<ItemListener> listeners = new ArrayList<>();
	private HashMap<Product, BigInteger> addedProducts = new HashMap<Product, BigInteger>(); //hashMap for both barcodedProduct and PLUCodedProduct
	private HashMap<BarcodedProduct, Integer> bulkyItems = new HashMap<BarcodedProduct, Integer>();
	private BarcodedProduct lastProduct;
	private Session session;
	private boolean addItems = false;

	public ItemManager(Session session) {
		this.session = session;
	}

	public void setAddItems(boolean value) {
		addItems = value;
	}

	/**
	 * Adds a barcoded product to the hashMap of the products. Updates the
	 * expected weight and price
	 * of the system based on the weight and price of the product.
	 *
	 * @param product
	 *                The product to be added to the HashMap.
	 */
	public void addItem(BarcodedProduct product) {
		if (addItems) {
			if (addedProducts.containsKey(product)) {
				addedProducts.replace(product, addedProducts.get(product).add(BigInteger.valueOf(1)));
			} else {
				addedProducts.put(product, BigInteger.valueOf(1));
			}
			double weight = product.getExpectedWeight();
			long price = product.getPrice();
			Mass mass = new Mass(weight);
			BigDecimal itemPrice = new BigDecimal(price);
			lastProduct = product;

			notifyItemAdded(mass, itemPrice);
		}
	}
	
	
	//Method for adding PLU coded item
	public void addItem(PLUCodedProduct product, Mass mass) {
		if(addItems) {
			addedProducts.put(product, mass.inMicrograms());
			
			BigDecimal price = new BigDecimal(product.getPrice());
			final int MICROGRAM_PER_KILOGRAM = 1_000_000_000;
			BigDecimal weightInKilogram = BigDecimal.valueOf(mass.inMicrograms().doubleValue()/MICROGRAM_PER_KILOGRAM);
			BigDecimal itemPrice = price.multiply(weightInKilogram);
			
			notifyItemAdded(mass, itemPrice);
		}
	}

	public void addBulkyItem() {
		if (bulkyItems.containsKey(lastProduct)) {
			bulkyItems.replace(lastProduct, bulkyItems.get(lastProduct) + 1);
		} else {
			bulkyItems.put(lastProduct, 1);
		}
	}

	/**
	 * Removes a selected product from the hashMap of barcoded items.
	 * Updates the weight and price of the products.
	 * 
	 * @param product
	 *                The product to be removed from the HashMap.
	 */
	public void removeItem(BarcodedProduct product) {
		double weight = product.getExpectedWeight();
		long price = product.getPrice();
		Mass mass = new Mass(weight);
		BigDecimal itemPrice = new BigDecimal(price);

		if (addedProducts.containsKey(product) && addedProducts.get(product).intValue() > 1) {
			addedProducts.replace(product, addedProducts.get(product).subtract(BigInteger.valueOf(1)));
		} else if (addedProducts.containsKey(product) && addedProducts.get(product).intValue() == 1) {
			addedProducts.remove(product);
		} else {
			throw new ProductNotFoundException("Item not found");
		}

		HashMap<BarcodedProduct, Integer> bulkyItems = session.getBulkyItems();
		if (bulkyItems.containsKey(product) && bulkyItems.get(product) >= 1) {
			bulkyItems.replace(product, bulkyItems.get(product) - 1);
			mass = new Mass(0);
		} else if (bulkyItems.containsKey(product) && bulkyItems.get(product) == 1) {
			bulkyItems.remove(product);
			mass = new Mass(0);
		}

		notifyItemRemoved(mass, itemPrice);
	}

	public void notifyItemAdded(Mass mass, BigDecimal price) {
		for (ItemListener l : listeners)
			l.anItemHasBeenAdded(mass, price);
	}

	public void notifyItemRemoved(Mass mass, BigDecimal price) {
		for (ItemListener l : listeners)
			l.anItemHasBeenRemoved(mass, price);
	}

	/**
	 * Methods for adding funds listeners to the funds
	 */
	public synchronized boolean deregister(ItemListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

		return listeners.remove(listener);
	}

	public synchronized void deregisterAll() {
		listeners.clear();
	}

	public final synchronized void register(ItemListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");

		listeners.add(listener);
	}

	public HashMap<Product, BigInteger> getItems() {
		return addedProducts;
	}

	public HashMap<BarcodedProduct, Integer> getBulkyItems() {
		return bulkyItems;
	}

}
