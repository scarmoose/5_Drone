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
		
		try{	
			droneTakeOff();
			System.out.println("Drone Tråden: Dronen letter nu.");
			Thread.sleep(1000);
			cmd.hover().doFor(7000);
			Thread.sleep(1000);
			
		} catch (InterruptedException e){
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
		
		//cmd.move(1, 5, 0, 0).doFor(2000);
		//cmd.forward(5).doFor(1000);
//		cmd.hover().doFor(5000);
//		cmd.backward(5).doFor(1000);
//		cmd.hover().doFor(2000);
		
//		cmd.hover().doFor(2000);
//		//cmd.up(20).doFor(2600);
//		cmd.hover().doFor(2000);
//		cmd.forward(15).doFor(500);
//		cmd.backward(15).doFor(500);
//		cmd.hover().doFor(5000);
//		cmd.spinLeft(20).doFor(4000);
//		cmd.hover().doFor(3000);
//		cmd.forward(15).doFor(500);
//		cmd.hover().doFor(3000);
//		cmd.move(1.1f, 1.1f, 1.1f, 1.1f);
		
		//cmd.spinLeft(40).doFor(10000);
		//cmd.hover().doFor(3000);
		//cmd.spinRight(40).doFor(10000);
		//Thread.currentThread().sleep(sleep);
		/*try {
			droneCirkelFlying(3000);
			System.out.println("rod");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		cmd.landing();
	}
	public void droneHeight(){
		
		if (navl.getAltitude() < 1450){
			cmd.up(speed).doFor(500);

		}else if(navl.getAltitude() > 1550){
				cmd.down(speed).doFor(500);

		}else if (navl.getAltitude() > 1450 && navl.getAltitude() <1550 ){
			cmd.hover();
		}
	}
	
	public void droneTakeOff(){
		System.out.println("We have Liftoff");
		cmd.flatTrim();
		cmd.takeOff();
		System.out.println("takeoff done");
	}
	public void droneFlyingForward(long interval){
		cmd.forward(speed).doFor(interval);
		cmd.hover();
	}
	public void droneFlyingBackward(long interval){
		cmd.backward(speed).doFor(interval);
		cmd.hover();
	}
	public void droneFlyingUp(long interval){
		cmd.up(speed).doFor(interval);
		cmd.hover();
	}
	public void droneFlyingDown(long interval){
		cmd.down(speed).doFor(interval);
		cmd.hover();
	}
	public void droneSpinLeft(long interval){
		cmd.spinLeft(speed).doFor(interval);
		cmd.hover();
	}
	public void droneSpinRight(long interval){
		cmd.spinRight(speed).doFor(interval);
		cmd.hover();
	}
	public void droneKillAll(){
		//cmd.emergency();
		cmd.landing();
	}
	public void droneGoLeft(long interval){
		cmd.goLeft(speed).doFor(interval);
		cmd.hover();
	}
	public void droneGoRight(long interval){
		cmd.goRight(speed).doFor(interval);
		cmd.hover();
	}
	
	public void takeOffAndLand(long interval){
		cmd.waitFor(interval);
		cmd.landing();
	}
	public void hover(long interval){
		cmd.hover().doFor(interval);
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
