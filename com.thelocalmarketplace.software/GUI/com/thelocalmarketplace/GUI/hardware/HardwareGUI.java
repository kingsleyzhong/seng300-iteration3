package com.thelocalmarketplace.GUI.hardware;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;

public class HardwareGUI {
	private JFrame hardwareFrame;
	private JPanel screens;
	private CardLayout cards;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width;
	private int height;
	
	public HardwareGUI(AbstractSelfCheckoutStation scs) {
		//width = (int) screenSize.getWidth();
		//height = (int) screenSize.getHeight();
		
		width = 1500;
		height = 800;
		
		hardwareFrame = new JFrame();
		hardwareFrame.setTitle("Hardware GUI");
		hardwareFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hardwareFrame.setSize(new Dimension(width, height));
		//hardwareFrame.setSize(screenSize);
		hardwareFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		hardwareFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		hardwareFrame.getContentPane().setBackground(Colors.color1);
		
		JPanel buttonPanel = buttonPanel();
		
		hardwareFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		
		
		
		hardwareFrame.setUndecorated(true);
		hardwareFrame.setVisible(true);
	}
	
	public JPanel buttonPanel() {
		JButton mainScanner = new PlainButton("MainScanner", Colors.color4);
		JButton handheldScanner = new PlainButton("Handheld Scanner", Colors.color4);
		JButton scanningArea = new PlainButton("Scanning Area", Colors.color4);
		JButton baggingArea = new PlainButton("Bagging Area", Colors.color4);
		JButton coinSlot = new PlainButton("Coin Slot", Colors.color4);
		JButton banknoteInput = new PlainButton("Banknote Input", Colors.color4);
		JButton coinTray = new PlainButton("Coin Tray", Colors.color4);
		JButton banknoteOutput = new PlainButton("Banknote Output", Colors.color4);
		JButton receiptPrinter = new PlainButton("Receipt Printer", Colors.color4);
		JButton cardDevice = new PlainButton("Card Device", Colors.color4);
		JButton sessionScreen = new PlainButton("Self Checkout Station Screen", Colors.color4);
		JButton attendantScreen = new PlainButton("Attendant Screen", Colors.color4);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(width, height/4));
		buttonPanel.setBackground(Colors.color2);
		GridLayout layout = new GridLayout(2,6,0,0);
		layout.setHgap(20);
		layout.setVgap(20);
		buttonPanel.setLayout(layout);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		buttonPanel.add(mainScanner);
		buttonPanel.add(scanningArea);
		buttonPanel.add(coinSlot);
		buttonPanel.add(coinTray);
		buttonPanel.add(receiptPrinter);
		buttonPanel.add(sessionScreen);
		buttonPanel.add(handheldScanner);
		buttonPanel.add(baggingArea);
		buttonPanel.add(banknoteInput);
		buttonPanel.add(banknoteOutput);
		buttonPanel.add(cardDevice);
		buttonPanel.add(attendantScreen);
		
		return buttonPanel;
	}
}