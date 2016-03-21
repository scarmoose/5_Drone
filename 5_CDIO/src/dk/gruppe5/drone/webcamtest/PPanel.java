package dk.gruppe5.drone.webcamtest;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import dk.gruppe5.drone.openCV.ImageProcessor;
import dk.gruppe5.shared.opticalFlowData;

public class PPanel extends JPanel implements Runnable {
	
	BufferedImage image;
	VideoCapture capture;
	ImageProcessor imgproc;
	static int WEBCAM = 0;
	
	List<Point> startPoints;
	List<Point> endPoints;
	Point direction;

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
		super.paintComponent(g);
		//g.drawString("HEJ VERDEN!", 400, 300);
		if(image != null) {
			
			long t = System.currentTimeMillis();
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
			long dt = System.currentTimeMillis() - t;
			//System.out.println("Image drawn in "+dt+"ms");
			int ofsetX = image.getWidth();
			//System.out.println(direction.x+":"+direction.y);
		
			for(int i = 0; i < startPoints.size(); i++){
				g.drawLine((int)(ofsetX+startPoints.get(i).x), (int)startPoints.get(i).y, (int)(ofsetX+ endPoints.get(i).x), (int)endPoints.get(i).y);
			}
			
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
			//System.out.println("Frame from camera obtained");
			long t = System.currentTimeMillis();
			//frame = imgproc.toCanny(frame);
			opticalFlowData flowData = imgproc.opticalFlow(frame, old_frame);
			Mat ofs_frame = flowData.getFrame();
			image = imgproc.toBufferedImage(ofs_frame);
			old_frame = frame;
			long dt = System.currentTimeMillis() - t;
			//System.out.println("Mat converted to BufferedImage in(s) " + dt/ 1000.0);
			
			startPoints = flowData.getStartPoints();
			endPoints = flowData.getEndPoints();
			double angletotal = 0;

			for(int i = 0; i < startPoints.size(); i ++){
				Point one = startPoints.get(i);
				Point two = endPoints.get(i);
			
				Double distance = Math.sqrt(Math.pow(one.x-two.x, 2)+Math.pow(one.y-two.y, 2));
				
				if(distance > 5){
					//System.out.println("Distance: "+distance);
				double angle =  Math.atan2(two.y - one.y, two.x - one.x);
				double angle2 = angle*(180/Math.PI);
				//System.out.println(angle2);
				if(angle2 < 0){
					//System.out.println(angle2);
					angletotal = angletotal+angle2+360;
				}else{
					//System.out.println(angle2);
					angletotal = angletotal+angle2;
				}
				}
				
			}
			//System.out.println("totalAngle:"+angletotal);
			//System.out.println("AverageAngle:"+angletotal/startPoints.size());
			double avAngle = angletotal/startPoints.size();
			
			if(startPoints.size()> 30){
				//System.out.println("Nr of vectors: " +startPoints.size());
		
		if(avAngle > 315 && avAngle < 360){
			System.out.println("Left - U");
			
		}else if(avAngle > 0 && avAngle < 45){
			System.out.println("Left - D");
			
		}else if(avAngle > 45 && avAngle < 90){
			System.out.println("Down - L");
			
		}else if(avAngle > 90 && avAngle < 135){
			System.out.println("Down - R");
			
		}else if(avAngle > 135 && avAngle < 180 ){
			System.out.println("Right - D");
			
		}else if(avAngle > 180 && avAngle <225){
			System.out.println("Right - U");
			
		}else if(avAngle > 225 && avAngle < 270){
			System.out.println("Up - R");
			
		}else if(avAngle > 270 && avAngle < 315){
			System.out.println("Up - L");
			
		}
		
		
			
			}
			
			repaint();
			
			//System.out.println("repaint() kaldt.");
			
//			try {
//				Thread.sleep(200);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			

			
			
		}
	}

	public void clear() {
	    image.flush();
	    image = null;
	    repaint();
	}
	

}
