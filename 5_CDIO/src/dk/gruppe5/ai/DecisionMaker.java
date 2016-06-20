package dk.gruppe5.ai;

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
			dc.findPosition(3000);
			
		}
	}
}
