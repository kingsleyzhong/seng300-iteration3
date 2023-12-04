package com.thelocalmarketplace.GUI.attendant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.thelocalmarketplace.GUI.session.SearchCatalogue;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class SearchBar extends JPanel implements ActionListener {
	JPanel searchPanel;
	JTextField searchField;
	SearchCatalogue catalogue;
	String searchText;
	JButton searchGo;
	private Map<Product, Integer> inventory;
	private JTextField textField;
	
	public SearchBar(Map<Product, Integer> inventory) {
		this.inventory = inventory;
		
		setLayout(new BorderLayout());
		
		searchField = new JTextField();
		searchField.setHorizontalAlignment(SwingConstants.CENTER);
		searchField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JButton searchGo = new JButton("SEARCH");
		searchGo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		add(searchField, BorderLayout.CENTER);
		add(searchGo, BorderLayout.EAST);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
	
}
