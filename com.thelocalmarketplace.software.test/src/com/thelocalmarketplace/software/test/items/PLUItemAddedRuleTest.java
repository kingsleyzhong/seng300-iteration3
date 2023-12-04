package com.thelocalmarketplace.software.test.items;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.Mass;
import com.jjjwelectronics.scale.ElectronicScaleBronze;
import com.jjjwelectronics.scale.ElectronicScaleSilver;
import com.thelocalmarketplace.hardware.PLUCodedItem;
import com.thelocalmarketplace.hardware.PLUCodedProduct;
import com.thelocalmarketplace.hardware.PriceLookUpCode;
import com.thelocalmarketplace.hardware.external.ProductDatabases;
import com.thelocalmarketplace.software.items.ItemManager;
import com.thelocalmarketplace.software.items.PLUItemAddedRule;

import powerutility.PowerGrid;

public class PLUItemAddedRuleTest{
	private ItemManager item;
	private ItemManager item2;
	private ElectronicScaleBronze bronzeScanningArea;
	private ElectronicScaleSilver silverScanningArea;
	private PLUItemAddedRule rule;
	private PLUItemAddedRule rule2;
    private PLUCodedProduct pluProduct;
    private PriceLookUpCode pluCode;
    private Mass pluProductMass;
    private PLUCodedItem pluItem;
   
	@Before
	public void setUp() {
        pluCode = new PriceLookUpCode("1234");
        pluProduct = new PLUCodedProduct(pluCode, "bread", 500); 
        pluProductMass = new Mass(BigInteger.valueOf(100 * Mass.MICROGRAMS_PER_GRAM));
        pluItem = new PLUCodedItem(pluCode, pluProductMass);

        bronzeScanningArea = new ElectronicScaleBronze();
        silverScanningArea = new ElectronicScaleSilver();
        PowerGrid grid = PowerGrid.instance();
        bronzeScanningArea.plugIn(grid); 
        bronzeScanningArea.turnOn();
        silverScanningArea.plugIn(grid);
        silverScanningArea.turnOn();
        item = new ItemManager();
        item2 = new ItemManager();
        rule = new PLUItemAddedRule(bronzeScanningArea, item);
        rule2 = new PLUItemAddedRule(silverScanningArea, item2);
        ProductDatabases.PLU_PRODUCT_DATABASE.put(pluCode, pluProduct);
	}
	
	@Test
	public void productAdded(){
		item.setAddItems(true);
		item.addItem(pluCode);
		bronzeScanningArea.addAnItem(pluItem);
		
		item2.setAddItems(true);
		item2.addItem(pluCode);
		silverScanningArea.addAnItem(pluItem);
		
		assertEquals(BigInteger.valueOf(100 * Mass.MICROGRAMS_PER_GRAM).intValue(), item.getItems().get(pluProduct).intValue());
		assertEquals(BigInteger.valueOf(100 * Mass.MICROGRAMS_PER_GRAM).intValue(), item2.getItems().get(pluProduct).intValue());
		
		// Reset
		item.removeItem(pluProduct);
		bronzeScanningArea.removeAnItem(pluItem);
		item2.removeItem(pluProduct);
		silverScanningArea.removeAnItem(pluItem);
		
	}
	
	@Test
	public void notInAddPLUState() {
		item.setAddItems(true);
		bronzeScanningArea.addAnItem(pluItem);
		item2.setAddItems(true);
		silverScanningArea.addAnItem(pluItem);
		
		assertEquals(item.getItems().get(pluProduct), null);
		assertEquals(item2.getItems().get(pluProduct), null);
	}
	
}
