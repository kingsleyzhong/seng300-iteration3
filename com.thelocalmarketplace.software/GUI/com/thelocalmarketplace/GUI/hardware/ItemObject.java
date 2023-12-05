package com.thelocalmarketplace.GUI.hardware;

import com.jjjwelectronics.Item;
import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scanner.Barcode;
import com.jjjwelectronics.scanner.BarcodedItem;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PriceLookUpCode;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * Represents a transferable object that can be moved between JLists.
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