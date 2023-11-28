package com.thelocalmarketplace.GUI.hardware;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PriceLookUpCode;

public class ItemObject {
	private String name;
	private Item item;
	
	public ItemObject(BarcodedItem item, String name) {
		this.name = name;
		this.item = item;
	}
	
	public ItemObject(PLUCodedItem item, String name) {
		this.name = name;
		this.item = item;
	}
	
	public Barcode getBarcode() {
		if(item instanceof BarcodedItem) {
			return ((BarcodedItem) item).getBarcode();
		}
		else return null;
	}
	
	public PriceLookUpCode getPLUCode() {
		if(item instanceof PLUCodedItem) {
			return ((PLUCodedItem) item).getPLUCode();
		}
		else return null;
	}
	
	public Mass getMass() {
		return item.getMass();
	}
	
	public Item getItem() {
		return item;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static DataFlavor flavor;
	
	public static Transferable createTransferable(final Object obj) {
        final String localObject = DataFlavor.javaJVMLocalObjectMimeType
                + ";class=" + obj.getClass().getCanonicalName();

        //final DataFlavor flavor;

        try {//from  w  w  w  . j  a  va 2s .  c  o m
            flavor = new DataFlavor(localObject);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] { flavor };
            }

            public boolean isDataFlavorSupported(DataFlavor oflavor) {
                if (oflavor.getRepresentationClass().isAssignableFrom(
                        obj.getClass()))
                    return true;
                return false;
            }

            public DataFlavor getDataFlavor() {
                return flavor;
            }

            public Object getTransferData(DataFlavor oflavor) {
                try {
                    if (isDataFlavorSupported(oflavor)) {
                        return obj;
                    } else {
                        return null;
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }

        };
    }
}
