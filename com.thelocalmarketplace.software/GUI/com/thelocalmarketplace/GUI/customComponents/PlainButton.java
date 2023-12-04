package com.thelocalmarketplace.GUI.customComponents;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * A simple button to replace the basic button look.
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