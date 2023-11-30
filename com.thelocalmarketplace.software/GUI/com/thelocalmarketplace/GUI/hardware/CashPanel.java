package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.MatteBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CashPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JPanel panel2;
	
	
	
	public CashPanel() {
		setBorder(new LineBorder(Colors.color1, 20));
		
		this.setBackground(Colors.color1);
		setLayout(new GridLayout(1, 0, 0, 0));
		
		//bill panel
		JPanel Bills = new JPanel();
		Bills.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Bill Acceptor", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		Bills.setBackground(Color.GREEN);
		this.setBackground(Colors.color5);
		add(Bills);
		Bills.setLayout(new GridLayout(5, 1, 20, 20));
		
		JButton btnBill_1 = new JButton("10$ Bill");
		Bills.add(btnBill_1);
		
		JButton btnNewButton = new JButton("20$ Bill");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Bills.add(btnNewButton);
		
		JButton btnBill_2 = new JButton("50$ Bill");
		Bills.add(btnBill_2);
		
		JButton btnNonbill = new JButton("Non-Bill");
		Bills.add(btnNonbill);
		
		JButton btnBill_3 = new JButton("100$ Bill");
		Bills.add(btnBill_3);
		
		JButton btnBill = new JButton("5$ Bill");
		btnBill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Bills.add(btnBill);

		
		
		//coin panel
		JPanel Coins = new JPanel();
		Coins.setBackground(Color.YELLOW);
		add(Coins);
		
		JLabel lblCoins = new JLabel("COINS");
		Coins.add(lblCoins);
		
		
		
	}
	
	
}
