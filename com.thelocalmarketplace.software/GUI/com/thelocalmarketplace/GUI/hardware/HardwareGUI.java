package com.thelocalmarketplace.GUI.hardware;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jjjwelectronics.Item;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;

public class HardwareGUI {
	private JFrame hardwareFrame;
	private JPanel content;
	private JPanel screens;
	private CardLayout cards;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width;
	private int height;
	public boolean clicked = false;
	
	private JButton selectedButton;
	private DefaultListModel<Item> itemsInCart = new DefaultListModel<>();
	private JPanel cartPanel;
	private DefaultListModel<Item> itemsInScanningArea = new DefaultListModel<>();
	private JPanel scanningPanel;
	private DefaultListModel<Item> itemsInBaggingArea = new DefaultListModel<>();
	private JPanel baggingPanel;
	
	public HardwareGUI(AbstractSelfCheckoutStation scs) {
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		width = 1500;
		height = 800;
		
		hardwareFrame = new JFrame();
		hardwareFrame.setTitle("Hardware GUI");
		hardwareFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hardwareFrame.setSize(new Dimension(width, height));
		//hardwareFrame.setSize(screenSize);
		hardwareFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		hardwareFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		hardwareFrame.getContentPane().setBackground(Colors.color1);
		
		JPanel buttonPanel = new ButtonPanel(this);
		buttonPanel.setPreferredSize(new Dimension(width, height/4));
		
		content = new JPanel();
		content.setBackground(Colors.color1);
		content.setLayout(new GridLayout(1,0));
		
		hardwareFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		hardwareFrame.getContentPane().add(content, BorderLayout.CENTER);
		
		screens = new JPanel();
		screens.setBackground(Colors.color1);
		
		//hardwareFrame.setUndecorated(true);
		hardwareFrame.setVisible(true);
	}
	
	
	public JPanel introPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Colors.color1);
		GridLayout layout = new GridLayout(1, 0);
		layout.setHgap(20);
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JButton addButton = new PlainButton("+", Colors.color4);
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		cartPanel = new ItemPanel("Items in cart", addButton, itemsInCart);
		JPanel panel2 = new JPanel();
		panel2.setBackground(Colors.color1);
		GridLayout layout2 = new GridLayout(0,1);
		layout2.setVgap(20);
		panel2.setLayout(layout2);
		scanningPanel = new ItemPanel("Items in scanning area", null, itemsInScanningArea);
		baggingPanel = new ItemPanel("Items in bagging area", null, itemsInBaggingArea);
		panel.add(cartPanel);
		panel2.add(scanningPanel);
		panel2.add(baggingPanel);
		panel.add(panel2);
		content.add(panel);
		content.add(screens);
		content.revalidate();
		return panel;
	}
	
	public class buttonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			if(source instanceof ItemButton) {
				selectedButton = (JButton) source;
			}
			
		}
		
	}
}