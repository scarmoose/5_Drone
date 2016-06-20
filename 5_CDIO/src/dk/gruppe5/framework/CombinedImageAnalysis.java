package dk.gruppe5.framework;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import CoordinateSystem.DronePosition;
import dk.gruppe5.controller.DistanceCalc;
import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.model.Contour;
import dk.gruppe5.model.DPoint;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.model.Wallmark;
import dk.gruppe5.positioning.Position;
import dk.gruppe5.view.Filterstates;

public class CombinedImageAnalysis {

	ImageProcessor imgProc = new ImageProcessor();

	public Mat findPositionFromQRandTriangles(Mat frame) {
		// frame = imgProc.calibrateCamera(frame);
		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 2;
		// frame = imgProc.downScale(backUp, ratio);
		// først gør vi det sort hvidt
		frame = imgProc.toGrayScale(frame);
		//
		frame = imgProc.equalizeHistogramBalance(frame);
		// blur virker bedre
		frame = imgProc.blur(frame);

		// Til canny for at nemmere kunne finde contourer
		frame = imgProc.toCanny(frame);
		Filterstates.setImage1(imgProc.toBufferedImage(frame));
		// Nu skal vi prøve at finde firkanter af en hvis størrelse
		List<Contour> contours = imgProc.findQRsquares(frame);

		List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
		List<Result> results = imgProc.readQRCodes(cutouts);
		// backUp = imgProc.markQrCodes(results, shapes, backUp);

		int contourNr = 0;
		Point leftTrianglePixelCenter = null;
		Point rightTrianglePixelCenter = null;
		java.awt.Point trianglePositionLeft = null;
		java.awt.Point trianglePositionRight = null;
		String nameOfQRMark;
		for (Result result : results) {
			if (result != null) {
				Contour currentContour = contours.get(contourNr);
				Scalar color = new Scalar(255, 20, 100);
				backUp = imgProc.drawLinesBetweenContourPoints(contours.get(contourNr), backUp, ratio, color);

				double minArea = currentContour.getBoundingRect(ratio).area() * 0.3;
				List<Contour> triangles = imgProc.findTriangles(frame, minArea);
				Point centerCurrentContour = currentContour.getCenter(ratio);
				for (Contour contour : triangles) {
					Point triangleCenter = contour.getCenter(ratio);
					// System.out.println(triangleCenter);
					color = new Scalar(0, 20, 255);
					backUp = imgProc.drawLinesBetweenContourPoints(contour, backUp, ratio, color);

					if (contour.getArea(ratio) > minArea && contour.getArea(ratio) < currentContour.getBoundingRect(ratio).area()*0.9) {

						if (triangleCenter.y > centerCurrentContour.y * 0.7 && triangleCenter.y < centerCurrentContour.y * 1.3) {
							if (centerCurrentContour.x < triangleCenter.x) {
								rightTrianglePixelCenter = triangleCenter;

							} else if (centerCurrentContour.x > triangleCenter.x) {
								leftTrianglePixelCenter = triangleCenter;
							}
						}

					}
				}
				if (rightTrianglePixelCenter != null && leftTrianglePixelCenter != null) {
					Scalar color1 = new Scalar(255, 20, 255);
					nameOfQRMark = result.getText();

					int nrWallMark = 0;
					for (Wallmark wallmark : Mathmagic.getArray()) {
						if (wallmark.getName().equals(nameOfQRMark)) {
							trianglePositionLeft = wallmark.getLeftTrianglePos();
							trianglePositionRight = wallmark.getRightTrianglePos();
							break;
						}
						nrWallMark++;
					}

					// find hvert af qrMærkernes positions data i koordinat
					// systemet, lav 2 nye punkter med halvt x værdi, lav
					// udregning for position og opdater korte
					if (trianglePositionLeft != null && trianglePositionRight != null) {
						DPoint trianglePosLef = new DPoint(trianglePositionLeft.x, trianglePositionLeft.y);
						DPoint trianglePosRight = new DPoint(trianglePositionRight.x, trianglePositionRight.y);
						Position test = new Position();
						Point mapPosition = test.getPositionFromPoints(trianglePosLef,
								Mathmagic.getPointFromName(nameOfQRMark), trianglePosRight, leftTrianglePixelCenter,
								centerCurrentContour, rightTrianglePixelCenter);
						if (mapPosition != null) {
							DronePosition.setPosition(mapPosition);
						}
					}

					backUp = imgProc.drawLine(rightTrianglePixelCenter, leftTrianglePixelCenter, backUp, color1);
				}
			}

			contourNr++;
		}

		return backUp;

	}

