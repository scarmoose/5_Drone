package dk.gruppe5.controller;

import java.awt.Canvas;
import java.util.Random;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import dk.gruppe5.app.App;
import dk.gruppe5.positioning.Movement;

public class DroneCommander extends Canvas {
	private final static int speed = 5;
	private final static int sleep = 500;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -869265015784363288L;
	
	CommandManager cmd;

	//private NavDataListener navl;
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
	
//	public void strayAround() throws InterruptedException
//	{
//		droneTakeOff();
//		cmd.hover().doFor(5000);
//		int direction = new Random().nextInt() % 4;
//		switch(direction)
//		{
//			case 0 : cmd.forward(speed).doFor(500); System.out.println("PaperChaseAutoController: Stray Around: FORWARD"); break;
//			case 1 : cmd.backward(speed).doFor(500); System.out.println("PaperChaseAutoController: Stray Around: BACKWARD");break;
//			case 2 : cmd.goLeft(speed).doFor(500); System.out.println("PaperChaseAutoController: Stray Around: LEFT"); break;
//			case 3 : cmd.goRight(speed).doFor(500); System.out.println("PaperChaseAutoController: Stray Around: RIGHT");break;
//		}
//		
//		Thread.currentThread().sleep(sleep);
//		cmd.landing();
//	}

	public void droneFlightControl(){
			
		
		while (true) {
			try{	
				cmd.takeOff();
				System.out.println("Drone Tråden: Dronen letter nu.");

				Thread.sleep(1000);
				
				if(cmd.takeOff() != null) {
					cmd.hover().doFor(5000);
					System.out.println("Drone Tråden: Dronen svæver nu.");
					Thread.sleep(1000);
				}
				
				cmd.forward(10).doFor(500);
				System.out.println("DroneTråden: Dronen flyver foran");
				Thread.sleep(1000);
				cmd.backward(10).doFor(100);
				Thread.sleep(1000);
				

				droneFlyingForward();
				hover();
				killAll();

			} catch (InterruptedException e){
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
			break;
		}
	}
	/*public void droneHeight(){
	
		if (navl.getAltitude() < 1450){
			cmd.up(speed).doFor(500);
			cmd.hover().doFor(1000);

		}else if(navl.getAltitude() > 1550){
				cmd.down(speed).doFor(500);
				cmd.hover().doFor(1000);

		}else if (navl.getAltitude() > 1450 && navl.getAltitude() <1550 ){
			cmd.hover();
		}
	}
	*/
	public void droneTakeOff(){
		System.out.println("We have Liftoff");
		cmd.flatTrim();
		cmd.takeOff();
		System.out.println("takeoff done");
	}
	public void droneFlyingForward() throws InterruptedException{
		cmd.forward(speed);
		Thread.currentThread().sleep(sleep);
	}
	public void droneFlyingBackward() throws InterruptedException{
		cmd.backward(speed);
		Thread.currentThread().sleep(sleep);
	}
	public void droneFlyingUp() throws InterruptedException{
		cmd.up(speed).doFor(speed);
		Thread.currentThread().sleep(sleep);
	}
	public void droneFlyingDown() throws InterruptedException{
		cmd.down(speed);
		Thread.currentThread().sleep(sleep);
	}
	public void droneSpinLeft() throws InterruptedException{
		cmd.spinLeft(speed*2);
		Thread.currentThread().sleep(sleep);
	}
	public void droneSpinRight() throws InterruptedException{
		cmd.spinRight(speed*2);
		Thread.currentThread().sleep(sleep);
	}
	public void droneKillAll(){
		cmd.landing();
	}
	public void droneGoLeft() throws InterruptedException{
		cmd.goLeft(speed);
		Thread.currentThread().sleep(sleep);
	}
	public void droneGoRight() throws InterruptedException{
		cmd.goRight(speed);
		Thread.currentThread().sleep(sleep);
	}
	
	public void takeOffAndLand(long interval){
		cmd.takeOff();
		cmd.waitFor(interval);
		cmd.landing();
	}
	
	public void hover() throws InterruptedException{
		cmd.hover();
		Thread.currentThread().sleep(sleep);
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
