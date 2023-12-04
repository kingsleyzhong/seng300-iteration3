package com.thelocalmarketplace.GUI.attendant;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.session.SearchCatalogue;
import com.thelocalmarketplace.software.attendant.TextSearchController;

import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.SwingConstants;

/**
 * Displays the text box that allows an attendant to search for items. Works with the
 * keyboard of the device simulating the system.
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
