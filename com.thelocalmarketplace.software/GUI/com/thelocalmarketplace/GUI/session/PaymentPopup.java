package com.thelocalmarketplace.GUI.session;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import com.tdc.CashOverloadException;
import com.tdc.DisabledException;
import com.tdc.NoCashAvailableException;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;

public class PaymentPopup {
	
	JFrame frame;
	
	JLabel amountLabel;
	JLabel paymentTypeLabel;
	
	// JFrame size
	private int width;
	private int height;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private JPanel panel;
	private JLabel paymentOptionsLabel;
	private PlainButton cashButton;
	private PlainButton cardButton;
	private PlainButton cancelPaymentButton;
	
	public PaymentPopup(Session session) {
		
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		frame = new JFrame();
		frame.setResizable(true);
		frame.setSize(new Dimension(900,500));
		frame.setLocationRelativeTo(null);
		
		frame.getContentPane().setBackground(Colors.color2);
		frame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		JPanel main = new JPanel();
		main.setBackground(Colors.color2);
		main.setLayout(new GridLayout(1,0,30,10));
		main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		JPanel totalDisplay = new JPanel();
		main.add(totalDisplay);
		totalDisplay.setLayout(new GridLayout(0,1));
		totalDisplay.setBackground(Colors.color4);
		
		JLabel totalRemainingLabel = new JLabel("Total Remaining:");
		totalRemainingLabel.setHorizontalAlignment(SwingConstants.CENTER);
		totalRemainingLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
		totalDisplay.add(totalRemainingLabel, BorderLayout.NORTH);
		totalRemainingLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		
		amountLabel = new JLabel("$ 0.00");
		amountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		amountLabel.setFont(new Font("Tahoma", Font.PLAIN, 45));
		totalDisplay.add(amountLabel, BorderLayout.CENTER);
		
		paymentTypeLabel = new JLabel("Payment Selected: None");
		paymentTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		paymentTypeLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		totalDisplay.add(paymentTypeLabel, BorderLayout.SOUTH);
		
		panel = new JPanel();
		panel.setBackground(Colors.color2);
		main.add(panel);
		panel.setLayout(new GridLayout(0, 1, 20, 20));
		
		paymentOptionsLabel = new JLabel("Payment Options:");
		paymentOptionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
		paymentOptionsLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		panel.add(paymentOptionsLabel);
		
		cashButton = new PlainButton("Cash", new Color(220, 196, 132));
		cashButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
            		session.payByCash();
            		paymentTypeLabel.setText("Payment Selected: Cash");
            	}
            	catch(CartEmptyException e1) {
					frame.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Cannot Pay for Empty Order");
              
            	}


            }
        });
		panel.add(cashButton);
		
		cardButton = new PlainButton("Debit/Credit", new Color(220, 196, 132));
		cardButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//paymentTypeLabel.setText("Payment Selected: Card");
            	try {
					session.payByCard();
					paymentTypeLabel.setText("Payment Selected: Card");
				} catch (CashOverloadException e1) 
            	{
					//session.notifyAttendant();
				} catch (NoCashAvailableException e1) {
					//session.notifyAttendant();
				} catch (DisabledException e1) {
					//session.notifyAttendant();
				} catch(CartEmptyException e1) {
					frame.setVisible(false);
                    JOptionPane.showMessageDialog(null, "Cannot Pay for Empty Order");
            	}

            	
            }
        });
		panel.add(cardButton);
		
		cancelPaymentButton = new PlainButton("New button", new Color(220, 196, 132));
		cancelPaymentButton.setText("Cancel Payment");
		cancelPaymentButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cancelPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(cancelPaymentButton, "Payment Cancelled. All change inserted during payment session will be returned shortly.");
                frame.setVisible(false);
            }
        });
		panel.add(cancelPaymentButton);
		
		frame.getContentPane().add(main);
}
	
	public void popUp() {
		
		this.frame.setVisible(true);
		this.frame.setAlwaysOnTop(true);
	}
	
	public void hide() {
		this.frame.setVisible(false);
	}
}
