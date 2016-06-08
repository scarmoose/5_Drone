package dk.gruppe5.ai;

import dk.gruppe5.positioning.Vector2;

public class DecisionMaker implements Runnable {
	
	Vector2[] pastMovement;
	Vector2[] possibleNextMove;
	Vector2 currentPos;
	Vector2 nextMove;
	
	Thread thinker;
	
	boolean think = true;

	@Override
	public void run() {
		while(think) {
			
		}
	}
	
	
	
	

}
