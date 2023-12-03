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

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.awt.event.ActionEvent;

public class CashPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private AbstractSelfCheckoutStation scs;
	
	public JButton FiveBillBtn;
	public JButton button_one_coin;
	
	//coins
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
		
		
		//5 Dollar Bill
		FiveBillBtn = new PlainButton("5$ Bill", Colors.color1);
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
		
		
		//10 Dollar Bill logic
		JButton TenBillBtn = new PlainButton("10$ Bill", Colors.color1);
		TenBillBtn.setForeground(Colors.color3);
		TenBillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getBanknoteInput().receive(Ten_Dollar_Bill);
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Bill Not Accepted");
				}
			}
		});
		Bills.add(TenBillBtn);
		
		
		//20 Dollar Bill logic
		JButton TwentyBillBtn = new PlainButton("20$ Bill", Colors.color1);
		TwentyBillBtn.setForeground(Colors.color3);
		TwentyBillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getBanknoteInput().receive(Twenty_Dollar_Bill);
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Bill Not Accepted");
				}
			}
		});
		Bills.add(TwentyBillBtn);
		
		
		//50 Dollar Bill logic
		JButton FiftyBillBtn = new PlainButton("50$ Bill", Colors.color1);
		FiftyBillBtn.setForeground(Colors.color3);
		FiftyBillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getBanknoteInput().receive(Fifty_Dollar_Bill);
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Bill Not Accepted");
				}
			}
		});
		Bills.add(FiftyBillBtn);
		
		
		//100 Dollar Bill logic
		JButton HundredBillButton = new PlainButton("100$ Bill", Colors.color1);
		HundredBillButton.setForeground(Colors.color3);
		HundredBillButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getBanknoteInput().receive(Hundred_Dollar_Bill);
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Bill Not Accepted");
				}
			}
		});
		Bills.add(HundredBillButton);
		
		
		//NON-Bill Logic
		JButton NonBillBtn = new PlainButton("Non-Bill", Colors.color1);
		NonBillBtn.setForeground(Colors.color3);
		NonBillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getBanknoteInput().receive(Non_Bill);
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Bill Not Accepted");
				}
			}
		});
		Bills.add(NonBillBtn);
		
		
		//Remove input bill
		JButton RemoveInputBill = new PlainButton("Remove Input Bill", Colors.color1);
		RemoveInputBill.setForeground(Colors.color3);
		RemoveInputBill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Banknote banknote = scs.getBanknoteInput().removeDanglingBanknote();
					DecimalFormat df = new DecimalFormat("#.00");
					String s = "$" + df.format(banknote.getDenomination());
					JOptionPane.showMessageDialog(null, s);
				} catch (NullPointerSimulationException e1) {
					JOptionPane.showMessageDialog(null, "There is nothing here.");
				}
			}
		});
		Bills.add(RemoveInputBill);
		
		
		//remove output bill
		JButton RemoveChangeBillBtn = new PlainButton("Remove Change Bill", Colors.color1);
		RemoveChangeBillBtn.setForeground(Colors.color3);
		RemoveChangeBillBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					List<Banknote> banknotes = scs.getBanknoteOutput().removeDanglingBanknotes();
					DecimalFormat df = new DecimalFormat("#.00");
					String s = "You collected:\n";
					for(int i = 0; i < banknotes.size(); i++) {
						s = s + "$" + df.format(banknotes.get(i).getDenomination()) + "\n";
					}
					JOptionPane.showMessageDialog(null, s);
				} catch(NullPointerSimulationException e1) {
					JOptionPane.showMessageDialog(null, "There is nothing here.");
				}
			}
		});
		Bills.add(RemoveChangeBillBtn);

		
		
		//coin panel
		JPanel Coins = new JPanel();
		Coins.setBorder(new TitledBorder(null, "COINS", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		Coins.setBackground(Colors.color3);
		add(Coins);
		Coins.setLayout(new GridLayout(5, 1, 15, 15));
		
		//5 cent coin button
		JButton button_five_cent = new PlainButton("5¢", Colors.color1);
		button_five_cent.setForeground(Colors.color3);
		button_five_cent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getCoinSlot().receive(Five_Cent_Coin);
					
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Coin Not Accepted");
				}
				
				
			}
		});
		Coins.add(button_five_cent);
		
		
		//TEN CENT COIN
		JButton button_ten_cent = new PlainButton("10¢", Colors.color1);
		button_ten_cent.setForeground(Colors.color3);
		button_ten_cent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getCoinSlot().receive( Ten_Cent_Coin );
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Coin Not Accepted");
				}
				
				
			}
		});
		Coins.add(button_ten_cent);
		
		
		//25 cent coin
		JButton button_twentyfive_cent = new PlainButton("25¢", Colors.color1);
		button_twentyfive_cent.setForeground(Colors.color3);
		button_twentyfive_cent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getCoinSlot().receive( TwentyFive_Cent_Coin );
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Coin Not Accepted");
				}
				
				
			}
		});
		Coins.add(button_twentyfive_cent);
		
		
		//1 Dollar Coin
		button_one_coin = new PlainButton("$1", Colors.color1);
		button_one_coin.setForeground(Colors.color3);
		button_one_coin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getCoinSlot().receive( One_Dollar_Coin );
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Coin Not Accepted");
				}
				
				
			}
		});
		Coins.add(button_one_coin);
		
		
		//2 Dollar coin
		JButton btn_two_coin = new PlainButton("$2", Colors.color1);
		btn_two_coin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getCoinSlot().receive( Two_Dollar_Coin );
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Coin Not Accepted");
				}
			}
		});
		btn_two_coin.setForeground(Colors.color3);
		Coins.add(btn_two_coin);
		
		JButton btnNoncoin = new PlainButton("Non-coin", Colors.color1);
		btnNoncoin.setForeground(Colors.color3);
		btnNoncoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					scs.getCoinSlot().receive( Non_Coin );
					//No exception gets thrown here, it just doesn't add it to the total
					
				} catch (DisabledException | CashOverloadException e1) {
					JOptionPane.showMessageDialog(null, "Coin Not Accepted");
				}
				
				
			}
		});
		Coins.add(btnNoncoin);
		
		
		//REMOVE COINS FROM TRAY
		JButton btn_remove_coins = new PlainButton("Remove Coin Tray Coins", Colors.color1);
		btn_remove_coins.setForeground(Colors.color3);
		btn_remove_coins.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				List<Coin> coins = scs.getCoinTray().collectCoins();
				DecimalFormat df = new DecimalFormat("#.00");
				String s = "You collected:\n";
				for(int i = 0; i < coins.size(); i++) {
					s = s + "$" + df.format(coins.get(i).getValue()) + "\n";
				}
				if(coins.isEmpty()) {
					JOptionPane.showMessageDialog(null, "There are no coins to collect");
				}
				else {
					JOptionPane.showMessageDialog(null, s);
				}
			}
		});
		Coins.add(btn_remove_coins);

		
		
	
		
	}
	
	public void setUpCashData() {
		Currency currency = Currency.getInstance(Locale.CANADA);
		
		Five_Cent_Coin = new Coin(currency, new BigDecimal(0.05));
		
		Ten_Cent_Coin  = new Coin(currency, new BigDecimal(0.10));
		TwentyFive_Cent_Coin  = new Coin(currency, BigDecimal.valueOf(0.25));
		
		One_Dollar_Coin  = new Coin(currency, new BigDecimal(1));
		Two_Dollar_Coin  = new Coin(currency, new BigDecimal(2));
		
		//Coin that should be rejected
		Non_Coin  = new Coin(currency, new BigDecimal(0.03));
		
		//bills
		Five_Dollar_Bill = new Banknote(currency, new BigDecimal(5));
		Ten_Dollar_Bill  = new Banknote(currency, new BigDecimal(10));
		Twenty_Dollar_Bill  = new Banknote(currency, new BigDecimal(20));
		Fifty_Dollar_Bill  = new Banknote(currency, new BigDecimal(50));
		Hundred_Dollar_Bill  = new Banknote(currency, new BigDecimal(100));
		
		//Bill that should be rejected
		Non_Bill  = new Banknote(Currency.getInstance( Locale.CANADA )   , BigDecimal.valueOf(5000.00));
		
		
		
	}
	
	
}