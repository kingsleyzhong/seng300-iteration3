package com.thelocalmarketplace.GUI.hardware;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jjjwelectronics.Item;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;

public class HardwareGUI {
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
	private CardLayout cards;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width;
	private int height;
	public boolean clicked = false;
	
	private DefaultListModel<Item> itemsInCart = new DefaultListModel<>();
	private JPanel cartPanel;
	private DefaultListModel<Item> itemsInScanningArea = new DefaultListModel<>();
	private JPanel scanningPanel;
	private DefaultListModel<Item> itemsInBaggingArea = new DefaultListModel<>();
	private JPanel baggingPanel;
	
	public HardwareGUI(AbstractSelfCheckoutStation scs) {
		this.scs = scs;
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
		hardwareFrame.setVisible(true);
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
		ItemObject item = new ItemObject(item1, "Item 1");
		listModel.addElement(item);
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
	
	public class ListTransferHandler extends TransferHandler {
        private int[] indices = null;
        private int addIndex = -1; //Location where items were added
        private int addCount = 0;  //Number of items added.
                
        /**
         * We only support importing strings.
         */
        public boolean canImport(TransferHandler.TransferSupport info) {
            // Check for String flavor
            //if (!info.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            //    return false;
            //}
            return true;
       }

        /**
         * Bundle up the selected items in a single list for export.
         * Each line is separated by a newline.
         */
        protected Transferable createTransferable(JComponent c) {
            JList list = (JList)c;
            indices = list.getSelectedIndices();
            Object[] values = list.getSelectedValues();
            
            Object buff = values[0];
            return ItemObject.createTransferable(buff);
        }
        
        /**
         * We support both copy and move actions.
         */
        public int getSourceActions(JComponent c) {
            return TransferHandler.COPY_OR_MOVE;
        }
        
        /**
         * Perform the actual import.  This demo only supports drag and drop.
         */
        public boolean importData(TransferHandler.TransferSupport info) {
            if (!info.isDrop()) {
                return false;
            }

            JList list = (JList)info.getComponent();
            DefaultListModel listModel = (DefaultListModel)list.getModel();
            JList.DropLocation dl = (JList.DropLocation)info.getDropLocation();
            Object addedObject = null;
            int index = dl.getIndex();
            boolean insert = dl.isInsert();

            // Get the string that is being dropped.
            Transferable t = info.getTransferable();
            ItemObject data;
            try {
                data = (ItemObject) t.getTransferData(ItemObject.flavor);
            } 
            catch (Exception e) { return false; }
                                    
            // Wherever there is a newline in the incoming data,
            // break it into a separate item in the list.
            /*String[] values = data.split("\n");
            
            addIndex = index;
            addCount = values.length;
            
            // Perform the actual import.  
            for (int i = 0; i < values.length; i++) {
                if (insert) {
                    listModel.add(index++, values[i]);
                } else {
                    // If the items go beyond the end of the current
                    // list, add them in.
                    if (index < listModel.getSize()) {
                        listModel.set(index++, values[i]);
                    } else {
                        listModel.add(index++, values[i]);
                    }
                }
            }
            */
            listModel.addElement(data);
            addedObject = data;
            System.out.println(addedObject.getClass());
            if(addedObject instanceof ItemObject) {
            	Item item = ((ItemObject) addedObject).getItem();
	            if(list.getName().equals("Items in scanning area")) {
	            	scs.getScanningArea().addAnItem(item);
	            	//System.out.println("added to scanning area");
	            }
	            else if(list.getName().equals("Items in bagging area")) {
	            	scs.getBaggingArea().addAnItem(item);
	            }
            }
            return true;
        }

        /**
         * Remove the items moved from the list.
         */
        protected void exportDone(JComponent c, Transferable data, int action) {
            JList source = (JList)c;
            DefaultListModel listModel  = (DefaultListModel)source.getModel();
            Object removedObject = null;

            if (action == TransferHandler.MOVE) {
                for (int i = indices.length - 1; i >= 0; i--) {
                	removedObject = listModel.elementAt(indices[i]);
                    listModel.remove(indices[i]);
                }
            }
            
            System.out.println(removedObject.getClass());
            if(removedObject instanceof ItemObject) {
            	Item item = ((ItemObject) removedObject).getItem();
	            if(source.getName().equals("Items in scanning area")) {
	            	//System.out.println("removed from scanning area");
	            	scs.getScanningArea().removeAnItem(item);
	            }
	            else if(source.getName().equals("Items in bagging area")) {
	            	scs.getBaggingArea().removeAnItem(item);
	            }
            }
            
            indices = null;
            addCount = 0;
            addIndex = -1;
        }
    }
}