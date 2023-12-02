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
import com.thelocalmarketplace.software.attendant.IssuePredictorListener;
import com.thelocalmarketplace.software.attendant.Issues;
import com.thelocalmarketplace.software.attendant.MaintenanceManagerListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import javax.swing.SwingConstants;

public class StationPanel extends JPanel implements ActionListener {
	Session session;
	
	private final Color warningColor = new Color(191, 114, 13);
	private final Color urgentColor = new Color(161, 40, 40);
	int statusN;
	Boolean enabled;
	
	JPanel sidePanel;
	JLabel stationName = new JLabel("");
	JLabel status = new JLabel("");
	JLabel info = new JLabel("");
	Color detailsColor;
	JButton power;	
	JButton resolve;
	Color powerColor;
	
	private static final long serialVersionUID = 1L;
	
	public StationPanel(Session session) {
		this.session = session;
		
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
		status.setText("STATUS: GOOD");
		
		power = new PlainButton("OFF", new Color(205, 92, 92));
		power.setFont(new Font("Tahoma", Font.BOLD, 18));
		power.addActionListener(this);
		
		resolve = new PlainButton("Mark resolved", Colors.color4);
		resolve.setFont(new Font("Tahoma", Font.BOLD, 18));
		resolve.addActionListener(this);
		
		add(stationName);
		add(status);
		
		info = new JLabel("Sample text for the display of a warning or error message when the problem occurs (empty otherwise)");
		info.setVerticalAlignment(SwingConstants.TOP);
		info.setFont(new Font("Tahoma", Font.PLAIN, 14));
		add(info);
		add(resolve);
		add(power);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == power) {
			if(!enabled) {
				session.disable();
				enabled = false;
				power.setText("ON");
				power.setBackground(new Color(158, 228, 144));
			} else {
				session.enable();
				enabled = true;
				power.setText("OFF");
				power.setBackground(new Color(205, 92, 92));
			}
		}
	}
	
	private class InnerPredictionListener implements IssuePredictorListener{

		@Override
		public void notifyPredictUnsupportedFeature(Session session, Issues issue) {
			// what is unsupported feature??			
		}

		@Override
		public void notifyPredictLowInk(Session session) {
			info.setText("WARNING: Predicting low ink");
			info.setForeground(warningColor);
			status.setText("STATUS: WARNING");
		}

		@Override
		public void notifyPredictLowPaper(Session session) {
			info.setText("WARNING: Predicting low paper");
			info.setForeground(urgentColor);
			status.setText("STATUS: WARNING");
		}

		@Override
		public void notifyPredictCoinsFull(Session session) {
			info.setText("WARNING: Predicting coin overflow");
			info.setForeground(urgentColor);
			status.setText("STATUS: WARNING");
		}

		@Override
		public void notifyPredictBanknotesFull(Session session) {
			info.setText("WARNING: Predicting banknote overflow");
			info.setForeground(urgentColor);
			status.setText("STATUS: WARNING");
		}

		@Override
		public void notifyPredictLowCoins(Session session) {
			info.setText("WARNING: Predicting insufficient coins");
			info.setForeground(urgentColor);
			status.setText("STATUS: WARNING");
		}

		@Override
		public void notifyPredictLowBanknotes(Session session) {
			info.setText("WARNING: Predicting insufficient banknotes");
			info.setForeground(urgentColor);
			status.setText("STATUS: WARNING");
		}
	}
	
	private class InnerResolutionListener implements MaintenanceManagerListener{

		@Override
		public void notifyInkAdded() {
			info.setText("");
			status.setText("STATUS: GOOD");
		}

		@Override
		public void notifyPaperAdded() {
			info.setText("");
			status.setText("STATUS: GOOD");
		}

		@Override
		public void notifyCoinAdded() {
			info.setText("");
			status.setText("STATUS: GOOD");
		}

		@Override
		public void notifyBanknoteAdded() {
			info.setText("");
			status.setText("STATUS: GOOD");
		}

		@Override
		public void notifyCoinRemoved() {
			info.setText("");
			status.setText("STATUS: GOOD");
		}

		@Override
		public void notifyBanknoteRemoved() {
			info.setText("");
			status.setText("STATUS: GOOD");
		}
		
	}
}
