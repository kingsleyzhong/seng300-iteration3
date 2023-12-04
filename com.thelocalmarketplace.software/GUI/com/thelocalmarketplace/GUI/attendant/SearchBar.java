package com.thelocalmarketplace.GUI.attendant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.session.SearchCatalogue;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.attendant.TextSearchController;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;

public class SearchBar extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2319944040517901209L;
	JPanel searchPanel;
	JTextField searchField;
	SearchCatalogue catalogue;
	String searchText;
	JButton searchGo;
	TextSearchController text;
	private JTextField textField;
	
	public SearchBar(TextSearchController textSearch) {
		text=textSearch;
		setBackground(Colors.color1);
		setLayout(new BorderLayout());
		
		searchField = new JTextField();
		searchField.setHorizontalAlignment(SwingConstants.CENTER);
		searchField.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		JButton searchGo = new PlainButton("SEARCH", Colors.color5);
		searchGo.setFont(new Font("Tahoma", Font.PLAIN, 16));
		searchGo.addActionListener(this);
		
		add(searchField, BorderLayout.CENTER);
		add(searchGo, BorderLayout.EAST);
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = searchField.getText();
		text.textSearchProduct(s);
	}
	
}
