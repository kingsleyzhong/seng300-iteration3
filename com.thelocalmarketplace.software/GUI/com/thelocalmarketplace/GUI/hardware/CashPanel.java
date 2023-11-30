package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import java.awt.Color;
import javax.swing.JLabel;

public class CashPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JPanel panel2;
	
	
	
	public CashPanel() {
		
		this.setBackground(Colors.color1);
		setLayout(new GridLayout(1, 0, 0, 0));
		
		
		JPanel Bills = new JPanel();
		Bills.setBackground(Color.GREEN);
		this.setBackground(Colors.color5);
		add(Bills);
		Bills.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel Coins = new JPanel();
		Coins.setBackground(Color.YELLOW);
		add(Coins);
		
		
		
	}
	
	
}
