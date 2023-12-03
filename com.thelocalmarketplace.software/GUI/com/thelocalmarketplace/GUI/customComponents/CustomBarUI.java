package com.thelocalmarketplace.GUI.customComponents;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

public class CustomBarUI extends BasicScrollBarUI{

	protected JButton createZeroButton() {
	    JButton button = new JButton("zero button");
	    Dimension zeroDim = new Dimension(0,0);
	    button.setPreferredSize(zeroDim);
	    button.setMinimumSize(zeroDim);
	    button.setMaximumSize(zeroDim);
	    return button;
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
	    return createZeroButton();
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
	    return createZeroButton();
	}
	
	@Override protected void configureScrollBarColors() { 
		this.thumbColor = new Color(0x7E7E31); 
		this.trackColor = Colors.color2;
		} 
	
}
