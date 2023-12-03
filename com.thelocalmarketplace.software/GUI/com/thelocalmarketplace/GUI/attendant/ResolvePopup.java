package com.thelocalmarketplace.GUI.attendant;

import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class ResolvePopup {
	private String message;
	List<String> issues;
	
	public ResolvePopup() {
		message = "Following issues need to be resolved";
		
		
	}
	
	public static void main(String[] a) {
	    JFrame frame = new JFrame();
	    int result = JOptionPane.showConfirmDialog(frame, "Continue printing?");
	    // JOptionPane.showInternalConfirmDialog(desktop, "Continue printing?");

	    System.out.println(JOptionPane.CANCEL_OPTION == result);
	  }
}
