package com.thelocalmarketplace.GUI.session;

import ca.ucalgary.seng300.simulation.InvalidArgumentSimulationException;
import com.thelocalmarketplace.GUI.customComponents.Colors;
import com.thelocalmarketplace.GUI.customComponents.PlainButton;
import com.thelocalmarketplace.software.Session;
import com.thelocalmarketplace.software.exceptions.InvalidActionException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
	
/***
 * 
 * NumPad for entering the membership number
 * 
* Project Iteration 3 Group 1
*
* Derek Atabayev 			: 30177060 
* Enioluwafe Balogun 		: 30174298 
* Subeg Chahal 			: 30196531 
* Jun Heo 					: 30173430 
* Emily Kiddle 			: 30122331 
* Anthony Kostal-Vazquez 	: 30048301 
* Jessica Li 				: 30180801 
* Sua Lim 					: 30177039 
* Savitur Maharaj 			: 30152888 
* Nick McCamis 			: 30192610 
* Ethan McCorquodale 		: 30125353 
* Katelan Ng 				: 30144672 
* Arcleah Pascual 			: 30056034 
* Dvij Raval 				: 30024340 
* Chloe Robitaille 		: 30022887 
* Danissa Sandykbayeva 	: 30200531 
* Emily Stein 				: 30149842 
* Thi My Tuyen Tran 		: 30193980 
* Aoi Ueki 				: 30179305 
* Ethan Woo 				: 30172855 
* Kingsley Zhong 			: 30197260 
*
*/
	
public class MembershipNumPad {

	
	private JFrame frame;
	
	// JFrame size

	String number;
	
	JLabel num;
	
	private Session session;
	private boolean validMembership;

	private PlainButton one;

	private PlainButton two;

	private PlainButton three;

	private PlainButton four;

	private PlainButton five;

	private PlainButton six;

	private PlainButton seven;

	private PlainButton eight;

	private PlainButton nine;

	private PlainButton delete;

	private PlainButton zero;

	private PlainButton done;
			
	public MembershipNumPad(Session session) {
		
		this.session = session;
				
		frame = new JFrame();
		frame.setResizable(true);
		frame.setSize(new Dimension(400, 500));
		frame.setLocationRelativeTo(null);
		
		frame.getContentPane().setBackground(Colors.color2);
		frame.getContentPane().setLayout(null);
		
		JPanel titlePane = new JPanel();
		titlePane.setBounds(80, 7, 227, 37);
		frame.getContentPane().add(titlePane);
		titlePane.setBackground(Colors.color2);
		titlePane.setLayout(new BorderLayout(0, 0));
		
		JLabel optionlabel = new JLabel("Enter Membership Number:");
		optionlabel.setAlignmentY(Component.TOP_ALIGNMENT);
		optionlabel.setHorizontalAlignment(SwingConstants.CENTER);
		optionlabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
		titlePane.add(optionlabel);
		
		JPanel numberPadPane = new JPanel();
		numberPadPane.setBounds(17, 115, 350, 335);
		frame.getContentPane().add(numberPadPane);
		numberPadPane.setBackground(Colors.color2);
		numberPadPane.setLayout(new GridLayout(4,3,15,15));
		
		number = "";
		
//All the numbers 
		one = new PlainButton("1",Colors.color4);
		one.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("1");
				updateDisplay();
			}
		});
		one.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(one);
		
		two = new PlainButton("2",Colors.color4);
		two.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("2");
				updateDisplay();
			}
		});
		two.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(two);
		
		three = new PlainButton("3",Colors.color4);
		three.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("3");
				updateDisplay();
			}
		});
		three.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(three);
		
		four = new PlainButton("4",Colors.color4);
		four.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("4");
				updateDisplay();
			}
		});
		four.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(four);
		
		five = new PlainButton("5",Colors.color4);
		five.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("5");
				updateDisplay();
			}
		});
		five.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(five);
		
		six = new PlainButton("6",Colors.color4);
		six.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("6");
				updateDisplay();
			}
		});
		six.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(six);
		
		seven = new PlainButton("7",Colors.color4);
		seven.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("7");
				updateDisplay();
			}
		});
		seven.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(seven);
		
		eight = new PlainButton("8",Colors.color4);
		eight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("8");
				updateDisplay();
			}
		});
		eight.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(eight);
		
		nine = new PlainButton("9",Colors.color4);
		nine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("9");
				updateDisplay();
			}
		});
		nine.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(nine);
		
