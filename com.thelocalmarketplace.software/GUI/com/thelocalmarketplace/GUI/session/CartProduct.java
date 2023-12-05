package com.thelocalmarketplace.GUI.session;

import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.customComponents.RoundPanel;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.items.ReusableBagProduct;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * A panel that represents an added product to the cart after adding.
 * Tracks the quanity/mass of the added product. Allows the customer to
 * remove items or add items of the same type.
 * 
 * Project Iteration 3 Group 1
 *
 * Derek Atabayev : 30177060
 * Enioluwafe Balogun : 30174298
 * Subeg Chahal : 30196531
 * Jun Heo : 30173430
 * Emily Kiddle : 30122331
 * Anthony Kostal-Vazquez : 30048301
 * Jessica Li : 30180801
 * Sua Lim : 30177039
 * Savitur Maharaj : 30152888
 * Nick McCamis : 30192610
 * Ethan McCorquodale : 30125353
 * Katelan Ng : 30144672
 * Arcleah Pascual : 30056034
 * Dvij Raval : 30024340
 * Chloe Robitaille : 30022887
 * Danissa Sandykbayeva : 30200531
 * Emily Stein : 30149842
 * Thi My Tuyen Tran : 30193980
 * Aoi Ueki : 30179305
 * Ethan Woo : 30172855
 * Kingsley Zhong : 30197260
 */

public class CartProduct extends JPanel {

	private static final long serialVersionUID = 1L;
	private Product product;
	private Session session;
	private int quantity;
	private JLabel quantityLabel;
	private final boolean isPLU;
	
	private String price;
	private String mass;
	private String description;
	private JLabel weightLabel;
	private JButton minusButton;
	private PlainButton plusButton;
	

	/**
	 * Create the panel.
	 */
	public CartProduct(Product product, Session session, Mass mass) {
		this.product = product;
		this.session = session;
		
		if(product.isPerUnit()) {
			isPLU = false;
			price = "$" + product.getPrice() + "/unit";
		}
		else {
			isPLU = true;
			price = "$" + product.getPrice() + "/kg";
		}
		
		this.mass = mass.inGrams().toString() + "g";
		
		if(product instanceof BarcodedProduct) {
			description = ((BarcodedProduct) product).getDescription();
			quantity = 1;
		}
		else if(product instanceof PLUCodedProduct) {
			description = ((PLUCodedProduct) product).getDescription();
			quantity = 1;
		} 
		else if (product instanceof ReusableBagProduct) {
			description = ((ReusableBagProduct) product).getDescription();
			quantity =  quantity + ReusableBagProduct.getAdded();
		}else {
			description = "Some product";
			quantity = 1;
		}
		
		
    	
		this.setBackground(Colors.color3);
		this.setPreferredSize(new Dimension(500, 50));
		this.setMaximumSize(getMaximumSize());
		
    	GridBagLayout gbl_panel = new GridBagLayout();
    	gbl_panel.columnWidths = new int[]{221, 92, 102, 116, 0};
    	gbl_panel.rowHeights = new int[]{250, 0};
    	gbl_panel.columnWeights = new double[]{1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
    	gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
    	this.setLayout(gbl_panel);
    	
    	JLabel descriptionLabel = new JLabel(description);
    	GridBagConstraints gbc_descriptionLabel = new GridBagConstraints();
    	gbc_descriptionLabel.insets = new Insets(0, 0, 0, 5);
    	gbc_descriptionLabel.gridx = 0;
    	gbc_descriptionLabel.gridy = 0;
    	this.add(descriptionLabel, gbc_descriptionLabel);
    	
    	JPanel panel_1 = new RoundPanel(15, Colors.color2);
    	panel_1.setBackground(Colors.color3);
    	GridBagConstraints gbc_panel_1 = new GridBagConstraints();
    	gbc_panel_1.insets = new Insets(0, 0, 0, 5);
    	gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
    	gbc_panel_1.gridx = 1;
    	gbc_panel_1.gridy = 0;
    	this.add(panel_1, gbc_panel_1);
    	
    	minusButton = new PlainButton("-", Colors.color2);
    	minusButton.setForeground(Colors.color3);
    	minusButton.setBorder(BorderFactory.createEmptyBorder());
    	minusButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(product instanceof BarcodedProduct) {
					session.getManager().removeItem((BarcodedProduct) product);
				}
				else if(product instanceof PLUCodedProduct) {
					session.getManager().removeItem((PLUCodedProduct) product);
				}
			}
    		
    	});
    	minusButton.setPreferredSize(new Dimension(20,20));
    	panel_1.add(minusButton);
    	
    	quantityLabel = new JLabel("" + quantity);
    	quantityLabel.setForeground(Colors.color3);
    	panel_1.add(quantityLabel);
    	
    	plusButton = new PlainButton("+", Colors.color2);
    	plusButton.setForeground(Colors.color3);
    	plusButton.setBorder(BorderFactory.createEmptyBorder());
    	plusButton.addActionListener(new ActionListener() {

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
    	plusButton.setPreferredSize(new Dimension(20,20));
    	panel_1.add(plusButton);
    	
    	weightLabel = new JLabel(this.mass);
    	GridBagConstraints gbc_weightLabel = new GridBagConstraints();
    	gbc_weightLabel.insets = new Insets(0, 0, 0, 5);
    	gbc_weightLabel.gridx = 2;
    	gbc_weightLabel.gridy = 0;
    	add(weightLabel, gbc_weightLabel);
    	
    	JLabel priceLabel = new JLabel(price);
    	GridBagConstraints gbc_priceLabel = new GridBagConstraints();
    	gbc_priceLabel.anchor = GridBagConstraints.WEST;
    	gbc_priceLabel.gridx = 3;
    	gbc_priceLabel.gridy = 0;
    	this.add(priceLabel, gbc_priceLabel);
    	
    	this.setMaximumSize(new Dimension(2000, 50));
	}
	
	/**
	 * 
	 * @return
	 * 			true if successfully added
	 */
	public boolean addProduct() {
		quantity = quantity + 1;
		quantityLabel.setText("" + quantity);
		revalidate();
		return true;
	}
	
	public boolean addProduct(int quantity) {
		this.quantity = quantity;
		quantityLabel.setText("" + quantity);
		revalidate();
		return true;
	}
	
	/**
	 * 
	 * @return
	 * 			return false if the panel now has 0 products
	 */
	public boolean removeProduct() {
		quantity = quantity - 1;
		quantityLabel.setText("" + quantity);
		revalidate();
		if(quantity <= 0) {
			return false;
		}
		return true;
	}

	public void updateMass(BigDecimal inGrams) {
		DecimalFormat df = new DecimalFormat("#.00");
		double mass = inGrams.doubleValue();
		String s = "";
		if(mass>=1000) {
			double temp = mass/1000;
			s = df.format(temp) + "kg";
		}
		else s = df.format(mass) + "g";
		weightLabel.setText(s);
		
	}

	public JButton getMinusButton() {
		return minusButton;
	}

	public PlainButton getPlusButton() {
		return plusButton;
	}

}
