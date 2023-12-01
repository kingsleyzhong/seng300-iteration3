package com.thelocalmarketplace.GUI.attendant;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.ISelfCheckoutStation;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class StationInfo extends JPanel implements ActionListener{
	int statusN;
	Boolean enabled;
	ISelfCheckoutStation station;
	JPanel sidePanel;
	JLabel stationName = new JLabel("");
	JLabel status = new JLabel("");
	JButton details;
	Color detailsColor;
	JButton power;	
	Color powerColor;
	
	private static final long serialVersionUID = 1L;
	private JLabel lblNewLabel;
	
	public StationInfo(ISelfCheckoutStation station) {
		this.station = station;		
		GridLayout layout = new GridLayout(1,5,0,0);
		layout.setHgap(20);
		layout.setVgap(20);
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		stationName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		stationName.setHorizontalAlignment(SwingConstants.LEFT);
		stationName.setText("Station name: Station 1");
		
		status.setFont(new Font("Tahoma", Font.PLAIN, 14));
		status.setHorizontalAlignment(SwingConstants.LEFT);
		status.setText("Station status: IDLE(?)");
		
		power = new PlainButton("Enable", Colors.color4);
		power.setFont(new Font("Tahoma", Font.PLAIN, 14));
		power.addActionListener(this);
		
		details = new PlainButton("Details", Colors.color4);
		details.setFont(new Font("Tahoma", Font.PLAIN, 14));
		details.addActionListener(this);
		
		add(stationName);
		add(status);
		
		lblNewLabel = new JLabel("Sample text for the display of a warning or error message when the problem occurs (empty otherwise)");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(lblNewLabel);
		add(power);
		add(details);
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
}
