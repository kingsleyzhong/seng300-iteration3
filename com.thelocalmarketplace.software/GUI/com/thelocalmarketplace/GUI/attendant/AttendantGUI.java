package com.thelocalmarketplace.GUI.attendant;

import javax.swing.JFrame;

import com.jjjwelectronics.screen.ITouchScreen;
import com.thelocalmarketplace.software.attendant.Attendant;

public class AttendantGUI {
	Attendant attendant;
	ITouchScreen screen;
	JFrame frame;
	
	public AttendantGUI(Attendant attendant, ITouchScreen screen) {
		this.attendant = attendant;
		this.screen = screen;
		frame = screen.getFrame();
	}
}