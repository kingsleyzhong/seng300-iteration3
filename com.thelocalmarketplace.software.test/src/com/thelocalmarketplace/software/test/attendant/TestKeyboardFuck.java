package com.thelocalmarketplace.software.test.attendant;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jjjwelectronics.DisabledDevice;
import com.jjjwelectronics.keyboard.USKeyboardQWERTY;
import com.thelocalmarketplace.hardware.AbstractSelfCheckoutStation;
import com.thelocalmarketplace.hardware.AttendantStation;
import com.thelocalmarketplace.software.SelfCheckoutStationLogic;
import com.thelocalmarketplace.software.attendant.Attendant;
import com.thelocalmarketplace.software.attendant.TextSearchController;
import com.thelocalmarketplace.software.test.AbstractTest;

public class TestKeyboardFuck extends AbstractTest {

	public TestKeyboardFuck(String testName, Class<? extends AbstractSelfCheckoutStation> scsClass) {
		super(testName, scsClass);
		// TODO Auto-generated constructor stub
	}

	private AttendantStation station;
	private Attendant a;

	public void stringToKeyboard(String input) throws DisabledDevice {
		USKeyboardQWERTY keyboard = a.getStation().keyboard;
		
		// Generate Mapping for Non Alphabetical Shift Modified Keys
		Map<Character, Boolean> shiftModified = new HashMap<>();
		Map<Character, String> labelLookup = new HashMap<>();
		
		for (String label : USKeyboardQWERTY.WINDOWS_QWERTY) {
			if(label.length() == 3 && label.charAt(1) == ' ') { // Desired Format
				Character c1 = label.charAt(0);
				Character c2 = label.charAt(2);
				
				shiftModified.put(c1, false);
				shiftModified.put(c2, true);
				
				labelLookup.put(c1, label);
				labelLookup.put(c2, label);
			}
		}
		
		
		for (int i = 0; i < input.length(); i++) {
			Character c = input.charAt(i);
			boolean shift = false;
			String targetLabel;
			
			if(c == ' ') {
				targetLabel = "Spacebar";
			}
			else if(Character.isLowerCase(c)) {
				targetLabel = String.valueOf(Character.toUpperCase(c));
			}
			else if(Character.isUpperCase(c)) {
				targetLabel = String.valueOf(c);
			}
			else if(labelLookup.containsKey(c)) {
				targetLabel = labelLookup.get(c);
				shift = shiftModified.get(c);
			}
			else {
				continue;
			}
			
			if(shift) {
				keyboard.getKey("Shift (Left)").press();
			}
			keyboard.getKey(targetLabel).press();
			keyboard.getKey(targetLabel).release();
			if(shift) {
				keyboard.getKey("Shift (Left)").release();
			}

		}
		
		
	}

	@Before
	public void setup() {
		basicDefaultSetup();
		station = new AttendantStation();
		a = new Attendant(station);
		SelfCheckoutStationLogic.installAttendantStation(station);
		station.plugIn(powerGrid);
		station.turnOn();
	}

	@Test
	public void populateSeachFieldTest() throws DisabledDevice {
		// Test to see if typing in the search field works
		String expectedSearch = "THISDOES";
		a.getStation().keyboard.getKey("Shift (Right)").press();
		a.getStation().keyboard.getKey("Shift (Right)").release();
		for (int i = 0; i < expectedSearch.length(); i++) {
			String keyStr = String.valueOf(expectedSearch.charAt(i));
//			System.out.println(keyStr);
			a.getStation().keyboard.getKey(keyStr).press();
			a.getStation().keyboard.getKey(keyStr).release();
			// Somehow retrieve the searchField
		}
		assertEquals(expectedSearch, a.getTextSearchController().getSearchField());
	}

	@Test
	public void fuckingHell() throws DisabledDevice {
		TextSearchController tsc = a.getTextSearchController();
		String expectedSearch = "Hi 69!";
		stringToKeyboard(expectedSearch);
		assertEquals("Input", expectedSearch, tsc.getSearchField());
	}
}
