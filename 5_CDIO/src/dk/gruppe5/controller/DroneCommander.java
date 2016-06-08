package dk.gruppe5.controller;

import java.awt.Canvas;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import dk.gruppe5.app.App;

public class DroneCommander extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -869265015784363288L;
	
	CommandManager cmd;
	private NavDataListener navl;


	public DroneCommander() {
		
		try {
			
			System.out.println("Connecting to drone...");
			
			//drone = new app.drone();
			navl = new NavDataListener((ARDrone) App.drone);
			
			App.drone.start();
			cmd = App.drone.getCommandManager();
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
	public void testFlight(long interval){
		System.out.println("We have Liftoff");
		cmd.flatTrim();
		cmd.takeOff();
		cmd.waitFor(interval);
		cmd.hover().spinLeft(10).doFor(interval);
		cmd.landing();
		System.out.println("Test Landing complete");
	}
	public void takeOffAndLand(long interval){
		cmd.flatTrim();
		cmd.takeOff();
		cmd.waitFor(interval);
		cmd.landing();
	}
	public void hover(long interval){
		cmd.flatTrim();
		cmd.takeOff();
		cmd.hover().doFor(interval);
		cmd.landing();
	}
	public void killAll(){
		cmd.emergency();
	}
	public CommandManager getCmd() {
		return cmd;
	}
	
	public IARDrone getDrone() {
		return App.drone;
	}


	public NavDataListener getNavl() {
		return navl;
	}

	
}
