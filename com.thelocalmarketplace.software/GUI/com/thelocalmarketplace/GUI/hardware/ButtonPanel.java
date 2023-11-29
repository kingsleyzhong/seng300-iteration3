package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;

public class ButtonPanel extends JPanel implements ActionListener {
	JButton mainScanner;
	JButton handheldScanner;
	JButton scanningArea;
	JButton baggingArea;
	JButton coinSlot;
	JButton banknoteInput;
	JButton coinTray;
	JButton banknoteOutput;
	JButton receiptPrinter;
	JButton cardDevice;
	JButton sessionScreen;
	JButton attendantScreen;
	
	private static final long serialVersionUID = 1L;
	private HardwareGUI gui;
	
	public ButtonPanel(HardwareGUI gui) {
		this.gui = gui;
		mainScanner = new PlainButton("MainScanner", Colors.color4);
		mainScanner.addActionListener(this);
		handheldScanner = new PlainButton("Handheld Scanner", Colors.color4);
		handheldScanner.addActionListener(this);
		scanningArea = new PlainButton("Scanning Area", Colors.color4);
		scanningArea.addActionListener(this);
		baggingArea = new PlainButton("Bagging Area", Colors.color4);
		baggingArea.addActionListener(this);
		coinSlot = new PlainButton("Coin Slot", Colors.color4);
		coinSlot.addActionListener(this);
		banknoteInput = new PlainButton("Banknote Input", Colors.color4);
		banknoteInput.addActionListener(this);
		coinTray = new PlainButton("Coin Tray", Colors.color4);
		coinTray.addActionListener(this);
		banknoteOutput = new PlainButton("Banknote Output", Colors.color4);
		banknoteOutput.addActionListener(this);
		receiptPrinter = new PlainButton("Receipt Printer", Colors.color4);
		receiptPrinter.addActionListener(this);
		cardDevice = new PlainButton("Card Device", Colors.color4);
		cardDevice.addActionListener(this);
		sessionScreen = new PlainButton("Self Checkout Station Screen", Colors.color4);
		sessionScreen.addActionListener(this);
		attendantScreen = new PlainButton("Attendant Screen", Colors.color4);
		attendantScreen.addActionListener(this);
		
		this.setBackground(Colors.color2);
		GridLayout layout = new GridLayout(2,6,0,0);
		layout.setHgap(20);
		layout.setVgap(20);
		this.setLayout(layout);
		this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		this.add(mainScanner);
		this.add(scanningArea);
		this.add(coinSlot);
		this.add(coinTray);
		this.add(receiptPrinter);
		this.add(sessionScreen);
		this.add(handheldScanner);
		this.add(baggingArea);
		this.add(banknoteInput);
		this.add(banknoteOutput);
		this.add(cardDevice);
		this.add(attendantScreen);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(!gui.clicked) {
			gui.introPanel();
			gui.clicked = true;
		}
		if(e.getSource() == mainScanner);
		
	}
}