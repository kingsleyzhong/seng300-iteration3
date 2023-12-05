package com.thelocalmarketplace.GUI.session;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.CartEmptyException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A popup that allows the user to select payment type.
 * Also allows the member to input their membership.
 * Tracks the amount due.
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

public class PaymentPopup {
	
	JFrame frame;
	
	public JLabel amountLabel;
	JLabel paymentTypeLabel;
	
	// JFrame size
	private int width;
	private int height;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private JPanel panel;
	private JLabel paymentOptionsLabel;
	private PlainButton cashButton;
	private PlainButton cardButton;
	private PlainButton membershipButton;
	private MembershipNumPad membershipPad;
	
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

//Amount that is subject to the total - amount paid by customer
		
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

//Cash Option Button
		cashButton = new PlainButton("Cash", new Color(220, 196, 132));
		cashButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
            		session.payByCash();
              		}
            	catch(CartEmptyException e1) {
                    hide();
                    JOptionPane.showMessageDialog(cashButton, "Cannot Pay for Empty Order");
            	}


            }
        });
		panel.add(cashButton);

//Card Option Button
		cardButton = new PlainButton("Debit/Credit", new Color(220, 196, 132));
		cardButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		cardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	try {
					session.payByCard();
            		}
               	catch(CartEmptyException e1) {
                    hide();
                    JOptionPane.showMessageDialog(cardButton, "Cannot Pay for Empty Order");
            	}

            }
        });
		panel.add(cardButton);

//Membership Option Button
		
		membershipButton = new PlainButton("Are you a Member?", new Color(220, 196, 132));
		membershipButton.setFont(new Font("Tahoma", Font.PLAIN, 30));
		
		membershipPad = new MembershipNumPad(session);
		membershipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hide();
                if (session.getMembershipNumber() != "") {
                	membershipPad.popUp();
                }
                else {
                    JOptionPane.showMessageDialog(membershipButton, "Membership already entered");
                    popUp();

                }
                
                if (membershipPad.getValidMembership() == true) {
                	popUp();
                }

            }
        });
		panel.add(membershipButton);
		
		frame.getContentPane().add(main);
}
	
	public void popUp() {
		
		this.frame.setVisible(true);
		this.frame.setAlwaysOnTop(true);
	}
	
	public void hide() {
		this.frame.setVisible(false);
	}
	
	public boolean isVisible() {
		return frame.isVisible();
	}
	
	public JButton getCashButton() {
		return cashButton;
	}
	
	public JButton getCardButton() {
		return cardButton;
	}
	
	public JButton getMembershipButton() {
		return membershipButton;
	}
	
	public MembershipNumPad getMembershipPad() {
		return membershipPad;
		
	}
}
