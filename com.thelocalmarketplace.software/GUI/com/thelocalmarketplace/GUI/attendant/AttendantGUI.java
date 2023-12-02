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
	Attendant attendant;
	List<Session> sessions;
	List<JPanel> stationPanels;
	ITouchScreen asScreen;
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int[] dimensions = {0,0};
	private int width;
	private int height;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public AttendantGUI(Attendant attendant) {
		this.attendant = attendant;
		this.asScreen = attendant.getStation().screen;
		
		sessions = attendant.getSessions();
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		width = 1500;
		height = 800;
		
		asScreen.getFrame().setTitle("Attendant Screen GUI");
		asScreen.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		asScreen.getFrame().setSize(new Dimension(width, height));
		asScreen.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
		asScreen.getFrame().getContentPane().setLayout(new GridLayout(6,0,0,0));
		
		asScreen.getFrame().getContentPane().setBackground(Colors.color1);
		
		
		
		populateSessions();
		asScreen.setVisible(false);
	}
	
	public void populateSessions() {
		int val = 0;
		if (sessions.size() != 0) {
		while(val < sessions.size()) {
			JPanel panel = new StationPanel(sessions.get(val));
			stationPanels.add(panel);
			panel.setPreferredSize(new Dimension(width/6, width/6));
			asScreen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
		}
		} else {
			JPanel panel = new StationPanel(null);
			panel.setPreferredSize(new Dimension(width/6, width/6));
			asScreen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
		}
	}
	
	public void hide() {
		asScreen.getFrame().setVisible(false);
	}
	
	public void unhide() {
		asScreen.getFrame().setVisible(true);
	}
	
}