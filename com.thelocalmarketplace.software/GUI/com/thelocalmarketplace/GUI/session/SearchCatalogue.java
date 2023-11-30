package com.thelocalmarketplace.GUI.session;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SearchCatalogue {

	
	public SearchCatalogue() {
		JFrame testing = new JFrame("Test");
		testing.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel mainPanel = new JPanel();
		
		
		
		testing.getContentPane().add(new ProductPanel());
		
		testing.setVisible(true);
	}
	
	public static void main(String[] args) {
		new SearchCatalogue();
	}
	
}
