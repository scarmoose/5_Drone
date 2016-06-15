package dk.gruppe5.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.google.zxing.Result;

import CoordinateSystem.DronePosition;
import dk.gruppe5.framework.DetectedWallmarksAndNames;
import dk.gruppe5.framework.ImageProcessor;
import dk.gruppe5.legacy.KeyInput;
import dk.gruppe5.model.Contour;
import dk.gruppe5.model.Shape;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.model.opticalFlowData;
import dk.gruppe5.positioning.Position;

public class PPanel extends JPanel implements Runnable {

	BufferedImage image;
	BufferedImage imageTest;
	VideoCapture capture;
	ImageProcessor imgproc;
	static int WEBCAM = 0;
	// Method is used to determine what filter is run on the image, 0 is none, 1
	// is opticalflow, 2 is image recognision.
	// public int method = 2;

	public int method = Values_cam.getMethod();

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

		if (!capture.isOpened())
			System.out.println("Error: Camera connection is not open.");
		else
			System.out.println("Success: Camera connection is open.");
		// dosent work atm
		// this.addKeyListener(new KeyInput());
	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		// g.drawString("HEJ VERDEN!", 400, 300);
		if (image != null) {

			int x = this.getWidth();
			int y = this.getHeight();

			g.drawImage(image, 0, 0, x, y, null);

			// System.out.println("Image drawn in "+dt+"ms");
			int ofsetX = image.getWidth();
			// System.out.println(direction.x+":"+direction.y);
			if (Values_cam.getMethod() == 10) {
				if (imageTest != null) {
					g.drawImage(imageTest, ofsetX, 0, imageTest.getWidth(), imageTest.getHeight(), null);

				}
			}

		}
	}

	Mat old_frame;

	@Override
	public void run() {
		while (true) {
			Mat frame = new Mat();
			capture.read(frame);

			//image = imgproc.toBufferedImage(frame);

			if (old_frame == null) {
				old_frame = frame;
			}

			long t = System.currentTimeMillis();
			long dt = System.currentTimeMillis() - t;

			repaint();

			if (Values_cam.getMethod() == 1) {
				opticalFlowCall(frame);
			} else if (Values_cam.getMethod() == 0) {

				image = imgproc.toBufferedImage(frame);
				frame = imgproc.toGrayScale(frame);
				Filterstates.setImage1(imgproc.toBufferedImage(frame));
				frame = imgproc.blur(frame);
				Filterstates.setImage2(imgproc.toBufferedImage(frame));
				frame = imgproc.toCanny(frame);
				Filterstates.setImage3(imgproc.toBufferedImage(frame));
				Filterstates.setImage4(image);

			} else if (Values_cam.getMethod() == 12) {
				Mat backUp = new Mat();
				backUp = frame;
				int ratio = 1;

				frame = imgproc.toGrayScale(frame);
				frame = imgproc.equalizeHistogramBalance(frame);
				frame = imgproc.blur(frame);
				frame = imgproc.toCanny(frame);
				
				List<Contour> listofCircles = imgproc.findCircles(frame);
				frame = imgproc.convertMatToColor(frame);

				for (Contour contour : listofCircles) {

					Scalar color = new Scalar(255, 255, 0);
					frame = imgproc.drawLinesBetweenContourPoints(contour, frame, ratio, color);
				
				}
				Filterstates.setImage1(imgproc.toBufferedImage(frame));
				image = imgproc.toBufferedImage(backUp);
			}else if(Values_cam.getMethod() == 10){
				Mat backUp = new Mat();
				backUp = frame;
				int ratio = 1;

				frame = imgproc.toGrayScale(frame);
				frame = imgproc.equalizeHistogramBalance(frame);
				frame = imgproc.blur(frame);
				frame = imgproc.toCanny(frame);
				// Nu skal vi prøve at finde firkanter af en hvis størrelse
				List<Contour> contours = imgproc.findQRsquares(frame);
		
				// vi finder de potentielle QR kode områder
				List<BufferedImage> cutouts = imgproc.warp(backUp, contours, ratio);
				List<Result> results = imgproc.readQRCodes(cutouts);
				
				int i = 0;
				for (Result result : results) {
					if (result != null) {
						// backUp =
						// imgProc.drawLinesBetweenBoundingRectPoints(contours.get(i),
						// backUp, ratio);
						Scalar color = new Scalar(255, 255, 0);
						backUp = imgproc.drawLinesBetweenContourCornerPoints(contours.get(i), backUp, ratio, color);
						backUp = imgproc.putText(result.getText(), contours.get(i).getCenter(ratio), backUp);
					}
					i++;
				}
				
				
				Filterstates.setImage1(imgproc.toBufferedImage(frame));
				image = imgproc.toBufferedImage(backUp);
			}
			repaint();
		}

	}

	public void opticalFlowCall(Mat frame) {
		opticalFlowData flowData = imgproc.opticalFlow(frame, old_frame);
		Mat ofs_frame = flowData.getFrame();
		image = imgproc.toBufferedImage(ofs_frame);
		old_frame = frame;

		startPoints = flowData.getStartPoints();
		endPoints = flowData.getEndPoints();
		double angletotal = 0;

		for (int i = 0; i < startPoints.size(); i++) {
			Point one = startPoints.get(i);
			Point two = endPoints.get(i);

			Double distance = Math.sqrt(Math.pow(one.x - two.x, 2) + Math.pow(one.y - two.y, 2));

			if (distance > 5) {
				// System.out.println("Distance: "+distance);
				double angle = Math.atan2(two.y - one.y, two.x - one.x);
				double angle2 = angle * (180 / Math.PI);
				// System.out.println(angle2);
				if (angle2 < 0) {
					// System.out.println(angle2);
					angletotal = angletotal + angle2 + 360;
				} else {
					// System.out.println(angle2);
					angletotal = angletotal + angle2;
				}
			}

		}
		// System.out.println("totalAngle:"+angletotal);
		// System.out.println("AverageAngle:"+angletotal/startPoints.size());
		double avAngle = angletotal / startPoints.size();

		directionGuess(avAngle);

	}

	/**
	 * Det er noget pjat den her metode, ikke pr�cis nok og giver for mange fejl
	 * retninger...
	 * 
	 * @param avAngle
	 */
	public void directionGuess(double avAngle) {
		if (startPoints.size() > 30) {
			// System.out.println("Nr of vectors: " +startPoints.size());

			if (avAngle > 315 && avAngle < 360) {
				System.out.println("Left - U");

			} else if (avAngle > 0 && avAngle < 45) {
				System.out.println("Left - D");

			} else if (avAngle > 45 && avAngle < 90) {
				System.out.println("Down - L");

			} else if (avAngle > 90 && avAngle < 135) {
				System.out.println("Down - R");

			} else if (avAngle > 135 && avAngle < 180) {
				System.out.println("Right - D");

			} else if (avAngle > 180 && avAngle < 225) {
				System.out.println("Right - U");

			} else if (avAngle > 225 && avAngle < 270) {
				System.out.println("Up - R");

			} else if (avAngle > 270 && avAngle < 315) {
				System.out.println("Up - L");

			}

		}
	}

	public void clear() {
		image.flush();
		image = null;
		repaint();
	}

}
