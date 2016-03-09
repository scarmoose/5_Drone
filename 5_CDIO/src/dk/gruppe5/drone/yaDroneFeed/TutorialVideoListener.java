package dk.gruppe5.drone.yaDroneFeed;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;
import dk.gruppe5.drone.openCV.ImageProcessor;

public class TutorialVideoListener extends JPanel {

	private BufferedImage displayImage = null;
	private int blurLevel = 0;
	private ImageProcessor imgProc = new ImageProcessor();
	private Mat frameOne = null;
	private Mat frameTwo = null;

	public TutorialVideoListener(final IARDrone drone) {

		drone.getVideoManager().addImageListener(new ImageListener() {
			
			public void imageUpdated(BufferedImage newImage) {
				
				//image = imgProc.toBufferedImage(imgProc.blur(imgProc.bufferedImageToMat(newImage), blurLevel));
				//image = imgProc.toGrayScale(newImage);
				if(frameOne == null && frameTwo == null){
					frameOne = imgProc.bufferedImageToMat(newImage);
					frameTwo = frameOne;
					
				}
				frameOne = imgProc.bufferedImageToMat(newImage);
			
			
				frameOne = imgProc.opticalFlow(frameOne,frameTwo);
				
				
				
				//displayImage = imgProc.toCanny(newImage);
				//displayImage = newImage;
				
				displayImage = imgProc.toBufferedImage(frameOne);
				//displayImage = imgProc.toBufferedImage(imgProc.blur(imgProc.bufferedImageToMat(newImage), blurLevel));
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaint();
					}
				});
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				drone.getCommandManager().setVideoChannel(VideoChannel.NEXT);
			}
		});

	}

	public synchronized void paint(Graphics g) {
		if (displayImage != null){
			g.drawImage(displayImage, 0, 0, displayImage.getWidth(), displayImage.getHeight(), null);
		}
	}

	public BufferedImage getImage() {
		return displayImage;
	}

	public void setBlurLevel(int blurLevel) {
		this.blurLevel = blurLevel;
		
	}
}