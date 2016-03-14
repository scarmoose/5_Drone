package dk.gruppe5.drone.webcamtest;

import java.awt.Window;

import javax.swing.JFrame;

public class PWindow {
	
	public PWindow(int w, int h) {
		JFrame frame = new JFrame();
		frame.setSize(w, h);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Super programmet");
		
		PPanel panel = new PPanel();
		frame.add(panel);

		frame.setVisible(true);
		
		Thread thread = new Thread(panel);
		thread.start();
		
	}

}
