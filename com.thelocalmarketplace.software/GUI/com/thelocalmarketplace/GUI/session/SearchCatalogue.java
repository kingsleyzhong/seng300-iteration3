package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.Session;

public class SearchCatalogue {

	private Map<Product, Integer> inventory = ProductDatabases.INVENTORY;
	Session session = new Session();
	
	public SearchCatalogue() {
		JFrame testing = new JFrame("Test");
		testing.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Colors.color1);
		
		JPanel orangePanel = new JPanel();
		orangePanel.setBackground(Colors.color5);
		orangePanel.setPreferredSize(new Dimension(5000, 50));
		orangePanel.setLayout(new BorderLayout());
		
		JButton backButton = new PlainButton("Cancel",  Colors.color5);
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				testing.setVisible(false);
			}
			
		});
		orangePanel.add(backButton, BorderLayout.WEST);
		
		mainPanel.add(orangePanel, BorderLayout.NORTH);
		//mainPanel.add(new ProductPanel());
		
		JPanel productPanel = new JPanel();
		productPanel.setLayout(new GridLayout(0,4,30,30));
		productPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		productPanel.setBackground(Colors.color1);
		
		inventory.forEach((key, value) -> {
			productPanel.add(new ProductPanel(key, session));
		});
		
		JScrollPane scroll = new JScrollPane(productPanel);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    	scroll.setBackground(Colors.color1);
		
    	mainPanel.add(scroll);
    	
		testing.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		testing.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//testing.setAlwaysOnTop(true);
		testing.setVisible(true);
	}
	
	public static void main(String[] args) {
		Barcode barcode1 = new Barcode(new Numeral[] { Numeral.one});
		BarcodedProduct product1 = new BarcodedProduct(barcode1, "baaakini", 15, 300.0);
		SelfCheckoutStationLogic.populateDatabase(barcode1, product1, 10);
		
		Barcode barcode2 = new Barcode(new Numeral[] {Numeral.two});
		BarcodedProduct product2 = new BarcodedProduct(barcode2, "wooly warm blanket", 60, 1000.0);
		SelfCheckoutStationLogic.populateDatabase(barcode2, product2, 5);
		
		Barcode barcode3 = new Barcode(new Numeral[] {Numeral.three});
		BarcodedProduct product3 = new BarcodedProduct(barcode3, "baaanana bread bites", 5, 125.0);
		SelfCheckoutStationLogic.populateDatabase(barcode3, product3, 20);
		
		PriceLookUpCode plu1 = new PriceLookUpCode(new String("0000"));
		PLUCodedProduct pluProduct1 = new PLUCodedProduct(plu1, "baaananas", 10);
		SelfCheckoutStationLogic.populateDatabase(plu1, pluProduct1, 10);
		
		PriceLookUpCode plu2 = new PriceLookUpCode(new String("0000"));
		PLUCodedProduct pluProduct2 = new PLUCodedProduct(plu2, "baaakliva", 10);
		SelfCheckoutStationLogic.populateDatabase(plu2, pluProduct2, 10);
		new SearchCatalogue();
	}
	
}
