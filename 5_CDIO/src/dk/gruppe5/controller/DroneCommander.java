package dk.gruppe5.controller;

import java.awt.Canvas;
import java.util.Random;

import javax.sound.midi.Receiver;

import CoordinateSystem.DronePosition;
import de.yadrone.apps.controlcenter.plugins.altitude.AltitudeChart;
import de.yadrone.apps.controlcenter.plugins.altitude.AltitudeChartPanel;
import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import de.yadrone.base.navdata.Altitude;

import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.NavData;
import de.yadrone.base.navdata.NavDataManager;
import dk.gruppe5.app.App;
import dk.gruppe5.positioning.Movement;
import dk.gruppe5.positioning.Position;

public class DroneCommander extends Canvas {
	private final static int speed = 5;
	private final static int sleep = 500;
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -869265015784363288L;
	
	CommandManager cmd;

	private Movement navl;
	public DroneCommander() {
		
		try {
			
			System.out.println("Connecting to drone...");
			
			
			App.drone.start();
			Thread.sleep(2000);
			cmd = App.drone.getCommandManager();
			Thread.sleep(2000);
			cmd.setVideoCodec(VideoCodec.H264_720P);
	
			Thread.sleep(500);
			cmd.setMaxAltitude(2000);
			Thread.sleep(500);
			cmd.setVideoChannel(VideoChannel.HORI);
			Thread.sleep(500);
			cmd.setVideoBitrate(3500);
			Thread.sleep(500);

		//	navl = new NavDataListener(App.drone); 
			//navl = new Movement();

		//	navl = new NavDataListener(App.drone);

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
		

		Thread thread = new Thread(new Runnable() {
	         public void run() {
	             
	        	 droneTakeOff();
	        	 
	        	 try{
	        		 //long t = System.currentTimeMillis();
	        		 //long end = t+100000;
	        		 while(true) {
	 					if(DronePosition.getXPoint()!=630 && DronePosition.getYPoint()!= -70){
							System.out.println("Yay!");
							long t = System.currentTimeMillis();
			        		long end = t+10000;
			        		while(System.currentTimeMillis()<end){
			        			cmd.spinLeft(30).doFor(100);
			        			Thread.sleep(10);
			        		}
							cmd.landing();
							break;
						}
	        			 cmd.hover().doFor(10);
	        			 System.out.println("Drone Thread: Drone is now Howering.");	
	        			 Thread.sleep(100);
	        			 //cmd.spinLeft(30).doFor(1000);
	        			 //Thread.sleep(1000);
	        			 System.out.println("Drone Flight Control Complete!");
	        		 }
	        	 } catch (InterruptedException e){
	        		 Thread.currentThread().interrupt();
	        		 e.printStackTrace();
	        	 }
	         }
	}); 
	thread.start();
		
	}
		

		



	public void droneTest(){
		droneTakeOff();
		hover();
		killAll();
		
		
	}
	
	
	public void droneHeight(){
		if (App.currentAltitude < 1450){

			cmd.up(speed).doFor(500);
			cmd.hover().doFor(1000);

		}else if(App.currentAltitude > 1550){
				cmd.down(speed).doFor(500);
				cmd.hover().doFor(1000);

		}else if (App.currentAltitude > 1450 && App.currentAltitude <1550 ){
			cmd.hover();
		}
	}

	public void droneTakeOff(){
		System.out.println("We have Liftoff");
		cmd.flatTrim();
		cmd.takeOff();
		System.out.println("takeoff done");
	}

	public void droneFlyingForward() throws InterruptedException{
		cmd.forward(speed);
	}
	public void droneFlyingBackward(){
		cmd.backward(speed);
	}
	public void droneFlyingUp(){
		cmd.up(speed).doFor(speed);
	}
	public void droneFlyingDown(){
		cmd.down(speed);
	}
	public void droneSpinLeft(){
		cmd.spinLeft(speed*2);
	}
	public void droneSpinRight(){
		cmd.spinRight(speed*2);
		}
	public void droneKillAll(){
		cmd.landing();
	}
	public void droneGoLeft(){
		cmd.goLeft(speed);
	}
	public void droneGoRight(){
		cmd.goRight(speed);
	}
	
	public void takeOffAndLand(long interval){
		cmd.takeOff();
		cmd.waitFor(interval);
		cmd.landing();
	}
	
	public void hover(){
		System.out.println("Altitude1: " + App.currentAltitude);
		cmd.hover().doFor(5000);
		System.out.println("Altitude2: " + App.currentAltitude);
	}
	public void killAll(){
		cmd.landing();
	}

	public CommandManager getCmd() {
		return cmd;
	}
	
	public IARDrone getDrone() {
		return App.drone;
	}


//	public NavDataListener getNavl() {
//		return navl;
//	}

	
}
