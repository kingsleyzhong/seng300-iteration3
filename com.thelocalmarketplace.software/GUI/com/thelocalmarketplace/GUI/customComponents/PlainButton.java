package com.thelocalmarketplace.GUI.customComponents;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

public class PlainButton extends JButton{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3273451399709793573L;

	public PlainButton(String content, Color color) {
		super(content);
		this.setBackground(color);
		this.setUI(new BasicButtonUI());
	}
}