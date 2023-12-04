package com.thelocalmarketplace.software.receipt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jjjwelectronics.EmptyDevice;
import com.jjjwelectronics.IDevice;
import com.jjjwelectronics.IDeviceListener;
import com.jjjwelectronics.OverloadedDevice;
import com.jjjwelectronics.printer.IReceiptPrinter;
import com.jjjwelectronics.printer.ReceiptPrinterListener;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.BarcodedProduct;
import powerutility.NoPowerException;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.Product;

import ca.ucalgary.seng300.simulation.NullPointerSimulationException;
/*
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
 */
public class Receipt {
	
	public ArrayList<ReceiptListener> listeners = new ArrayList<>();
	private boolean isOutOfPaper = false; //Flag for running out of paper, set to false in default
	private boolean isOutOfInk = false; //Flag for running out of ink, set to false in default
	private boolean duplicateNeeded = false; //Flag for if the receipt was not printed out fully and a duplicate is needed.
	private String receipt; //The receipt that should be printed
	private IReceiptPrinter printer; //The printer associated with the session;

	int charsPrinted;
	int linesUsed;

	
	/**
     * Constructor that initializes the funds and registers an inner listener to the self-checkout station.
     * 
     * @param printer The self-checkout station
     */
    public Receipt (IReceiptPrinter printer) {
        if (printer == null) {
            throw new IllegalArgumentException("SelfCheckoutStation should not be null.");
        }
        InnerListener listener = new InnerListener();
        printer.register(listener);
        this.printer = printer;
    }
    
    /**
     * Inner class to listen for valid coin additions and update the paid amount.
     */
    public class InnerListener implements ReceiptPrinterListener {

		@Override
		public void aDeviceHasBeenEnabled(IDevice<? extends IDeviceListener> device) {
		}

		@Override
		public void aDeviceHasBeenDisabled(IDevice<? extends IDeviceListener> device) {
		}

		@Override
		public void aDeviceHasBeenTurnedOn(IDevice<? extends IDeviceListener> device) {
		}

		@Override
		public void aDeviceHasBeenTurnedOff(IDevice<? extends IDeviceListener> device) {
		}

		@Override
		public void thePrinterIsOutOfPaper() {
			isOutOfPaper = true;
			duplicateNeeded = true;
		}

		@Override
		public void thePrinterIsOutOfInk() {
			isOutOfInk = true;
			duplicateNeeded = true;
		}

		@Override
		public void thePrinterHasLowInk() {
		}

		@Override
		public void thePrinterHasLowPaper() {
		}

		@Override
		public void paperHasBeenAddedToThePrinter() {
			isOutOfPaper = false;
			for(ReceiptListener l : listeners) {
				l.notifiyPaperRefilled();
			}
			if (duplicateNeeded) {
				// start printing duplicate copy of the receipt again if one is needed
				print();
			}
		}

		@Override
		public void inkHasBeenAddedToThePrinter() {
			isOutOfInk = false;
			for(ReceiptListener l : listeners) {
				l.notifiyInkRefilled();
			}
			if (duplicateNeeded) {
				// start printing duplicate copy of the receipt again if one is needed
				print();
			}
		}
    }
    
    public void printReceipt(HashMap<Product, BigInteger> items) {
    	receipt = "";
		for (Map.Entry<Product, BigInteger> item : items.entrySet()) {
			if(item.getKey() instanceof BarcodedProduct) {
				BarcodedProduct product = (BarcodedProduct)item.getKey();
				int numberOfProduct = item.getValue().intValue();
				// barcoded item does not store the price for items which need to be weighted
				double overallPrice = product.getPrice()*numberOfProduct;
				receipt = receipt.concat("Item: " + product.getDescription() + " Amount: " + numberOfProduct + " Price: " + overallPrice + "\n");
			} else if (item.getKey() instanceof PLUCodedProduct) {
				PLUCodedProduct product = (PLUCodedProduct)item.getKey();
				BigInteger mass = item.getValue();
				
				final int MICROGRAM_PER_KILOGRAM = 1_000_000_000;
				double weightInKilogram = mass.doubleValue()/MICROGRAM_PER_KILOGRAM;
				long pricePerKilogram = product.getPrice();
				double price = pricePerKilogram * weightInKilogram;
				receipt = receipt.concat("Item: " + product.getDescription() + " Weight: " + weightInKilogram + " /kg" + " Price: " + price + "\n");
			}
			
		}
    	print();
    }
    
    private void print() {
    	try {
    		printer.print('\n'); // Ensures any new receipt being printed starts on a fresh line
			trackPrint('\n');
        	for (int i = 0, n = receipt.length() ; i < n ; i++) {
        		// Notify and break out of the printing loop if out of paper or ink
        		if (isOutOfPaper) {
        			for(ReceiptListener l : listeners) {
        				l.notifiyOutOfPaper();
        			}
        			throw new EmptyDevice("Out of Paper");
        		}
        		if (isOutOfInk) {
        			for(ReceiptListener l : listeners) {
        				l.notifiyOutOfInk();
        			}
        			throw new EmptyDevice("Out of Ink");
        		}
        		// Send the character to the printer to print
    			printer.print(receipt.charAt(i));
				trackPrint(receipt.charAt(i));
        	}
        	// If the condition is passed, then all characters were successfully printed to the receipt
        		for(ReceiptListener l : listeners) {
    				l.notifiyReceiptPrinted(linesUsed, charsPrinted);
    			}

			linesUsed = 0;
			charsPrinted = 0;

        // The empty device exception is thrown within the loop when the printer is out of paper or ink
    	} catch (EmptyDevice e) {
			System.err.println("There is either no ink or no paper in the printer");
		} catch (OverloadedDevice e) {
			System.err.println("The line is too long. Add a newline");
		}
    }

	private void trackPrint(char c) {
		if(c == '\n') {
			linesUsed++;
		}
		else if(c != ' ' && Character.isWhitespace(c))
			return;

		if(!Character.isWhitespace(c)) {
			charsPrinted++;
		}
	}
    
    /**
     * Methods for adding listeners to the PrintReceipt
     */
	public synchronized boolean deregister(ReceiptListener listener) {
		return listeners.remove(listener);
	}

	public synchronized void deregisterAll() {
		listeners.clear();
	}

	public final synchronized void register(ReceiptListener listener) {
		if (listener == null)
			throw new NullPointerSimulationException("listener");
		listeners.add(listener);
	}

}
