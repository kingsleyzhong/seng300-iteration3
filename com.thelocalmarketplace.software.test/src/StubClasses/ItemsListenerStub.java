package StubClasses;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.items.ItemListener;

public class ItemsListenerStub implements ItemListener{
	private HashMap<Product, BigInteger> products = new HashMap<Product, BigInteger>();
	private BigDecimal price = BigDecimal.ZERO;
	PLUCodedProduct product;

	@Override
	public void anItemHasBeenAdded(Product product, Mass mass, BigDecimal price) {
		products.put(product, BigInteger.valueOf(1));
		price =  price.add(price);
		
	}

	@Override
	public void anItemHasBeenRemoved(Product product, Mass mass, BigDecimal price) {
		products.put(product, products.get(product).subtract(BigInteger.valueOf(1)));
		price = price.subtract(price);
		
	}
	
	public HashMap<Product, BigInteger> getProducts(){
		return products;
	}

	public BigDecimal getPrice() {
		return price;
	}

	@Override
	public void aPLUCodeHasBeenEntered(PLUCodedProduct product) {
		this.product = product;
		
	}
	
	public PLUCodedProduct getProduct() {
		return product;
	}
}
