package com.thelocalmarketplace.software.items;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;

import java.math.BigDecimal;

public interface ItemListener {

	void anItemHasBeenAdded(Product product, Mass mass, BigDecimal price);

	void anItemHasBeenRemoved(Product product, Mass mass, BigDecimal price);
	
	void aPLUCodeHasBeenEntered(PLUCodedProduct product);
}
