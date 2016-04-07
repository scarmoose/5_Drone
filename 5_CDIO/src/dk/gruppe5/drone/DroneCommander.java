package dk.gruppe5.drone;

import java.awt.Canvas;
import java.awt.Dimension;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import dk.gruppe5.controller.NavDataListener;
import dk.gruppe5.drone.window.VideoListener;
import dk.gruppe5.legacy.KeyInput;

public class DroneCommander extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -869265015784363288L;
	
	private IARDrone drone;
	private CommandManager cmd;
	private VideoListener videol;
	private NavDataListener navl;


	public DroneCommander() {
		
		try {
			
			System.out.println("Connecting to drone...");
			
			drone = new ARDrone();
			videol = new VideoListener((ARDrone) drone);
			navl = new NavDataListener((ARDrone) drone);
			
			drone.start();
			cmd = drone.getCommandManager();
			cmd.setVideoChannel(VideoChannel.HORI);
			System.out.println("Drone connected.");
		
		} catch (Exception e) {
			
			e.printStackTrace();
			System.err.println("Could not connect to drone.");
			
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	//	System.out.println("--> Trying to take off.");
	//	takeOffAndLand(1000);
	//	System.out.println("--> Takeoff and landing complete.");
		
	}
	
	public void takeOffAndLand(long interval) {
		cmd.takeOff();
		cmd.waitFor(interval);
		//cmd.hover().doFor(interval);
		cmd.landing();
	}

	public CommandManager getCmd() {
		return cmd;
	}
	
	public IARDrone getDrone() {
		return drone;
	}

	public VideoListener getVideol() {
		return videol;
	}

	public NavDataListener getNavl() {
		return navl;
	}

	
}
