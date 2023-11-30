package com.thelocalmarketplace.GUI.attendant;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.hardware.ISelfCheckoutStation;
import com.thelocalmarketplace.software.Session;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;

public class StationPanel extends JPanel implements ActionListener {
	int statusN;
	Boolean enabled;
	ISelfCheckoutStation station;
	JLabel stationName;
	JLabel status;
	JButton details;
	Color detailsColor;
	JButton power;	
	Color powerColor;
	
	private static final long serialVersionUID = 1L;
	
	public StationPanel(ISelfCheckoutStation station) {
		this.station = station;
		
		details = new PlainButton("Details", Colors.color4);
		details.addActionListener(this);
		power = new PlainButton("Enable", Colors.color4);
		power.addActionListener(this);
		
		this.setBackground(Colors.color2);
		GridLayout layout = new GridLayout(2,4,0,0);
		layout.setHgap(20);
		layout.setVgap(20);
		this.setLayout(layout);
		this.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		this.add(details);
		this.add(power);
		this.revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == details) {
			// pop-up with details 
			if (statusN == 0) {
				// nothing displayed
			} else if (statusN == 1) {
				// warning (prediction)
			} else if (statusN == 2) {
				// problem Display message from the station
			}
		} else if (e.getSource() == power) {
			if(enabled) {
				station.turnOff();
				power.setText("Enable");
				power.setBackground(new Color(248, 144, 144));
			} else {
				station.turnOn();
				power.setText("Disable");
				power.setBackground(new Color(158, 228, 144));
			}
			
		}
		
	}
	
	/*@Override
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
			try {
				receipt = gui.getStation().getPrinter().removeReceipt();
				JOptionPane.showMessageDialog(null, receipt);
			} catch(InvalidArgumentSimulationException e1) {
				JOptionPane.showMessageDialog(null, "There is no receipt to collect.");
			}
		}
		else if(e.getSource() == sessionScreen) {
			gui.getStation().getScreen().getFrame().setVisible(true);
		}
		else if(e.getSource() == attendantScreen) {
			gui.getAttendantStation().screen.getFrame().setVisible(true);
		}
	}*/
}

>>>>>>> Stashed changes
