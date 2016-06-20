package dk.gruppe5.controller;

import java.awt.Canvas;

import CoordinateSystem.DronePosition;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;
import dk.gruppe5.app.App;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.positioning.Movement;
import dk.gruppe5.positioning.Movement.MyAltitudeListener;

public class DroneCommander extends Canvas {
	private final static int speed = 5;
	private final static int sleep = 500;


	/**
	 * 
	 */

	private static final long serialVersionUID = -869265015784363288L;

	CommandManager cmd;
	Movement navl = new Movement();

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
				getDroneAltitude(Movement.currentAltitude);

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
		//getDroneAltitude(Movement.currentAltitude);
		droneTakeOff();
		cmd.hover().doFor(10000);
		killAll();


	}

	public void getDroneAltitude(int altitude){
		MyAltitudeListener alt = null;

		Movement.currentAltitude = altitude;
		if (Movement.currentAltitude <= 2000){

			System.out.println("Drone Altitude" + altitude);
		} else {
			System.out.println("Drone Altitude Null!:" + altitude);
		}
	}

	public void droneTakeOff(){
		cmd.flatTrim();
		cmd.takeOff();
	}

	public void takeOffAndLand(long interval){
		cmd.takeOff();
		cmd.waitFor(interval);
		cmd.landing();
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


	public void cleanStartUp(){

		cmd.takeOff();
		cmd.hover().doFor(5000);
		
	}

	public void findPosition(){
		
		cleanStartUp();
		
		cmd.forward(10).doFor(500);
		cmd.backward(10).doFor(10);
		cmd.hover().doFor(5000);
		boolean gogo = true;
		
		while(gogo) {
			Values_cam.setMethod(2);
			for(int i = 0; i < 4; i++){

					cmd.hover().doFor(3000);
					cmd.spinRight(100).doFor(100);
					cmd.hover().doFor(3000);
			}

			if(DronePosition.getXPoint() != 0 && DronePosition.getYPoint()!= 0){
				cmd.hover().doFor(500);	
				lookForAirfield();
			}
		}
	}

	public void lookForAirfield(){
		
		cleanStartUp();
				
		cmd.setVideoChannel(VideoChannel.VERT);
		cmd.hover().doFor(500);
		cmd.down(10).doFor(1000);
		cmd.hover().doFor(500);
		cmd.setVideoChannel(VideoChannel.HORI);
		cmd.up(10).doFor(1000);
		cmd.hover().doFor(500);
		cmd.landing();
		
	}

	public void closeToWall(){
		if((DronePosition.getXPoint()>=830 || DronePosition.getXPoint()<=100 || DronePosition.getYPoint()<=100 || DronePosition.getYPoint()>=830) && DronePosition.getYPoint()!=-77){
			long t = System.currentTimeMillis();
			long end = t+3000;
			try {
				if(DronePosition.getDegree()<=0){
					while(System.currentTimeMillis()<end){
						cmd.spinLeft(100).doFor(500);
						Thread.sleep(10);
					}
				}
				else if(DronePosition.getDegree()>0){
					while(System.currentTimeMillis()<end){
						cmd.spinRight(100).doFor(500);
						Thread.sleep(10);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
public void flyBackToStartField(){
		
		cmd.setVideoChannel(VideoChannel.VERT);
		cmd.hover().doFor(500);
		cmd.down(10).doFor(1000);
		cmd.hover().doFor(500);
		cmd.setVideoChannel(VideoChannel.HORI);
		if(DronePosition.getXPoint() == 630 && DronePosition.getYPoint() == -70){
			cmd.landing();	
		} else {
			cmd.up(10).doFor(1000);
			cmd.hover().doFor(5000);
			findPosition();
		}
		
	}
}
