package com.thelocalmarketplace.GUI.attendant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import com.thelocalmarketplace.software.Session;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

import javax.swing.JFrame;
import javax.swing.JLabel;

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.ISelfCheckoutStation;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictorListener;
import com.thelocalmarketplace.software.attendant.Issues;

public class AttendantGUI {
	AttendantStation attendant;
	List<ISelfCheckoutStation> stations;
	List<JPanel> stationPanels;
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int[] dimensions = {0,0};
	private int width;
	private int height;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public AttendantGUI(AttendantStation attendant) {
		
		stations = attendant.supervisedStations();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		width = 1500;
		height = 800;
		
		attendant.screen.getFrame().setTitle("Attendant Screen GUI");
		attendant.screen.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		attendant.screen.getFrame().setSize(new Dimension(width, height));
		attendant.screen.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
		attendant.screen.getFrame().getContentPane().setLayout(new GridLayout(6,0,0,0));
		
		attendant.screen.getFrame().getContentPane().setBackground(Colors.color1);
		
		this.attendant = attendant;
		
		populateSessions();
		attendant.screen.setVisible(false);
	}
	
	public void populateSessions() {
		int val = 0;
		if (stations.size() != 0) {
		while(val < stations.size()) {
			JPanel panel = new StationPanel(stations.get(val));
			stationPanels.add(panel);
			panel.setPreferredSize(new Dimension(width/6, width/6));
			attendant.screen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
		}
		} else {
			JPanel panel = new StationPanel(null);
			panel.setPreferredSize(new Dimension(width/6, width/6));
			attendant.screen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
		}
	}
	
	public void hide() {
		attendant.screen.getFrame().setVisible(false);
	}
	
	public void unhide() {
		attendant.screen.getFrame().setVisible(true);
	}
	
	
}