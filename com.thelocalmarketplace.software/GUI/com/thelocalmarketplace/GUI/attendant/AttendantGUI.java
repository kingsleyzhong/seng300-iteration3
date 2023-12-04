package com.thelocalmarketplace.GUI.attendant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import com.thelocalmarketplace.software.Session;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JFrame;
import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.customComponents.Colors;

import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;

import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.attendant.TextSearchController;

public class AttendantGUI {
	Attendant attendant;
	MaintenanceManager manager;
	IssuePredictor predictor;
	HashMap<Session, Requests> sessions;
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
	public AttendantGUI(Attendant attendant, MaintenanceManager manager) {
		this.attendant = attendant;
		this.manager = manager;
		this.asScreen = attendant.getStation().screen;
		this.textSearch = attendant.getTextSearchController();

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