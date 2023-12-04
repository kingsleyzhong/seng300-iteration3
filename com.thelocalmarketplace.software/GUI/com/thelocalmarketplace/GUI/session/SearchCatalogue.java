package com.thelocalmarketplace.GUI.session;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.CustomBarUI;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.session.ProductPanel;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.Session;

public class SearchCatalogue extends JFrame{
	private static final long serialVersionUID = 1L;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Map<Product, Integer> inventory = ProductDatabases.INVENTORY;
	Session session;
	private JFrame frame = this;
	
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
		
		inventory.forEach((key, value) -> {
			productPanel.add(new ProductPanel(key, session));
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
}