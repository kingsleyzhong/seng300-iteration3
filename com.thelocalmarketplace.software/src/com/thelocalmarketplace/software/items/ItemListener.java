package com.thelocalmarketplace.software.items;

import java.math.BigDecimal;

import com.jjjwelectronics.Mass;

public interface ItemListener {
	
	void anItemHasBeenAdded(Mass mass, BigDecimal price);
	
	void anItemHasBeenRemoved(Mass mass, BigDecimal price);
}
