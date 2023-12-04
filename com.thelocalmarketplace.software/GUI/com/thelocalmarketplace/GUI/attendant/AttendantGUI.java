package com.thelocalmarketplace.GUI.attendant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import com.thelocalmarketplace.software.Session;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.ArrayList;
<<<<<<< HEAD
import java.util.HashMap;
=======
>>>>>>> f2c75a51bda1718d4ee10da8e7e10b254c2b0108
import java.util.List;
import javax.swing.JPanel;

import javax.swing.JFrame;
import javax.swing.JLabel;
<<<<<<< HEAD
import javax.swing.JOptionPane;
=======
>>>>>>> f2c75a51bda1718d4ee10da8e7e10b254c2b0108

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.ISelfCheckoutStation;
import com.thelocalmarketplace.software.attendant.Attendant;
<<<<<<< HEAD
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.IssuePredictorListener;
import com.thelocalmarketplace.software.attendant.Issues;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.attendant.Requests;
=======
import com.thelocalmarketplace.software.attendant.IssuePredictorListener;
import com.thelocalmarketplace.software.attendant.Issues;
>>>>>>> f2c75a51bda1718d4ee10da8e7e10b254c2b0108
import com.thelocalmarketplace.software.attendant.TextSearchController;

public class AttendantGUI {
	Attendant attendant;
<<<<<<< HEAD
	MaintenanceManager manager;
	IssuePredictor predictor;
	HashMap<Session, Requests> sessions;
=======
	List<Session> sessions;
>>>>>>> f2c75a51bda1718d4ee10da8e7e10b254c2b0108
	List<JPanel> stationPanels;
	ITouchScreen asScreen;
	TextSearchController textSearch;
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int[] dimensions = {0,0};
	private int width;
	private int height;
	
	/**
	 * @wbp.parser.entryPoint
	 */
<<<<<<< HEAD
	public AttendantGUI(Attendant attendant, MaintenanceManager manager) {
		this.attendant = attendant;
		this.manager = manager;
		this.asScreen = attendant.getStation().screen;
		this.textSearch = attendant.getTextSearchController();

=======
	public AttendantGUI(Attendant attendant) {
		this.attendant = attendant;
		this.asScreen = attendant.getStation().screen;
		this.textSearch = attendant.getTextSearchController();
		
>>>>>>> f2c75a51bda1718d4ee10da8e7e10b254c2b0108
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
<<<<<<< HEAD
			for(Session session : sessions.keySet()) {
				// maintenance managers?
				// issue predictors?
				JPanel panel = new StationPanel(session, attendant, predictor, manager);
				panel.setPreferredSize(new Dimension(width/6, width/6));
				asScreen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
			}
		} else {
			// maintenance managers?
			// issue predictors?
			JPanel panel = new StationPanel(null, attendant, predictor, manager);
=======
		while(val < sessions.size()) {
			JPanel panel = new StationPanel(sessions.get(val), attendant);
			stationPanels.add(panel);
			attendant.registerOn(sessions.get(val));
			panel.setPreferredSize(new Dimension(width/6, width/6));
			asScreen.getFrame().getContentPane().add(panel, BorderLayout.SOUTH);
		}
		} else {
			JPanel panel = new StationPanel(null, attendant);
			//attendant.registerOn(null);
>>>>>>> f2c75a51bda1718d4ee10da8e7e10b254c2b0108
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