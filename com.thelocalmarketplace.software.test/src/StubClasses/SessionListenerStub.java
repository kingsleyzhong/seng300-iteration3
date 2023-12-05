package StubClasses;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionListener;
import com.thelocalmarketplace.software.attendant.Requests;

public class SessionListenerStub implements SessionListener{
	public Requests request;
	public HashMap<Product, BigInteger> products = new HashMap<Product, BigInteger>();
	public BigDecimal currentExpectedPrice;
	public boolean discrepancy;
	public PLUCodedProduct pluProduct;
	public boolean sessionEnded;
	
	@Override
	public void getRequest(Session session, Requests request) {
		this.request = request;
		
	}

	@Override
	public void itemAdded(Session session, Product product, Mass ofProduct, Mass currentExpectedWeight,
			BigDecimal currentExpectedPrice) {
		products.put(product, BigInteger.valueOf(1));
		this.currentExpectedPrice = currentExpectedPrice;
		
	}

	@Override
	public void itemRemoved(Session session, Product product, Mass ofProduct, Mass currentExpectedMass,
			BigDecimal currentExpectedPrice) {
		this.products.remove(product);
		this.currentExpectedPrice = currentExpectedPrice;
	}

	@Override
	public void addItemToScaleDiscrepancy(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeItemFromScaleDiscrepancy(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discrepancy(Session session, String message) {
		discrepancy = true;
		
	}

	@Override
	public void discrepancyResolved(Session session) {
		discrepancy = false;
		
	}

	public void pricePaidUpdated(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionAboutToStart(Session session) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionEnded(Session session) {
		this.sessionEnded = true;
		
	}

	@Override
	public void pluCodeEntered(PLUCodedProduct product) {
		this.pluProduct = product;
		
	}

	@Override
	public void pricePaidUpdated(Session session, BigDecimal amountDue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionStateChanged() {
		// TODO Auto-generated method stub
		
	}

}
