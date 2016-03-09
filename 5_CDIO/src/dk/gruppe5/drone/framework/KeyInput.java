package dk.gruppe5.drone.framework;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import de.yadrone.base.IARDrone;
import dk.gruppe5.drone.DroneCommander;


public class KeyInput extends Thread implements KeyListener {
	
	DroneCommander dc;
	
	public KeyInput(DroneCommander dc) {
		this.dc = dc;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		System.out.println(e.toString() + " was pressed.");
		if(key == KeyEvent.VK_SPACE) {
		
			//dc.takeOffAndLand(3000);
		}
		if(key == KeyEvent.VK_K) {

			//dc.getCmd().emergency();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void run() {
		
	}
	
	

}
