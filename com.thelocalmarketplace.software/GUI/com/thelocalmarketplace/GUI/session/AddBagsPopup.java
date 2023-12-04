package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionState;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/***
 * 
 * Pop Up Menu for the Add Bags option
 * Customer can either select the option to add their own bag, or purchase a store bag
 * 
* Project Iteration 3 Group 1
*
* Derek Atabayev 			: 30177060 
* Enioluwafe Balogun 		: 30174298 
* Subeg Chahal 			: 30196531 
* Jun Heo 					: 30173430 
* Emily Kiddle 			: 30122331 
* Anthony Kostal-Vazquez 	: 30048301 
* Jessica Li 				: 30180801 
* Sua Lim 					: 30177039 
* Savitur Maharaj 			: 30152888 
* Nick McCamis 			: 30192610 
* Ethan McCorquodale 		: 30125353 
* Katelan Ng 				: 30144672 
* Arcleah Pascual 			: 30056034 
* Dvij Raval 				: 30024340 
* Chloe Robitaille 		: 30022887 
* Danissa Sandykbayeva 	: 30200531 
* Emily Stein 				: 30149842 
* Thi My Tuyen Tran 		: 30193980 
* Aoi Ueki 				: 30179305 
* Ethan Woo 				: 30172855 
* Kingsley Zhong 			: 30197260 
*
*/

public class AddBagsPopup {

	JFrame frame;
	
	// JFrame size

	private JPanel panel;
	private JLabel bagOptions;

	private JButton addPersonalBagButton;
	private JButton addStoreBagButton;
	private JButton cancelButton;

	private NumberOfBags numOfBagsScreen;
	
	public AddBagsPopup(Session session) {
				
		frame = new JFrame();
		frame.setResizable(true);
		frame.setSize(new Dimension(400,500));
		frame.setLocationRelativeTo(null);
		
		frame.getContentPane().setBackground(Colors.color2);
		frame.getContentPane().setLayout(new GridLayout(1, 1, 0, 0));
		
		JPanel main = new JPanel();
		main.setBackground(Colors.color2);
		main.setLayout(new GridLayout(1,0,30,10));
		main.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		frame.getContentPane().add(main);

		panel = new JPanel();
		panel.setBackground(Colors.color2);
		panel.setLayout(new GridLayout(0, 1, 30, 30));
		main.add(panel);

		//Header for the screen
		bagOptions = new JLabel("Bag Options:");
		bagOptions.setHorizontalAlignment(SwingConstants.CENTER);
		bagOptions.setFont(new Font("Tahoma", Font.PLAIN, 30));
		panel.add(bagOptions);
		
		//Personal Bag Option, puts session into ADDING_BAGS
		addPersonalBagButton = new PlainButton("Add Personal Bag", Colors.color4);
		addPersonalBagButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		addPersonalBagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (session.getState() != SessionState.BLOCKED) {
					session.addBags();
					JOptionPane.showMessageDialog(addPersonalBagButton, "Please Place Your Bags in the Bagging Area");
					hide();
				}			
				else {
					JOptionPane.showMessageDialog(addPersonalBagButton, "Cannot Add Bag at this moment");

				}
			}
		});
		panel.add(addPersonalBagButton);
		
		//Store bags option, will open a window to see how many bags user wants
		numOfBagsScreen = new NumberOfBags(session);
		addStoreBagButton = new PlainButton("Add Stores Bags", Colors.color4);
		addStoreBagButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		addStoreBagButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (session.getState() != SessionState.BLOCKED) {
					numOfBagsScreen.popUp();
					hide();
					
				}
				else {
	            JOptionPane.showMessageDialog(addPersonalBagButton, "Cannot Add Bag at this moment");

				}			}
		});
		
		panel.add(addStoreBagButton);
		
		//Closes window
		cancelButton = new PlainButton("Close", Colors.color4);
		cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 25));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});
		panel.add(cancelButton);

		
	}
	
	public void popUp() {
		this.frame.setVisible(true);
	}
	
	public void hide() {
		this.frame.setVisible(false);
	}

	public JButton getAddPersonalBagButton() {
		return addPersonalBagButton;
	}

	public JButton getAddStoreBagButton() {
		return addStoreBagButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}
	
	public NumberOfBags getNumOfBagsScreen() {
		return numOfBagsScreen;
	}

	public boolean isVisible() {
		return frame.isVisible();
	}
	
	
}
