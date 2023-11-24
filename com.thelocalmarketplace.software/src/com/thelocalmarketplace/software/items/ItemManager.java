package com.thelocalmarketplace.software.items;

import java.math.BigDecimal;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.ProductNotFoundException;

/**
 * Manages aspects to adding items
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
	private Session session;
	private HashMap<BarcodedProduct, Integer> barcodedItems;
	private HashMap<BarcodedProduct, Integer> bulkyItems = new HashMap<BarcodedProduct, Integer>();
	private BarcodedProduct lastProduct;
	
	
	public ItemManager(Session session) {
		this.session = session;
		barcodedItems = session.getBarcodedItems();
		bulkyItems = session.getBulkyItems();
	}

	/**
	 * Adds a barcoded product to the hashMap of the barcoded products. Updates the
	 * expected weight and price
	 * of the system based on the weight and price of the product.
	 *
	 * @param product
	 *                The product to be added to the HashMap.
	 */
	public void addItem(BarcodedProduct product) {
		if (barcodedItems.containsKey(product)) {
			barcodedItems.replace(product, barcodedItems.get(product) + 1);
		} else {
			barcodedItems.put(product, 1);
		}
		double weight = product.getExpectedWeight();
		long price = product.getPrice();
		Mass mass = new Mass(weight);
		BigDecimal itemPrice = new BigDecimal(price);
		session.getWeight().update(mass);
		session.getFunds().update(itemPrice);
		lastProduct = product;
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
		BigDecimal ItemPrice = new BigDecimal(price);
		
		if (barcodedItems.containsKey(product) && barcodedItems.get(product) > 1 ) {
			barcodedItems.replace(product, barcodedItems.get(product)-1);
		} else if (barcodedItems.containsKey(product) && barcodedItems.get(product) == 1 ) { 
			barcodedItems.remove(product);
		} else {
			throw new ProductNotFoundException("Item not found");
		}
		
		session.getFunds().removeItemPrice(ItemPrice);
		
		if (bulkyItems.containsKey(product) && bulkyItems.get(product) >= 1 ) {
			bulkyItems.replace(product, bulkyItems.get(product)-1);
		} else if (bulkyItems.containsKey(product) && bulkyItems.get(product) == 1 ) {
			bulkyItems.remove(product);
		} else {
			session.getWeight().removeItemWeightUpdate(mass);
		}
	} 
	
}
