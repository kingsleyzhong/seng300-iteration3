package com.thelocalmarketplace.GUI.session;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.CustomBarUI;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;

/**
 * Represents the visual catalogue that allows a customer to add any item in the inventory.
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

public class SearchCatalogue extends JFrame{
	private static final long serialVersionUID = 1L;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Map<Product, Integer> inventory = ProductDatabases.INVENTORY;
	Session session;
	private JFrame frame = this;
	private HashMap<Product, ProductPanel>  hashMapForButtons;
	
	public SearchCatalogue(Session session) {
		this.session = session;
		this.setTitle("Search Catalog");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		
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
				frame.setVisible(false);
			}
			
		});
		orangePanel.add(backButton, BorderLayout.WEST);
		
		mainPanel.add(orangePanel, BorderLayout.NORTH);
		
		JPanel productPanel = new JPanel();
		productPanel.setLayout(new GridLayout(0,4,30,30));
		productPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		productPanel.setBackground(Colors.color1);
		
		hashMapForButtons = new HashMap<>();

		
		inventory.forEach((key, value) -> {
			productPanel.add(new ProductPanel(key, session));
			hashMapForButtons.put(key, new ProductPanel(key, session));

		});
		
		
		
		JScrollPane scroll = new JScrollPane(productPanel);
		scroll.setMaximumSize(screenSize);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUI(new CustomBarUI());
    	scroll.setBackground(Colors.color1);
		
    	mainPanel.add(scroll);
    	
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setAlwaysOnTop(true);
	}
	

	public HashMap<Product, ProductPanel> getHashMapForButtons() {
		return hashMapForButtons;
	}
	
}