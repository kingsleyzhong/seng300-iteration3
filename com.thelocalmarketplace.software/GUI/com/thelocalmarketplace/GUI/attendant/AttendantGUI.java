package com.thelocalmarketplace.GUI.attendant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import com.thelocalmarketplace.software.Session;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JFrame;
import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.SoftwareGUI;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.attendant.TextSearchController;

/**
 * An interface to display interactions with the attendant and attendant station.
 * 
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev : 30177060
 * Enioluwafe Balogun : 30174298
 * Subeg Chahal : 30196531
 * Jun Heo : 30173430
 * Emily Kiddle : 30122331
 * Anthony Kostal-Vazquez : 30048301
 * Jessica Li : 30180801
 * Sua Lim : 30177039
 * Savitur Maharaj : 30152888
 * Nick McCamis : 30192610
 * Ethan McCorquodale : 30125353
 * Katelan Ng : 30144672
 * Arcleah Pascual : 30056034
 * Dvij Raval : 30024340
 * Chloe Robitaille : 30022887
 * Danissa Sandykbayeva : 30200531
 * Emily Stein : 30149842
 * Thi My Tuyen Tran : 30193980
 * Aoi Ueki : 30179305
 * Ethan Woo : 30172855
 * Kingsley Zhong : 30197260
 */

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