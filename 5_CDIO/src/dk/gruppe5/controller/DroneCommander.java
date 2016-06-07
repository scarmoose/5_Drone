package dk.gruppe5.controller;

import java.awt.Canvas;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;

public class DroneCommander extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -869265015784363288L;
	
	private IARDrone drone;
	private CommandManager cmd;
	private NavDataListener navl;


	public DroneCommander() {
		
		try {
			
			System.out.println("Connecting to drone...");
			
			drone = new ARDrone();
			navl = new NavDataListener((ARDrone) drone);
			
			drone.start();
			cmd = drone.getCommandManager();
			cmd.setMaxAltitude(2000);
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


	public NavDataListener getNavl() {
		return navl;
	}

	
}