//Delete last character		
		delete = new PlainButton("Delete",Colors.color4);
		delete.setFont(new Font("Tahoma", Font.PLAIN, 20));
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					number = number.substring(0, number.length()-1);
				}
				catch(StringIndexOutOfBoundsException e2) {
					updateDisplay();
				}
				updateDisplay();
			}
		});
		numberPadPane.add(delete);
		
		zero = new PlainButton("0",Colors.color4);
		zero.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				number = number.concat("0");
				updateDisplay();
			}
		});
		zero.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numberPadPane.add(zero);
		
//Finished entering number		
		done = new PlainButton("Done",Colors.color4);
		done.setFont(new Font("Tahoma", Font.PLAIN, 20));
		done.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				returnNum();
				hide();		
				num.setText("0");
				number = "";

			}
		});
		numberPadPane.add(done);
		
		JPanel numDisplay = new JPanel();
		numDisplay.setBounds(17, 56, 350, 37);
		frame.getContentPane().add(numDisplay);
		numDisplay.setLayout(new BorderLayout(0, 0));
		
		num = new JLabel("0");
		num.setHorizontalAlignment(SwingConstants.TRAILING);
		num.setFont(new Font("Tahoma", Font.PLAIN, 20));
		numDisplay.add(num, BorderLayout.CENTER);
		
		JLabel space = new JLabel("  ");
		numDisplay.add(space, BorderLayout.EAST);


	}
	
	public void popUp() {
		this.frame.setVisible(true);
	}
	
	public void hide() {
		this.frame.setVisible(false);
	}
	
	public void returnNum() {
		
		if(number != null && !number.equals("")) {
			
			try {
				session.getMembership().typeMembership(number);
				JOptionPane.showMessageDialog(null, "Valid Membership");
				hide();
				validMembership = true;
			}
			catch (InvalidArgumentSimulationException e) {
				JOptionPane.showMessageDialog(null, "Invalid Membership Number.");
				reset();
			}
			catch (InvalidActionException e) {
				JOptionPane.showMessageDialog(null, "Membership Number not found in database.");
				reset();
			}
			
		}
		else {
			JOptionPane.showMessageDialog(null, "Invalid Membership Number.");
			reset();
		}
		
		validMembership = false;
		
	}
		
	public void updateDisplay() {
		if (number == "") {
		num.setText("0");
		}
		else {
		num.setText(number);
		}
		
	}
	
	public void reset() {
		number = "";
		num.setText("0");			
	}
	
	public boolean getValidMembership() {
		return validMembership; 
	}

	public String getNumber() {
		return number;
	}

	public PlainButton getOne() {
		return one;
	}

	public PlainButton getTwo() {
		return two;
	}

	public PlainButton getThree() {
		return three;
	}

	public PlainButton getFour() {
		return four;
	}

	public PlainButton getFive() {
		return five;
	}

	public PlainButton getSix() {
		return six;
	}

	public PlainButton getSeven() {
		return seven;
	}

	public PlainButton getEight() {
		return eight;
	}

	public PlainButton getNine() {
		return nine;
	}

	public PlainButton getDelete() {
		return delete;
	}

	public PlainButton getZero() {
		return zero;
	}

	public PlainButton getDone() {
		return done;
	}

	public boolean isVisible() {
		return frame.isVisible();
	}
	
	
}
