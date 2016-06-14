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

import CoordinateSystem.DronePosition;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;
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
	}

	public synchronized void paint(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
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
				} else if (Values_cam.getMethod() == 4) {

					frame = imgProc.toGrayScale(frame);
					frame = imgProc.blur(frame);
					frame = imgProc.toCanny(frame);

					frame = imgProc.findAirfield(frame);

					image = imgProc.toBufferedImage(frame);

				} else if (Values_cam.getMethod() == 10) {
					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 2;
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
							backUp = imgProc.drawLinesBetweenContourPoints(contours.get(i), backUp, ratio, color);
						}
						i++;
					}

					image = imgProc.toBufferedImage(backUp);
				} else if (Values_cam.getMethod() == 5) {

					/*
					 * Skal fixes imorgen!
					 */
					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 2;
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

					// Nu skal vi prøve at finde firkanter af en hvis størrelse
					List<Contour> contours = imgProc.findQRsquares(frame);
					// System.out.println(contours.size());
					// int z = 0;
					// for(Contour contour : contours){
					//
					// Scalar color = new Scalar(0, 255, 0);
					// backUp =
					// imgProc.drawLinesBetweenBoundingRectPoints(contours.get(z),
					// backUp, ratio, color);
					// z++;
					// }
					// vi finder de potentielle QR kode områder
					List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
					List<Result> results = imgProc.readQRCodes(cutouts);

					int i = 0;
					for (Result result : results) {
						if (result != null) {
							// backUp =
							// imgProc.drawLinesBetweenBoundingRectPoints(contours.get(i),
							// backUp, ratio);
							Scalar color = new Scalar(255, 255, 0);
							backUp = imgProc.drawLinesBetweenContourPoints(contours.get(i), backUp, ratio, color);
							System.out.println(i + " found a qr square, positive matches = " + results.size());
						}
						i++;
					}
					// Vi aflæser de potentielle QR koder og ser om vi har nogen
					// matches, hvis vi har!
					// så marker dette og firkanter der har ca samme højde og
					// størrelse!
					// skriv i disse hvilken en firkant de nok er ud fra dataene
					// vi har.
					// tegn streg mellem dem og skriv pixel afstand
					// udregn afstand til QR kode via python afstands
					// bestemmelse på papir

					// backUp = imgProc.markQrCodes(results, shapes, backUp);

					DetectedWallmarksAndNames data = imgProc.markQrCodes(results, contours, backUp, ratio);

					if (data != null) {
						if (!Double.isNaN(data.getPoints()[0].x) && !Double.isNaN(data.getPoints()[1].x)
								&& !Double.isNaN(data.getPoints()[2].x)) {
							if (data.getQrNames()[0] != null && data.getQrNames()[1] != null
									&& data.getQrNames()[2] != null) {
								Scalar color = new Scalar(0, 0, 255);
								backUp = imgProc.drawLine(data.getPoints()[0], data.getPoints()[1], backUp, color);
								backUp = imgProc.drawLine(data.getPoints()[1], data.getPoints()[2], backUp, color);
								System.out.println("point1:" + data.getQrNames()[0] + " point 2:" + data.getQrNames()[1]
										+ " point 3:" + data.getQrNames()[2]);
								System.out.println("point1:" + data.getPoints()[0] + " point 2:" + data.getPoints()[1]
										+ " point 3:" + data.getPoints()[2]);
								Position test = new Position();
								/*
								 * Vi skal hente punkterne for de navne vi
								 * finder, de skal sendes, også skal der sendes
								 * de pixel positions værdier vi har fundet
								 */
								Point mapPosition = test.getPositionFromPoints(data.getQrNames(), data.getPoints()[0],
										data.getPoints()[1], data.getPoints()[2]);
								DronePosition.setPosition(mapPosition);
								System.out.println(mapPosition);
								// test.getPositionFromPoints(data.getPoints()[0],
								// data.getPoints()[1], data.getPoints()[3]);
							}

						} else if (!Double.isNaN(data.getPoints()[1].x)) {

							Scalar color = new Scalar(255, 0, 0);
							Imgproc.putText(backUp, data.getQrNames()[1], data.getPoints()[1], 5, 2, color);
							Imgproc.putText(backUp, data.getDistance() + "", data.getPoints()[1], 5, 2, color);
							if (!Double.isNaN(data.getPoints()[0].x)) {
								Imgproc.putText(backUp, "firkant", data.getPoints()[0], 5, 2, color);

							}
							if (!Double.isNaN(data.getPoints()[2].x)) {
								Imgproc.putText(backUp, "firkant", data.getPoints()[2], 5, 2, color);

							}

						}

					} else {
						image = imgProc.toBufferedImage(backUp);
					}

					image = imgProc.toBufferedImage(backUp);
				} else if (Values_cam.getMethod() == 6) {

					Mat backUp = new Mat();
					backUp = frame;
					int ratio = 2;
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

					// Nu skal vi prøve at finde firkanter af en hvis størrelse
					List<Contour> contours = imgProc.findQRsquares(frame);
					// System.out.println(contours.size());
					// int z = 0;
					// for(Contour contour : contours){
					//
					// Scalar color = new Scalar(0, 255, 0);
					// backUp =
					// imgProc.drawLinesBetweenBoundingRectPoints(contours.get(z),
					// backUp, ratio, color);
					// z++;
					// }
					// vi finder de potentielle QR kode områder
					List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
					List<Result> results = imgProc.readQRCodes(cutouts);

					int i = 0;
					for (Result result : results) {
						if (result != null) {
							// backUp =
							// imgProc.drawLinesBetweenBoundingRectPoints(contours.get(i),
							// backUp, ratio);
							Scalar color = new Scalar(255, 255, 0);
							backUp = imgProc.drawLinesBetweenContourPoints(contours.get(i), backUp, ratio, color);
						}
						i++;
					}
					// Vi aflæser de potentielle QR koder og ser om vi har nogen
					// matches, hvis vi har!
					// så marker dette og firkanter der har ca samme højde og
					// størrelse!
					// skriv i disse hvilken en firkant de nok er ud fra dataene
					// vi har.
					// tegn streg mellem dem og skriv pixel afstand
					// udregn afstand til QR kode via python afstands
					// bestemmelse på papir

					// backUp = imgProc.markQrCodes(results, shapes, backUp);
					int contourNr = 0;
					for (Result result : results) {
						if (result != null) {
							DetectedWallmarksAndNames data = imgProc.markQrCodesV2(contours.get(contourNr), contours,
									backUp, result.getText(), ratio);
							if (data != null) {
								
								System.out.println(data.getQrNames()[0]+","+data.getQrNames()[1]+","+data.getQrNames()[2]);
								System.out.println(data.getPoints()[0]+","+data.getPoints()[1]+","+data.getPoints()[2]);

								if (!Double.isNaN(data.getPoints()[0].x) && !Double.isNaN(data.getPoints()[1].x)
										&& !Double.isNaN(data.getPoints()[2].x)) {
									if (data.getQrNames()[0] != null && data.getQrNames()[1] != null
											&& data.getQrNames()[2] != null) {
										Scalar color1 = new Scalar(0, 0, 255);
										backUp = imgProc.drawLine(data.getPoints()[0], data.getPoints()[1], backUp,
												color1);
										
										backUp = imgProc.drawLine(data.getPoints()[1], data.getPoints()[2], backUp,
												color1);
										System.out.println("point1:" + data.getQrNames()[0] + " point 2:"
												+ data.getQrNames()[1] + " point 3:" + data.getQrNames()[2]);
										Point ofset = new Point(data.getPoints()[1].x, data.getPoints()[1].y + 30);
										System.out.println("point1:" + data.getPoints()[0] + " point 2:" + ofset + " point 3:" + data.getPoints()[2]);
										
										Position test = new Position();
										/*
										 * Vi skal hente punkterne for de navne
										 * vi finder, de skal sendes, også skal
										 * der sendes de pixel positions værdier
										 * vi har fundet
										 */
										Point mapPosition = test.getPositionFromPoints(data.getQrNames(),
												data.getPoints()[0], data.getPoints()[1], data.getPoints()[2]);
										if(mapPosition != null){
											DronePosition.setPosition(mapPosition);
											System.out.println(mapPosition);
											
										}
										// test.getPositionFromPoints(data.getPoints()[0],
										// data.getPoints()[1],
										// data.getPoints()[3]);
									}

								} else if (!Double.isNaN(data.getPoints()[1].x)) {

									Scalar color1 = new Scalar(255, 0, 0);
									Imgproc.putText(backUp, data.getQrNames()[1], data.getPoints()[1], 5, 2, color1);
									Point ofset = new Point(data.getPoints()[1].x, data.getPoints()[1].y + 30);
									Imgproc.putText(backUp, data.getDistance() + "", ofset, 5, 2, color1);

									if (!Double.isNaN(data.getPoints()[0].x)) {
										Point ofset1 = new Point(data.getPoints()[0].x,data.getPoints()[0].y);
										Imgproc.putText(backUp, "firkant", ofset1, 5, 2, color1);

									}
									if (!Double.isNaN(data.getPoints()[2].x)) {
										Point ofset2 = new Point(data.getPoints()[2].x,data.getPoints()[2].y);
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
				System.out.println(image.getWidth() +","+ image.getHeight());

//				addMouseListener(new MouseAdapter() {
//					public void mouseClicked(MouseEvent e) {
//						
//						imgProc.saveImage(imgProc.bufferedImageToMat(image), "IMAGE" + picsNr + ".jpg");
//
//						picsNr++;
//					}
//				});
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaint();
					}
				});
			}

		}
	}
}