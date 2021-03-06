package dk.gruppe5.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.google.zxing.Result;

import CoordinateSystem.DronePosition;
import dk.gruppe5.framework.DetectedWallmarksAndNames;
import dk.gruppe5.framework.ImageProcessor;
import dk.gruppe5.controller.DistanceCalc;
import dk.gruppe5.framework.CombinedImageAnalysis;
import dk.gruppe5.legacy.KeyInput;
import dk.gruppe5.model.Contour;
import dk.gruppe5.model.DPoint;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.model.opticalFlowData;
import dk.gruppe5.positioning.Position;

public class WebcamPanel extends JPanel implements Runnable {

	BufferedImage image;
	BufferedImage imageTest;
	VideoCapture capture;
	ImageProcessor imgproc;
	static int WEBCAM = 0;
	// Method is used to determine what filter is run on the image, 0 is none, 1
	// is opticalflow, 2 is image recognision.

	CombinedImageAnalysis combi = new CombinedImageAnalysis();

	List<Point> startPoints;
	List<Point> endPoints;
	Point direction;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8195841601716878275L;

	public WebcamPanel() {
		capture = new VideoCapture(WEBCAM);
		imgproc = new ImageProcessor();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Thread sleep was interrupted");
		}

		if (!capture.isOpened())
			System.out.println("Error: Camera connection is not open.");
		else
			System.out.println("Success: Camera connection is open.");
	}

	@Override
	public void paint(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			int x = this.getWidth();
			int y = this.getHeight();
			g.drawImage(image, 0, 0, x, y, null);
			int ofsetX = image.getWidth();

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
			int method = Values_cam.getMethod();
			Mat frame = new Mat();
			capture.read(frame);

			if (old_frame == null) {
				old_frame = frame;
			}

			long t = System.currentTimeMillis();
			long dt = System.currentTimeMillis() - t;

			if (Values_cam.getMethod() == 0) {
				image = imgproc.toBufferedImage(frame);
				frame = imgproc.toGrayScale(frame);
				Filterstates.setImage1(imgproc.toBufferedImage(frame));
				frame = imgproc.blur(frame);
				Filterstates.setImage2(imgproc.toBufferedImage(frame));
				frame = imgproc.toCanny(frame);
				Filterstates.setImage3(imgproc.toBufferedImage(frame));
				Filterstates.setImage4(image);
				
			} else if (Values_cam.getMethod() == 1){
				opticalFlowCall(frame);
				
			
			} else if (Values_cam.getMethod() == 2) {
				//location from 1 qr and 2 squares on both sides
				Mat backUp = new Mat();
				backUp = frame;
				combi.locationEstimationFrom3Points(frame);
				image = imgproc.toBufferedImage(backUp);

			}else if (Values_cam.getMethod() == 3) {
				// looks for a QR code, if one is found, update some place.
				// with time of finding it
				// what the QR code is and the distance to it.
				frame = combi.findQrCodeInImage(frame);
				image = imgproc.toBufferedImage(frame);

			}else if(Values_cam.getMethod() == 4){
				Mat backUp = new Mat();
				backUp = frame;
				int ratio = 1;

				frame = imgproc.toGrayScale(frame);
				frame = imgproc.equalizeHistogramBalance(frame);
				frame = imgproc.blur(frame);
				frame = imgproc.toCanny(frame);
				
				//find firkanter, tegn dem på billedet.			
				List<Contour> contours = imgproc.findQRsquares(frame);
				
				for(Contour contour: contours){
					Scalar color = new Scalar(200,100,20);
					backUp = imgproc.drawLinesBetweenContourPoints(contour, backUp, ratio, color);
					
				}
				image = imgproc.toBufferedImage(backUp);
				
			}else if(Values_cam.getMethod() == 5){
				Mat backUp = new Mat();
				backUp = frame;
				int ratio = 1;

				frame = imgproc.toGrayScale(frame);
				frame = imgproc.equalizeHistogramBalance(frame);
				frame = imgproc.blur(frame);
				frame = imgproc.toCanny(frame);

				// find firkanter, tegn dem på billedet.
				List<Contour> contours = imgproc.findQRsquares(frame);

				// vi finder de potentielle QR kode områder
				List<BufferedImage> cutouts = imgproc.warp(backUp, contours, ratio);
				List<Result> results = imgproc.readQRCodes(cutouts);
				int contourNr = 0;
				for (Result result : results) {
					if (result != null) {
						Scalar color = new Scalar(200, 100, 20);
						backUp = imgproc.drawLinesBetweenContourPoints(contours.get(contourNr), backUp, ratio, color);
					}
					contourNr++;
				}
				
				image = imgproc.toBufferedImage(backUp);
			
			}else if(Values_cam.getMethod() == 6){
				
				//Warp på enkelt billede,
				//load ind enkelt billede
				Mat testImage = imgproc.loadImage("pics/testfiles/TestBilledeTilWarp.png");
				Mat backUp = testImage;
				frame = imgproc.toGrayScale(testImage);
				frame = imgproc.equalizeHistogramBalance(frame);
				frame = imgproc.blur(frame);
				frame = imgproc.toCanny(frame);
				List<Contour> contours = imgproc.findQRsquares(frame);
				
				// vi finder de potentielle QR kode områder
				List<BufferedImage> cutouts = imgproc.warp(backUp, contours, 1);
				int imageNrName = 0;
				for(BufferedImage image : cutouts){
					imgproc.saveImage(imgproc.bufferedImageToMat(image), "image"+imageNrName+".png");
					imageNrName++;
				}
				image = imgproc.toBufferedImage(testImage);
				//find firkanter
				//klip dem ud
				//warp, gem warpede billede.				
			}
			else if(Values_cam.getMethod()==23){
				Mat backUp = new Mat();
				backUp = frame;
				
				Mat blurredImage = new Mat();
				Mat hsvImage = new Mat();
				Mat mask = new Mat();
				Mat morphOutput = new Mat();

				// remove some noise
				Imgproc.blur(frame, blurredImage, new Size(7, 7));

				// convert the frame to HSV
				Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

				/*
				 * get thresholding values from the UI
				 * remember: H ranges 0-180, S and V range 0-255
				 */

				//		 for black colors:
				//		 Scalar minValues = new Scalar(0,0,0);
				//		 Scalar maxValues = new Scalar(179, 50, 100);
				//		 
				//		for blue colors:
				//		Scalar minValues = new Scalar(49, 64, 50);
				//		Scalar maxValues = new Scalar(128, 184, 255);

				Scalar minValues = new Scalar(49, 64, 50);
				Scalar maxValues = new Scalar(128, 184 , 255);
				Core.inRange(hsvImage, minValues, maxValues, mask);
				Filterstates.setImage1(imgproc.toBufferedImage(mask));

				/*
				 * morphological operators
				 * dilate with large element, erode with small ones
				 */
				Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
				Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));
				Imgproc.erode(mask, morphOutput, erodeElement);
				Imgproc.erode(mask, morphOutput, erodeElement);
				Imgproc.dilate(mask, morphOutput, dilateElement);
				Imgproc.dilate(mask, morphOutput, dilateElement);
				Filterstates.setImage2(imgproc.toBufferedImage(morphOutput));

				// init
				List<MatOfPoint> contours = new ArrayList<>();
				Mat hierarchy = new Mat();

				// find contours
				Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

				// if any contour exist...
				if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
				{
					// for each contour, display it in blue
					for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
					{
						Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
					}
				}
				Filterstates.setImage3(imgproc.toBufferedImage(hsvImage));
				Filterstates.setImage4(imgproc.toBufferedImage(blurredImage));
				image = imgproc.toBufferedImage(backUp);
			}

			else if(Values_cam.getMethod() == 15) {
				Mat dst = Mat.zeros(frame.size(), 5);
				List<MatOfPoint> contours = imgproc.getContourList(frame);
				List<MatOfPoint2f> approxs = imgproc.getApproxCurves(contours, 0.1);
				List<RotatedRect> rects = imgproc.getMinAreaRects(approxs);
			} 
			else if(Values_cam.getMethod() == 99) {
				System.out.println("Method "+99+" started");
				Mat _frame = frame.clone();
				frame = imgproc.toCanny(frame);
				List<MatOfPoint> contours = imgproc.getContourList(frame);
				List<MatOfPoint2f> approxs = imgproc.getApproxCurves(contours, 0.1);
				List<RotatedRect> rects = imgproc.getMinAreaRects(approxs);
				imgproc.drawRotatedRects(_frame, rects);
				image = imgproc.toBufferedImage(_frame);
				System.out.println("Method "+99+" ended");
				
			}
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repaint();
				}
			});
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
				double angle = Math.atan2(two.y - one.y, two.x - one.x);
				double angle2 = angle * (180 / Math.PI);
				if (angle2 < 0) {
					angletotal = angletotal + angle2 + 360;
				} else {
					angletotal = angletotal + angle2;
				}
			}

		}
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
