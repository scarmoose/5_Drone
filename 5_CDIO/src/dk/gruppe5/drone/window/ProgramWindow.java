package dk.gruppe5.drone.window;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import dk.gruppe5.drone.Program;

public class ProgramWindow /* extends JFrame */ {
	/**
	 * 
	 */
	private static final long serialVersionUID = -134624688539693311L;
	public int w;
	public int h;
	
	private VideoListener videoL; 
	
	public ProgramWindow(String title, int w, int h) {
		JFrame frame = new JFrame(title);
		Program prog = new Program(this);
		videoL = prog.getDc().getVideol();
		this.h = h;
		this.w = w;
		
		frame.setMinimumSize(new Dimension(h, w));
		frame.add(videoL);
		frame.setVisible(true);
		
	}

}
