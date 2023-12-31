package com.thelocalmarketplace.GUI.hardware;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.Numeral;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PriceLookUpCode;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that allows the user to interact with the hardware. Only allows
 * hard-coded items.
 * Can support drag and drop to move items between cart, scanning area, and
 * bagging area.
 * Can support scanning with main scanner and handheld scanner.
 * Can support paying with banknotes + coins
 * Can support swiping/inserting/tapping cards
 * Can support dispensing bags.
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

public class HardwareGUI {
	private AttendantStation supervisor;
	private AbstractSelfCheckoutStation scs;
	public static JFrame hardwareFrame;
	public JPanel content;
	public JPanel screens;
	public JPanel start;
	public JPanel cashInput;
	public CardPanel card;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width;
	private int height;
	public boolean clicked = false;

	private DefaultListModel<ItemObject> itemsInCart = new DefaultListModel<>();
	public JPanel cartPanel;
	public JList<ItemObject> cartList = new JList<ItemObject>(itemsInCart);
	private DefaultListModel<ItemObject> itemsInScanningArea = new DefaultListModel<>();
	public JPanel scanningPanel;
	public JList<ItemObject> scanningList = new JList<ItemObject>(itemsInScanningArea);
	private DefaultListModel<ItemObject> itemsInBaggingArea = new DefaultListModel<>();
	public JPanel baggingPanel;
	public ButtonPanel buttonPanel;
	public JList<ItemObject> baggingList = new JList<ItemObject>(itemsInBaggingArea);

	private ItemObject lastObject = null;
	protected ItemObject lastItem;
	private JList<ItemObject> importList;
	private JList<ItemObject> exportList;

