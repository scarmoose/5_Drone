package dk.gruppe5.framework;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.opencv.video.Video;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import dk.gruppe5.controller.DistanceCalc;
import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.model.Contour;
import dk.gruppe5.model.Shape;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.model.Wallmark;
import dk.gruppe5.model.opticalFlowData;
import dk.gruppe5.model.templateMatch;

public class ImageProcessor {

	public BufferedImage toBufferedImage(Mat matrix) {
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (matrix.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
		byte[] buffer = new byte[bufferSize];
		matrix.get(0, 0, buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
		return image;
	}

	public Mat bufferedImageToMat(BufferedImage bi) {
		Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC3);
		byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, data);
		return mat;
	}

	public Mat blur(Mat input) {
		Mat sourceImage = input.clone();
		Mat destImage = new Mat();

		Imgproc.GaussianBlur(sourceImage, destImage, new Size(5.0, 5.0), (double) 2.0);

		return destImage;
	}

	public Mat erode(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.erode(input, outputImage, element);
		return outputImage;
	}

	public Mat dilate(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.dilate(input, outputImage, element);
		return outputImage;
	}

	public Mat downScale(Mat backUp, int i) {
		int width = backUp.width() / i;
		int height = backUp.height() / i;
		Mat dst = new Mat();
		Imgproc.resize(backUp, dst, new Size(width, height));
		return dst;
	}

