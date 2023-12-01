package com.thelocalmarketplace.GUI.session;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;

public class ProductPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private Product product;
	private Session session;
	
	/**
	 * Create the panel.
	 */
	public ProductPanel(Product product, Session session) {
		this.product = product;
		this.session = session;
		
		this.setSize(250, 400);
		
		ImageIcon image = new ImageIcon(new ImageIcon("images/sheepIcon.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
		JLabel label = new JLabel(image);
		add(label);
	}
	
	public ProductPanel() {
		this.setSize(250, 400);
		
		ImageIcon image = new ImageIcon(new ImageIcon("images/sheepIcon.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
		JLabel label = new JLabel(image);
		add(label);
	}

}
