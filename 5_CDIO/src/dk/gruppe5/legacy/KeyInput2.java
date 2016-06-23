package dk.gruppe5.legacy;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyInput2 implements KeyListener {

	@Override
	public void keyTyped(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if(key == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

}
