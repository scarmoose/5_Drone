package dk.gruppe5.controller;

import java.awt.Canvas;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;
import de.yadrone.base.command.VideoChannel;
import dk.gruppe5.app.App;

public class DroneCommander extends Canvas {
	private final static int speed = 5;
	private final static int sleep = 500;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -869265015784363288L;
	
	CommandManager cmd;
	private NavDataListener navl;


	public DroneCommander() {
		
		try {
			
			System.out.println("Connecting to drone...");
			
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
			
	}

	public void droneFlightControl(){
		droneTakeOff();
		//Thread.currentThread().sleep(sleep);
		droneHoverAndSpin(2000);
		cmd.landing();
	}
	
	public void droneTakeOff(){
		System.out.println("We have Liftoff");
		cmd.flatTrim();
		//cmd.setLedsAnimation(LEDAnimation.BLINK_GREEN_RED, 3, 1);
		cmd.takeOff();
		System.out.println("takeoff done");
	}
	public void droneHoverAndSpin(long interval){
		cmd.hover().doFor(interval);
		cmd.spinLeft(speed * 2).doFor(interval);
	}
	public void droneFlyingForward(long interval){
		cmd.forward(speed).doFor(interval);
		cmd.hover();
	}
	public void droneFlyingBackward(long interval){
		cmd.backward(speed).doFor(interval);
		cmd.hover();
	}
	
	public void takeOffAndLand(long interval){
		cmd.flatTrim();
		cmd.takeOff();
		cmd.waitFor(interval);
		cmd.landing();
	}
	
	public void killAll(){
		//cmd.emergency();
		cmd.landing();
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
