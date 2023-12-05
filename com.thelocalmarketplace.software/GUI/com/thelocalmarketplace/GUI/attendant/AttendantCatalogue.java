package com.thelocalmarketplace.GUI.attendant;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.CustomBarUI;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.attendant.Attendant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Displays the result of the text search.
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

public class AttendantCatalogue extends JFrame {

	private static final long serialVersionUID = 1L;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private Map<String, Product> inventory;
	Session session;
	private static JFrame frame;
	
	public AttendantCatalogue(Session session, Attendant attendant) {
		frame = this;
		inventory = attendant.getTextSearchController().getSearchResults();
		this.session = session;
		this.setTitle("Search Catalog");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Colors.color1);
		
		JPanel orangePanel = new JPanel();
		orangePanel.setBackground(Colors.color5);
		orangePanel.setPreferredSize(new Dimension(5000, 60));
		orangePanel.setLayout(new BorderLayout());
		
		JButton backButton = new PlainButton("Cancel",  Colors.color5);
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
			}
			
		});
		
		orangePanel.add(backButton, BorderLayout.WEST);
		mainPanel.add(orangePanel, BorderLayout.NORTH);
		
		JPanel productPanel = new JPanel();
		productPanel.setLayout(new GridLayout(0,4,30,30));
		productPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		productPanel.setBackground(Colors.color1);
		
		inventory.forEach((key, value) -> {
			productPanel.add(new ProductItem(value, session, attendant));
		});
		
		JScrollPane scroll = new JScrollPane(productPanel);
		scroll.setMaximumSize(screenSize);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUI(new CustomBarUI());
    	scroll.setBackground(Colors.color1);
		
    	mainPanel.add(scroll);
    	
		getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setVisible(true);
		setAlwaysOnTop(true);
	}
	
	public static void remove() {
		frame.dispose();
	}
}
