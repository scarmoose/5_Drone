package dk.gruppe5.view;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import CoordinateSystem.DronePosition;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;
import dk.gruppe5.app.App;
import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.framework.DetectedWallmarksAndNames;
import dk.gruppe5.framework.FrameGrabber;
import dk.gruppe5.framework.ImageProcessor;
import dk.gruppe5.model.Shape;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.model.Contour;
import dk.gruppe5.model.DPoint;
import dk.gruppe5.positioning.Position;

public class VideoListenerPanel extends JPanel implements Runnable {

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

	public VideoListenerPanel(final IARDrone drone) {
		frameGrabber = new FrameGrabber(drone);
		imgProc = new ImageProcessor();

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				// imgProc.saveImage(imgProc.bufferedImageToMat(image), "IMAGE"
				// + picsNr + ".jpg");
				//
				// picsNr++;
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
			// g.drawImage(image, 0, 0,image.getWidth(), image.getHeight(),
			// null);
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	public void directionGuess(double avAngle) {
		if (startPoints.size() > 30) {
			// System.out.println("Nr of vectors: "
			// +startPoints.size());

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
					image = imgProc.toBufferedImage(frame);
				}else if(Values_cam.getMethod() == 11){
					findAirFieldInImageWithBottomCamera(frame);
				}
				else if (Values_cam.getMethod() == 10) {

					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 1;
					frame = imgProc.downScale(backUp, ratio);

					// kig på whitebalancing og eventuelt at reducere området
					// som vi kigger igennem for firkanter.
					// frame = imgProc.equalizeHistogramBalance(frame);

					// først gør vi det sort hvidt
					frame = imgProc.toGrayScale(frame);

					//
					frame = imgProc.equalizeHistogramBalance(frame);
					// Vi tester først med blur og ser hvor godt det bliver
					// prøv også uden
					// blur virker bedre
					frame = imgProc.blur(frame);

					// Til canny for at nemmere kunne finde contourer
					frame = imgProc.toCanny(frame);

					// Nu skal vi prøve at finde firkanter
					List<Contour> contours = imgProc.findQRsquares(frame);
					List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
					List<Result> results = imgProc.readQRCodes(cutouts);
					int i = 0;
					for (Result result : results) {
						if (result != null) {
							Scalar color = new Scalar(0, 255, 0);
							backUp = imgProc.drawLinesBetweenContourCornerPoints(contours.get(i), backUp, ratio, color);
						}
						i++;
					}

					image = imgProc.toBufferedImage(backUp);
				
				} else if (Values_cam.getMethod() == 6) {
					
					locationEstimationFrom3Points(frame);

				}
				else if (Values_cam.getMethod() == 12) {
					
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
				}

				//System.out.println(image.getWidth() +","+ image.getHeight());


				 
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaint();
					}
				});
			}

		}
	}

	public void findAirFieldInImageWithBottomCamera(Mat frame) {
		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 1;
		//frame = imgProc.calibrateCamera(frame);
		frame = imgProc.toGrayScale(frame);
		frame = imgProc.equalizeHistogramBalance(frame);
		frame = imgProc.blur(frame);
		frame = imgProc.toCanny(frame);
		// Nu skal vi prøve at finde firkanter af en hvis størrelse

		// vi finder de potentielle QR kode områder
		//List<BufferedImage> cutouts = imgProc.getImagesFromContours(backUp,contours,ratio);
		Result result = imgProc.readQRcodeFromWholeImage(imgProc.toBufferedImage(backUp));
		if(result != null){
			Scalar color = new Scalar(0, 0, 255);
			ResultPoint[] Rpoints = result.getResultPoints();
			List<Point> points = new ArrayList<>();
			int rPointsSpot = 0;
			for(ResultPoint point : Rpoints){
				points.add(new Point(Rpoints[rPointsSpot].getX(),Rpoints[rPointsSpot].getY()));
				rPointsSpot++;
			}
			backUp = imgProc.drawLinesBetweenPoints(backUp, points, color);
			
			
		}

		image = imgProc.toBufferedImage(backUp);
	}

	public void locationEstimationFrom3Points(Mat frame) {
		frame = imgProc.calibrateCamera(frame);
		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 2;
		frame = imgProc.downScale(backUp, ratio);
		// først gør vi det sort hvidt
		frame = imgProc.toGrayScale(frame);
		//
		frame = imgProc.equalizeHistogramBalance(frame);
		// blur virker bedre
		frame = imgProc.blur(frame);

		// Til canny for at nemmere kunne finde contourer
		frame = imgProc.toCanny(frame);

		// Nu skal vi prøve at finde firkanter af en hvis størrelse
		List<Contour> contours = imgProc.findQRsquares(frame);

		// vi finder de potentielle QR kode områder
		List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
		List<Result> results = imgProc.readQRCodes(cutouts);
		// backUp = imgProc.markQrCodes(results, shapes, backUp);
		int contourNr = 0;
		for (Result result : results) {
			if (result != null) {
				DetectedWallmarksAndNames data = imgProc.markQrCodesV2(contours.get(contourNr), contours,
						backUp, result.getText(), ratio);
				if (data != null) {
					if (!Double.isNaN(data.getPoints()[0].x) && !Double.isNaN(data.getPoints()[1].x)
							&& !Double.isNaN(data.getPoints()[2].x)) {
						if (data.getQrNames()[0] != null && data.getQrNames()[1] != null
								&& data.getQrNames()[2] != null) {
							Scalar color1 = new Scalar(0, 0, 255);
							backUp = imgProc.drawLine(data.getPoints()[0], data.getPoints()[1], backUp, color1);
							backUp = imgProc.drawLine(data.getPoints()[1], data.getPoints()[2], backUp, color1);
							Position test = new Position();
							Point mapPosition = test.getPositionFromPoints(data.getQrNames(), data.getPoints()[0], data.getPoints()[1], data.getPoints()[2]);
							if (mapPosition != null) {
								DronePosition.setPosition(mapPosition);
								// System.out.println(mapPosition);
								int screenWidth = image.getWidth();
								int middleOfScreen = screenWidth/2;
								int pixelsFromMiddleToQr =  Math.abs(((int)data.getPoints()[1].x-middleOfScreen)); 
								DPoint mapPos = new DPoint(mapPosition);
								System.out.println(test.getDirectionAngleRelativeToYAxis(mapPos, data.getQrNames()[1], pixelsFromMiddleToQr));
								
							}
				
						}

					} else if (!Double.isNaN(data.getPoints()[1].x)) {

						Scalar color1 = new Scalar(255, 0, 0);
						Imgproc.putText(backUp, data.getQrNames()[1], data.getPoints()[1], 5, 2, color1);
						Point ofset = new Point(data.getPoints()[1].x, data.getPoints()[1].y + 30);
						Imgproc.putText(backUp, data.getDistance() + "", ofset, 5, 2, color1);

						if (!Double.isNaN(data.getPoints()[0].x)) {
							Point ofset1 = new Point(data.getPoints()[0].x, data.getPoints()[0].y);
							Imgproc.putText(backUp, "firkant", ofset1, 5, 2, color1);

						}
						if (!Double.isNaN(data.getPoints()[2].x)) {
							Point ofset2 = new Point(data.getPoints()[2].x, data.getPoints()[2].y);
							Imgproc.putText(backUp, "firkant", ofset2, 5, 2, color1);

						}

					}

				} else {
					image = imgProc.toBufferedImage(backUp);
				}
			}
			contourNr++;

		}

		image = imgProc.toBufferedImage(backUp);
	}
}