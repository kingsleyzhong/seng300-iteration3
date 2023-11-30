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

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.ISelfCheckoutStation;
import com.thelocalmarketplace.software.attendant.Attendant;

public class AttendantGUI {
	Attendant attendant;
	ITouchScreen screen;
	List<ISelfCheckoutStation> stations;
	List<JPanel> stationPanels;
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int[] dimensions = {0,0};
	private int width;
	private int height;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public AttendantGUI(Attendant attendant, ITouchScreen screen) {
		stations = attendant.getStations();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		width = 1500;
		height = 800;
		
		screen.getFrame().setTitle("Attendant Screen GUI");
		screen.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.getFrame().setSize(new Dimension(width, height));
		screen.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
		screen.getFrame().getContentPane().setLayout(new BorderLayout(0,0));
		
		screen.getFrame().getContentPane().setBackground(Colors.color1);
		screen.getFrame().getContentPane().add(new PlainButton("test", Colors.color4));
		screen.getFrame().getContentPane().add(new PlainButton("test", Colors.color4));
		
		JPanel panel = new StationPanel(attendant.getStation().supervisedStations().get(0));
		stationPanels.add(panel);
		panel.setPreferredSize(new Dimension(width/6, width/6));
		
		panel.setBackground(Colors.color1);
		screen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
		
		this.attendant = attendant;
		//populateSessions();
		screen.setVisible(true);
	}
	
	public void populateSessions() {
		int val = 0;
		while(val < stations.size()) {
			// add a session panel
			JPanel panel = new StationPanel(stations.get(val));
			stationPanels.add(panel);
			panel.setPreferredSize(new Dimension(width/6, width/6));
			
			panel.setBackground(Colors.color1);
			screen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
		}
	}
	
	public void hide() {
		screen.getFrame().setVisible(false);
	}
	
	public void unhide() {
		screen.getFrame().setVisible(true);
	}
}