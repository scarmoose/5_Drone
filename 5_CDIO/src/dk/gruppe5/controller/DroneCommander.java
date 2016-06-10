package dk.gruppe5.controller;

import java.awt.Canvas;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.LEDAnimation;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
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
			cmd.setVideoCodec(VideoCodec.H264_720P);
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
/*	
	public void droneFlightControl(){
		cmd.flatTrim();
		cmd.setLedsAnimation(LEDAnimation.BLINK_GREEN_RED, 3, 2);
		cmd.takeOff();
		cmd.hover().up(1).doFor(1);
		cmd.spinLeft(1).doFor(1);
		if (qr != 0){
			cmd.hover();
			cmd.spinLeft(1).doFor(1);
			if (airfield != 0){
			}
		}
	}
	*/
	public void testFlight(long interval){
		System.out.println("We have Liftoff");
		cmd.flatTrim();
		cmd.setLedsAnimation(LEDAnimation.BLINK_GREEN_RED, 3, 2);
		cmd.takeOff();
		cmd.hover().doFor(interval);
		cmd.spinLeft(40).doFor(interval);
		cmd.spinRight(50).doFor(interval);
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
		cmd.setLedsAnimation(LEDAnimation.RED, 4, 3);
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
