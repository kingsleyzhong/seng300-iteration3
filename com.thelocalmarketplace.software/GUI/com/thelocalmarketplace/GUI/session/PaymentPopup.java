package com.thelocalmarketplace.GUI.session;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;

import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class PaymentPopup {
	
	JFrame frame;
	
	PlainButton cashButton;
	PlainButton cardButton;
	PlainButton cancelPaymentButton;
	
	JLabel amountLabel;
	JLabel paymentTypeLabel;
	
	// JFrame size
	private int width;
	private int height;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	public PaymentPopup() {
		
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		this.frame = new JFrame();
		frame.setResizable(false);
		this.frame.setSize(width/2,height/2);
		this.frame.setLocation(width/2-this.frame.getSize().width/2, height/2-this.frame.getSize().height/2);
		
		this.frame.getContentPane().setBackground(Colors.color2);
		frame.getContentPane().setLayout(null);
		
		JPanel totalDisplay = new JPanel();
		totalDisplay.setBounds(100, 56, 363, 387);
		frame.getContentPane().add(totalDisplay);
		totalDisplay.setLayout(new BorderLayout(0, 0));
		totalDisplay.setBackground(Colors.color4);
		
		JLabel totalRemainingLabel = new JLabel("Total Remaining:");
		totalRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalRemainingLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		totalDisplay.add(totalRemainingLabel, BorderLayout.NORTH);
		totalRemainingLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		
		this.amountLabel = new JLabel("$ 0.00");
		amountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		amountLabel.setFont(new Font("Tahoma", Font.PLAIN, 45));
		totalDisplay.add(amountLabel, BorderLayout.CENTER);
		
		this.paymentTypeLabel = new JLabel("Payment Selected: None");
		paymentTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		paymentTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		totalDisplay.add(paymentTypeLabel, BorderLayout.SOUTH);
		
		this.cashButton = new PlainButton("Cash", Colors.color4);
		cashButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cashButton.setBounds(545, 127, 301, 86);
		frame.getContentPane().add(cashButton);
        cashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	paymentTypeLabel.setText("Payment Selected: Cash");
               
            }
        });
		
        this.cardButton = new PlainButton("Debit/Credit", new Color(220, 196, 132));
		cardButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cardButton.setBounds(545, 242, 301, 86);
		frame.getContentPane().add(cardButton);
        cardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	paymentTypeLabel.setText("Payment Selected: Card");
            }
        });		
        this.cancelPaymentButton = new PlainButton("New button", new Color(220, 196, 132));
		cancelPaymentButton.setText("Cancel Payment");
		cancelPaymentButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cancelPaymentButton.setBounds(545, 357, 301, 86);
		frame.getContentPane().add(cancelPaymentButton);
		cancelPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle card payment logic here
                JOptionPane.showMessageDialog(cancelPaymentButton, "Payment Cancelled. All change inserted during payment session will be returned shortly.");
                frame.setVisible(false);
            }
        });
		
		JLabel paymentOptionsLabel = new JLabel("Payment Options:");
		paymentOptionsLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		paymentOptionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		paymentOptionsLabel.setBounds(565, 57, 263, 40);
		frame.getContentPane().add(paymentOptionsLabel);
		

	}
	
	public void popUp() {
		
		this.frame.setVisible(true);
	}
}
