package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.MatteBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.awt.event.ActionEvent;

public class CashPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private AbstractSelfCheckoutStation scs;
	
	
	//coins
	private Coin One_Cent_Coin;
	private Coin Five_Cent_Coin;
	private Coin Ten_Cent_Coin;
	private Coin TwentyFive_Cent_Coin;
	
	private Coin One_Dollar_Coin;
	private Coin Two_Dollar_Coin;
	
	private Coin Non_Coin;
	
	//bills
	private Banknote Five_Dollar_Bill;
	private Banknote Ten_Dollar_Bill;
	private Banknote Twenty_Dollar_Bill;
	private Banknote Fifty_Dollar_Bill;
	private Banknote Hundred_Dollar_Bill;
	
	private Banknote Non_Bill;
	
	
	public CashPanel(AbstractSelfCheckoutStation scs) {
		this.scs = scs;
		
		setUpCashData();
		
		
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
	
	public void setUpCashData() {
		Currency currency = Currency.getInstance(Locale.CANADA);
		
		One_Cent_Coin = new Coin(currency, BigDecimal.valueOf(0.01));
		
		Five_Cent_Coin = new Coin(currency, BigDecimal.valueOf(0.05));
		
		Ten_Cent_Coin  = new Coin(currency, BigDecimal.valueOf(0.10));
		TwentyFive_Cent_Coin  = new Coin(currency, BigDecimal.valueOf(0.25));
		
		One_Dollar_Coin  = new Coin(currency, BigDecimal.valueOf(1.00));
		Two_Dollar_Coin  = new Coin(currency, BigDecimal.valueOf(2.00));
		
		//Coin that should be rejected
		//Coin Non_Coin  = new Coin(Currency.getInstance(Locale.CHINESE), BigDecimal.valueOf(1.05));
		
		//bills
		Five_Dollar_Bill = new Banknote(currency, BigDecimal.valueOf(5.00));
		Ten_Dollar_Bill  = new Banknote(currency, BigDecimal.valueOf(10.00));
		Twenty_Dollar_Bill  = new Banknote(currency, BigDecimal.valueOf(20.00));
		Fifty_Dollar_Bill  = new Banknote(currency, BigDecimal.valueOf(50.00));
		Hundred_Dollar_Bill  = new Banknote(currency, BigDecimal.valueOf(100.00));
		
		//Bill that should be rejected
		//Non_Bill  = new Banknote(Currency.getInstance( Locale.CHINESE )   , BigDecimal.valueOf(5.00));
		
		
		
	}
	
	
}
