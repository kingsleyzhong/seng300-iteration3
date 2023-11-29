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
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.software.attendant.Attendant;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class StartScreenGUI {
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width;
	private int height;
	JFrame frame;
	
	public StartScreenGUI() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setForeground(Colors.color1);
		frame.setBackground(Colors.color1);
		frame.setAlwaysOnTop(true);
		
		//setting size
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		frame.setSize(width, height);
		
		
		
		//this is the start button
		JButton btnStart = new JButton("Start");
		btnStart.setFont(new Font("Dialog", Font.BOLD, 40));
		btnStart.setForeground(Color.BLACK);
		btnStart.setBackground(Colors.color1);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close_start_screen();
				
				//new Simulation();
				
			}
		});
		
		
		frame.getContentPane().add(btnStart, BorderLayout.CENTER);
		
		JLabel lblWelcome = new JLabel("WELCOME");
		lblWelcome.setBackground(Color.RED);
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setVerticalAlignment(SwingConstants.BOTTOM);
		lblWelcome.setFont(new Font("Dialog", Font.BOLD, 92));
		frame.getContentPane().add(lblWelcome, BorderLayout.NORTH);
		frame.setVisible(true);	
		
		
		
	}
	
	
	//call this to get rid of the start screen
	public void close_start_screen() {
		frame.setVisible(false);	
	}
	
	//Call this to open the start screen
	public void open_start_screen() {
		frame.setVisible(false);	
	}
	
	
	
}
