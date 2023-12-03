package com.thelocalmarketplace.software.items;

import java.math.BigDecimal;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;

public interface ItemListener {

	void anItemHasBeenAdded(Product product, Mass mass, BigDecimal price);

	void anItemHasBeenRemoved(Product product, Mass mass, BigDecimal price);
	
	void aPLUCodeHasBeenEntered(PLUCodedProduct product);
}
