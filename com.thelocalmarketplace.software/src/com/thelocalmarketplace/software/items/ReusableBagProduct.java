package com.thelocalmarketplace.software.items;
import java.math.BigInteger;

/*
* Class that represents reusable bags in the selfcheckout system's software. 
* Allows for a price to be associated with an instance of ReusableBagProduct.
* 
* Allows for multiple types of ReusableBags to be offered at a given store
* 
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
*
*/
import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.Product;

public final class ReusableBagProduct extends Product{
	
	private static final Mass idealMass = new Mass(BigInteger.valueOf(5_000_000)); // this is copied from com.jjjwelectronics.bags.ReusableBag
																				   // to insure parity with the support Item type bag		

	public ReusableBagProduct(long price) {
		super(price, true); // bags are priced perEach, so they are a preEach item by default
	}
	
}
