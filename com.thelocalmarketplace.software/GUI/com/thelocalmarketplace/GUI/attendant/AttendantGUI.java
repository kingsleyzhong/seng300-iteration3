package com.thelocalmarketplace.GUI.attendant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import com.thelocalmarketplace.software.Session;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.SoftwareGUI;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.ISelfCheckoutStation;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.IssuePredictorListener;
import com.thelocalmarketplace.software.attendant.Issues;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.attendant.TextSearchController;

public class AttendantGUI {
	Attendant attendant;
	MaintenanceManager manager;
	IssuePredictor predictor;
	HashMap<Session, Requests> sessions;
	List<JPanel> stationPanels;
	static ITouchScreen asScreen;
	TextSearchController textSearch;
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int[] dimensions = {0,0};
	private int width;
	private int height;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public AttendantGUI(Attendant attendant, MaintenanceManager manager, IssuePredictor predictor) {
		this.attendant = attendant;
		this.manager = manager;
		this.predictor = predictor;
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
		asScreen.getFrame().getContentPane().setLayout(new BorderLayout());
		
		asScreen.getFrame().getContentPane().setBackground(Colors.color1);
		// ORANGE PANEL FOR ITEM DETAILS:
		JPanel orangePanel = new JPanel();
		orangePanel.setBackground(Colors.color5);
		orangePanel.setLayout(new GridLayout(1,0,10,10));
					
		JButton hardwareButton = new PlainButton("Hardware GUI", Colors.color5);
		hardwareButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HardwareGUI.setVisibility(true);
						
			}		
		});
		JButton softwareButton = new PlainButton("Software GUI", Colors.color5);
		softwareButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SoftwareGUI.unhide();
			}		
		});
		JPanel search = new SearchBar(textSearch);
		
		orangePanel.add(hardwareButton);
		orangePanel.add(softwareButton);
		orangePanel.add(search);
		asScreen.getFrame().getContentPane().add(orangePanel, BorderLayout.NORTH);
		
		populateSessions();
		asScreen.setVisible(false);
	}
	
	public void populateSessions() {
		JPanel main = new JPanel();
		main.setBackground(Colors.color1);
		main.setLayout(new GridLayout(6,0,0,0));
		if (sessions.size() != 0) {
			for(Session session : sessions.keySet()) {
				JPanel panel = new StationPanel(session, attendant, predictor, manager);
				panel.setPreferredSize(new Dimension(width/6, width/6));
				main.add(panel);
			}
		} else {
			JPanel panel = new StationPanel(null, attendant, predictor, manager);
			panel.setPreferredSize(new Dimension(width/6, width/6));
			main.add(panel);
		}
		asScreen.getFrame().getContentPane().add(main, BorderLayout.CENTER);
		asScreen.getFrame().getContentPane().revalidate();
	}
	
	public static void hide() {
		asScreen.getFrame().setVisible(false);
	}
	
	public static void unhide() {
		asScreen.getFrame().setVisible(true);
	}
	
}