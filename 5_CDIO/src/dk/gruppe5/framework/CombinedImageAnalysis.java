package dk.gruppe5.framework;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import CoordinateSystem.DronePosition;
import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.model.Contour;
import dk.gruppe5.model.DPoint;
import dk.gruppe5.model.Wallmark;
import dk.gruppe5.positioning.Position;
import dk.gruppe5.view.Filterstates;

public class CombinedImageAnalysis {

	ImageProcessor imgProc = new ImageProcessor();

	public Mat findPositionFromQRandTriangles(Mat frame) {
		// frame = imgProc.calibrateCamera(frame);
		Mat backUp = new Mat();
		backUp = frame;
		int ratio = 1;
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
				System.out.println("Min ARea ---_>" + minArea);
				List<Contour> triangles = imgProc.findTriangles(frame, minArea);
				System.out.println("------------------------------------------------------------------------");
				Point centerCurrentContour = currentContour.getCenter(ratio);
				System.out.println(triangles.size());
				for (Contour contour : triangles) {
					Point triangleCenter = contour.getCenter(ratio);
					// System.out.println(triangleCenter);
					color = new Scalar(0, 20, 255);
					backUp = imgProc.drawLinesBetweenContourPoints(contour, backUp, ratio, color);

					if (contour.getArea(ratio) > minArea) {

						if (triangleCenter.y > centerCurrentContour.y * 0.7
								&& triangleCenter.y < centerCurrentContour.y * 1.3) {
							double minDistance = 100.0;
							if (centerCurrentContour.x < triangleCenter.x + minDistance) {
								rightTrianglePixelCenter = triangleCenter;

							} else if (centerCurrentContour.x > triangleCenter.x + minDistance) {
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
						DPoint trianglePosLef = new DPoint(trianglePositionLeft.x,trianglePositionLeft.y);
						DPoint trianglePosRight = new DPoint(trianglePositionRight.x,trianglePositionRight.y);
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
}
