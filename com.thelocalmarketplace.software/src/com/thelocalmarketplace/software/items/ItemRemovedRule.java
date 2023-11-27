package com.thelocalmarketplace.software.items;

/**
 * Class that handles involvement of removing items/products
 * from the session/item manager
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

public class ItemRemovedRule {

	private ItemManager itemManager;
	/**
	 * Will need a method to accept an input from GUI to remove later
	 * Leaving as barcode for now
	 */

	public void removeWithGUI(Barcode barcode) {
		Map<Barcode, BarcodedProduct> database = ProductDatabases.BARCODED_PRODUCT_DATABASE;
		// Checks if product is in database. Throws exception if not in database.
        	if (database.containsKey(barcode)) {
            		BarcodedProduct product = database.get(barcode);
            		itemManager.removeItem(product);
        	} else {
            		throw new InvalidArgumentSimulationException("Not in database");
        	}
    	}

	public ItemRemovedRule(ItemManager itemManager) {
		// TODO Auto-generated constructor stub
	}

}
