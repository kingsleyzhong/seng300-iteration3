package com.thelocalmarketplace.GUI.hardware;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.RoundPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JList;

public class ItemPanel extends JPanel{

	private JPanel items;
	/**
	 * 
	 */
	private static final long serialVersionUID = 322949305620599418L;

	public ItemPanel(String title, JButton button, DefaultListModel<Item> listModel) {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{17, 287, 0};
		gbl_panel.rowHeights = new int[]{25, 0};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0};
		gbl_panel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel label = new JLabel(title);
		label.setForeground(Color.WHITE);
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.insets = new Insets(0, 0, 0, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		panel.add(label, gbc_label);
		
		if(button != null) {
			RoundPanel roundButton = new RoundPanel(15, Colors.color3);
			roundButton.setPreferredSize(new Dimension(25,15));
			roundButton.setBackground(Colors.color2);
			GridBagConstraints gbc_roundButton = new GridBagConstraints();
			gbc_roundButton.insets = new Insets(0, 0, 0, 5);
			gbc_roundButton.anchor = GridBagConstraints.NORTHWEST;
			gbc_roundButton.gridx = 2;
			gbc_roundButton.gridy = 0;
			button.setBorder(BorderFactory.createEmptyBorder());
			button.setPreferredSize(new Dimension(20,10));
			button.setOpaque(false);
			panel.add(roundButton, gbc_roundButton);
			roundButton.add(button);
		}
		panel.setMaximumSize(new Dimension(1000, 25));
		panel.setBackground(Colors.color2);
		
		this.add(panel, BorderLayout.NORTH);
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
		items = new JPanel();
		items.setBackground(Colors.color3);
		items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
		this.add(items, BorderLayout.CENTER);
		
		Item item1 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) }), new Mass(20.0));
		listModel.addElement(item1);
		JList<Item> list = new JList<>(listModel);
		list.setVisibleRowCount(5);
		items.add(list);
	}
}
