package com.thelocalmarketplace.GUI;

import com.thelocalmarketplace.GUI.startscreen.StartScreenGUI;

public class Main {
	
	public static void main(String[] args) {
		Simulation simulation = new Simulation();
		new StartScreenGUI(simulation);
	}

}