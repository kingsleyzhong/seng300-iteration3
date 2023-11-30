package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.Simulation;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.startscreen.StartScreenGUI;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import javax.swing.SwingConstants;

public class SoftwareGUI{
	JFrame frame;
	Session session;
	
	// Buttons for user to interact with:
	JButton exit;
	JButton addBags;
	JButton pluCode;
	JButton searchCatalogue;
	JButton callAttendant;
	JButton pay;
	
	
	// JFrame size
	private int width;
	private int height;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	
	// Panel components of JFrame
	JPanel orangePanel;
	JPanel cartInfoPanel;
	JPanel cartItemsPanel;
	JPanel buttonPanel;
	
	// Buttons
	
	public SoftwareGUI(Session session) {
		frame = session.getStation().getScreen().getFrame();
		// Setting window size
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		frame.setSize(width, height);
		frame.setTitle("Software GUI");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
		
		// ORANGE PANEL FOR ITEM DETAILS:
		orangePanel = new JPanel();
		orangePanel.setBackground(Colors.color5);
		orangePanel.setLayout(new BorderLayout());
		
		JPanel oLeft = new JPanel();
		oLeft.setBackground(Colors.color5);
		oLeft.setPreferredSize(new Dimension(width/3, height/13));
		
		JPanel oRight = new JPanel();
		oRight.setBackground(Colors.color5);
		oRight.setPreferredSize(new Dimension( 2 * (width/3), height/13));
		oRight.setLayout(new FlowLayout(FlowLayout.LEADING, width/10, height/32));
		oRight.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Colors.color1));
		
		// Text for right side of orange panel
		JLabel desc = new JLabel("Product Description");
		desc.setFont(new Font("Dialog", Font.BOLD, 15));
		JLabel qty = new JLabel("Quantity");
		qty.setFont(new Font("Dialog", Font.BOLD, 15));
		JLabel mass = new JLabel("Mass");
		mass.setFont(new Font("Dialog", Font.BOLD, 15));
		JLabel price = new JLabel("Price");
		price.setFont(new Font("Dialog", Font.BOLD, 15));
		
		oRight.add(desc);
		oRight.add(qty);
		oRight.add(mass);
		oRight.add(price);
		
		orangePanel.add(oLeft, BorderLayout.WEST);
		orangePanel.add(oRight, BorderLayout.EAST);
		
		// CENTER SECTION
		cartInfoPanel = new JPanel();
		cartInfoPanel.setPreferredSize(new Dimension(width/3, height));
		cartInfoPanel.setBackground(Colors.color4);
		
		cartInfoPanel.setLayout(new BorderLayout());
		
		JPanel infoTop = new JPanel();
		infoTop.setBackground(Colors.color4);
		infoTop.setPreferredSize(new Dimension(width, 300));
		infoTop.setLayout(new GridLayout(0,1,20,20));
		infoTop.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
	
		JPanel infoTop1 = new JPanel();
		infoTop1.setLayout(new BorderLayout());
		infoTop1.setBackground(Colors.color4);
		JLabel infoQtyString = new JLabel("Quantity");
		infoQtyString.setFont(new Font("Dialog", Font.BOLD,20));
		JLabel infoQtyNumber = new JLabel("0");
		infoQtyNumber.setFont(new Font("Dialog", Font.BOLD,20));
		infoTop1.setLayout(new BorderLayout(0, 0));
		infoTop1.add(infoQtyString, BorderLayout.WEST);
		infoTop1.add(infoQtyNumber, BorderLayout.EAST);
		
		JPanel infoTop2 = new JPanel();
		infoTop2.setBackground(Colors.color4);
		infoTop2.setLayout(new BorderLayout());
		infoTop2.setBackground(Colors.color4);
		JLabel infoItemString = new JLabel("Item Count");
		infoItemString.setFont(new Font("Dialog", Font.BOLD,20));
		JLabel infoItemNumber = new JLabel("0");
		infoItemNumber.setFont(new Font("Dialog", Font.BOLD,20));
		infoTop2.add(infoItemString, BorderLayout.WEST);
		infoTop2.add(infoItemNumber, BorderLayout.EAST);
		
		JPanel infoTop3 = new JPanel();
		infoTop3.setBackground(Colors.color4);
		infoTop3.setLayout(new BorderLayout());
		infoTop3.setBackground(Colors.color4);
		JLabel infoWeightString = new JLabel("Weight");
		infoWeightString.setFont(new Font("Dialog", Font.BOLD,20));
		JLabel infoWeightNumber = new JLabel("0kg");
		infoWeightNumber.setFont(new Font("Dialog", Font.BOLD,20));
		infoTop3.add(infoWeightString, BorderLayout.WEST);
		infoTop3.add(infoWeightNumber, BorderLayout.EAST);
		
		infoTop.add(infoTop1);
		infoTop.add(infoTop2);
		infoTop.add(infoTop3);
		
		JPanel infoBottom = new JPanel();
		infoBottom.setBackground(Colors.color4);
		infoBottom.setLayout(new GridLayout(0,1));
		infoBottom.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Colors.color1));
		
		JPanel infoBottomInner = new JPanel();
		infoBottomInner.setLayout(new BorderLayout());
		infoBottomInner.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		infoBottomInner.setBackground(Colors.color4);
		
		JLabel cartTotalString = new JLabel("Cart Total");
		cartTotalString.setFont(new Font("Dialog", Font.BOLD, 20));
		JLabel cartTotalInDollars = new JLabel("$0.00");
		cartTotalInDollars.setFont(new Font("Dialog", Font.BOLD,40));
		
		infoBottomInner.add(cartTotalString, BorderLayout.WEST);
		infoBottomInner.add(cartTotalInDollars, BorderLayout.EAST);
		
		infoBottom.add(infoBottomInner);
		
		cartInfoPanel.add(infoTop, BorderLayout.NORTH);
		cartInfoPanel.add(infoBottom, BorderLayout.SOUTH);
		
		cartItemsPanel = new JPanel();
		cartItemsPanel.setBackground(Colors.color3);
		cartItemsPanel.setPreferredSize(new Dimension( 2 * (width/3), height));
				
		
		
		
		
		// EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE
		// EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE
		// EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE
		// EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE
		// EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE EMILY INSERT YOUR STUFF AROUND HERE
		// WORK WITH THE cartItemsPanel that is above
		
		
		// BUTTON PANEL FOR BOTTOM OF SCREEN:
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Colors.color2);
		buttonPanel.setLayout(new GridLayout(1,0, 20,0));
		
		// Create buttons
		exit = new PlainButton("Exit session", Colors.color4);
		addBags = new PlainButton("Add Bags", Colors.color4);
		pluCode = new PlainButton("<html>Enter PLU <br/>&nbsp;&nbsp;&nbsp;Code</html>", Colors.color4);
		searchCatalogue = new PlainButton("<html>&nbsp;&nbsp;Search <br/>Catalogue</html>", Colors.color4);
		callAttendant = new PlainButton("<html>&nbsp;&nbsp;&nbsp;Call <br/>Attendant</html>", Colors.color4);
		pay = new PlainButton("PAY", Colors.color4);
	
		//Add action listeners to buttons
		exit.addActionListener(new ButtonListener());
		addBags.addActionListener(new ButtonListener());
		pluCode.addActionListener(new ButtonListener());
		searchCatalogue.addActionListener(new ButtonListener());
		callAttendant.addActionListener(new ButtonListener());
		pay.addActionListener(new ButtonListener());
		
		//Set button sizes
		exit.setFont(new Font("Dialog", Font.BOLD, 30));
		addBags.setFont(new Font("Dialog", Font.BOLD, 30));
		pluCode.setFont(new Font("Dialog", Font.BOLD, 30));
		searchCatalogue.setFont(new Font("Dialog", Font.BOLD, 30));
		callAttendant.setFont(new Font("Dialog", Font.BOLD, 30));
		pay.setFont(new Font("Dialog", Font.BOLD, 75));
		
		// Align texts for buttons
		exit.setHorizontalTextPosition(JButton.CENTER);
		addBags.setHorizontalTextPosition(JButton.CENTER);		
		pluCode.setHorizontalTextPosition(JButton.CENTER);		
		searchCatalogue.setHorizontalTextPosition(JButton.CENTER);		
		callAttendant.setHorizontalTextPosition(JButton.CENTER);		
		pay.setHorizontalTextPosition(JButton.CENTER);		
		
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		buttonPanel.add(exit);
		buttonPanel.add(addBags);
		buttonPanel.add(pluCode);
		buttonPanel.add(searchCatalogue);
		buttonPanel.add(callAttendant);
		buttonPanel.add(pay);
		
		
		// SET PANEL SIZES FOR JFRAME:
		orangePanel.setPreferredSize(new Dimension(width, height/13));
		buttonPanel.setPreferredSize(new Dimension(width, height/6));
		
		// ADD PANELS TO JFRAME:
		frame.getContentPane().add(orangePanel, BorderLayout.NORTH);
		frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		frame.getContentPane().add(cartInfoPanel, BorderLayout.WEST);
		frame.getContentPane().add(cartItemsPanel, BorderLayout.EAST);
	}

	
	public void hide() {
    	frame.setVisible(false);
    }
	
	public void unhide() {
		frame.setVisible(true);
	}
	
	public class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}