	public HardwareGUI(AbstractSelfCheckoutStation scs, AttendantStation supervisor) {
		this.scs = scs;
		this.supervisor = supervisor;

		populateItems();

		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();

		width = 1500;
		height = 800;

		hardwareFrame = new JFrame();
		hardwareFrame.setTitle("Hardware GUI");
		hardwareFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hardwareFrame.getContentPane().setBackground(Colors.color1);
		hardwareFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		hardwareFrame.getContentPane().setLayout(new BorderLayout(0, 0));
		hardwareFrame.getContentPane().setBackground(Colors.color1);
		hardwareFrame.setUndecorated(true);
		hardwareFrame.setVisible(true);

		buttonPanel = new ButtonPanel(this);
		buttonPanel.setPreferredSize(new Dimension(width, height / 4));

		start = new JPanel();
		start.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));
		start.setBackground(Colors.color1);

		// ImageIcon image1 = new ImageIcon("images/stationIcon.png");

		ImageIcon image1;
		try {
			// Load the image as a resource
			InputStream is = getClass().getResourceAsStream("/com/thelocalmarketplace/GUI/img/stationIcon.png");
			BufferedImage bufferedImage = ImageIO.read(is);
			Image scaledImage = bufferedImage;

			// Create an ImageIcon from the scaled image
			image1 = new ImageIcon(scaledImage);
		} catch (IOException e) {
			e.printStackTrace();
			image1 = null; // or handle the error as you see fit
		}

		GridBagLayout gbl_start = new GridBagLayout();
		gbl_start.columnWidths = new int[] { 209, 222, 0 };
		gbl_start.rowHeights = new int[] { 40, 60, 0 };
		gbl_start.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gbl_start.rowWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		start.setLayout(gbl_start);
		JLabel title = new JLabel("<html>Self Checkout Station<br>Hardware</html>");
		title.setFont(new Font("Dialog", Font.BOLD, 50));
		title.setForeground(Colors.color4);
		title.setBackground(Colors.color1);
		GridBagConstraints gbc_title = new GridBagConstraints();
		gbc_title.anchor = GridBagConstraints.NORTH;
		gbc_title.fill = GridBagConstraints.HORIZONTAL;
		gbc_title.insets = new Insets(0, 0, 5, 0);
		gbc_title.gridx = 1;
		gbc_title.gridy = 0;
		start.add(title, gbc_title);

		ImageIcon image;
		try {
			// Load the image as a resource
			InputStream is = getClass().getResourceAsStream("/com/thelocalmarketplace/GUI/img/sheepIcon.png");
			BufferedImage bufferedImage = ImageIO.read(is);
			Image scaledImage = bufferedImage.getScaledInstance(300, 300, Image.SCALE_DEFAULT);

			// Create an ImageIcon from the scaled image
			image = new ImageIcon(scaledImage);
		} catch (IOException e) {
			e.printStackTrace();
			image = null; // or handle the error as you see fit
		}

		// ImageIcon image = new ImageIcon(new
		// ImageIcon("images/sheepIcon.png").getImage().getScaledInstance(300, 300,
		// Image.SCALE_DEFAULT));

		content = new JPanel();
		content.setBackground(Colors.color1);
		content.setLayout(new GridLayout(1, 0));

		content.add(start);
		JLabel label1 = new JLabel(image1);
		label1.setVerticalAlignment(JLabel.BOTTOM);
		GridBagConstraints gbc_label1 = new GridBagConstraints();
		gbc_label1.anchor = GridBagConstraints.WEST;
		gbc_label1.fill = GridBagConstraints.VERTICAL;
		gbc_label1.insets = new Insets(0, 0, 0, 5);
		gbc_label1.gridx = 0;
		gbc_label1.gridy = 1;
		start.add(label1, gbc_label1);
		JLabel label = new JLabel(image);
		label.setVerticalAlignment(JLabel.BOTTOM);
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.fill = GridBagConstraints.VERTICAL;
		gbc_label.gridx = 1;
		gbc_label.gridy = 1;
		start.add(label, gbc_label);

		cashInput = new CashPanel(scs);
		cashInput.setBackground(Colors.color1);
		card = new CardPanel(scs);
		card.setBackground(Colors.color1);

		screens = new JPanel();
		screens.setLayout(new GridLayout(0, 1));
		screens.setBackground(Colors.color1);
		screens.add(cashInput);
		screens.add(card);

		hardwareFrame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		hardwareFrame.getContentPane().add(content, BorderLayout.CENTER);

	}

	public void populateItems() {
		BarcodedItem bitem1 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.one }), new Mass(300.0));
		ItemObject item1 = new ItemObject(bitem1, "Baaakini");
		itemsInCart.addElement(item1);

		BarcodedItem bitem2 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.two }), new Mass(1000.0));
		ItemObject item2 = new ItemObject(bitem2, "Wooly warm blanket");
		itemsInCart.addElement(item2);

		BarcodedItem bitem3 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.three }), new Mass(125.0));
		ItemObject item3 = new ItemObject(bitem3, "Baaanana bread bites");
		itemsInCart.addElement(item3);

		BarcodedItem bitem3_1 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.three }), new Mass(125.0));
		ItemObject item3_1 = new ItemObject(bitem3_1, "Baaanana bread bites");
		itemsInCart.addElement(item3_1);

		BarcodedItem bitem4 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.four }), new Mass(50.0));
		ItemObject item4 = new ItemObject(bitem4, "Flock of socks");
		itemsInCart.addElement(item4);

		PriceLookUpCode plu1 = new PriceLookUpCode(new String("0000"));
		PLUCodedItem pluitem1 = new PLUCodedItem(plu1, new Mass(200.0));
		ItemObject item5 = new ItemObject(pluitem1, "Baaananas");
		itemsInCart.addElement(item5);

		PriceLookUpCode plu2 = new PriceLookUpCode(new String("0001"));
		PLUCodedItem pluitem2 = new PLUCodedItem(plu2, new Mass(100.0));
		ItemObject item6 = new ItemObject(pluitem2, "Baaakliva");
		itemsInCart.addElement(item6);
	}

	public JPanel introPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Colors.color1);
		GridLayout layout = new GridLayout(1, 0);
		layout.setHgap(20);
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		cartPanel = itemPanel("Items in cart", cartList);
		JPanel panel2 = new JPanel();
		panel2.setBackground(Colors.color1);
		GridLayout layout2 = new GridLayout(0, 1);
		layout2.setVgap(20);
		panel2.setLayout(layout2);
		scanningPanel = itemPanel("Items in scanning area", scanningList);
		baggingPanel = itemPanel("Items in bagging area", baggingList);
		panel.add(cartPanel);
		panel2.add(scanningPanel);
		panel2.add(baggingPanel);
		panel.add(panel2);
		content.add(panel);
		content.add(screens);
		content.remove(start);
		content.repaint();
		content.revalidate();
		return panel;
	}

	public JPanel itemPanel(String title, JList<ItemObject> list) {
		JPanel thisPanel = new JPanel();
		thisPanel.setLayout(new BoxLayout(thisPanel, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();

		JLabel label = new JLabel(title);
		label.setForeground(Color.WHITE);
		panel.add(label);

		panel.setMaximumSize(new Dimension(1000, 25));
		panel.setBackground(Colors.color2);

		thisPanel.add(panel, BorderLayout.NORTH);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 5));
		JPanel items = new JPanel();
		items.setBackground(Colors.color3);
		items.setLayout(new BoxLayout(items, BoxLayout.Y_AXIS));
		thisPanel.add(items, BorderLayout.CENTER);

		BarcodedItem item1 = new BarcodedItem(new Barcode(new Numeral[] { Numeral.valueOf((byte) 1) }), new Mass(20.0));
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				JList<ItemObject> list = (JList<ItemObject>) e.getSource();
				if (list == cartList) {
					int index = list.getSelectedIndex();
					if (index == -1) {
						lastItem = null;
					} else {
						lastItem = list.getModel().getElementAt(index);
					}
				}
			}

		});
		ItemObject prototype = new ItemObject(item1, "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		list.setPrototypeCellValue(prototype);
		list.setFixedCellHeight(20);
		list.setVisibleRowCount(15);
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new ListTransferHandler());
		list.setMinimumSize(new Dimension(20, 20));
		list.setName(title);
		list.setBackground(Colors.color3);
		items.add(new JScrollPane(list));

		return thisPanel;
	}

	public void addData(ItemObject data, JList<ItemObject> list) {
		if (list == scanningList) {
			scs.getScanningArea().addAnItem(data.getItem());
		} else if (list == baggingList) {
			scs.getBaggingArea().addAnItem(data.getItem());
		}
	}

	public void removeData(ItemObject data, JList<ItemObject> list) {
		if (list == scanningList) {
			scs.getScanningArea().removeAnItem(data.getItem());
		} else if (list == baggingList) {
			scs.getBaggingArea().removeAnItem(data.getItem());
		}
	}

	public void mainScan() {
		if (lastItem == null) {
			JOptionPane.showMessageDialog(hardwareFrame, "Please select an item from the cart.");
		} else {
			Item item = lastItem.getItem();
			if (item instanceof BarcodedItem) {
				scs.getMainScanner().scan((BarcodedItem) item);
			} else {
				JOptionPane.showMessageDialog(null, "This item does not have a barcode to scan.");
			}
		}
	}

	public void handScan() {
		if (lastItem == null) {
			JOptionPane.showMessageDialog(null, "Please select an item from the cart.");
		} else {
			Item item = lastItem.getItem();
			if (item instanceof BarcodedItem) {
				scs.getHandheldScanner().scan((BarcodedItem) item);
			} else {
				JOptionPane.showMessageDialog(null, "This item does not have a barcode to scan.");
			}
		}
	}

	public AttendantStation getSupervisor() {
		return supervisor;
	}

	public AbstractSelfCheckoutStation getStation() {
		return scs;
	}

	/**
	 * Class that handles moving ItemObjects between JLists
	 */
	public class ListTransferHandler extends TransferHandler {
		private int index = -1;
		private int addIndex = -1; // Location where items were added
		private int addCount = 0; // Number of items added.

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
			JList<ItemObject> list = (JList<ItemObject>) c;
			index = list.getSelectedIndex();
			Object value = list.getModel().getElementAt(index);
			return ItemObject.createTransferable(value);
		}

		/**
		 * We support move actions.
		 */
		public int getSourceActions(JComponent c) {
			return TransferHandler.MOVE;
		}

		/**
		 * Perform the actual import. This demo only supports drag and drop.
		 */
		public boolean importData(TransferHandler.TransferSupport info) {
			if (!info.isDrop()) {
				return false;
			}

			JList<ItemObject> list = (JList<ItemObject>) info.getComponent();
			DefaultListModel<ItemObject> listModel = (DefaultListModel<ItemObject>) list.getModel();
			JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
			// Get the string that is being dropped.
			Transferable t = info.getTransferable();
			ItemObject data;
			try {
				data = (ItemObject) t.getTransferData(ItemObject.flavor);
			} catch (Exception e) {
				return false;
			}

			if (data == null)
				return false;

			listModel.addElement(data);
			importList = list;
			lastObject = data;

			return true;
		}

		/**
		 * Remove the items moved from the list.
		 */
		protected void exportDone(JComponent c, Transferable data, int action) {
			JList<ItemObject> source = (JList<ItemObject>) c;
			DefaultListModel<ItemObject> listModel = (DefaultListModel<ItemObject>) source.getModel();
			ItemObject removedObject = null;

			if (action == TransferHandler.MOVE) {
				removedObject = listModel.elementAt(index);
				listModel.remove(index);
			}
			exportList = source;

			addData(lastObject, importList);
			removeData(lastObject, exportList);

			index = -1;
			addCount = 0;
			addIndex = -1;
		}
	}

	public DefaultListModel<ItemObject> getItemsInBaggingArea() {
		return itemsInBaggingArea;
	}

	public DefaultListModel<ItemObject> getItemsInCart() {
		return itemsInCart;
	}

	public DefaultListModel<ItemObject> getItemsInScanningArea() {
		return itemsInScanningArea;
	}

	public void setLastItem(ItemObject item) {
		lastItem = item;
	}

	public static void setVisibility(boolean visibility) {
		hardwareFrame.setVisible(visibility);
	}
}