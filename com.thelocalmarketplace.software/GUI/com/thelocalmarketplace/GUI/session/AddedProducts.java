package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
	
	private HashMap<ArrayList<Object>, CartProduct> productList = new HashMap<ArrayList<Object>, CartProduct>();
	private ArrayList<ArrayList<Object>> products = new ArrayList<ArrayList<Object>>();
	private ArrayList<CartProduct> panels = new ArrayList<CartProduct>();

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
		ArrayList<Object> temp = new ArrayList<Object>();
		temp.add(product);
		temp.add(mass);
		
		for(int i = 0; i<products.size(); i++) {
			if(temp.equals(products.get(i))) {
				current = panels.get(i);
			}
		}
		/*
		if(productList.containsKey(temp)) {
			current = productList.get(temp);
		}
		*/
		if(current == null || !(product instanceof BarcodedProduct)) {
			CartProduct newPanel = new CartProduct(product, session, mass);
			centralPanel.add(newPanel);
			centralPanel.add(Box.createRigidArea(new Dimension(0, 20)));
			centralPanel.repaint();
			centralPanel.revalidate();
			products.add(temp);
			panels.add(newPanel);
		}
		else {
			current.addProduct();
		}
	}

	public void removeProduct(Product product, Mass mass) {
		CartProduct current = null;
		ArrayList<Object> temp = new ArrayList<Object>();
		temp.add(product);
		temp.add(mass);
		
		for(int i = 0; i<products.size(); i++) {
			if(temp.equals(products.get(i))) {
				current = panels.get(i);
			}
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
			}
		}
	}
}
