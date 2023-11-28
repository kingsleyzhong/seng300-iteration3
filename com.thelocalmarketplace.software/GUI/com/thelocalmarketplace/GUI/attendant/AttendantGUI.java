package com.thelocalmarketplace.GUI.attendant;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.software.attendant.Attendant;

public class AttendantGUI {
	Attendant attendant;
	ITouchScreen screen;
	JFrame attendantFrame;
	
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int[] dimensions = {0,0};
	private int width;
	private int height;
	
	public AttendantGUI(Attendant attendant, ITouchScreen screen) {
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		width = 1500;
		height = 800;
		
		screen.getFrame().setTitle("Attendant Screen GUI");
		screen.getFrame().setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		screen.getFrame().setSize(new Dimension(width, height));
		screen.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
		screen.getFrame().getContentPane().setLayout(new GridLayout(10,10));
		
		GridBagConstraints gridConstraints = new GridBagConstraints();
		gridConstraints.insets = new Insets(1, 1, 1, 1);
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		
		screen.getFrame().getContentPane().setBackground(Colors.color1);
		screen.getFrame().getContentPane().add(new PlainButton("test", Colors.color4));
		
		this.attendant = attendant;
		screen.setVisible(true);
	}
}