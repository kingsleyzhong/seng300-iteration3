package com.thelocalmarketplace.software.items;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;
import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

/**
 * Manages aspects to adding items to an order
 * 
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev 			: 30177060 
 * Enioluwafe Balogun 		: 30174298 
 * Subeg Chahal 			: 30196531 
 * Jun Heo 					: 30173430 
 * Emily Kiddle 			: 30122331 
 * Anthony Kostal-Vazquez 	: 30048301 
 * Jessica Li 				: 30180801 
 * Sua Lim 					: 30177039 
 * Savitur Maharaj 			: 30152888 
 * Nick McCamis 			: 30192610 
 * Ethan McCorquodale 		: 30125353 
 * Katelan Ng 				: 30144672 
 * Arcleah Pascual 			: 30056034 
 * Dvij Raval 				: 30024340 
 * Chloe Robitaille 		: 30022887 
 * Danissa Sandykbayeva 	: 30200531 
 * Emily Stein 				: 30149842 
 * Thi My Tuyen Tran 		: 30193980 
 * Aoi Ueki 				: 30179305 
 * Ethan Woo 				: 30172855 
 * Kingsley Zhong 			: 30197260 
 */

public class ItemManager {
	protected ArrayList<ItemListener> listeners = new ArrayList<>();
	private HashMap<Product, BigInteger> addedProducts = new HashMap<Product, BigInteger>(); //hashMap for both barcodedProduct and PLUCodedProduct
	private HashMap<BarcodedProduct, Integer> bulkyItems = new HashMap<BarcodedProduct, Integer>();
	private HashMap<String, Product> visualCatalogue = new HashMap<String, Product>();
	private HashMap<Product, Mass> PLUProductWeights = new HashMap<Product, Mass>();
	
	private PLUCodedProduct pluProduct;
	private Product lastProduct;
	private boolean addItems = false;
	private boolean addPLUItemState = false;

	public void setAddItems(boolean value) {
		addItems = value;
		addPLUItemState = false;
	}
	
	public boolean getAddItems() {
		return addItems;
	}
	
	//Get PLU code from the GUI
	public void addItem(PriceLookUpCode code) {
		if(addItems) { 
			if (ProductDatabases.PLU_PRODUCT_DATABASE.containsKey(code)) {
				addPLUItemState = true;
				pluProduct = ProductDatabases.PLU_PRODUCT_DATABASE.get(code);
				notifyPLUCode(pluProduct);
			} else {
				throw new InvalidActionException("Item not in Database");
			}
		}
	}
	
	public boolean isAddPLUItemState() {
		return addPLUItemState;
	}
	
	public PLUCodedProduct getPluProduct() {
		return pluProduct;
	}
	
	public Product getLastProduct() {
		return lastProduct;
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
		if(addItems) {
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
			
			notifyItemAdded(product, mass, itemPrice);
		}
	}
	
	
	//Method for adding PLU coded item
	public void addItem(PLUCodedProduct product, Mass mass) {
		if(addItems) {
			if (addedProducts.containsKey(product)) {
				addedProducts.replace(product, addedProducts.get(product).add(mass.inMicrograms()));
			} else {
				addedProducts.put(product, mass.inMicrograms());
				PLUProductWeights.put(product,mass);
			}
			lastProduct = product;
			BigDecimal price = new BigDecimal(product.getPrice());
			final int MICROGRAM_PER_KILOGRAM = 1_000_000_000;
			BigDecimal weightInKilogram = BigDecimal.valueOf(mass.inMicrograms().doubleValue()/MICROGRAM_PER_KILOGRAM);
			BigDecimal itemPrice = price.multiply(weightInKilogram);
			
			notifyItemAdded(product, mass, itemPrice);
		}
	}

	

