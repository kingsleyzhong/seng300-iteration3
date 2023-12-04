package com.thelocalmarketplace.GUI.hardware;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.bag.ReusableBag;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

public class ButtonPanel extends JPanel implements ActionListener {
	private ReusableBag[] bags;
	private int numBags = 0;
	
	JButton mainScanner;
	JButton handheldScanner;
	JButton receiptPrinter;
	JButton sessionScreen;
	JButton attendantScreen;
	JButton startButton;
	JButton addBags;
	JButton removeBags;
	
	private static final long serialVersionUID = 1L;
	private HardwareGUI gui;
	
	public ButtonPanel(HardwareGUI gui) {
		this.gui = gui;
		mainScanner = new PlainButton("Scan With Main Scanner", Colors.color4);
		mainScanner.addActionListener(this);
		handheldScanner = new PlainButton("Scan With Handheld Scanner", Colors.color4);
		handheldScanner.addActionListener(this);
		receiptPrinter = new PlainButton("Collect Receipt", Colors.color4);
		receiptPrinter.addActionListener(this);
		sessionScreen = new PlainButton("Self Checkout Station Screen", Colors.color4);
		sessionScreen.addActionListener(this);
		attendantScreen = new PlainButton("Attendant Screen", Colors.color4);
		attendantScreen.addActionListener(this);
		addBags = new PlainButton("Dispense bags", Colors.color4);
		addBags.addActionListener(this);
		removeBags = new PlainButton("Remove all bags", Colors.color4);
		removeBags.addActionListener(this);
		
		startButton = new PlainButton("Start Hardware Simulation", Colors.color4);
		startButton.addActionListener(this);
		
		this.setBackground(Colors.color2);
		GridLayout layout = new GridLayout(1,0,0,0);
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
		this.add(addBags);
		this.add(removeBags);
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
		else if(e.getSource() == addBags) {
			try {
				ReusableBag bag = gui.getStation().getReusableBagDispenser().dispense();
				gui.getStation().getBaggingArea().addAnItem(bag);
				bags[numBags] = bag;
				numBags += 1;
			} catch (EmptyDevice e1) {
				JOptionPane.showMessageDialog(null, "No more bags to dispense.");
			}
		}
		else if(e.getSource() == removeBags) {
			for(int i = 0; i<numBags; i++) {
				gui.getStation().getBaggingArea().removeAnItem(bags[i]);
			}
			numBags = 0;
		}
		
	}
}
