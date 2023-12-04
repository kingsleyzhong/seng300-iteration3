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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.tdc.banknote.BanknoteStorageUnit;
import com.tdc.coin.CoinStorageUnit;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.SearchCatalogue;
import com.thelocalmarketplace.hardware.ISelfCheckoutStation;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.IssuePredictor;
import com.thelocalmarketplace.software.attendant.IssuePredictorListener;
import com.thelocalmarketplace.software.attendant.Issues;
import com.thelocalmarketplace.software.attendant.MaintenanceManager;
import com.thelocalmarketplace.software.attendant.MaintenanceManagerListener;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import javax.swing.SwingConstants;

public class StationPanel extends JPanel implements ActionListener {
	Session session;
	String issuesText;
	JFrame searchCatalogue;
	private Attendant attendant;
	private HashMap<String, Boolean> issues = new HashMap<String, Boolean>();
	private HashMap<String, String> issueMessages = new HashMap<String, String>();
	
	private final Color warningColor = new Color(191, 114, 13);
	private final Color urgentColor = new Color(161, 40, 40);
	private final Color goodColor = new Color(60, 179, 113);
	int statusN;
	Boolean enabled = false;
	
	JPanel sidePanel;
	JLabel stationName = new JLabel("");
	JLabel status = new JLabel("");
	JLabel info = new JLabel("");
	Color detailsColor;
	
	JButton addBySearch;
	JButton power;	
	Color powerColor;
	
	private static final long serialVersionUID = 1L;
	
	public StationPanel(Session session, Attendant attendant, IssuePredictor predictor, MaintenanceManager manager) {
		this.attendant = attendant;
		this.session = session;
		
		// register listeners
		predictor.register(new InnerPredictionListener());
		manager.register(new InnerResolutionListener());		
		
		GridLayout layout = new GridLayout(1,6,0,0);
		layout.setHgap(20);
		layout.setVgap(20);
		setLayout(layout);
		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		if (session != null) {
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
			
			addBySearch = new PlainButton("<html>Add by<br>search</html>", Colors.color4);
			addBySearch.setFont(new Font("Tahoma", Font.BOLD, 18));
			addBySearch.addActionListener(this);
			
			add(stationName);
			add(status);
			
			info = new JLabel(issuesText);
			info.setVerticalAlignment(SwingConstants.TOP);
			info.setFont(new Font("Tahoma", Font.PLAIN, 14));
			populateIssueMessages();
			populateIssues(false);
			updateIssues(issues);
			add(info);
			add(addBySearch);
			add(power);
		} else {
			JLabel empty = new JLabel("NO STATIONS REGISTERED");
			empty.setForeground(new Color(60, 179, 113));
			empty.setFont(new Font("Tahoma", Font.BOLD, 16));
			empty.setHorizontalAlignment(SwingConstants.CENTER);
			add(empty);
		}
	}

	private void populateIssueMessages() {
		issueMessages.put("ink", "WARNING: Predicting low ink");
		issueMessages.put("paper", "WARNING: Predicting low paper");
		issueMessages.put("coinsLow", "WARNING: Predicting low coins");
		issueMessages.put("banknotesLow", "WARNING: Predicting low banknotes");
		issueMessages.put("coinsFull", "WARNING: Predicting full coins");
		issueMessages.put("banknotesFull", "WARNING: Predicting full banknotes");
	}
	
	private void populateIssues(boolean stat) {
		issues.put("ink", stat);
		issues.put("paper", stat);
		issues.put("coinsLow", stat);
		issues.put("banknotesLow", stat);
		issues.put("coinsFull", stat);
		issues.put("banknotesFull", stat);
	}
	
	public boolean issuesPresent() {
		return issues.containsValue(true);
	}
	
	public HashMap<String, Boolean> getIssues() {
		return issues;
	}
	
	private void updateIssues(HashMap<String, Boolean> map) {
		issuesText = "<html>";
		for(String key : map.keySet()) {
			if(issues.get(key) == true) {
				JOptionPane.showMessageDialog(null, issueMessages.get(key));
				issuesText = issuesText.concat(issueMessages.get(key));
				issuesText = issuesText.concat("<br>");
			}
		}
		issuesText = issuesText.concat("</html>");
		
		if(issuesPresent()) {
			status.setForeground(warningColor);
			status.setText("STATUS: WARNING");
		}else {
			status.setForeground(goodColor);
			status.setText("STATUS: STABLE");
		}
		
		if(!enabled) {
			status.setForeground(urgentColor);
			status.setText("STATUS: DISABLED");
		}
		
		info.setText(issuesText);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == power) {
			if(!enabled) {
				attendant.enableStation(session);
				enabled = true;
				power.setText("ON");
				power.setBackground(new Color(158, 228, 144));
				updateIssues(issues);
			} else {
				attendant.disableStation(session);
				enabled = false;
				power.setText("OFF");
				power.setBackground(new Color(205, 92, 92));
				updateIssues(issues);
			}
		} else if (e.getSource() == addBySearch) {
			new AttendantCatalogue(session, attendant);
		}
	}
	
	private class InnerPredictionListener implements IssuePredictorListener{

		@Override
		public void notifyPredictLowInk(Session session) {
			issues.put("ink", true);
			updateIssues(issues);
		}

		@Override
		public void notifyPredictLowPaper(Session session) {
			issues.put("paper", true);
			updateIssues(issues);
		}

		@Override
		public void notifyPredictCoinsFull(Session session) {
			issues.put("coinsFull", true);
			updateIssues(issues);
		}

		@Override
		public void notifyPredictBanknotesFull(Session session) {
			issues.put("banknotesFull", true);
			updateIssues(issues);
		}

		@Override
		public void notifyPredictLowCoins(Session session) {
			issues.put("coinsLow", true);
			updateIssues(issues);
		}

		@Override
		public void notifyPredictLowBanknotes(Session session) {
			issues.put("banknotesLow", true);
			updateIssues(issues);
		}

		@Override
		public void notifyNoIssues(Session session) {
			populateIssues(false);
			updateIssues(issues);
		}
		
	}
	
	private class InnerResolutionListener implements MaintenanceManagerListener{

		@Override
		public void notifyInkAdded(Session session) {
			issues.put("ink", false);
			updateIssues(issues);
		}

		@Override
		public void notifyPaperAdded(Session session) {
			issues.put("paper", false);
			updateIssues(issues);
		}

		@Override
		public void notifyCoinAdded(Session session) {
			issues.put("coinsLow", false);
			updateIssues(issues);
		}

		@Override
		public void notifyBanknoteAdded(Session session) {
			issues.put("banknotesLow", false);
			updateIssues(issues);
		}

		@Override
		public void notifyCoinRemoved(Session session, CoinStorageUnit coinStorage) {
			issues.put("coinsFull", false);
			updateIssues(issues);
		}

		@Override
		public void notifyBanknoteRemoved(Session session, BanknoteStorageUnit banknoteStorage) {
			issues.put("banknotesFull", false);
			updateIssues(issues);
		}

		@Override
		public void notifyHardwareOpened(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void notifyHardwareClosed(Session session) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
