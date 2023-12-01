package com.thelocalmarketplace.GUI.customComponents;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GradientPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private Color color1;
	private Color color2;
	/**
	 * Create the panel.
	 */
	public GradientPanel(Color color1, Color color2) {
		super();
		this.color1 = color1;
		this.color2 = color2;
	}

	    @Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2d = (Graphics2D) g;
	    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	    int w = getWidth(), h = getHeight();
	    GradientPaint gp = new GradientPaint(0, 0, color1, 0, h - h/5, color2);
	    g2d.setPaint(gp);
	    g2d.fillRect(0, 0, w, h);
	}
}
