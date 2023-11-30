package com.thelocalmarketplace.GUI.hardware;

import javax.swing.JPanel;

import com.jjjwelectronics.card.Card;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;

import java.awt.GridLayout;
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
		panel.add(creditCardButton);
		
		JButton debitCardButton = new PlainButton("Debit Card", Colors.color3);
		panel.add(debitCardButton);
		
		JButton invalidCardButton = new PlainButton("Invalid Card", Colors.color3);
		panel.add(invalidCardButton);
		
		JButton membershipCardButton = new PlainButton("Membership Card", Colors.color3);
		panel.add(membershipCardButton);
		
		JPanel panel2 = new JPanel();
		panel2.setBackground(Colors.color1);
		add(panel2);
	}

}
