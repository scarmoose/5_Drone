package dk.gruppe5.drone.window;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;

public class VideoListener extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2405332428433795611L;
	
	private BufferedImage image;
	private final IARDrone drone;
	
	public VideoListener(final ARDrone drone) {
		super();
		this.drone = drone;
		init();
		
	}
	
	private void init() {
		//this.setSize(new Dimension(640, 360));
		this.setVisible(true);
		this.drone.getVideoManager().addImageListener(new ImageListener() {
			@Override
			public void imageUpdated(BufferedImage arg0) {
				image = arg0;
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						repaint();
						System.out.println("--> repaint() blev kaldt.");
					}
				});				
			}	
		});
		
	}
	
	@Override
	public void paint(Graphics g) {
		if (image != null) 
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
	}	

}
