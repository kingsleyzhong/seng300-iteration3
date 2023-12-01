package com.thelocalmarketplace.GUI.startscreen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.Simulation;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.GradientPanel;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.software.attendant.Attendant;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

public class StartScreenGUI {
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width;
	private int height;
	
	JFrame frame;
	
	public StartScreenGUI() {
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setForeground(Colors.color1);
		frame.setBackground(Colors.color1);
		frame.setAlwaysOnTop(true);
		
		//setting size
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		frame.setSize(width, height);
		
		JPanel main = new GradientPanel(Colors.color2, Colors.color1);
		main.setLayout(new GridLayout(0,1,50,50));
		
		//this is the start button
		JButton btnStart = new PlainButton("Start", Colors.color1);
		btnStart.setOpaque(false);
		btnStart.setFont(new Font("Dialog", Font.BOLD, 40));
		btnStart.setBorder(BorderFactory.createEmptyBorder());
		btnStart.setMaximumSize(new Dimension(200,100));
		btnStart.setForeground(Color.BLACK);
		btnStart.setBackground(Colors.color1);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close_start_screen();
				HardwareGUI.setVisibility(true);
				//simulation.unhide();
			}
		});
		
		
		
		JLabel lblWelcome = new JLabel("WELCOME");
		lblWelcome.setBackground(Color.RED);
		lblWelcome.setForeground(Colors.color3);
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setVerticalAlignment(SwingConstants.BOTTOM);
		lblWelcome.setFont(new Font("Dialog", Font.BOLD, 92));
		main.add(lblWelcome, JLabel.CENTER_ALIGNMENT);
		main.add(btnStart, JButton.CENTER_ALIGNMENT);
		
		frame.getContentPane().add(main, BorderLayout.CENTER);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);	
		
		
	}
	
	
	//call this to get rid of the start screen
	public void close_start_screen() {
		frame.setVisible(false);	
	}
	
	//Call this to open the start screen
	public void open_start_screen() {
		frame.setVisible(true);	
	}
	
	
	
}
