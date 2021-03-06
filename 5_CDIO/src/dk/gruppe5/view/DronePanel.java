package dk.gruppe5.view;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import CoordinateSystem.DronePosition;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;
import dk.gruppe5.app.App;
import dk.gruppe5.controller.DistanceCalc;
import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.exceptions.Fejl40;
import dk.gruppe5.framework.DetectedWallmarksAndNames;
import dk.gruppe5.framework.FrameGrabber;
import dk.gruppe5.framework.ImageProcessor;
import dk.gruppe5.framework.CombinedImageAnalysis;
import dk.gruppe5.model.Shape;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.model.Contour;
import dk.gruppe5.model.DPoint;
import dk.gruppe5.positioning.Movement;
import dk.gruppe5.positioning.Position;
import dk.gruppe5.test.CircleTest;

public class DronePanel extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5575916801733831478L;
	int picsNr;
	BufferedImage image;
	VideoCapture capture;
	ImageProcessor imgProc;

	List<Point> startPoints;
	List<Point> endPoints;
	Point direction;
	Mat old_frame;
	FrameGrabber frameGrabber;
	CombinedImageAnalysis combi = new CombinedImageAnalysis();

	public DronePanel(final IARDrone drone) {
		frameGrabber = new FrameGrabber(drone);
		imgProc = new ImageProcessor();

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				App.drone.getCommandManager().setVideoChannel(VideoChannel.NEXT);
			}
		});
	}

	public synchronized void paint(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			int x = this.getWidth();
			int y = this.getHeight();
			g.drawImage(image, 0, 0, x, y, null);
		}
	}

	public BufferedImage getImage() {
		return image;
	}

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

	public void run() {
		while (true) {

			if (frameGrabber.getCurrent() != null) {
				BufferedImage currentFrame = frameGrabber.getCurrent();
				Mat frame = imgProc.bufferedImageToMat(currentFrame);

				if (old_frame == null) {
					old_frame = frame;
				}

				if (Values_cam.getMethod() == 0) {
					//normalt billede
					image = imgProc.toBufferedImage(frame);
				} else if (Values_cam.getMethod() == 1) {
					//2 qr code poss finding
					findPosWith2QrCodes(frame);
				} else if (Values_cam.getMethod() == 2) {
					//location from 1 qr and 2 squares on both sides
					Mat backUp = new Mat();
					backUp = frame;
					frame = imgProc.calibrateCamera(frame);
					
					combi.locationEstimationFrom3Points(frame);
					backUp =  combi.locationEstimationFrom3Points(frame);
					image = imgProc.toBufferedImage(backUp);

				} else if (Values_cam.getMethod() == 3) {

					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 1;

					frame = imgProc.toGrayScale(frame);
					frame = imgProc.equalizeHistogramBalance(frame);
					frame = imgProc.blur(frame);
					frame = imgProc.toCanny(frame);

					List<Contour> listofCircles = imgProc.findCircles(frame);
					frame = imgProc.convertMatToColor(frame);

					for (Contour contour : listofCircles) {

						Scalar color = new Scalar(255, 255, 0);
						frame = imgProc.drawLinesBetweenContourPoints(contour, frame, ratio, color);

					}
					Filterstates.setImage1(imgProc.toBufferedImage(frame));
					image = imgProc.toBufferedImage(backUp);

				} else if (Values_cam.getMethod() == 4) {
					frame = combi.locationEstimationFrom3Points(frame);
					image = imgProc.toBufferedImage(frame);
				} else if (Values_cam.getMethod() == 5) {

					frame = combi.findPositionFromQRandTriangles(frame);
					image = imgProc.toBufferedImage(frame);
				} else if (Values_cam.getMethod() == 6) {
					// looks for a QR code, if one is found, update some place.
					// with time of finding it
					// what the QR code is and the distance to it.
					frame = combi.findQrCodeInImage(frame);
					image = imgProc.toBufferedImage(frame);

				}else if (Values_cam.getMethod() ==7) {
					Point qrPoint = findAirFieldInImageWithBottomCamera(frame);
					if (qrPoint != null) {
						Movement movement = new Movement();
						movement.centerPointInFrame(new DPoint(qrPoint), new DPoint(frame.width(), frame.height()));

					}
					image = imgProc.toBufferedImage(frame);
				}else if(Values_cam.getMethod() == 8){
					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 1;

					frame = imgProc.toGrayScale(frame);
					frame = imgProc.equalizeHistogramBalance(frame);
					frame = imgProc.blur(frame);
					frame = imgProc.toCanny(frame);
					
					//find firkanter, tegn dem på billedet.					
					List<Contour> contours = imgProc.findQRsquares(frame);
					
					for(Contour contour: contours){
						Scalar color = new Scalar(200,100,20);
						backUp = imgProc.drawLinesBetweenContourPoints(contour, backUp, ratio, color);
						
					}
					image = imgProc.toBufferedImage(backUp);
					
				
				}else if(Values_cam.getMethod() == 9){
					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 1;

					frame = imgProc.toGrayScale(frame);
					frame = imgProc.equalizeHistogramBalance(frame);
					frame = imgProc.blur(frame);
					frame = imgProc.toCanny(frame);

					// find firkanter, tegn dem på billedet.
					List<Contour> contours = imgProc.findQRsquares(frame);
					// vi finder de potentielle QR kode områder
					List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
					List<Result> results = imgProc.readQRCodes(cutouts);
					int contourNr = 0;
					for (Result result : results) {
						if (result != null) {
							Scalar color = new Scalar(200, 100, 20);
							backUp = imgProc.drawLinesBetweenContourPoints(contours.get(contourNr), backUp, ratio, color);
						}
						contourNr++;
					}
					
					image = imgProc.toBufferedImage(backUp);
				
				} else if(Values_cam.getMethod() == 10){
					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 1;

					frame = imgProc.toGrayScale(frame);
					frame = imgProc.equalizeHistogramBalance(frame);
					frame = imgProc.blur(frame);
					frame = imgProc.toCanny(frame);

					// find firkanter, tegn dem på billedet.
					List<Contour> contours = imgProc.findQRsquares(frame);
					// vi finder de potentielle QR kode områder
					List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
					List<Result> results = imgProc.readQRCodes(cutouts);
					int contourNr = 0;
					for (Result result : results) {
						if (result != null) {
							Scalar color = new Scalar(200, 100, 20);
							backUp = imgProc.drawLinesBetweenContourPoints(contours.get(contourNr), backUp, ratio, color);
						}
						contourNr++;
					}
					
					image = imgProc.toBufferedImage(backUp);
				
				}
				else if(Values_cam.getMethod()==11){
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
					Filterstates.setImage1(imgProc.toBufferedImage(mask));

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
					Filterstates.setImage2(imgProc.toBufferedImage(morphOutput));

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
					Filterstates.setImage3(imgProc.toBufferedImage(hsvImage));
					Filterstates.setImage4(imgProc.toBufferedImage(blurredImage));
					image = imgProc.toBufferedImage(backUp);
				}else if (Values_cam.getMethod() == 13) {

					Mat blurredImage = new Mat();
					Mat hsvImage = new Mat();
					Mat mask = new Mat();
					Mat morphOutput = new Mat();

					// remove some noise
					Imgproc.blur(frame, blurredImage, new Size(7, 7));
 
					// convert the frame to HSV
					Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_BGR2HSV);

					// get thresholding values from the UI
					// remember: H ranges 0-180, S and V range 0-255
					Scalar minValues = new Scalar(110, 50, 50);
					Scalar maxValues = new Scalar(130, 255, 255);

					// threshold HSV image to select tennis balls
					Core.inRange(hsvImage, minValues, maxValues, mask);
					// show the partial output
					Filterstates.setImage1(imgProc.toBufferedImage(mask));

					// morphological operators
					// dilate with large element, erode with small ones
					Mat dilateElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24));
					Mat erodeElement = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(12, 12));

					Imgproc.erode(mask, morphOutput, erodeElement);
					Imgproc.erode(mask, morphOutput, erodeElement);

					Imgproc.dilate(mask, morphOutput, dilateElement);
					Imgproc.dilate(mask, morphOutput, dilateElement);

					// show the partial output
					Filterstates.setImage2(imgProc.toBufferedImage(morphOutput));

					// init
					List<MatOfPoint> contours = new ArrayList<>();
					Mat hierarchy = new Mat();

					// find contours
					Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

					// if any contour exist...
					if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
						// for each contour, display it in blue
						for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
							Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
						}
					}
					image = imgProc.toBufferedImage(blurredImage);
				} else if (Values_cam.getMethod() == 15) {
					Mat dst = new Mat(frame.width(), frame.height(), 1);
					dst = frame.clone();
					frame = imgProc.toGrayScale(frame);
					new CircleTest().findHoughCircles(frame, dst);
					if (!dst.empty()) {
						System.out.println("LOL");
						image = imgProc.toBufferedImage(dst);
					} else
						System.err.println("FEJL I CIRKLEFINDING");
				} else if (Values_cam.getMethod() == 80) {
					frame = imgProc.calibrateCamera(frame);
					image = imgProc.toBufferedImage(frame);

				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaint();
					}
				});
			}

		}
	}

	public void findPosWith2QrCodes(Mat frame) {
		frame = imgProc.calibrateCamera(frame);
		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 2;
		frame = imgProc.downScale(backUp, ratio);

		// kig på whitebalancing og eventuelt at reducere området
		// som vi kigger igennem for firkanter.

		// først gør vi det sort hvidt
		frame = imgProc.toGrayScale(frame);

		frame = imgProc.equalizeHistogramBalance(frame);
		// Vi tester først med blur og ser hvor godt det bliver
		// prøver også uden
		// blur virker bedre
		frame = imgProc.blur(frame);

		// Til canny for at nemmere kunne finde contourer
		frame = imgProc.toCanny(frame);

		// Nu skal vi prøve at finde firkanter
		List<Contour> contours = imgProc.findQRsquares(frame);
		List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
		List<Result> results = imgProc.readQRCodes(cutouts);
		List<String> qrNamesFound = new ArrayList<>();

		HashMap<String, Integer> qrMap = new HashMap<>();
		int i = 0;
		for (Result result : results) {
			if (result != null) {
				Point centerPoint = contours.get(i).getCenter(ratio);
				Scalar color = new Scalar(0, 255, 0);
				backUp = imgProc.drawLinesBetweenContourCornerPoints(contours.get(i), backUp, ratio, color);
				backUp = imgProc.putText(result.getText(), centerPoint, backUp);
				int height = contours.get(i).getBoundingRect(ratio).height;
				String qrName = result.getText();
				qrNamesFound.add(qrName);
				if (!qrMap.containsKey(qrName)) {
					qrMap.put(qrName, height);
				} else {
					int heightExisting = qrMap.get(qrName);
					heightExisting = (heightExisting + height) / 2;
					qrMap.put(qrName, heightExisting);
				}
				System.out.println(height);
				System.out.println("Distance is:" + DistanceCalc.distanceFromCamera(height));
				backUp = imgProc.putText("DISTANCE IS" + height, new Point(centerPoint.x, centerPoint.y + 20), backUp);
			}
			i++;
		}

		String qrNameOne = null;
		String qrNameTwo = null;
		for (String qrName : qrNamesFound) {
			if (qrNameOne == null) {
				qrNameOne = qrName;
			}
			if (!qrNameOne.equals(qrName)) {
				qrNameTwo = qrName;
				break;
			}

		}
		if (qrNameOne != null && qrNameTwo != null) {
			Position pos = new Position();
			DPoint p1 = Mathmagic.getPointFromName(qrNameOne);
			DPoint p2 = Mathmagic.getPointFromName(qrNameTwo);
			int p1PixelHeight = qrMap.get(qrNameOne);
			int p2PixelHeight = qrMap.get(qrNameTwo);
			DPoint position;
			try {
				position = pos.getPosition(p1, p2, (double) p1PixelHeight, (double) p2PixelHeight);
				DronePosition.setPosition(position);

			} catch (Fejl40 e) {
				e.printStackTrace();
			}

		}

		image = imgProc.toBufferedImage(backUp);
	}

	public Point findAirFieldInImageWithBottomCamera(Mat frame) {
		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 1;
		frame = imgProc.toGrayScale(frame);
		frame = imgProc.equalizeHistogramBalance(frame);
		frame = imgProc.blur(frame);
		frame = imgProc.toCanny(frame);
		// Nu skal vi prøve at finde firkanter af en hvis størrelse

		// vi finder de potentielle QR kode omrÃ¥der
		Result result = imgProc.readQRcodeFromWholeImage(imgProc.toBufferedImage(backUp));
		Point qrCenter = null;
		if (result != null) {
			Scalar color = new Scalar(0, 0, 255);
			ResultPoint[] Rpoints = result.getResultPoints();
			List<Point> points = new ArrayList<>();
			int rPointsSpot = 0;
			for (ResultPoint point : Rpoints) {
				points.add(new Point(Rpoints[rPointsSpot].getX(), Rpoints[rPointsSpot].getY()));
				rPointsSpot++;
			}

			backUp = imgProc.drawLinesBetweenPoints(backUp, points, color);
			double qrX = (points.get(0).x + points.get(1).x + points.get(2).x) / 3;
			double qrY = (points.get(0).y + points.get(1).y + points.get(2).y) / 3;
			qrCenter = new Point(qrX, qrY);

		}

		image = imgProc.toBufferedImage(backUp);

		return qrCenter;
	}

}