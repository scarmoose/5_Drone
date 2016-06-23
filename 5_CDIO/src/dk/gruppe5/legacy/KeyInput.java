package dk.gruppe5.legacy;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import dk.gruppe5.controller.DroneCommander;


public class KeyInput extends Thread implements KeyListener {
	
	DroneCommander dc;
	
	public KeyInput(DroneCommander dc) {
		this.dc = dc;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		System.out.println(e.toString() + " was pressed.");
		
		if(key == KeyEvent.VK_SPACE) {
			System.out.println("space pressed");
			dc.takeOffAndLand(3000);
		}
		if(key == KeyEvent.VK_K) {

			dc.getCmd().emergency();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	@Override
	public void run() {
		
	}
	
	

}
