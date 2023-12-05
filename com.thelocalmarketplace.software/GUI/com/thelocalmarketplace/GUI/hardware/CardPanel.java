package com.thelocalmarketplace.GUI.hardware;

import javax.swing.JPanel;

import com.jjjwelectronics.card.BlockedCardException;
import com.jjjwelectronics.card.Card;
import com.jjjwelectronics.card.InvalidPINException;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.customComponents.RoundPanel;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.funds.CardIssuerDatabase;
import com.thelocalmarketplace.software.funds.SupportedCardIssuers;
import com.thelocalmarketplace.software.membership.MembershipDatabase;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Panel that holds user interaction with scanning/tapping/inserting a card.
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

public class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private AbstractSelfCheckoutStation scs;
	
	public Card cardSelected;
	public String cardSelectedString = "Credit Card";
	public Card debitCard;
	public Card creditCard;
	public Card invalidCard;
	public Card membershipCard;
	public JLabel infoLabel;
	public JPanel infoPanel;
	public JPanel panel2;
	public JButton creditCardButton;
	public JButton debitCardButton;
	public JButton invalidCardButton;
	public JButton membershipCardButton;
	
	private boolean inserted = false;
	
	public JButton swipeButton;
	public JButton insertButton;
	public JButton tapButton;
	/**
	 * Create the panel.
	 */
	public CardPanel(AbstractSelfCheckoutStation scs) {
		this.scs = scs;
		setupCardData();
		
		this.setBackground(Colors.color1);
		setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel panel = new JPanel();
		panel.setBackground(Colors.color1);
		add(panel);
		
		panel.setLayout(new GridLayout(0, 1, 0, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		JLabel label = new JLabel("Select a Card");
		label.setForeground(Colors.color3);
		panel.add(label);
		
		creditCardButton = new PlainButton("Credit Card", Colors.color3);
		creditCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = creditCard;
				cardSelectedString = "Credit Card";
				updateSelection();
			}
			
		});
		panel.add(creditCardButton);
		
		debitCardButton = new PlainButton("Debit Card", Colors.color3);
		debitCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = debitCard;
				cardSelectedString = "Debit Card";
				updateSelection();
			}
			
		});
		panel.add(debitCardButton);
		
		invalidCardButton = new PlainButton("Invalid Card", Colors.color3);
		invalidCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = invalidCard;
				cardSelectedString = "Invalid Card";
				updateSelection();
			}
			
		});
		panel.add(invalidCardButton);
		
		membershipCardButton = new PlainButton("Membership Card", Colors.color3);
		membershipCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = membershipCard;
				cardSelectedString = "Membership Card";
				updateSelection();
			}
			
		});
		panel.add(membershipCardButton);
		
		panel2 = new JPanel();
		panel2.setBackground(Colors.color1);
		panel2.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		GridBagLayout gbl_panel2 = new GridBagLayout();
		gbl_panel2.columnWidths = new int[]{82, 0, 0};
		gbl_panel2.rowHeights = new int[]{32, 19, 36, 36, 36, 0};
		gbl_panel2.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_panel2.rowWeights = new double[]{0.0, 0.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
		panel2.setLayout(gbl_panel2);
		
		swipeButton = new PlainButton("Swipe", Colors.color3);
		swipeButton.addActionListener(new CardListener());
		JLabel label1 = new JLabel("Card Selected:   ");
		label1.setForeground(Colors.color3);
		label1.setHorizontalTextPosition(JLabel.CENTER);
		GridBagConstraints gbc_label1 = new GridBagConstraints();
		gbc_label1.fill = GridBagConstraints.BOTH;
		gbc_label1.insets = new Insets(0, 0, 5, 5);
		gbc_label1.gridx = 0;
		gbc_label1.gridy = 0;
		panel2.add(label1, gbc_label1);
		infoPanel = new RoundPanel(15, Colors.color4);
		infoPanel.setBackground(Colors.color1);
		infoLabel = new JLabel(cardSelectedString);
		infoLabel.setForeground(Colors.color1);
		infoLabel.setHorizontalTextPosition(JLabel.CENTER);
		infoPanel.add(infoLabel);
		GridBagConstraints gbc_infoPanel = new GridBagConstraints();
		gbc_infoPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_infoPanel.insets = new Insets(0, 0, 5, 0);
		gbc_infoPanel.gridx = 1;
		gbc_infoPanel.gridy = 0;
		panel2.add(infoPanel, gbc_infoPanel);
		
		GridBagConstraints gbc_swipeButton = new GridBagConstraints();
		gbc_swipeButton.gridwidth = 2;
		gbc_swipeButton.fill = GridBagConstraints.BOTH;
		gbc_swipeButton.insets = new Insets(0, 0, 5, 5);
		gbc_swipeButton.gridx = 0;
		gbc_swipeButton.gridy = 2;
		gbc_swipeButton.insets = new Insets(10,0,10,0);
		panel2.add(swipeButton, gbc_swipeButton);
		insertButton = new PlainButton("Insert", Colors.color3);
		insertButton.addActionListener(new CardListener());
		GridBagConstraints gbc_insertButton = new GridBagConstraints();
		gbc_insertButton.gridwidth = 2;
		gbc_insertButton.fill = GridBagConstraints.BOTH;
		gbc_insertButton.insets = new Insets(0, 0, 5, 5);
		gbc_insertButton.gridx = 0;
		gbc_insertButton.gridy = 3;
		gbc_insertButton.insets = new Insets(10,0,10,0);
		panel2.add(insertButton, gbc_insertButton);
		
		add(panel2);
		tapButton = new PlainButton("Tap", Colors.color3);
		tapButton.addActionListener(new CardListener());
		GridBagConstraints gbc_tapButton = new GridBagConstraints();
		gbc_tapButton.gridwidth = 2;
		gbc_tapButton.insets = new Insets(0, 0, 0, 5);
		gbc_tapButton.fill = GridBagConstraints.BOTH;
		gbc_tapButton.gridx = 0;
		gbc_tapButton.gridy = 4;
		gbc_tapButton.insets = new Insets(10,0,10,0);
		panel2.add(tapButton, gbc_tapButton);
		
		
	}
	
	protected void updateSelection() {
		infoLabel.setText(cardSelectedString);
		infoLabel.revalidate();
	}

	public void setupCardData() {
		ArrayList<CardIssuer> supportedCardsClasses = new ArrayList<CardIssuer>();
		
		CardIssuer ci1 = new CardIssuer(SupportedCardIssuers.ONE.getIssuer(), 1);
		CardIssuer ci2 = new CardIssuer(SupportedCardIssuers.TWO.getIssuer(), 5);
		CardIssuer ci3 = new CardIssuer(SupportedCardIssuers.THREE.getIssuer(), 99);
		CardIssuer ci4 = new CardIssuer(SupportedCardIssuers.FOUR.getIssuer(), 2);
		
		supportedCardsClasses.add(ci1);
		supportedCardsClasses.add(ci2);
		supportedCardsClasses.add(ci3);
		supportedCardsClasses.add(ci4);
		
		int index = 0;
		for (SupportedCardIssuers supportedCards : SupportedCardIssuers.values()) {
			CardIssuerDatabase.CARD_ISSUER_DATABASE.put(supportedCards.getIssuer(), supportedCardsClasses.get(index));
			index++;
		}
		
		debitCard = new Card(SupportedCardIssuers.ONE.getIssuer(), "5299334598001547", "Brandon Chan", "666", "1234", true, true);
		creditCard = new Card(SupportedCardIssuers.TWO.getIssuer(), "4504389022574000", "Doris Giles", "343", "1234", true, true);
		invalidCard = new Card(SupportedCardIssuers.THREE.getIssuer(), "1111111111111111", "Not A Real Person", "420", "1234", true, true);
		
		membershipCard = new Card("membership", "1234", "John Doe", "", "", false,
	            false);
		MembershipDatabase.registerMember("1234", "John Doe");

		Calendar exp = Calendar.getInstance();
		exp.set(Calendar.YEAR, 2099);
		exp.set(Calendar.MONTH, 12);

		ci1.addCardData(debitCard.number, debitCard.cardholder, exp, debitCard.cvv, 10000);
		ci2.addCardData(creditCard.number, creditCard.cardholder, exp, creditCard.cvv, 7500);
		ci3.addCardData("0", invalidCard.cardholder, exp, invalidCard.cvv, 1000);
		//ci4.addCardData(membershipCard.number, membershipCard.cardholder, exp, membershipCard.cvv, 2000);
		
		cardSelected = creditCard;
	}
	
	public class CardListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			if(source == swipeButton) {
				try {
					scs.getCardReader().swipe(cardSelected);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Card swipe failed.");
				}
			}
			else if(source == insertButton) {
				if(!inserted) {
					String result = JOptionPane.showInputDialog(null,"Input PIN", "Input PIN", JOptionPane.PLAIN_MESSAGE);
					if(result != null && !result.equals("")) {
						try {
							scs.getCardReader().insert(cardSelected, result);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null, "Card insert failed.");
						}catch(InvalidPINException ePIN) {
							JOptionPane.showMessageDialog(null, "Invalid PIN");
						}catch(BlockedCardException ec) {
							JOptionPane.showMessageDialog(null, "Card Blocked");
						}catch(Exception e2) {
							e2.printStackTrace();
						}
						inserted = true;
						insertButton.setText("Remove");
						insertButton.revalidate();
					} else {
						JOptionPane.showMessageDialog(null, "No PIN was entered");
					}
					
				}
				else {
					scs.getCardReader().remove();
					inserted = false;
					insertButton.setText("Insert");
					insertButton.revalidate();
				}
			}
			else if(source == tapButton) {
				try {
					scs.getCardReader().tap(cardSelected);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, "Card tap failed.");
				}
			}
		}
		
	}
}
