package com.thelocalmarketplace.software.items;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.Product;

import java.math.BigInteger;

public final class ReusableBagProduct extends Product{
	
	private static final Mass idealMass = new Mass(BigInteger.valueOf(5_000_000)); // this is copied from com.jjjwelectronics.bags.ReusableBag
	private static long bagPrice;																			   // to insure parity with the support Item type bag		
	private static ReusableBagProduct bag;
			
	private ReusableBagProduct() {
		super(bagPrice, true); // bags are priced perEach, so they are a preEach item by default
	}
	
	public static ReusableBagProduct getBag() {
		return bag;
	}
	
	public double getExpectedWeight() {
		return idealMass.inGrams().doubleValue();
	}
	
	public static void setPrice(long newPrice) {
		bagPrice = newPrice;
		bag = new ReusableBagProduct();
	}
	
	
}
