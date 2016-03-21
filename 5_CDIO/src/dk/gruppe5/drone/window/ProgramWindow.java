package dk.gruppe5.drone.window;

import java.awt.Dimension;
import javax.swing.JFrame;

import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManager;
import dk.gruppe5.drone.Program;

public class ProgramWindow /* extends JFrame */ {
	/**
	 * 
	 */
	private static final long serialVersionUID = -134624688539693311L;
	public int w;
	public int h;
	public JFrame frame;
	private VideoListener videoL; 
	
	public ProgramWindow(String title, int w, int h) {
		frame = new JFrame(title);
		
		frame.setFocusable(true);
		Program prog = new Program(this);
		videoL = prog.getDc().getVideol();
		frame.addKeyListener(new KeyboardCommandManager((prog.getDc().getDrone())));
		
		
		this.h = h;
		this.w = w;
		
		frame.setMinimumSize(new Dimension(h, w));
		frame.add(videoL);
		frame.setVisible(true);
		
	}

}
