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
	
	
}