	public Mat locationEstimationFrom3Points(Mat frame) {

		frame = imgProc.calibrateCamera(frame);

		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 2;
		frame = imgProc.downScale(backUp, ratio);
		// fÃ¸rst gÃ¸r vi det sort hvidt
		frame = imgProc.toGrayScale(frame);
		//
		frame = imgProc.equalizeHistogramBalance(frame);
		// blur virker bedre
		frame = imgProc.blur(frame);

		// Til canny for at nemmere kunne finde contourer
		frame = imgProc.toCanny(frame);
		Filterstates.setImage1(imgProc.toBufferedImage(frame));
		// Nu skal vi prøve at finde firkanter af en hvis stÃ¸rrelse
		List<Contour> contours = imgProc.findQRsquares(frame);

		// vi finder de potentielle QR kode områder
		List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
		List<Result> results = imgProc.readQRCodes(cutouts);
		// backUp = imgProc.markQrCodes(results, shapes, backUp);
		int contourNr = 0;
		for (Result result : results) {
			if (result != null) {
				DetectedWallmarksAndNames data = imgProc.markQrCodesV2(contours.get(contourNr), contours, backUp,
						result.getText(), ratio);
				if (data != null) {
					if (!Double.isNaN(data.getPoints()[0].x) && !Double.isNaN(data.getPoints()[1].x)
							&& !Double.isNaN(data.getPoints()[2].x)) {
						if (data.getQrNames()[0] != null && data.getQrNames()[1] != null
								&& data.getQrNames()[2] != null) {
							Scalar color1 = new Scalar(0, 0, 255);
							backUp = imgProc.drawLine(data.getPoints()[0], data.getPoints()[1], backUp, color1);
							backUp = imgProc.drawLine(data.getPoints()[1], data.getPoints()[2], backUp, color1);
							Position test = new Position();
							Point mapPosition = test.getPositionFromPoints(data.getQrNames(), data.getPoints()[0],
									data.getPoints()[1], data.getPoints()[2]);
							if (mapPosition != null) {
								DronePosition.setPosition(mapPosition);
								// System.out.println(mapPosition);

								int screenWidth = 1280;
								int middleOfScreen = screenWidth / 2;
								int pixelsFromMiddleToQr = Math.abs(((int) data.getPoints()[1].x - middleOfScreen));
								DPoint mapPos = new DPoint(mapPosition);
								String text = data.getQrNames()[0];
								String wallNr = "" + text.charAt(2);
								int x = Integer.parseInt(wallNr);
								// System.out.println(test.getDirectionAngleRelativeToYAxis(mapPos,
								// data.getQrNames()[1], pixelsFromMiddleToQr));

								DronePosition.setDegree((90.0 * x) - test.getDirectionAngleRelativeToYAxis(mapPos,
										data.getQrNames()[1], pixelsFromMiddleToQr));
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

				}
			}
			contourNr++;

		}

		return backUp;
	}

	public Mat findQrCodeInImage(Mat frame) {
		frame = imgProc.calibrateCamera(frame);

		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 2;
		frame = imgProc.downScale(backUp, ratio);
		// fÃ¸rst gÃ¸r vi det sort hvidt
		frame = imgProc.toGrayScale(frame);
		//
		frame = imgProc.equalizeHistogramBalance(frame);
		// blur virker bedre
		frame = imgProc.blur(frame);

		// Til canny for at nemmere kunne finde contourer
		frame = imgProc.toCanny(frame);
		// Nu skal vi prøve at finde firkanter af en hvis stÃ¸rrelse
		List<Contour> contours = imgProc.findQRsquares(frame);

		// vi finder de potentielle QR kode områder
		List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
		List<Result> results = imgProc.readQRCodes(cutouts);
		// backUp = imgProc.markQrCodes(results, shapes, backUp);
		int contourNr = 0;
		for (Result result : results) {
			if(result != null){
				Values_cam.lastQrCodeFound = result.getText();
				Values_cam.timeOfFindingSingleQRCode = System.currentTimeMillis();
				Values_cam.distanceToLastQr = DistanceCalc.distanceFromCamera(contours.get(contourNr).getBoundingRect(ratio).height);
			}
			contourNr++;
			
		}
		return frame;
	}

}
