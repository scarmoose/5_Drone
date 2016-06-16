package dk.gruppe5.framework;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import dk.gruppe5.model.Contour;

public class combinedImageAnalysis {

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

		// Nu skal vi prøve at finde firkanter af en hvis størrelse
		List<Contour> contours = imgProc.findQRsquares(frame);

		List<BufferedImage> cutouts = imgProc.warp(backUp, contours, ratio);
		List<Result> results = imgProc.readQRCodes(cutouts);
		// backUp = imgProc.markQrCodes(results, shapes, backUp);
		List<Contour> triangles = imgProc.findTriangles(frame, 100);
		int contourNr = 0;
		for (Result result : results) {
			if (result != null) {
				Contour currentContour = contours.get(contourNr);
				Scalar color = new Scalar(255, 20, 100);
				backUp = imgProc.drawLinesBetweenContourPoints(contours.get(contourNr), backUp, ratio, color);

				double minArea = currentContour.getArea(ratio) * 0.3;

				Point centerCurrentContour = currentContour.getCenter(ratio);
				for (Contour contour : triangles) {
					Point triangleCenter = contour.getCenter(ratio);
					// System.out.println(triangleCenter);
					color = new Scalar(0, 20, 255);
					backUp = imgProc.drawLinesBetweenContourPoints(contour, backUp, ratio, color);
					
					if (minArea < contour.getArea(ratio)) {

						if (triangleCenter.y > centerCurrentContour.y * 0.7
								&& triangleCenter.y < centerCurrentContour.y * 1.3) {
							color = new Scalar(0, 20, 255);
							backUp = imgProc.drawLinesBetweenContourPoints(contour, backUp, ratio, color);
							imgProc.drawLine(centerCurrentContour, triangleCenter, backUp, color);

						} else {
							color = new Scalar(0, 20, 255);
							backUp = imgProc.drawLinesBetweenContourPoints(contour, backUp, ratio, color);
						}

					}
				}
			}

			contourNr++;
		}

		return backUp;

	}

}
