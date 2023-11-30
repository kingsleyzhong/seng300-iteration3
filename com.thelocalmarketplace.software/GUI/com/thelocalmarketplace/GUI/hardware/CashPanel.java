package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;

public class CashPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JPanel panel2;
	
	
	
	public CashPanel() {
		
		this.setBackground(Colors.color1);
		setLayout(new GridLayout(1, 0, 0, 0));
		
		
		JPanel cash_panel = new JPanel();
		this.setBackground(Colors.color5);
		add(cash_panel);
		
		
		
	}
	
	
}
