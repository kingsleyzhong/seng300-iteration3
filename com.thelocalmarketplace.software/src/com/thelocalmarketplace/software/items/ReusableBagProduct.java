package com.thelocalmarketplace.software.items;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.Product;

import java.math.BigInteger;

public final class ReusableBagProduct extends Product{
	
	private static final Mass idealMass = new Mass(BigInteger.valueOf(5_000_000)); // this is copied from com.jjjwelectronics.bags.ReusableBag
	private static double bagPrice;																			   // to insure parity with the support Item type bag		
	private static ReusableBagProduct bag;
	private static int amtAdded = 0;
	private static String description;
			
	private ReusableBagProduct() {
		super((long)bagPrice, true); // bags are priced perEach, so they are a preEach item by default
	}
	
	public static ReusableBagProduct getBag() {
		return bag;
	}
	
	public double getExpectedWeight() {
		return idealMass.inGrams().doubleValue();
	}
	public String getDescription() {
		return description;
	}
	public static void setAdded(int amt) {
		amtAdded = amt;
	}
	public static int getAdded() {
		return amtAdded;
	}
	public static void setPrice(double newPrice) {
		bagPrice = newPrice;
		description = "Store Reusable Bag";
		bag = new ReusableBagProduct();
	}
	
	
}