	public Mat open(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_OPEN, element);
		return outputImage;
	}

	public Mat close(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input, outputImage, Imgproc.MORPH_CLOSE, element);
		return outputImage;
	}

	private Mat getKernelFromShape(int elementSize, int elementShape) {
		return Imgproc.getStructuringElement(elementShape, new Size(elementSize * 2 + 1, elementSize * 2 + 1),
				new Point(elementSize, elementSize));
	}

	public BufferedImage toGrayScale(BufferedImage input) {

		byte[] data = ((DataBufferByte) input.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(input.getHeight(), input.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, data);

		Mat mat1 = new Mat(input.getHeight(), input.getWidth(), CvType.CV_8UC3);
		Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2GRAY);

		byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int) (mat1.elemSize())];
		mat1.get(0, 0, data1);
		BufferedImage image1 = new BufferedImage(mat1.cols(), mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);
		image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);

		return image1;

	}

	public Mat toGrayScale(Mat input) {
		Mat imageGray = new Mat();
		Imgproc.cvtColor(input, imageGray, Imgproc.COLOR_BGR2GRAY);
		return imageGray;

	}

	public Mat toCanny(Mat grayImg) {
		Mat imageGray = grayImg;
		Mat imageCny = new Mat();
		Imgproc.Canny(imageGray, imageCny, Values_cam.getCanTres1(), Values_cam.getCanTres2(), Values_cam.getCanAp(),
				true);

		return imageCny;
	}

	/**
	 * Dosent work as intended decrapted or whatever.....
	 * 
	 * @param input
	 * @return
	 */
	public Mat findContours(Mat input) {
		List<MatOfPoint> contours_1 = new ArrayList<MatOfPoint>();
		Mat hierarchy_1 = new Mat();
		// Imgproc.findContours(input, contours_1, hierarchy_1,
		// Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours(input, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
		// draw contours?
		Random rn = new Random();
		Mat standIn = new Mat();
		Imgproc.cvtColor(input, standIn, Imgproc.COLOR_BayerBG2RGB);

		// Detecting shapes in the contours
		for (int i = 0; i < contours_1.size(); i++) {
			MatOfPoint2f contour = new MatOfPoint2f(contours_1.get(i).toArray());

			MatOfPoint2f approxCurve = new MatOfPoint2f();
			double epsilon = Imgproc.arcLength(contour, true) * 0.02;
			// houghcircles houghlines

			// if(contours_1.get(i).size().area() >10 &&
			// Imgproc.isContourConvex(contours_1.get(i))) { // Minimum size
			// allowed for consideration
			//
			// Scalar color = new Scalar(rn.nextInt(255), rn.nextInt(255),
			// rn.nextInt(255));
			// Imgproc.drawContours(standIn, contours_1, i, color, 3);
			//
			// }
			Scalar color = new Scalar(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));
			Imgproc.drawContours(standIn, contours_1, i, color, 3);
			// we wanna se if a contour is a square.
			// Imgproc.approxPolyDP(contour, approxCurve, epsilon, true);
			//
			// MatOfPoint approxf1 = new MatOfPoint();
			// approxCurve.convertTo(approxf1, CvType.CV_32S);
			//
			// System.out.println(approxf1.width()*approxf1.height());
			// if( (approxf1.width()*approxf1.height()) == 4 &&
			// Math.abs(Imgproc.contourArea(approxf1)) > 10 &&
			// Imgproc.isContourConvex(approxf1) ){

			// System.out.println("I found shape Huurr durr");
			//
			//
			// }

		}

		return standIn;
	}

	public Mat templateMatching(Mat input) {

		BufferedImage img2 = null;

		try {
			img2 = ImageIO.read(new File("pics/Airfield.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		Mat templateImg = bufferedImageToMat(img2);
		// System.out.println("tempCols:"+templateImg.cols());
		// System.out.println("frameCols:"+input.cols());

		int match_method = Imgproc.TM_CCOEFF;
		List<templateMatch> matches = new ArrayList<>();
		// Initial rezise of image to be a little smaller than the frame image.
		templateImg = blur(templateImg);

		Imgproc.resize(templateImg, templateImg, new Size(input.width(), input.height()));

		// gray and to canny??
		// input = toGrayScale(input);
		// input = toCanny(input);
		//
		// templateImg = toGrayScale(templateImg);
		// templateImg = toCanny(templateImg);

		//
		int i = 0;
		// As long as the size of template image is bigger than a 10th of the
		// image, we will look at finding the best match
		while (templateImg.width() > input.width() / 30 || templateImg.height() > input.height() / 30) {
			// / Create the result matrix
			int result_cols = input.cols() - templateImg.cols() + 1;
			int result_rows = input.rows() - templateImg.rows() + 1;
			Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

			// / Do the Matching and Normalize
			Imgproc.matchTemplate(input, templateImg, result, match_method);
			// Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new
			// Mat());
			// / Localizing the best match with minMaxLoc
			MinMaxLocResult mmr = Core.minMaxLoc(result);
			Point matchLoc;
			if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
				matchLoc = mmr.minLoc;

			} else {
				matchLoc = mmr.maxLoc;
				// System.out.println(mmr.maxVal);

			}

			// / Show me what you got
			// System.out.println(matchLoc);

			i++;
			// System.out.println(templateImg.width()+":"+templateImg.height());
			templateMatch tempMatch = new templateMatch(matchLoc, templateImg.width(), templateImg.height(),
					mmr.maxVal);
			matches.add(tempMatch);

			try {
				// retrieve image
				BufferedImage bi = toBufferedImage(templateImg);
				File outputfile = new File("saveImg" + i + ".png");
				ImageIO.write(bi, "png", outputfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Imgproc.resize(templateImg, templateImg, new Size(input.width() - 16 * i, input.height() - 12 * i));

		}
		// System.out.println(matches.size());
		// double highestValue = 0;
		// int matchNr = 0;
		// for(int z = 0; z < matches.size(); z++){
		//
		//
		// if(matches.get(z).getMatchValue() > highestValue){
		// highestValue =matches.get(z).getMatchValue();
		// matchNr = z;
		// }
		//
		// }
		// input = toColor(input);
		// //System.out.println(matchNr);
		// //Imgproc.rectangle(input, matchLoc, new Point(matchLoc.x +
		// templateImg.cols(), matchLoc.y + templateImg.rows()), new Scalar(0,
		// 255, 0));
		// Imgproc.rectangle(input, matches.get(matchNr).getCoordinate(), new
		// Point(matches.get(matchNr).getCoordinate().x +
		// matches.get(matchNr).getPicWidth(),
		// matches.get(matchNr).getCoordinate().y +
		// matches.get(matchNr).getPicHeight()), new Scalar(0, 255, 0));
		System.out.println("-----------");
		for (int x = 0; x < matches.size(); x++) {

			if (matches.get(x).getMatchValue() > 5.1E8) {
				Imgproc.rectangle(input, matches.get(x).getCoordinate(),
						new Point(matches.get(x).getCoordinate().x + matches.get(x).getPicWidth(),
								matches.get(x).getCoordinate().y + matches.get(x).getPicHeight()),
						new Scalar(0, 255, 0));
				System.out.println(matches.get(x).getMatchValue());
			}

		}
		System.out.println("-----------");

		return input;

	}

	public Mat toColor(Mat input) {
		Mat standIn = new Mat();
		Imgproc.cvtColor(input, standIn, Imgproc.COLOR_BayerBG2RGB);
		return standIn;
	}

	public opticalFlowData opticalFlow(Mat frameOne, Mat frameTwo) {
		// TODO Auto-generated method stub
		// Først finder vi de gode features at tracker
		frameOne = toGrayScale(frameOne);
		frameTwo = toGrayScale(frameTwo);
		frameOne = toCanny(frameOne);
		frameTwo = toCanny(frameTwo);
		List<MatOfPoint> contours_1 = new ArrayList<MatOfPoint>();
		List<MatOfPoint> contours_2 = new ArrayList<MatOfPoint>();
		Mat hierarchy_1 = new Mat();
		Mat hierarchy_2 = new Mat();
		Imgproc.findContours(frameOne, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours(frameTwo, contours_2, hierarchy_2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat standIn = new Mat();
		MatOfPoint corners1 = new MatOfPoint();
		MatOfPoint corners2 = new MatOfPoint();
		// Imgproc.goodFeaturesToTrack(frameOne, corners1, 100, 0.1, 30);
		// Imgproc.goodFeaturesToTrack(frameTwo, corners2, 100, 0.1, 30);

		Imgproc.goodFeaturesToTrack(frameOne, corners1, Values_cam.getCorn(), Values_cam.getQual(),
				Values_cam.getDist());
		Imgproc.goodFeaturesToTrack(frameTwo, corners2, Values_cam.getCorn(), Values_cam.getQual(),
				Values_cam.getDist());
		// Now that we have found good features and added them to the corners1
		// and 2
		// we add colour back to the picture so that we can draw lovely lines
		Imgproc.cvtColor(frameOne, standIn, Imgproc.COLOR_BayerBG2RGB);

		// This draws the good features that we have found in the 2 frames.
		for (int x = 0; x < corners1.width(); x++) {
			for (int y = 0; y < corners1.height(); y++) {
				Imgproc.circle(standIn, new Point(corners1.get(y, x)), 7, new Scalar(200, 0, 50), 1);
				Imgproc.circle(standIn, new Point(corners2.get(y, x)), 2, new Scalar(0, 250, 0), 2);

			}
		}

		MatOfByte status = new MatOfByte();
		MatOfFloat err = new MatOfFloat();
		MatOfPoint2f corners1f = new MatOfPoint2f(corners1.toArray());
		MatOfPoint2f corners2f = new MatOfPoint2f(corners2.toArray());
		Video.calcOpticalFlowPyrLK(frameOne, frameTwo, corners1f, corners2f, status, err);
		List<Point> startPoints = new ArrayList<>();
		List<Point> endPoints = new ArrayList<>();

		Double averageCalc = 0.0;
		int nrOfVec = 0;
		for (int i = 0; i < corners1f.height(); i++) {
			Point startP = new Point(corners2f.get(i, 0));
			Point endP = new Point(corners1f.get(i, 0));
			Double distance = Math
					.sqrt((startP.x - endP.x) * (startP.x - endP.x) + (startP.y - endP.y) * (startP.y - endP.y));

			if (distance > 20) {
				// System.out.println("Distance:"+distance);
				averageCalc = averageCalc + distance;
				// System.out.println(err.get(i, 0)[0]);
				nrOfVec++;
			}

		}

		averageCalc = averageCalc / nrOfVec;
		int threshold = 1;
		for (int i = 0; i < corners1f.height(); i++) {

			// Imgproc.line(standIn,startP,endP,new Scalar(0,250,0),5);
			Point startP = new Point(corners2f.get(i, 0));
			Point endP = new Point(corners1f.get(i, 0));
			Double distance = Math
					.sqrt((startP.x - endP.x) * (startP.x - endP.x) + (startP.y - endP.y) * (startP.y - endP.y));
			/*
			 * By calculating an average in the distance between points in the
			 * picture, we can use this to remove Unwanted vectors, for example
			 * vectors that is longer than a certain threshold in the picture
			 * 
			 * We could also use a an estimation on the different vectors to see
			 * if the drone has moved or a single object in the frame has moved
			 * we can do this by looking at the average movement, if an area of
			 * vectors are moving much longer than the rest of the frame
			 * vectors, there is likely to be an object moving in that area.
			 */

			/*
			 * This is used to draw arrows between the two points found matching
			 * in the two frames. The scalar is colour.
			 */
			if (distance < threshold * averageCalc && distance > 4) {

				Imgproc.arrowedLine(standIn, startP, endP, new Scalar(0, 250, 0));
				// System.out.println("test");
				startPoints.add(startP);
				endPoints.add(endP);
			}
		}

		return new opticalFlowData(standIn, startPoints, endPoints);
	}

	public Mat findAirfield(Mat input) {
		List<MatOfPoint> contours_1 = new ArrayList<MatOfPoint>();
		Mat hierarchy_1 = new Mat();
		Imgproc.findContours(input, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Random rn = new Random();
		Mat standIn = new Mat();
		Imgproc.cvtColor(input, standIn, Imgproc.COLOR_BayerBG2RGB);
		List<Shape> rects = new ArrayList();
		List<Shape> cirRects = new ArrayList();

		// Detecting shapes in the contours
		for (int i = 0; i < contours_1.size(); i++) {
			MatOfPoint2f contour = new MatOfPoint2f(contours_1.get(i).toArray());

			MatOfPoint2f approxCurve = new MatOfPoint2f();
			double epsilon = Imgproc.arcLength(contour, true) * 0.01;
			// we wanna se if a contour is a square.
			Imgproc.approxPolyDP(contour, approxCurve, epsilon, true);
			if (approxCurve.height() == 4) {

				// We find the rect that surronds the square and adds it to
				// rects
				Rect r = Imgproc.boundingRect(contours_1.get(i));

				if ((r.width * 2 > r.height) && (r.height / 2 < r.width)) {

					rects.add(new Shape(r.area(), r.tl(), r.br(), approxCurve.height()));
					// Scalar color = new Scalar(rn.nextInt(255),
					// rn.nextInt(255), rn.nextInt(255));
					// Imgproc.rectangle(standIn, r.br(), r.tl(), color);
				}
				// Imgproc.drawContours(standIn, contours_1, i, color, 2);

			}
			// we just say any contour/shap with more than 6 edges we call it a
			// circle
			if (approxCurve.height() > 10) {

				// Scalar color = new Scalar(0, 0, 255);
				Rect r = Imgproc.boundingRect(contours_1.get(i));
				double area = Imgproc.contourArea(contours_1.get(i));
				int radius = r.width / 2;

				// Imgproc.rectangle(standIn, r.br(), r.tl(), color);
				// System.out.println(Math.abs(1 - (r.width / r.height)));
				if (Math.abs(1 - ((double) r.width / (double) r.height)) <= 0.2
						&& Math.abs(1 - (area / (Math.PI * Math.pow((double) radius, 2)))) <= 0.1) {
					if (r.area() > 80) {
						// System.out.println(r.area());
						// Imgproc.drawContours(standIn, contours_1, i,
						// color,2);
						cirRects.add(new Shape(r.area(), r.tl(), r.br(), approxCurve.height()));

					}

				}

			}

		}

		double pixelWidth = 0.0;
		int nr = 0;
		// check if circles are contained in a rect
		for (Shape rect : rects) {

			Point tlPt = rect.getTlPoint();
			Point brPt = rect.getBrPoint();
			int containedCircles = 0;
			for (Shape cirRect : cirRects) {
				double carea = cirRect.getArea();
				Point ctlPt = cirRect.getTlPoint();
				Point cbrPt = cirRect.getBrPoint();
				// area check dosent work, buuut its unlikely anything will
				// match the requirements for airfield1 or 2
				if (cirRect.getArea() > rect.getArea() * 0.15 && true) {

					if (ctlPt.inside(rect.getRect())) {
						containedCircles++;
						Scalar color = new Scalar(255, 0, 0);
						Imgproc.rectangle(standIn, cirRect.getBrPoint(), cirRect.getTlPoint(), color, 3);
					}
				}

			}
			// Den t�ller hver cirkel dobbelt wat, noget med canny og de kanter
			// den giver tror jeg
			// indre cirkel og ydre cirkel. G�r det nok ogs� med firkant...

			if (containedCircles == 6) {
				Scalar color = new Scalar(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));
				Imgproc.rectangle(standIn, tlPt, brPt, color, 3);
				Point txtPoint = new Point(tlPt.x + rect.getWidth() / 4, tlPt.y + rect.getHeight() / 2);

				Imgproc.putText(standIn, "Airfield1", txtPoint, 5, 2, color);

			} else if (containedCircles == 10) {
				Scalar color = new Scalar(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));
				Imgproc.rectangle(standIn, tlPt, brPt, color, 3);
				Point txtPoint = new Point(tlPt.x + rect.getWidth() / 4, tlPt.y + rect.getHeight() / 2);

				Imgproc.putText(standIn, "Airfield2", txtPoint, 5, 2, color);
			} else if (containedCircles == 2) {
				Scalar color = new Scalar(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));
				Imgproc.rectangle(standIn, tlPt, brPt, color, 3);
				Point txtPoint = new Point(tlPt.x + rect.getWidth() / 4, tlPt.y + rect.getHeight() / 2);

				pixelWidth += rect.getHeight();
				nr++;

				Imgproc.putText(standIn, "testAirfield", txtPoint, 5, 2, color);
			}

		}
		double pixelWidthAverage = pixelWidth / (double) nr;
		// System.out.println(pixelWidthAverage);
		System.out.println(DistanceCalc.distanceFromCamera(pixelWidthAverage));

		return standIn;
	}

	public Mat findDirection(Mat frameOne, Mat frameTwo) {
		// Først finder vi de gode features at tracker
		frameOne = toGrayScale(frameOne);
		frameTwo = toGrayScale(frameTwo);
		frameOne = toCanny(frameOne);
		frameTwo = toCanny(frameTwo);
		List<MatOfPoint> contours_1 = new ArrayList<MatOfPoint>();
		List<MatOfPoint> contours_2 = new ArrayList<MatOfPoint>();
		Mat hierarchy_1 = new Mat();
		Mat hierarchy_2 = new Mat();
		Imgproc.findContours(frameOne, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours(frameTwo, contours_2, hierarchy_2, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		Mat standIn = new Mat();
		MatOfPoint corners1 = new MatOfPoint();
		MatOfPoint corners2 = new MatOfPoint();
		// Imgproc.goodFeaturesToTrack(frameOne, corners1, 100, 0.1, 30);
		// Imgproc.goodFeaturesToTrack(frameTwo, corners2, 100, 0.1, 30);

		Imgproc.goodFeaturesToTrack(frameOne, corners1, Values_cam.getCorn(), Values_cam.getQual(),
				Values_cam.getDist());
		Imgproc.goodFeaturesToTrack(frameTwo, corners2, Values_cam.getCorn(), Values_cam.getQual(),
				Values_cam.getDist());
		// Now that we have found good features and added them to the corners1
		// and 2
		// we add colour back to the picture so that we can draw lovely lines
		Imgproc.cvtColor(frameOne, standIn, Imgproc.COLOR_BayerBG2RGB);

		// This draws the good features that we have found in the 2 frames.
		for (int x = 0; x < corners1.width(); x++) {
			for (int y = 0; y < corners1.height(); y++) {
				Imgproc.circle(standIn, new Point(corners1.get(y, x)), 30, new Scalar(200, 0, 50), 1);
				Imgproc.circle(standIn, new Point(corners2.get(y, x)), 20, new Scalar(0, 250, 0), 2);

			}
		}

		MatOfByte status = new MatOfByte();
		MatOfFloat err = new MatOfFloat();
		MatOfPoint2f corners1f = new MatOfPoint2f(corners1.toArray());
		MatOfPoint2f corners2f = new MatOfPoint2f(corners2.toArray());
		Video.calcOpticalFlowPyrLK(frameOne, frameTwo, corners1f, corners2f, status, err);
		List<Point> startPoints = new ArrayList<>();
		List<Point> endPoints = new ArrayList<>();

		Double averageCalc = 0.0;
		int nrOfVec = 0;
		for (int i = 0; i < corners1f.height(); i++) {
			Point startP = new Point(corners2f.get(i, 0));
			Point endP = new Point(corners1f.get(i, 0));
			Double distance = Math
					.sqrt((startP.x - endP.x) * (startP.x - endP.x) + (startP.y - endP.y) * (startP.y - endP.y));

			if (distance > 20) {
				// System.out.println("Distance:"+distance);
				averageCalc = averageCalc + distance;
				// System.out.println(err.get(i, 0)[0]);
				nrOfVec++;
			}

		}

		averageCalc = averageCalc / nrOfVec;
		int threshold = 1;
		for (int i = 0; i < corners1f.height(); i++) {

			// Imgproc.line(standIn,startP,endP,new Scalar(0,250,0),5);
			Point startP = new Point(corners2f.get(i, 0));
			Point endP = new Point(corners1f.get(i, 0));
			Double distance = Math
					.sqrt((startP.x - endP.x) * (startP.x - endP.x) + (startP.y - endP.y) * (startP.y - endP.y));
			/*
			 * By calculating an average in the distance between points in the
			 * picture, we can use this to remove Unwanted vectors, for example
			 * vectors that is longer than a certain threshold in the picture
			 * 
			 * We could also use a an estimation on the different vectors to see
			 * if the drone has moved or a single object in the frame has moved
			 * we can do this by looking at the average movement, if an area of
			 * vectors are moving much longer than the rest of the frame
			 * vectors, there is likely to be an object moving in that area.
			 */

			/*
			 * This is used to draw arrows between the two points found matching
			 * in the two frames. The scalar is colour.
			 */
			if (distance < threshold * averageCalc && distance > 4) {

				Imgproc.arrowedLine(standIn, startP, endP, new Scalar(0, 250, 0));
				// System.out.println("test");
				startPoints.add(startP);
				endPoints.add(endP);
			}
		}

		return standIn;

	}

	public Mat loadImage(String fileName) {

		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bufferedImageToMat(img);
	}

	public boolean saveImage(Mat frame, String saveFileName) {
		try {
			// retrieve image
			BufferedImage bi = toBufferedImage(frame);
			File outputfile = new File("pics/testfiles/" + saveFileName);
			ImageIO.write(bi, "jpg", outputfile);
		} catch (IOException e) {
			return false;
		}

		return true;

	}

	public List<Contour> findQRsquares(Mat frame) {

		// Here contours are stored, we will check each one to see if it matches
		List<MatOfPoint> contours_1 = new ArrayList<MatOfPoint>();
		Mat hierarchy_1 = new Mat();
		Imgproc.findContours(frame, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		List<Contour> contours = new ArrayList<>();

		// Detecting shapes in the contours
		for (int i = 0; i < contours_1.size(); i++) {
			MatOfPoint2f contour = new MatOfPoint2f(contours_1.get(i).toArray());

			MatOfPoint2f approxCurve = new MatOfPoint2f();
			double epsilon = Imgproc.arcLength(contour, true) * 0.05;

			// we wanna se if a contour is a square, or has one or more edges so
			// we save them.
			Imgproc.approxPolyDP(contour, approxCurve, epsilon, true);
			Rect r = Imgproc.boundingRect(contours_1.get(i));
			if (r.area() > 80) {
				if (approxCurve.total() > 3 && approxCurve.total() < 10) {
					Contour contour1 = new Contour(contour, approxCurve);
					contours.add(contour1);
				}

			}
		}

		return contours;
	}

	public List<Result> readQRCodes(List<BufferedImage> potentialQRcodes) {
		// try to detect QR code
		List<Result> qrData = new ArrayList<>();

		for (BufferedImage image : potentialQRcodes) {
			Result scanResult;

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

			// decode the barcode (if only QR codes are used, the QRCodeReader
			// might be a better choice)
			MultiFormatReader reader = new MultiFormatReader();
			try {
				scanResult = reader.decode(bitmap);
			} catch (ReaderException e) {
				// no code found.
				scanResult = null;
			}
			qrData.add(scanResult);

		}
		// for (Result Qrdata : qrData) {
		// if (Qrdata != null) {
		// System.out.println(Qrdata.getText());
		// }
		//
		// }
		return qrData;
	}

	public DetectedWallmarksAndNames markQrCodes(List<Result> results, List<Contour> contours, Mat backUp, int ratio) {
		// find each qr code from the list, mark it on the image,
		// then find any squares that match the height of this QR code and
		// roughly size(width and height)
		// if any are found the determine which they probaly are and mark them
		// with name

		List<Contour> qrCodeShapeConfirms = new ArrayList<Contour>();
		List<Result> qrCodeResultConfirms = new ArrayList<Result>();

		Point leftPoint;
		Point rightPoint;
		Point QrPoint;
		List<Point> leftPoints = new ArrayList<Point>();
		List<Point> rightPoints = new ArrayList<Point>();
		List<Point> QrPointsPoints = new ArrayList<Point>();

		double distance = 0;
		for (int i = 0; i < results.size(); i++) {
			if (results.get(i) != null) {
				// draw the shape and write the result in the area
				Contour contour = contours.get(i);
				qrCodeShapeConfirms.add(contour);
				qrCodeResultConfirms.add(results.get(i));

			}
		}

		String nameOfQROnTheRight = null;
		String nameOfQROnTheLeft = null;
		String nameOfQRCodeFound = null;

		for (int z = 0; z < qrCodeShapeConfirms.size(); z++) {
			Contour qrCodeConfirmedShape = qrCodeShapeConfirms.get(z);
			String qrCodeResultText = qrCodeResultConfirms.get(z).getText();
			// den her afstand skal lidt styres af afstanden vi er til qr koden

			// finder lige midten af qr koden.
			QrPointsPoints.add(qrCodeConfirmedShape.getCenter(ratio));

			for (int i = 0; i < contours.size(); i++) {
				// check om det er en firkant som ikke ligger lige ved siden af
				// sig QR koden, der skal være en vis afstand
				Contour shape = contours.get(i);
				if (isShapeValidQRAndPosition(qrCodeConfirmedShape, shape, ratio)) {
					/*
					 * Check om qrkoden er til venstre eller om den er til
					 * højre.
					 */
					if (shape.getTlPoint(ratio).x > (qrCodeConfirmedShape.getTlPoint(ratio).x)) {

						/*
						 * Her checker vi hvilken position i listen som vores QR
						 * kode, vi har fundet, har.
						 */
						int nrWallMark = 0;
						for (Wallmark wallmark : Mathmagic.getArray()) {

							if (wallmark.getName().equals(qrCodeResultText)) {

								Point txtPoint = shape.getCenter(ratio);
								txtPoint = new Point(txtPoint.x * 2, txtPoint.y * 2);
								rightPoints.add(txtPoint);

								// System.out.println("name on right set");
								nameOfQROnTheRight = Mathmagic.getNameFromInt(nrWallMark + 1);
								break;
							}
							nrWallMark++;
						}

					} else if (shape.getTlPoint(ratio).x < (qrCodeConfirmedShape.getTlPoint(ratio).x)) {
						// System.out.println("found one left off");
						int nrWallMark = 0;
						for (Wallmark wallmark : Mathmagic.getArray()) {

							if (wallmark.getName().equals(qrCodeResultConfirms.get(z).getText())) {
								Point txtPoint = shape.getCenter(ratio);
								txtPoint = new Point(txtPoint.x * 2, txtPoint.y * 2);
								leftPoints.add(txtPoint);
								// System.out.println("name on left set");
								nameOfQROnTheLeft = Mathmagic.getNameFromInt(nrWallMark - 1);
								break;
							}
							nrWallMark++;
						}

					}

				}

			}
			// System.out.println("qr code name set");
			nameOfQRCodeFound = qrCodeResultConfirms.get(z).getText();
			distance += DistanceCalc.distanceFromCamera(qrCodeConfirmedShape.getWidth());
		}

		// find gennemsnit af punkter og returner 3 punkter, [left,middle,right]
		Point leftAverage = averagePoint(leftPoints);
		Point rightAverage = averagePoint(rightPoints);
		Point QrAverage = averagePoint(QrPointsPoints);

		Point[] points = { leftAverage, QrAverage, rightAverage };
		// hent de 3 gemte felters navne
		String[] qrNames = { nameOfQROnTheLeft, nameOfQRCodeFound, nameOfQROnTheRight };

		if (Double.isNaN(points[1].x) || qrNames[1] == null) {
			// System.out.println("points null");
			// System.out.println("not valid names
			// "+nameOfQROnTheRight+","+nameOfQRCodeFound+","+
			// nameOfQROnTheLeft);
			// System.out.println("not valid Points: -->
			// "+rightAverage+","+QrAverage+","+leftAverage);
			return null;
		}
		// System.out.println("Names: -->
		// "+nameOfQROnTheRight+","+nameOfQRCodeFound+","+ nameOfQROnTheLeft);
		// System.out.println("Points: -->
		// "+rightAverage+","+QrAverage+","+leftAverage);
		DetectedWallmarksAndNames data = new DetectedWallmarksAndNames(qrNames, points,
				distance / qrCodeShapeConfirms.size());

		return data;
	}

	public DetectedWallmarksAndNames markQrCodesV2(Contour qrCodeConfirmedShape, List<Contour> contours, Mat backUp,
			String qrSquareName, int ratio) {
		// find each qr code from the list, mark it on the image,
		// then find any squares that match the height of this QR code and
		// roughly size(width and height)
		// if any are found the determine which they probaly are and mark them
		// with name

		List<Point> leftPoints = new ArrayList<Point>();
		List<Point> rightPoints = new ArrayList<Point>();
		List<Point> QrPointsPoints = new ArrayList<Point>();

		double distance = 0;

		String nameOfQROnTheRight = null;
		String nameOfQROnTheLeft = null;

		QrPointsPoints.add(qrCodeConfirmedShape.getCenter(ratio));

		for (int i = 0; i < contours.size(); i++) {
			// check om det er en firkant som ikke ligger lige ved siden af
			// sig QR koden, der skal være en vis afstand
			Contour shape = contours.get(i);
			if (isShapeValidQRAndPosition(qrCodeConfirmedShape, shape, ratio)) {
				/*
				 * Check om qrkoden er til venstre eller om den er til højre.
				 */
				if (shape.getTlPoint(ratio).x > (qrCodeConfirmedShape.getTlPoint(ratio).x)) {

					/*
					 * Her checker vi hvilken position i listen som vores QR
					 * kode, vi har fundet, har.
					 */
					int nrWallMark = 0;
					for (Wallmark wallmark : Mathmagic.getArray()) {

						if (wallmark.getName().equals(qrSquareName)) {

							Point txtPoint = shape.getCenter(ratio);
							txtPoint = new Point(txtPoint.x, txtPoint.y);
							rightPoints.add(txtPoint);

							// System.out.println("name on right set");
							nameOfQROnTheRight = Mathmagic.getNameFromInt(nrWallMark + 1);
							break;
						}
						nrWallMark++;
					}

				} else if (shape.getTlPoint(ratio).x < (qrCodeConfirmedShape.getTlPoint(ratio).x)) {
					// System.out.println("found one left off");
					int nrWallMark = 0;
					for (Wallmark wallmark : Mathmagic.getArray()) {

						if (wallmark.getName().equals(qrSquareName)) {
							Point txtPoint = shape.getCenter(ratio);
							txtPoint = new Point(txtPoint.x, txtPoint.y);
							leftPoints.add(txtPoint);
							// System.out.println("name on left set");
							nameOfQROnTheLeft = Mathmagic.getNameFromInt(nrWallMark - 1);
							break;
						}
						nrWallMark++;
					}

				}

			}
			// System.out.println("qr code name set");
			distance += DistanceCalc.distanceFromCamera(qrCodeConfirmedShape.getWidth());
		}

		// find gennemsnit af punkter og returner 3 punkter, [left,middle,right]
		Point leftAverage = averagePoint(leftPoints);
		Point rightAverage = averagePoint(rightPoints);
		Point QrAverage = averagePoint(QrPointsPoints);

		Point[] points = { leftAverage, QrAverage, rightAverage };
		// hent de 3 gemte felters navne
		String[] qrNames = { nameOfQROnTheLeft, qrSquareName, nameOfQROnTheRight };

		if (Double.isNaN(points[1].x) || qrNames[1] == null) {

			return null;
		}
		DetectedWallmarksAndNames data = new DetectedWallmarksAndNames(qrNames, points, distance);

		return data;
	}

	private boolean isShapeValidQRAndPosition(Contour QRCodeShape, Contour currentContour, int ratio) {
		int minDistance = 100;
		if (currentContour.getTlPoint(ratio).x > (QRCodeShape.getTlPoint(ratio).x + minDistance)
				|| currentContour.getTlPoint(ratio).x < (QRCodeShape.getTlPoint(ratio).x - minDistance)) {

			// System.out.println("x position far enough for shape");
			// check om højde i billede ca passer med den bekræfte QR
			// kodes position og hver firkant, dem der matcher checker
			// vi størrelse
			// check if 10% under and 10% above
			if (currentContour.getTlPoint(ratio).y > (QRCodeShape.getTlPoint(ratio).y * 0.5)
					&& currentContour.getTlPoint(ratio).y < (QRCodeShape.getTlPoint(ratio).y * 1.5)) {
				// System.out.println("Y Position is good!");
				// check om størrelse passer ca indenfor ~ 20% mindre og
				// større?
				// den kunne være en del mindre, da der er den der
				// alcove
				Rect r = currentContour.getBoundingRect(ratio);
				if (r.area() > (QRCodeShape.getBoundingRect(ratio).area() * 0.8)
						&& r.area() < (QRCodeShape.getBoundingRect(ratio).area() * 1.2)) {
					// System.out.println("Area is good!");
					// check om formen ca passer med et A4 højde er
					// længere end bredde
					if (r.height > r.width) {
						// System.out.println("Shape is good!");
						return true;
					}
				}
			}

		}
		return false;
	}

	private Point averagePoint(List<Point> Points) {
		Point preAverage = new Point();
		for (Point point : Points) {
			preAverage = new Point(preAverage.x + point.x, preAverage.y + point.y);

		}
		return new Point(preAverage.x / Points.size(), preAverage.y / Points.size());
	}

	public Mat equalizeHistogramBalance(Mat frame) {
		Mat dst = new Mat();
		Imgproc.equalizeHist(frame, dst);
		return dst;
	}

	public Mat drawShapes(List<Shape> shapes, Mat backUp) {

		for (int i = 0; i < shapes.size(); i++) {

			// draw the shape and write the result in the area
			// Red for squares with 5 edges
			Shape shape = shapes.get(i);
			Scalar color = new Scalar(255, 0, 0);
			Imgproc.rectangle(backUp, shape.getTlPoint(), shape.getBrPoint(), color, 3);

		}
		return backUp;
	}

	public Mat drawLine(Point point, Point point2, Mat backUp, Scalar color) {

		Imgproc.line(backUp, point, point2, color);

		return backUp;
	}

	public Mat drawShape(Contour contour, Mat image, int ratio) {
		Rect r = contour.getBoundingRect(ratio);

		Scalar color = new Scalar(0, 255, 0);
		Imgproc.rectangle(image, r.tl(), r.br(), color, 3);
		return image;
	}

	public Mat putText(String string, Point position, Mat image) {
		Scalar color = new Scalar(0, 255, 0);
		Imgproc.putText(image, string, position, 5, 2, color);
		return image;
	}

	public Mat warpImage(Mat mat) {
		Mat qrMat = new Mat();
		qrMat = Mat.zeros(560, 400, CvType.CV_32S);

		MatOfPoint2f point1 = new MatOfPoint2f();
		MatOfPoint2f point2 = new MatOfPoint2f();
		List<Point> lp = new ArrayList<>();
		List<Point> lp2 = new ArrayList<>();
		Point t1 = new Point(0, 0);
		Point t2 = new Point(0, qrMat.height());
		Point t3 = new Point(qrMat.width(), qrMat.height());
		Point t4 = new Point(qrMat.width(), 0);

		// noget med lavest Y værdi
		Point p1 = new Point(0, 0);
		Point p2 = new Point(0, mat.height());
		Point p3 = new Point(mat.width(), mat.height());
		Point p4 = new Point(mat.width(), 0);
		lp2.add(t1);
		lp2.add(t2);
		lp2.add(t3);
		lp2.add(t4);
		lp.add(p1);
		lp.add(p2);
		lp.add(p3);
		lp.add(p4);

		point1.fromList(lp);
		point2.fromList(lp2);

		Mat dst = new Mat();
		Mat warp = Imgproc.getPerspectiveTransform(point1, point2);

		Imgproc.warpPerspective(mat, dst, warp, qrMat.size());

		return dst;
	}

	public List<BufferedImage> warp(Mat inputMat, List<Contour> contours, int ratio) {

		List<BufferedImage> outputs = new ArrayList<>();

		for (Contour contour : contours) {
			List<Point> points = contour.getCorners(ratio);
			// List<Point> points = contour.getBoundingRectPoints(ratio);

			Mat startM = Converters.vector_Point2f_to_Mat(points);

			int resultWidth = startM.width() + 100;
			int resultHeight = startM.height() + 100;

			Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC4);

			// determine which of the points has the lowest Y
			int i = 0;
			int index = 100;
			double testdistance = 100000;
			double y = 100000;
			for (Point point : points) {

				double dx = point.x;
				double dy = point.y;
				double distance = Math.sqrt(dx * dx + dy * dy);

				if (distance < testdistance || point.y < y) {
					index = i;
					testdistance = distance;
					y = point.y;
					// System.out.println(distance);
				}
				i++;

			}
			// System.out.println(index);

			if (index == 0 || index == 1) {
				Point ocvPOut1 = new Point(0, 0);
				Point ocvPOut2 = new Point(0, resultHeight);
				Point ocvPOut3 = new Point(resultWidth, resultHeight);
				Point ocvPOut4 = new Point(resultWidth, 0);
				List<Point> dest = new ArrayList<Point>();
				dest.add(ocvPOut1);
				dest.add(ocvPOut2);
				dest.add(ocvPOut3);
				dest.add(ocvPOut4);
				Mat endM = Converters.vector_Point2f_to_Mat(dest);
				Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

				Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight),
						Imgproc.INTER_CUBIC);
				outputs.add(toBufferedImage(outputMat));

			} else {
				Point ocvPOut4 = new Point(0, 0);
				Point ocvPOut3 = new Point(0, resultHeight);
				Point ocvPOut2 = new Point(resultWidth, resultHeight);
				Point ocvPOut1 = new Point(resultWidth, 0);
				List<Point> dest = new ArrayList<Point>();
				dest.add(ocvPOut1);
				dest.add(ocvPOut2);
				dest.add(ocvPOut3);
				dest.add(ocvPOut4);
				Mat endM = Converters.vector_Point2f_to_Mat(dest);
				Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

				Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight),
						Imgproc.INTER_CUBIC);
				outputs.add(toBufferedImage(outputMat));

			}

		}

		return outputs;
	}

	public Mat drawLinesBetweenContourPoints(Contour contour, Mat image, int ratio, Scalar color) {
		List<Point> points = contour.getCorners(ratio);
		int n = points.size();
		for (int i = 0; i < n; i++) {
			drawLine(points.get(i), points.get((i + 1) % n), image, color);
		}

		return image;
	}

	public Mat drawLinesBetweenBoundingRectPoints(Contour contour, Mat image, int ratio, Scalar color) {
		List<Point> points = contour.getBoundingRectPoints(ratio);
		int n = points.size();
		for (int i = 0; i < n; i++) {
			drawLine(points.get(i), points.get((i + 1) % n), image, color);
		}

		return image;
	}

	public static boolean isContourSquare(MatOfPoint thisContour) {

	    Rect ret = null;

	    MatOfPoint2f thisContour2f = new MatOfPoint2f();
	    MatOfPoint approxContour = new MatOfPoint();
	    MatOfPoint2f approxContour2f = new MatOfPoint2f();

	    thisContour.convertTo(thisContour2f, CvType.CV_32FC2);

	    Imgproc.approxPolyDP(thisContour2f, approxContour2f, 2, true);

	    approxContour2f.convertTo(approxContour, CvType.CV_32S);

	    if (approxContour.size().height == 4) {
	        ret = Imgproc.boundingRect(approxContour);
	    }

	    return (ret != null);
	}
	
	public static List<MatOfPoint> getSquareContours(List<MatOfPoint> contours) {

	    List<MatOfPoint> squares = null;

	    for (MatOfPoint c : contours) {

	        if (isContourSquare(c)) {
	      
	            if (squares == null)
	                squares = new ArrayList<MatOfPoint>();
	            squares.add(c);
	            
	        } 
	    }

	    return squares;
	}

	
	public Mat calibrateCamera(Mat frame) {
		Mat cameraMatrix = new Mat(3, 3, 5);
		cameraMatrix.put(0, 0, 1.1220e03);
		cameraMatrix.put(0, 1, 0.0);
		cameraMatrix.put(0, 2, 644.4117);
		cameraMatrix.put(1, 0, 0.0);
		cameraMatrix.put(1, 1, 1.1198e03);
		cameraMatrix.put(1, 2, 343.6528);
		cameraMatrix.put(2, 0, 0.0);
		cameraMatrix.put(2, 1, 0.0);
		cameraMatrix.put(2, 2, 1);
		
		Mat dst = new Mat();
		Mat distCoeffs = new Mat(1,4,5);
		distCoeffs.put(0, 0, -0.5675);
		distCoeffs.put(0, 1, 0.4046);
		distCoeffs.put(0, 2, 0);
		distCoeffs.put(0, 3, 0);
		
		Imgproc.undistort(frame, dst, cameraMatrix, distCoeffs);
		
		return dst;
	}

	
	
	public List<Contour> findCircles(Mat frame) {

		// Here contours are stored, we will check each one to see if it matches
		List<MatOfPoint> contours_1 = new ArrayList<MatOfPoint>();
		Mat hierarchy_1 = new Mat();
		Imgproc.findContours(frame, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

		List<Contour> contours = new ArrayList<>();

		// Detecting shapes in the contours
		for (int i = 0; i < contours_1.size(); i++) {
			MatOfPoint2f contour = new MatOfPoint2f(contours_1.get(i).toArray());

			MatOfPoint2f approxCurve = new MatOfPoint2f();
			double epsilon = Imgproc.arcLength(contour, true) * 0.05;

			// we wanna se if a contour is a square, or has one or more edges so
			// we save them.
			Imgproc.approxPolyDP(contour, approxCurve, epsilon, true);
				if (approxCurve.total() > 3) {
					Contour contour1 = new Contour(contour, approxCurve);
					contours.add(contour1);
				}
		}

		return contours;
	}

}
