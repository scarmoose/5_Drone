package dk.gruppe5.ai;

import CoordinateSystem.DronePosition;
import dk.gruppe5.controller.DroneCommander;
import dk.gruppe5.model.DPoint;

public class DecisionMaker implements Runnable {
	
	DroneCommander dc = new DroneCommander();
	DPoint[] pastMovement;
	DPoint[] possibleNextMove;
	DPoint currentPos;
	DPoint nextMove;
	
	Thread thinker;
	
	boolean think = true;

	@Override
	public void run() {
		while(think) {
			
			dc.cleanStartUp(7000);
			dc.findPosition();
			
			dc.closeToWall();
			
			if(DronePosition.getXPoint()!=630 && DronePosition.getYPoint()!=-70){
				dc.lookForAirfield();
			}
		}
	}
}
