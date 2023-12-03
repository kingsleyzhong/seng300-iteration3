package com.thelocalmarketplace.GUI.session;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JButton;

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
		
		//this.setSize(200, 400);
		this.setBackground(Colors.color1);
		this.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{216, 0};
		gridBagLayout.rowHeights = new int[]{200, 69, 63, 93, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		setLayout(gridBagLayout);
		
		ImageIcon image = new ImageIcon(new ImageIcon("images/sheepIcon.png").getImage().getScaledInstance(180, 180, Image.SCALE_DEFAULT));
		JLabel label = new JLabel(image);
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		add(label, gbc_label);
		
		String productDescription;
		if(product instanceof BarcodedProduct) {
			productDescription = ((BarcodedProduct) product).getDescription();
		} 
		else if(product instanceof PLUCodedProduct) {
			productDescription = ((PLUCodedProduct) product).getDescription();
		}
		else productDescription = "Some product";
		
		JLabel description = new JLabel("<html>" + productDescription + "</html>");
		description.setHorizontalAlignment(JLabel.CENTER);
		description.setFont(new Font("Tahoma", Font.BOLD, 30));
		GridBagConstraints gbc_description = new GridBagConstraints();
		gbc_description.insets = new Insets(0, 0, 5, 0);
		gbc_description.gridx = 0;
		gbc_description.gridy = 1;
		gbc_description.weightx = 1;
		gbc_description.fill = GridBagConstraints.HORIZONTAL;
		gbc_description.anchor = GridBagConstraints.SOUTH;
		add(description, gbc_description);
		
		String price;
		String per;
		if(product.isPerUnit()) {
			per = "/unit";
		}
		else per = "/kg";
		price = product.getPrice() + per;
		
		JLabel priceLbl = new JLabel("$"+price);
		priceLbl.setHorizontalAlignment(JLabel.CENTER);
		GridBagConstraints gbc_priceLbl = new GridBagConstraints();
		gbc_priceLbl.insets = new Insets(0, 0, 5, 0);
		gbc_priceLbl.fill = GridBagConstraints.BOTH;
		gbc_priceLbl.gridx = 0;
		gbc_priceLbl.gridy = 2;
		gbc_priceLbl.weightx = 1;
		gbc_priceLbl.fill = GridBagConstraints.HORIZONTAL;
		add(priceLbl, gbc_priceLbl);
		
		JButton btnNewButton = new PlainButton("Add", Colors.color2);
		btnNewButton.setForeground(Colors.color3);
		btnNewButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(product instanceof BarcodedProduct) {
					session.getManager().addItem((BarcodedProduct) product);
				}
				else if(product instanceof PLUCodedProduct) {
					session.getManager().addItem(((PLUCodedProduct) product).getPLUCode());
				}
			}
			
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
		gbc_btnNewButton.gridx = 0;
		gbc_btnNewButton.gridy = 3;
		gbc_btnNewButton.weightx = 1;
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		add(btnNewButton, gbc_btnNewButton);
	}

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension arcs = new Dimension(30, 30);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        //Draws the rounded panel with borders.
        
        graphics.setColor(Colors.color3);
       
        graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint background
        //graphics.setColor(getForeground());
        //graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height); //paint border
    }

}
