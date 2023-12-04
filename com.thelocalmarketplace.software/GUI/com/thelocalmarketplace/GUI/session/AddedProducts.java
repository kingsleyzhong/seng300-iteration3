package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.math.BigInteger;
import java.util.ArrayList;
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
			}else {
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
