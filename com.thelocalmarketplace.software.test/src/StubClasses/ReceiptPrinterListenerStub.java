package StubClasses;

import com.thelocalmarketplace.software.receipt.ReceiptListener;

public class ReceiptPrinterListenerStub implements ReceiptListener{
	private boolean outOfPaper = false;
	private boolean outOfInk = false;
	private boolean paperRefilled = false;
	private boolean inkRefilled = false;
	private boolean receiptPrinted = false;
	
	
	@Override
	public void notifiyOutOfPaper() {
		outOfPaper = true;
		
	}

	@Override
	public void notifiyOutOfInk() {
		outOfInk = true;
		
	}

	@Override
	public void notifiyPaperRefilled() {
		paperRefilled = true;
		
	}

	@Override
	public void notifiyInkRefilled() {
		inkRefilled = true;
		
	}

	@Override
	public void notifiyReceiptPrinted() {
		receiptPrinted = true;
		
	}
	
	public boolean getOutOfPaper() {
		return outOfPaper;
	}
	
	public boolean getOutOfInk() {
		return outOfInk;
	}
	
	public boolean getPaperRefilled() {
		return paperRefilled;
	}
	
	public boolean getInkRefilled() {
		return inkRefilled;
	}
	
	public boolean getReceiptPrinted() {
		return receiptPrinted;
	}
	
	


}
