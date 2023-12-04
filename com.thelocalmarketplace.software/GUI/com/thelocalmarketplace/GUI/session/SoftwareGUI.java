package com.thelocalmarketplace.GUI.session;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import com.jjjwelectronics.Mass;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.GradientPanel;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.Product;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.SessionListener;
import com.thelocalmarketplace.software.SessionState;
import com.thelocalmarketplace.software.attendant.Requests;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import javax.swing.SwingConstants;
import javax.swing.Timer;

public class SoftwareGUI{
	public JFrame frame;
	public JFrame catalogue;
	public JPanel mainPane;
	public JPanel startPane;
	public JPanel endPane;
	
	Session session;
	
	// Buttons for user to interact with:
	public JButton btnStart;
	public JButton cancel;
	public JButton addBags;
	public JButton pluCode;
	public JButton searchCatalogue;
	public JButton callAttendant;
	public JButton pay;
	
    public PaymentPopup paymentScreen;
	public AddBagsPopup addBagsScreen;


    int quantity = 0;
    public JLabel itemAmount;
    int productCount;
    public JLabel productAmount;
    String weight;
    public JLabel infoWeightNumber;
    String cartPrice;
    public JLabel cartTotalInDollars;
	
	// JFrame size
	private int width;
	private int height;
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	
	// Panel components of JFrame
	JPanel orangePanel;
	JPanel cartInfoPanel;
	public AddedProducts cartItemsPanel;
	JPanel buttonPanel;
	
	// Buttons
	public SoftwareGUI(Session session) {
		session.register(new InnerListener());
		this.session = session;
		frame = session.getStation().getScreen().getFrame();
		catalogue = new SearchCatalogue(session);
		
		// Setting window size
		width = (int) screenSize.getWidth();
		height = (int) screenSize.getHeight();
		
		frame.setSize(width, height);
		frame.setTitle("Software GUI");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		
		displayStart();
	}

