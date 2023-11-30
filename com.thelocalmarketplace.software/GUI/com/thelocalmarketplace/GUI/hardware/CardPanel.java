package com.thelocalmarketplace.GUI.hardware;

import javax.swing.JPanel;

import com.jjjwelectronics.card.Card;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.external.CardIssuer;
import com.thelocalmarketplace.software.funds.CardIssuerDatabase;
import com.thelocalmarketplace.software.funds.SupportedCardIssuers;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JButton;

public class CardPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Card cardSelected;
	private Card debitCard;
	private Card creditCard;
	private Card invalidCard;
	private Card membershipCard;
	/**
	 * Create the panel.
	 */
	public CardPanel() {
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
		
		JButton creditCardButton = new PlainButton("Credit Card", Colors.color3);
		creditCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = creditCard;
				updateSelection();
			}
			
		});
		panel.add(creditCardButton);
		
		JButton debitCardButton = new PlainButton("Debit Card", Colors.color3);
		debitCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = debitCard;
				updateSelection();
			}
			
		});
		panel.add(debitCardButton);
		
		JButton invalidCardButton = new PlainButton("Invalid Card", Colors.color3);
		invalidCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = invalidCard;
				updateSelection();
			}
			
		});
		panel.add(invalidCardButton);
		
		JButton membershipCardButton = new PlainButton("Membership Card", Colors.color3);
		membershipCardButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				cardSelected = membershipCard;
				updateSelection();
			}
			
		});
		panel.add(membershipCardButton);
		
		JPanel panel2 = new JPanel();
		panel2.setBackground(Colors.color1);
		add(panel2);
	}
	
	protected void updateSelection() {
		// TODO Auto-generated method stub
		
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
		membershipCard = new Card(SupportedCardIssuers.FOUR.getIssuer(), "5160617843321186", "Robehrt Lazar", "111", "1234", true, true);

		Calendar exp = Calendar.getInstance();
		exp.set(Calendar.YEAR, 2099);
		exp.set(Calendar.MONTH, 12);

		ci1.addCardData(debitCard.number, debitCard.cardholder, exp, debitCard.cvv, 10000);
		ci2.addCardData(creditCard.number, creditCard.cardholder, exp, creditCard.cvv, 7500);
		ci3.addCardData("0", invalidCard.cardholder, exp, invalidCard.cvv, 1000);
		ci4.addCardData(membershipCard.number, membershipCard.cardholder, exp, membershipCard.cvv, 2000);
	}
}
