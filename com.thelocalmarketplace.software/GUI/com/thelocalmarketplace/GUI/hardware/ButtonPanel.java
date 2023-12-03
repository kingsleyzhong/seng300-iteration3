package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

public class ButtonPanel extends JPanel implements ActionListener {
	JButton mainScanner;
	JButton handheldScanner;
	JButton coinSlot;
	JButton banknoteInput;
	JButton coinTray;
	JButton banknoteOutput;
	JButton receiptPrinter;
	JButton cardDevice;
	JButton sessionScreen;
	JButton attendantScreen;
	JButton startButton;
	
	private static final long serialVersionUID = 1L;
	private HardwareGUI gui;
	
	public ButtonPanel(HardwareGUI gui) {
		this.gui = gui;
		mainScanner = new PlainButton("Scan With Main Scanner", Colors.color4);
		mainScanner.addActionListener(this);
		handheldScanner = new PlainButton("Scan With Handheld Scanner", Colors.color4);
		handheldScanner.addActionListener(this);
		coinSlot = new PlainButton("Coin Slot", Colors.color4);
		coinSlot.addActionListener(this);
		banknoteInput = new PlainButton("Banknote Input", Colors.color4);
		banknoteInput.addActionListener(this);
		coinTray = new PlainButton("Coin Tray", Colors.color4);
		coinTray.addActionListener(this);
		banknoteOutput = new PlainButton("Banknote Output", Colors.color4);
		banknoteOutput.addActionListener(this);
		receiptPrinter = new PlainButton("Collect Receipt", Colors.color4);
		receiptPrinter.addActionListener(this);
		cardDevice = new PlainButton("Card Device", Colors.color4);
		cardDevice.addActionListener(this);
		sessionScreen = new PlainButton("Self Checkout Station Screen", Colors.color4);
		sessionScreen.addActionListener(this);
		attendantScreen = new PlainButton("Attendant Screen", Colors.color4);
		attendantScreen.addActionListener(this);
		
		startButton = new PlainButton("Start Hardware Simulation", Colors.color4);
		startButton.addActionListener(this);
		
		this.setBackground(Colors.color2);
		GridLayout layout = new GridLayout(0,5,0,0);
		layout.setHgap(20);
		layout.setVgap(20);
		this.setLayout(layout);
		this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
		this.add(startButton);
	}

	public void populateButtons() {
		this.remove(startButton);
		this.repaint();
		
		this.add(mainScanner);
		this.add(handheldScanner);
		this.add(receiptPrinter);
		this.add(sessionScreen);
		this.add(attendantScreen);
		this.revalidate();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(!gui.clicked) {
			gui.introPanel();
			gui.clicked = true;
			populateButtons();
		}
		if(e.getSource() == mainScanner) {
			gui.mainScan();
		}
		else if(e.getSource() == handheldScanner) {
			gui.handScan();
		}
		else if(e.getSource() == receiptPrinter) {
			String receipt = "";
			gui.getStation().getPrinter().cutPaper();
			try {
				receipt = gui.getStation().getPrinter().removeReceipt();
				if(receipt.length()>0)
				JOptionPane.showMessageDialog(null, receipt);
				else JOptionPane.showMessageDialog(null, "There is no receipt to collect.");
			} catch(InvalidArgumentSimulationException e1) {
				JOptionPane.showMessageDialog(null, "There is no receipt to collect.");
			}
		}
		else if(e.getSource() == sessionScreen) {
			gui.getStation().getScreen().getFrame().setVisible(true);
		}
		else if(e.getSource() == attendantScreen) {
			
		}
		
	}
}
