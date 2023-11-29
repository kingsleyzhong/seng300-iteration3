package com.thelocalmarketplace.GUI.hardware;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.TransferHandler;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.customComponents.RoundPanel;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.external.ProductDatabases;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;

public class HardwareGUI {
	private AbstractSelfCheckoutStation scs;
	private JFrame hardwareFrame;
	private JPanel content;
	private JPanel screens;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width;
	private int height;
	public boolean clicked = false;
	
	private DefaultListModel<ItemObject> itemsInCart = new DefaultListModel<>();
	private JPanel cartPanel;
	private DefaultListModel<ItemObject> itemsInScanningArea = new DefaultListModel<>();
	private JPanel scanningPanel;
	private DefaultListModel<ItemObject> itemsInBaggingArea = new DefaultListModel<>();
	private JPanel baggingPanel;
	
	private DefaultListModel<ItemObject> lastModel = new DefaultListModel<>();
	private ItemObject lastObject = null;
	
	public HardwareGUI(AbstractSelfCheckoutStation scs) {
		this.scs = scs;
		
		populateItems();
		
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		width = 1500;
		height = 800;
		
		hardwareFrame = new JFrame();
		hardwareFrame.setTitle("Hardware GUI");
		hardwareFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hardwareFrame.setSize(new Dimension(width, height));
		//hardwareFrame.setSize(screenSize);
		hardwareFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		hardwareFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		hardwareFrame.getContentPane().setBackground(Colors.color1);
		
		JPanel buttonPanel = new ButtonPanel(this);
		buttonPanel.setPreferredSize(new Dimension(width, height/4));
		
		content = new JPanel();
		content.setBackground(Colors.color1);
		content.setLayout(new GridLayout(1,0));
		
		hardwareFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		hardwareFrame.getContentPane().add(content, BorderLayout.CENTER);
		
		screens = new JPanel();
		screens.setBackground(Colors.color1);
		
		//hardwareFrame.setUndecorated(true);
	}
	