	public JPanel start() {
		JPanel main = new GradientPanel(Colors.color2, Colors.color1);
		
		main.setLayout(new GridLayout(0,1,50,50));
		
		JButton hardwareBtn = new PlainButton("Hardware GUI", Colors.color1);
		hardwareBtn.setOpaque(false);
		hardwareBtn.setBorder(BorderFactory.createEmptyBorder());
		hardwareBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HardwareGUI.setVisibility(true);
			}
		});
		
		main.add(hardwareBtn, BorderLayout.NORTH);
		
		
		//this is the start button
		btnStart = new PlainButton("Start", Colors.color1);
		btnStart.setOpaque(false);
		btnStart.setFont(new Font("Dialog", Font.BOLD, 40));
		btnStart.setBorder(BorderFactory.createEmptyBorder());
		btnStart.setMaximumSize(new Dimension(200,100));
		btnStart.setForeground(Color.BLACK);
		btnStart.setBackground(Colors.color1);
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				session.start();
				displayMain();
			}
		});
		
		
		
		JLabel lblWelcome = new JLabel("WELCOME");
		lblWelcome.setBackground(Color.RED);
		lblWelcome.setForeground(Colors.color3);
		lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
		lblWelcome.setVerticalAlignment(SwingConstants.TOP);
		lblWelcome.setFont(new Font("Dialog", Font.BOLD, 92));
		main.add(lblWelcome, JLabel.CENTER_ALIGNMENT);
		main.add(btnStart, JButton.CENTER_ALIGNMENT);	
		
		return main;
	}
	
	public JPanel end() {
		JPanel main = new GradientPanel(Colors.color1, Colors.color2);
		main.setLayout(new GridLayout(1, 0, 0, 0));
		
		JButton hardwareBtn = new PlainButton("Hardware GUI", Colors.color1);
		hardwareBtn.setOpaque(false);
		hardwareBtn.setBorder(BorderFactory.createEmptyBorder());
		hardwareBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HardwareGUI.setVisibility(true);
			}
		});
		main.add(hardwareBtn);
		
		Timer timer = new Timer(20000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				displayStart();
			}
			
		});
		timer.setRepeats(false);
		timer.start();
		
		JButton thankYouButton = new PlainButton("<html>Thanks For Shopping!<br><br>Please Collect Your Receipt</html>", Colors.color1);
		thankYouButton.setOpaque(false);
		thankYouButton.setFont(new Font("Dialog", Font.BOLD, 40));
		thankYouButton.setForeground(Colors.color3);
		thankYouButton.setBorder(BorderFactory.createEmptyBorder());
		thankYouButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				displayStart();
				timer.stop();
			}
			
		});
		
		main.add(thankYouButton);
		
		return main;
	}
	
	public JPanel mainPanel() {
		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		
		// ORANGE PANEL FOR ITEM DETAILS:
		orangePanel = new JPanel();
		orangePanel.setBackground(Colors.color5);
		orangePanel.setLayout(new BorderLayout());
				
		JButton hardwareButton = new PlainButton("Hardware GUI", Colors.color5);
		hardwareButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				HardwareGUI.setVisibility(true);
					
			}
					
		});
				
		JPanel oLeft = new JPanel();
		oLeft.setBackground(Colors.color5);
		oLeft.setLayout(new FlowLayout());
		oLeft.add(hardwareButton);
		oLeft.setPreferredSize(new Dimension(width/3, height/13));
		
		JPanel oRight = new JPanel();
		oRight.setBackground(Colors.color5);
		oRight.setPreferredSize(new Dimension( 2 * (width/3), height/13));
		oRight.setLayout(new FlowLayout(FlowLayout.LEADING, width/10, height/32));
		oRight.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, Colors.color1));
		
		// Text for right side of orange panel
		JLabel desc = new JLabel("Product Description");
		desc.setFont(new Font("Dialog", Font.BOLD, 15));
		JLabel qty = new JLabel("Quantity");
		qty.setFont(new Font("Dialog", Font.BOLD, 15));
		JLabel mass = new JLabel("Mass");
		mass.setFont(new Font("Dialog", Font.BOLD, 15));
		JLabel price = new JLabel("Price");
		price.setFont(new Font("Dialog", Font.BOLD, 15));
		
		oRight.add(desc);
		oRight.add(qty);
		oRight.add(mass);
		oRight.add(price);
		
		orangePanel.add(oLeft, BorderLayout.WEST);
		orangePanel.add(oRight, BorderLayout.EAST);
		
		// CENTER SECTION
		cartInfoPanel = new JPanel();
		cartInfoPanel.setPreferredSize(new Dimension(width/3, height));
		cartInfoPanel.setBackground(Colors.color4);
		
		cartInfoPanel.setLayout(new BorderLayout());
		
		JPanel infoTop = new JPanel();
		infoTop.setBackground(Colors.color4);
		infoTop.setPreferredSize(new Dimension(width, 300));
		infoTop.setLayout(new GridLayout(0,1,20,20));
		infoTop.setBorder(BorderFactory.createEmptyBorder(20,50,20,50));
		JPanel infoTop1 = new JPanel();
		infoTop1.setLayout(new BorderLayout());
		infoTop1.setBackground(Colors.color4);
		JLabel infoQtyString = new JLabel("Number of Items");
		infoQtyString.setFont(new Font("Dialog", Font.BOLD,20));
		itemAmount = new JLabel("0");
		itemAmount.setFont(new Font("Dialog", Font.BOLD,20));
		infoTop1.setLayout(new BorderLayout(0, 0));
		infoTop1.add(infoQtyString, BorderLayout.WEST);
		infoTop1.add(itemAmount, BorderLayout.EAST);
				
		JPanel infoTop2 = new JPanel();
		infoTop2.setBackground(Colors.color4);
		infoTop2.setLayout(new BorderLayout());
		infoTop2.setBackground(Colors.color4);
		JLabel infoItemString = new JLabel("Number of Products");
		infoItemString.setFont(new Font("Dialog", Font.BOLD,20));
		productAmount = new JLabel("0");
		productAmount.setFont(new Font("Dialog", Font.BOLD,20));
		infoTop2.add(infoItemString, BorderLayout.WEST);
		infoTop2.add(productAmount, BorderLayout.EAST);
				
		JPanel infoTop3 = new JPanel();
		infoTop3.setBackground(Colors.color4);
		infoTop3.setLayout(new BorderLayout());
		infoTop3.setBackground(Colors.color4);
		JLabel infoWeightString = new JLabel("Weight");
		infoWeightString.setFont(new Font("Dialog", Font.BOLD,20));
		infoWeightNumber = new JLabel("0kg");
		infoWeightNumber.setFont(new Font("Dialog", Font.BOLD,20));
		infoTop3.add(infoWeightString, BorderLayout.WEST);
		infoTop3.add(infoWeightNumber, BorderLayout.EAST);
		
		infoTop.add(infoTop1);
		infoTop.add(infoTop2);
		infoTop.add(infoTop3);
			 
		JPanel infoBottom = new JPanel();
		infoBottom.setBackground(Colors.color4);
		infoBottom.setLayout(new GridLayout(0,1));
		infoBottom.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Colors.color1));
				
		JPanel infoBottomInner = new JPanel();
		infoBottomInner.setLayout(new BorderLayout());
		infoBottomInner.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		infoBottomInner.setBackground(Colors.color4);
				
		JLabel cartTotalString = new JLabel("Cart Total");
		cartTotalString.setFont(new Font("Dialog", Font.BOLD, 20));
		cartTotalInDollars = new JLabel("$0.00");
		cartTotalInDollars.setFont(new Font("Dialog", Font.BOLD,40));
			
		infoBottomInner.add(cartTotalString, BorderLayout.WEST);
		infoBottomInner.add(cartTotalInDollars, BorderLayout.EAST);
				
		infoBottom.add(infoBottomInner);
			
		ImageIcon image = new ImageIcon(new ImageIcon("images/sheepIcon.png").getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT));
		JLabel label = new JLabel(image);
		label.setVerticalAlignment(JLabel.BOTTOM);
		
		cartInfoPanel.add(infoTop, BorderLayout.NORTH);
		cartInfoPanel.add(infoBottom, BorderLayout.SOUTH);
		cartInfoPanel.add(label, BorderLayout.CENTER);
				
		cartItemsPanel = new AddedProducts(session);
		//cartItemsPanel.setBackground(Colors.color3);
		cartItemsPanel.setPreferredSize(new Dimension( 2 * (width/3), height));
				
		// BUTTON PANEL FOR BOTTOM OF SCREEN:
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Colors.color2);
		buttonPanel.setLayout(new GridLayout(1,0, 20,0));
			
		// Create buttons
		cancel = new PlainButton("Cancel", Colors.color4);
		addBags = new PlainButton("Add Bags", Colors.color4);
		pluCode = new PlainButton("<html>Enter PLU <br/>&nbsp;&nbsp;&nbsp;Code</html>", Colors.color4);
		searchCatalogue = new PlainButton("<html>&nbsp;&nbsp;Search <br/>Catalogue</html>", Colors.color4);
		callAttendant = new PlainButton("<html>&nbsp;&nbsp;&nbsp;Call <br/>Attendant</html>", Colors.color4);
		pay = new PlainButton("PAY", Colors.color4);
		
		//Add action listeners to buttons
		cancel.addActionListener(new ButtonListener());
		
		addBagsScreen = new AddBagsPopup(session);
		addBags.addActionListener(new ButtonListener(){
		      @Override
		       public void actionPerformed(ActionEvent e) {
		    	  addBagsScreen.popUp();		          	            	            
		       }
		});
		pluCode.addActionListener(new ButtonListener());
		searchCatalogue.addActionListener(new ButtonListener());
		callAttendant.addActionListener(new ButtonListener());
				
		paymentScreen = new PaymentPopup(this.session);
		pay.addActionListener(new ButtonListener(){
		      @Override
		       public void actionPerformed(ActionEvent e) {
		            paymentScreen.popUp();
		            
		            //we need to put session into pay mode here//
		            
		          }
		});
				
		//Set button sizes
		cancel.setFont(new Font("Dialog", Font.BOLD, 30));
		addBags.setFont(new Font("Dialog", Font.BOLD, 30));
		pluCode.setFont(new Font("Dialog", Font.BOLD, 30));
		searchCatalogue.setFont(new Font("Dialog", Font.BOLD, 30));
		callAttendant.setFont(new Font("Dialog", Font.BOLD, 30));
		pay.setFont(new Font("Dialog", Font.BOLD, 75));
			
		// Align texts for buttons
		cancel.setHorizontalTextPosition(JButton.CENTER);
		addBags.setHorizontalTextPosition(JButton.CENTER);		
		pluCode.setHorizontalTextPosition(JButton.CENTER);		
		searchCatalogue.setHorizontalTextPosition(JButton.CENTER);		
		callAttendant.setHorizontalTextPosition(JButton.CENTER);		
		pay.setHorizontalTextPosition(JButton.CENTER);		
			
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		buttonPanel.add(cancel);
		buttonPanel.add(addBags);
		buttonPanel.add(pluCode);
		buttonPanel.add(searchCatalogue);
		buttonPanel.add(callAttendant);
		buttonPanel.add(pay);
			
				
		// SET PANEL SIZES FOR JFRAME:
		orangePanel.setPreferredSize(new Dimension(width, height/13));
		buttonPanel.setPreferredSize(new Dimension(width, height/6));
			
		// ADD PANELS TO JFRAME:
		main.add(orangePanel, BorderLayout.NORTH);
		main.add(buttonPanel, BorderLayout.SOUTH);
		main.add(cartInfoPanel, BorderLayout.WEST);
		main.add(cartItemsPanel, BorderLayout.EAST);
		
		return main;
	}
	
	public void displayStart() {
		frame.getContentPane().removeAll();
		startPane = start();
		frame.getContentPane().add(startPane, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		frame.setVisible(true);
	}
	
	public void displayMain() {
		frame.getContentPane().removeAll();
		mainPane = mainPanel();
		frame.getContentPane().add(mainPane, BorderLayout.CENTER);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		frame.setVisible(true);
	}
	
	public void displayEnd() {
		frame.getContentPane().removeAll();
		endPane = end();
		frame.getContentPane().add(endPane);
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		frame.setVisible(true);
	}
	
	public void update(double price, double mass) {
		DecimalFormat df = new DecimalFormat("#.00");
		cartPrice = "$" + df.format(price);
		cartTotalInDollars.setText(cartPrice);
		if(mass>=1000) {
			double temp = mass/1000;
			weight = df.format(temp) + "kg";
		}
		else weight = df.format(mass) + "g";
		infoWeightNumber.setText(weight);
		productCount = cartItemsPanel.amount();
		productAmount.setText(Integer.toString(productCount));
		itemAmount.setText(Integer.toString(quantity));
	}
	
	public void hide() {
    	frame.setVisible(false);
    }
	
	public void unhide() {
		frame.setVisible(true);
	}
	
	public class ButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton source = (JButton) e.getSource();
			if(source == searchCatalogue) {
				if(session.getState() == SessionState.IN_SESSION)
					catalogue.setVisible(true);
				else JOptionPane.showMessageDialog(null, "You cannot add an item right now.");
			}
			else if(source == cancel) {
				if(session.getState() == SessionState.IN_SESSION) {
					int n = JOptionPane.showConfirmDialog(null, "Are you sure you want to leave the session and cancel your order?", 
							"Confirm Cancel", JOptionPane.YES_NO_OPTION);
					if(n == JOptionPane.YES_OPTION) {
						displayStart();
					}
					else {
						return;
					}
				}
				else if(session.getState() == SessionState.BLOCKED) {
					JOptionPane.showMessageDialog(null, "Cannot cancel. Please resolve discrepancy on weight scale.");
				}
				session.cancel();
			}
			else if(source == pluCode) {
				if(session.getState() == SessionState.IN_SESSION) {
					String result = JOptionPane.showInputDialog(null,"Input PLU Code", 
							"Input PLU Code", JOptionPane.PLAIN_MESSAGE);
					if(result != null && !result.equals("")) {
						PriceLookUpCode plu = new PriceLookUpCode(result);
						try {
							session.getManager().addItem(plu);
						} catch (InvalidActionException e1) {
							JOptionPane.showMessageDialog(null, "This is not a valid PLU Code");
						}
					}
				}
				else JOptionPane.showMessageDialog(null, "Cannot add PLU item right now.");
			}
		}	
	}
	
	private class InnerListener implements SessionListener{
		
		@Override
		public void itemAdded(Session session, Product product, Mass ofProduct, Mass currentExpectedWeight,
				BigDecimal currentExpectedPrice) {
			catalogue.setVisible(false);
			cartItemsPanel.addProduct(product, ofProduct);
			quantity = quantity + 1;
			update(currentExpectedPrice.doubleValue(), currentExpectedWeight.inGrams().doubleValue());
		}

		@Override
		public void itemRemoved(Session session, Product product, Mass ofProduct, Mass currentExpectedMass,
				BigDecimal currentExpectedPrice) {
			cartItemsPanel.removeProduct(product, ofProduct);
			quantity = quantity - 1;
			update(currentExpectedPrice.doubleValue(), currentExpectedMass.inGrams().doubleValue());
		}

		@Override
		public void addItemToScaleDiscrepancy(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void removeItemFromScaleDiscrepancy(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void discrepancy(Session session, String message) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void discrepancyResolved(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void pricePaidUpdated(Session session, BigDecimal amountDue) {
			DecimalFormat df = new DecimalFormat("#.00");
			String s = "$" + df.format(amountDue);
			paymentScreen.amountLabel.setText(s);
			
		}

		@Override
		public void getRequest(Session session, Requests request) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionAboutToStart(Session session) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionEnded(Session session) {
			paymentScreen.hide();
			displayEnd();
		}

		@Override
		public void pluCodeEntered(PLUCodedProduct product) {
			catalogue.setVisible(false);
			int n = JOptionPane.showConfirmDialog(null, "Would you like to add: " + product.getDescription(), 
					"Confirm Product", JOptionPane.YES_NO_OPTION);
			if(n == JOptionPane.NO_OPTION) {
				session.cancel();
			}
			else if(n == JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(null, "Please place " + product.getDescription() + " on the scanning scale.");
			}
		}

		@Override
		public void sessionStateChanged() {
			
			if (session.getState() == SessionState.PAY_BY_CASH) {
				paymentScreen.paymentTypeLabel.setText("Payment Selected: Cash");
			}
			
			if (session.getState() == SessionState.PAY_BY_CARD) {
				paymentScreen.paymentTypeLabel.setText("Payment Selected: Card");
			}

			
		}

		
	}
}