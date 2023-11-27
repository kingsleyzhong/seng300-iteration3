package com.thelocalmarketplace.GUI.session;

import javax.swing.JFrame;

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.software.Session;

public class SoftwareGUI {
	Session session;
	ITouchScreen screen;
	JFrame frame;
	
	public SoftwareGUI(Session session, ITouchScreen screen) {
		this.session = session;
		this.screen = screen;
		screen.getFrame();
	}

}