	public void populateItems() {
		BarcodedItem bitem1 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one}), new Mass(300.0));
		ItemObject item1 = new ItemObject(bitem1, "Baaakini1");
		itemsInCart.addElement(item1);
		
		BarcodedItem bitem2 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one}), new Mass(300.0));
		ItemObject item2 = new ItemObject(bitem1, "Baaakini2");
		itemsInCart.addElement(item2);
		
		BarcodedItem bitem3 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one}), new Mass(300.0));
		ItemObject item3 = new ItemObject(bitem1, "Baaakini3");
		itemsInCart.addElement(item3);
	}
	
	public JPanel introPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Colors.color1);
		GridLayout layout = new GridLayout(1, 0);
		layout.setHgap(20);
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		JButton addButton = new PlainButton("+", Colors.color4);
		addButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		cartPanel = itemPanel("Items in cart", addButton, itemsInCart);
		JPanel panel2 = new JPanel();
		panel2.setBackground(Colors.color1);
		GridLayout layout2 = new GridLayout(0,1);
		layout2.setVgap(20);
		panel2.setLayout(layout2);
		scanningPanel = itemPanel("Items in scanning area", null, itemsInScanningArea);
		baggingPanel = itemPanel("Items in bagging area", null, itemsInBaggingArea);
		panel.add(cartPanel);
		panel2.add(scanningPanel);
		panel2.add(baggingPanel);
		panel.add(panel2);
		content.add(panel);
		content.add(screens);
		content.revalidate();
		return panel;
	}
	
	public JPanel itemPanel(String title, JButton button, DefaultListModel<ItemObject> listModel) {
		JPanel thisPanel = new JPanel();
		thisPanel.setLayout(new BoxLayout(thisPanel, BoxLayout.Y_AXIS));
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
		
		thisPanel.add(panel, BorderLayout.NORTH);
		
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
		JPanel items = new JPanel();
		items.setBackground(Colors.color3);
		items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
		thisPanel.add(items, BorderLayout.CENTER);
		
		BarcodedItem item1 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) }), new Mass(20.0));
		JList<ItemObject> list = new JList<>(listModel);
		ItemObject prototype = new ItemObject(item1, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		list.setPrototypeCellValue(prototype);
		list.setFixedCellHeight(20);
		list.setVisibleRowCount(15);
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new ListTransferHandler());
		list.setMinimumSize(new Dimension(20,20));
		list.setName(title);
		items.add(new JScrollPane(list));
		return thisPanel;
	}
	
	public void addData(ItemObject data, DefaultListModel<ItemObject> listModel) {
		System.out.println("HI");
		if(listModel == itemsInScanningArea) {
        	//System.out.println("removed from scanning area");
        	scs.getScanningArea().addAnItem(data.getItem());
        }
        else if(listModel == itemsInBaggingArea) {
        	scs.getBaggingArea().addAnItem(data.getItem());
        }
	}
	
	public void removeData(ItemObject data, DefaultListModel<ItemObject> listModel) {
		if(listModel == itemsInScanningArea) {
        	//System.out.println("removed from scanning area");
        	scs.getScanningArea().removeAnItem(data.getItem());
        }
        else if(listModel == itemsInBaggingArea) {
        	scs.getBaggingArea().removeAnItem(data.getItem());
        }
	}
	
	public class ListTransferHandler extends TransferHandler {
        private int[] indices = null;
        private int addIndex = -1; //Location where items were added
        private int addCount = 0;  //Number of items added.
                
        /**
         * We only support importing strings.
         */
        public boolean canImport(TransferHandler.TransferSupport info) {
            // Check for String flavor
            if (!info.isDataFlavorSupported(ItemObject.flavor)) {
               return false;
            }
            return true;
       }

        /**
         * Bundle up the selected items in a single list for export.
         * Each line is separated by a newline.
         */
        protected Transferable createTransferable(JComponent c) {
            JList<ItemObject> list = (JList<ItemObject>)c;
            indices = list.getSelectedIndices();
            Object[] values = list.getSelectedValues();
            
            Object buff = values[0];
            return ItemObject.createTransferable(buff);
        }
        
        /**
         * We support move actions.
         */
        public int getSourceActions(JComponent c) {
            return TransferHandler.MOVE;
        }
        
        /**
         * Perform the actual import.  This demo only supports drag and drop.
         */
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }

            JList<ItemObject> list = (JList<ItemObject>) info.getComponent();
            DefaultListModel<ItemObject> listModel = (DefaultListModel<ItemObject>)list.getModel();
            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
            // Get the string that is being dropped.
            Transferable t = info.getTransferable();
            ItemObject data;
            try {
                data = (ItemObject) t.getTransferData(ItemObject.flavor);
            } 
            catch (Exception e) { return false; }
            
            if(data == null) return false;
            
            listModel.addElement(data);
	        //addData(data, listModel);
            lastModel = listModel;
            lastObject = data;
            
            System.out.println("Loop");
            return true;
        }

        /**
         * Remove the items moved from the list.
         */
        protected void exportDone(JComponent c, Transferable data, int action) {
            JList<ItemObject> source = (JList<ItemObject>)c;
            DefaultListModel<ItemObject> listModel  = (DefaultListModel<ItemObject>)source.getModel();
            ItemObject removedObject = null;

            //System.out.println(action);
            if (action == TransferHandler.MOVE) {
            	//System.out.println("move");
                for (int i = indices.length - 1; i >= 0; i--) {
                	removedObject = listModel.elementAt(indices[i]);
                    listModel.remove(indices[i]);
                }
            }
            
            addData(lastObject, listModel);
            removeData(lastObject, listModel);
            
            
            indices = null;
            addCount = 0;
            addIndex = -1;
        }
    }
	
	public void hide() {
    	hardwareFrame.setVisible(false);
    }
	
	public void unhide() {
		hardwareFrame.setVisible(true);
	}
}