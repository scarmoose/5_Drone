package dk.gruppe5.ai;

import dk.gruppe5.model.DPoint;

public class DecisionMaker implements Runnable {
	
	DPoint[] pastMovement;
	DPoint[] possibleNextMove;
	DPoint currentPos;
	DPoint nextMove;
	
	Thread thinker;
	
	boolean think = true;

	@Override
	public void run() {
		while(think) {
			
		}
	}
	
	public void cleanStartUp(){
		/*
		 *  this method assumes the drone is starting at an Airfield, as 
		 *  if it's the first time it is starting up
		 */
	}
	
	public void findPosition(){
		/*
		 * flies in y direction in x interval.
		 * if no QR-code is found in current direction, turn 45 degrees.
		 * keep going until 360 degrees is achieved.
		 * if there's still no QR-code, repeat the process
		 */
	}
	
	public void lookForAirfield(){
		/*
		 * this method assumes a new start-position (at dronen har fundet sin placering i rummet)
		 * if possible, the drone should descent in x interval, and look for an airfield with bottom camera
		 */
	}
	
	public void closeToWall(){
		/*
		 * method is called every time the drone has flown in x direction,
		 * in order to make sure we're not about to hit a wall
		 * based on the distance to a QR code or a position
		 */
	}
	
	public void updatePosition(){
		/*
		 * assumes the drone has found a new position, which needs to be saved/remebered
		 */
	}
	
	
}
