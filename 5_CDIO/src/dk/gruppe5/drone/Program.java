package dk.gruppe5.drone;

import java.awt.Dimension;

import de.yadrone.base.ARDrone;
import dk.gruppe5.drone.framework.KeyInput;
import dk.gruppe5.drone.window.NavDataListener;
import dk.gruppe5.drone.window.ProgramWindow;
import dk.gruppe5.drone.window.VideoListener;

public class Program extends Thread {
	
	private DroneCommander dc;
	
	public Program(ProgramWindow window) {

		dc = new DroneCommander(window);

	}

	public DroneCommander getDc() {
		return dc;
	}
	
}
