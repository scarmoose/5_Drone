package dk.gruppe5.framework;

import java.awt.image.BufferedImage;

import de.yadrone.base.IARDrone;
import de.yadrone.base.video.ImageListener;

public class FrameGrabber extends Thread {

	private volatile BufferedImage oldOne;
	private volatile BufferedImage current;
	private final IARDrone drone;
	
	public FrameGrabber(final IARDrone drone) {
		this.drone = drone;
		this.drone.getVideoManager().addImageListener(new ImageListener() {

			@Override
			public void imageUpdated(BufferedImage newImage) {
				if(newImage != null){
					synchronized (this) {
						oldOne = current;
						current = newImage;
					}
					
				}
				
			}
			
		});
	}
	
	
	public BufferedImage getOldOne() {
		return oldOne;
	}

	public BufferedImage getCurrent() {
		return current;
	}

	
	
	@Override 
	public void run() {
		
	}

}