	public void addBulkyItem() {
		if (bulkyItems.containsKey((BarcodedProduct)lastProduct)) {
			bulkyItems.replace((BarcodedProduct)lastProduct, bulkyItems.get(lastProduct) + 1);
		} else {
			bulkyItems.put((BarcodedProduct)lastProduct, 1);
		}
	}
	
	
	/**
	 * Update the hashmap of bought products, the expected weight, 
	 * and the total cost of customer's purchase after a bag is dispensed
	 * @param product - a reusable bag 
	 */
	public void addPurchasedBags(ReusableBagProduct product, int amt) {
		if (addedProducts.containsKey(product)) {
			addedProducts.replace(product, addedProducts.get(product).add(BigInteger.valueOf(amt)));
		} else {
			addedProducts.put(product, BigInteger.valueOf(amt));
		}
		double weight = product.getExpectedWeight()*amt;
		long price = product.getPrice()*amt;
		Mass mass = new Mass(weight);
		BigDecimal itemPrice = new BigDecimal(price);
		System.out.println("h");
		notifyItemAdded(product, mass, itemPrice);	
	}
	
	/**
	 * Removes a selected product from the hashMap of current session's products .
	 * Updates the weight and price of the products.
	 * @param product - reusable bag
	 */
	public void removeItem(ReusableBagProduct product) {
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
		
		notifyItemRemoved(product, mass, itemPrice);
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

		if (bulkyItems.containsKey(product) && bulkyItems.get(product) > 1 ) {
			bulkyItems.replace(product, bulkyItems.get(product)-1);
			mass = new Mass(0);
		} else if (bulkyItems.containsKey(product) && bulkyItems.get(product) == 1 ) {
			bulkyItems.remove(product);
			mass = new Mass(0);
		}
		
		notifyItemRemoved(product, mass, itemPrice);
	} 
	
	/**
	 *  this method will remove the entirety of a plu coded product from the addedProducts database and 
	 *  Subsequently remove all of these products from the transaction, if a customer wants a partial removal
	 *  they will need to remove all products then re add only the products that they want in the transaction.
	 *  
	 *  example: 3 tomatoes added from plu code but customer wants to remove 1, they need to remove all 3 then only
	 *  add the weight of 2.
	 * @param product the plu product to be removed
	 */
	public void removeItem(PLUCodedProduct product) {
		if (addedProducts.containsKey(product)) {
			BigDecimal price = new BigDecimal(product.getPrice());
			BigInteger productWeightMicro = addedProducts.get(product);
			final int MICROGRAM_PER_KILOGRAM = 1_000_000_000;
			//gets the weight in kilograms to find price
			BigInteger weightInKilogram = productWeightMicro.divide(BigInteger.valueOf(MICROGRAM_PER_KILOGRAM));
			BigDecimal itemPrice = price.multiply(BigDecimal.valueOf(weightInKilogram.doubleValue()));
			addedProducts.remove(product);
			PLUProductWeights.remove(product);
			notifyItemRemoved(product, new Mass(productWeightMicro), itemPrice);
		} else {
			throw new ProductNotFoundException("Item not found");
		}
		
	}
	
	public void notifyItemAdded(Product product, Mass mass, BigDecimal price) {
		for (ItemListener l : listeners)
			l.anItemHasBeenAdded(product, mass, price);
	}
	
	public void notifyItemRemoved(Product product, Mass mass, BigDecimal price) {
		for (ItemListener l : listeners)
			l.anItemHasBeenRemoved(product, mass, price);
	}
	
	public void notifyPLUCode(PLUCodedProduct product) {
		for (ItemListener l : listeners)
			l.aPLUCodeHasBeenEntered(product);
	}
	
	
	/**
	 * Methods for adding funds listeners to the items
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
	
	public ArrayList<ItemListener> getListeners(){
		return listeners;
	}

	public HashMap<Product, BigInteger> getItems() {
		return addedProducts;
	}
	
	public HashMap<BarcodedProduct, Integer> getBulkyItems(){
		return bulkyItems;
	}

	public HashMap<String, Product> getVisualCatalogue() {
		return visualCatalogue;
	}

	public void clear() {
		addedProducts = new HashMap<>();
	}
}
