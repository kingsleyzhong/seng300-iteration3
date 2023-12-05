package com.thelocalmarketplace.software.GUI;

import com.thelocalmarketplace.GUI.Simulation;
import com.thelocalmarketplace.GUI.attendant.AttendantGUI;
import com.thelocalmarketplace.GUI.hardware.HardwareGUI;
import com.thelocalmarketplace.GUI.session.SoftwareGUI;
import com.thelocalmarketplace.software.exceptions.NotDisabledSessionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class SetupSimulationTest {

	private HardwareGUI hardwareGUI;
	private SoftwareGUI softwareGUI;
	private AttendantGUI attendantGUI;

	@Before
	public void setup() throws NotDisabledSessionException {
		Simulation simulation = new Simulation();
		hardwareGUI = simulation.getHardwareGUI();
		softwareGUI = simulation.getSoftwareGUI();
		attendantGUI = simulation.getAttendantGUI();
	}

	@Test
	public void initialization() {
		assertNotNull(hardwareGUI);
		assertNotNull(softwareGUI);
		assertNotNull(attendantGUI);
	}
}
