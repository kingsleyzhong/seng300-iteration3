package com.thelocalmarketplace.software.GUI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.thelocalmarketplace.GUI.Simulation;
import com.thelocalmarketplace.GUI.attendant.AttendantGUI;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.SoftwareGUI;

public class SetupSimulationTest {

	private HardwareGUI hardwareGUI;
	private SoftwareGUI softwareGUI;
	private AttendantGUI attendantGUI;
	
	@Before
	public void setup() {
		Simulation simulation = new Simulation();
		hardwareGUI = simulation.getHardwareGUI();
		softwareGUI = simulation.getSoftwareGUI();
		attendantGUI = simulation.getAttendantGUI();
	}
	
	@Test
	public void testInitialization() {
		assertNotNull(hardwareGUI);
		assertNotNull(softwareGUI);
		//assertNotNull(attendantGUI);
	}
}
