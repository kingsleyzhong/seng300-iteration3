package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigInteger;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.CustomBarUI;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.items.ReusableBagProduct;

/**
 * A class that allows products to be displayed on the screen in a scrollable list.
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
 */

public class AddedProducts extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JPanel centralPanel;
	private Session session;
	
	private HashMap<Product, CartProduct> productList = new HashMap<Product, CartProduct>();

	/**
	 * Create the panel.
	 */
	public AddedProducts(Session session) {
		this.session = session;
		
		centralPanel = new JPanel();
    	centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS));
    	centralPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
    	centralPanel.setBackground(Colors.color1);
    	JScrollPane scroll = new JScrollPane(centralPanel);
    	scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	scroll.getVerticalScrollBar().setUI(new CustomBarUI());
    	scroll.setBackground(Colors.color1);
    	this.setLayout(new BorderLayout());
    	add(scroll, BorderLayout.CENTER);
    	
	}
	
	public void addProduct(Product product, Mass mass) {
		CartProduct current = null;
		
		if(productList.containsKey(product)) {
			current = productList.get(product);
		}
		
		if(current == null) {
			CartProduct newPanel = new CartProduct(product, session, mass);
			centralPanel.add(newPanel);
			centralPanel.add(Box.createRigidArea(new Dimension(0, 20)));
			centralPanel.repaint();
			centralPanel.revalidate();
			productList.put(product, newPanel);
		}
		else {
			if(product instanceof BarcodedProduct) {
				current.addProduct();
			}else if (product instanceof ReusableBagProduct){
				current.addProduct(session.getItems().get(product).intValue());
			}
			else {
				BigInteger massinMicrograms = session.getItems().get(product);
				current.updateMass((new Mass(massinMicrograms)).inGrams());
			}
		}
	}

	public void removeProduct(Product product, Mass mass) {
		CartProduct current = null;
		
		if(productList.containsKey(product)) {
			current = productList.get(product);
		}
		if(current == null) {
			//Do nothing
		}
		else {
			boolean value = current.removeProduct();
			if(!value) {
				centralPanel.remove(current);
				centralPanel.repaint();
				centralPanel.revalidate();
				productList.remove(product);
			}
		}
	}
	
	public HashMap<Product,CartProduct> getProducts() {
		return productList;
	}
	
	public boolean contains(Product product) {
		return productList.containsKey(product);
	}
	
	public JPanel getPanel(Product product) {
		if(productList.containsKey(product)) {
			return productList.get(product);
		}
		return null;
	}
	
	public int amount() {
		return productList.size();
	}
}
