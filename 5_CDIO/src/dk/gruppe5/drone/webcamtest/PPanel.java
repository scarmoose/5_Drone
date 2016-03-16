package dk.gruppe5.drone.webcamtest;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import dk.gruppe5.drone.openCV.ImageProcessor;
import dk.gruppe5.shared.opticalFlowData;

public class PPanel extends JPanel implements Runnable {
	
	BufferedImage image;
	VideoCapture capture;
	ImageProcessor imgproc;
	static int WEBCAM = 0;
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -8195841601716878275L;
	
	public PPanel() {
		capture = new VideoCapture(WEBCAM);
		imgproc = new ImageProcessor();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Thread sleep was interrupted");
		}
		
		if(!capture.isOpened()) 
			System.out.println("Error: Camera connection is not open.");
		else
			System.out.println("Success: Camera connection is open.");
		this.addKeyListener(new KeyInput());
	}
	
	@Override 
	public void paint(Graphics g) {
		//g.drawString("HEJ VERDEN!", 400, 300);
		if(image != null) {
			long t = System.currentTimeMillis();
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			long dt = System.currentTimeMillis() - t;
			System.out.println("Image drawn in "+dt+"ms");
		}
	}

	Mat old_frame;
	
	@Override
	public void run() {
		while (true) {
			Mat frame = new Mat();
			capture.read(frame);
			if(old_frame == null) {
				old_frame = frame;
			}
			System.out.println("Frame from camera obtained");
			long t = System.currentTimeMillis();
			//frame = imgproc.toCanny(frame);
			opticalFlowData flowData =imgproc.opticalFlow(frame, old_frame);
			Mat ofs_frame = flowData.getFrame();
			image = imgproc.toBufferedImage(ofs_frame);
			old_frame = frame;
			long dt = System.currentTimeMillis() - t;
			System.out.println("Mat converted to BufferedImage in(s) " + dt/ 1000.0);
			repaint();
			System.out.println("repaint() kaldt.");
			
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
			
			
		}
	}
}
