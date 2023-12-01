package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;

import javax.swing.JPanel;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.banknote.Banknote;
import com.tdc.coin.Coin;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;

import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
		Bills.setLayout(new GridLayout(5, 1, 15, 15));
		
		JButton FiveBillBtn = new PlainButton("5$ Bill", Colors.color1);
		FiveBillBtn.setForeground(Colors.color3);
		FiveBillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getBanknoteInput().receive(Five_Dollar_Bill);
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Bill Not Accepted");
				}
			}
		});
		Bills.add(FiveBillBtn);
		
		JButton btnBill_1 = new PlainButton("10$ Bill", Colors.color1);
		btnBill_1.setForeground(Colors.color3);
		
		JButton TwentyBillBtn = new PlainButton("20$ Bill", Colors.color1);
		TwentyBillBtn.setForeground(Colors.color3);
		TwentyBillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Bills.add(TwentyBillBtn);
		
		JButton FiftyBillBtn = new PlainButton("50$ Bill", Colors.color1);
		FiftyBillBtn.setForeground(Colors.color3);
		Bills.add(FiftyBillBtn);
		
		JButton HundredBillButton = new PlainButton("100$ Bill", Colors.color1);
		HundredBillButton.setForeground(Colors.color3);
		Bills.add(HundredBillButton);
		
		JButton NonBillBtn = new PlainButton("Non-Bill", Colors.color1);
		NonBillBtn.setForeground(Colors.color3);
		Bills.add(NonBillBtn);
		
		
		JButton RemoveInputBill = new JButton("Remove Input Bill");
		RemoveInputBill.setBackground(Color.GREEN);
		RemoveInputBill.setForeground(Colors.color3);
		RemoveInputBill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scs.getBanknoteInput().removeDanglingBanknote();
			}
		});
		Bills.add(RemoveInputBill);
		
		JButton RemoveChangeBillBtn = new JButton("Remove Change Bill");
		Bills.add(RemoveChangeBillBtn);

		
		
		//coin panel
		JPanel Coins = new JPanel();
		Coins.setBorder(new TitledBorder(null, "COINS", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		Coins.setBackground(Colors.color3);
		add(Coins);
		Coins.setLayout(new GridLayout(5, 1, 15, 15));
		
		JButton button = new PlainButton("1¢", Colors.color1);
		button.setForeground(Colors.color3);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				
				
				
			}
		});
		Coins.add(button);
		
		JButton button_1 = new PlainButton("5¢", Colors.color1);
		button_1.setForeground(Colors.color3);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		Coins.add(button_1);
		
		JButton button_2 = new PlainButton("10¢", Colors.color1);
		button_2.setForeground(Colors.color3);
		Coins.add(button_2);
		
		JButton button_3 = new PlainButton("25¢", Colors.color1);
		button_3.setForeground(Colors.color3);
		Coins.add(button_3);
		
		JButton button_4 = new PlainButton("$1", Colors.color1);
		button_4.setForeground(Colors.color3);
		Coins.add(button_4);
		
		JButton button_5 = new PlainButton("$2", Colors.color1);
		button_5.setForeground(Colors.color3);
		Coins.add(button_5);
		
		JButton btnNoncoin = new PlainButton("Non-coin", Colors.color1);
		btnNoncoin.setForeground(Colors.color3);
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
