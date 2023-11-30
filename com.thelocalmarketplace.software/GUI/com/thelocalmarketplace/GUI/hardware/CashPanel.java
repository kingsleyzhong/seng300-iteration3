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
		setLayout(new GridLayout(1, 0, 20, 0));
		
		//bill panel
		JPanel Bills = new JPanel();
		Bills.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Bill Acceptor", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(51, 51, 51)));
		Bills.setBackground(Colors.color3);
		add(Bills);
		Bills.setLayout(new GridLayout(5, 1, 20, 20));
		
		JButton btnBill = new JButton("5$ Bill");
		btnBill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Bills.add(btnBill);
		
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
		
		JButton btnBill_3 = new JButton("100$ Bill");
		Bills.add(btnBill_3);
		
		JButton btnNonbill = new JButton("Non-Bill");
		Bills.add(btnNonbill);

		
		
		//coin panel
		JPanel Coins = new JPanel();
		Coins.setBorder(new TitledBorder(null, "COINS", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		Coins.setBackground(Colors.color3);
		add(Coins);
		Coins.setLayout(new GridLayout(5, 1, 20, 20));
		
		JButton button = new JButton("1¢");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Coins.add(button);
		
		JButton button_1 = new JButton("5¢");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Coins.add(button_1);
		
		JButton button_2 = new JButton("10¢");
		Coins.add(button_2);
		
		JButton button_3 = new JButton("25¢");
		Coins.add(button_3);
		
		JButton button_4 = new JButton("1$");
		Coins.add(button_4);
		
		JButton button_5 = new JButton("2$");
		Coins.add(button_5);
		
		JButton btnNoncoin = new JButton("Non-Coin");
		btnNoncoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Coins.add(btnNoncoin);
		
		//coin tray via a popup event
		
		
		
		
		//hanging bill via popup event
		
		
		
		
		
	}
	
	
}
