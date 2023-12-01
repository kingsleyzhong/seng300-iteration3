package com.thelocalmarketplace.GUI.attendant;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
import javax.swing.SwingConstants;

public class StationPanel extends JPanel implements ActionListener {
	int statusN;
	Boolean enabled;
	ISelfCheckoutStation station;
	JPanel sidePanel;
	JLabel stationName = new JLabel("");
	JLabel status = new JLabel("");
	Color detailsColor;
	JButton power;	
	Color powerColor;
	
	private static final long serialVersionUID = 1L;
	private JLabel lblNewLabel;
	
	public StationPanel(ISelfCheckoutStation station) {
		this.station = station;	
		
		if (station != null) {
			enabled = true;
		} else {
			enabled = false;
		}
		
		GridLayout layout = new GridLayout(1,5,0,0);
		layout.setHgap(20);
		layout.setVgap(20);
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		stationName.setFont(new Font("Tahoma", Font.BOLD, 18));
		stationName.setHorizontalAlignment(SwingConstants.LEFT);
		stationName.setText("STATION 1");
		
		status.setForeground(new Color(60, 179, 113));
		status.setFont(new Font("Tahoma", Font.BOLD, 16));
		status.setHorizontalAlignment(SwingConstants.LEFT);
		status.setText("STATUS: IDLE");
		
		power = new PlainButton("OFF", new Color(205, 92, 92));
		power.setFont(new Font("Tahoma", Font.BOLD, 18));
		power.addActionListener(this);
		
		add(stationName);
		add(status);
		
		lblNewLabel = new JLabel("Sample text for the display of a warning or error message when the problem occurs (empty otherwise)");
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(lblNewLabel);
		add(power);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == power) {
			if(enabled) {
				station.turnOff();
				power.setText("ON");
				power.setBackground(new Color(158, 228, 144));
			} else {
				station.turnOn();
				power.setText("OFF");
				power.setBackground(new Color(205, 92, 92));
			}
			
		}
		
	}
}
