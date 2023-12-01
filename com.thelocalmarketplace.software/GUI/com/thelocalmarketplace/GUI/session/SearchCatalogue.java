package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;

public class SearchCatalogue {

	
	public SearchCatalogue() {
		JFrame testing = new JFrame("Test");
		testing.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBackground(Colors.color1);
		
		JPanel orangePanel = new JPanel();
		orangePanel.setBackground(Colors.color5);
		orangePanel.setPreferredSize(new Dimension(5000, 50));
		
		mainPanel.add(orangePanel, BorderLayout.NORTH);
		
		testing.getContentPane().add(mainPanel, BorderLayout.CENTER);
		
		testing.setVisible(true);
	}
	
	public static void main(String[] args) {
		new SearchCatalogue();
	}
	
}
