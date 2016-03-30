package dk.gruppe5.drone.openCV;

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
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import dk.gruppe5.drone.yaDroneFeed.Values_cam;
import dk.gruppe5.shared.opticalFlowData;
import dk.gruppe5.shared.templateMatch;

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
		
		Imgproc.GaussianBlur(sourceImage, destImage, new Size(5.0,5.0),(double)2.0);
		
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
		// First we grayscale the picture
		Mat imageGray = grayImg;
		Mat imageCny = new Mat();
		// Imgproc.cvtColor(img, imageGray, Imgproc.COLOR_BGR2GRAY);
		// martin Webcam settings

		// Imgproc.Canny(imageGray, imageCny, 20, 150, 3, true);

		Imgproc.Canny(imageGray, imageCny, Values_cam.getCanTres1(), Values_cam.getCanTres2(), Values_cam.getCanAp(),
				true);
		// Drone webcame settings?????
		// Imgproc.Canny(imageGray, imageCny, 10, 100, 3, true);
		// Thomas Webcam settings
		// Imgproc.Canny(imageGray, imageCny, 20, 200, 3, true);

		return imageCny;
	}
/**
 * Dosent work as intended decrapted or whatever.....
 * @param input
 * @return
 */
	public Mat findContours(Mat input) {
		List<MatOfPoint> contours_1 = new ArrayList<MatOfPoint>();
		Mat hierarchy_1 = new Mat();
		//Imgproc.findContours(input, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.findContours(input, contours_1, hierarchy_1, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_NONE);
		// draw contours?
		Random rn = new Random();
		Mat standIn = new Mat();
		Imgproc.cvtColor(input, standIn, Imgproc.COLOR_BayerBG2RGB);
		
		//Detecting shapes in the contours
		for (int i = 0; i < contours_1.size(); i++) {
			MatOfPoint2f contour = new MatOfPoint2f(contours_1.get(i).toArray());
			
			MatOfPoint2f approxCurve = new MatOfPoint2f();
			double epsilon = Imgproc.arcLength(contour, true)*0.02;
			//houghcircles houghlines
			
//			if(contours_1.get(i).size().area() >10 && Imgproc.isContourConvex(contours_1.get(i))) { // Minimum size allowed for consideration
//		     
//				Scalar color = new Scalar(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));
//				Imgproc.drawContours(standIn, contours_1, i, color, 3);
//				
//		     }
			Scalar color = new Scalar(rn.nextInt(255), rn.nextInt(255), rn.nextInt(255));
			Imgproc.drawContours(standIn, contours_1, i, color, 3);
//			we wanna se if a contour is a square.
			//Imgproc.approxPolyDP(contour, approxCurve, epsilon, true);
//			
//			MatOfPoint approxf1 = new MatOfPoint();
//			approxCurve.convertTo(approxf1, CvType.CV_32S);
//			
//			System.out.println(approxf1.width()*approxf1.height());
			//if(	(approxf1.width()*approxf1.height()) == 4 && Math.abs(Imgproc.contourArea(approxf1)) > 10 && Imgproc.isContourConvex(approxf1) ){
				
//			System.out.println("I found shape Huurr durr");
//			
//				
//			}
			
		}

		return standIn;
	}
	
	
	public Mat templateMatching(Mat input){
		
		BufferedImage img2 = null;

		try 
		{
		    img2 = ImageIO.read(new File("pics/Airfield.jpg")); 
		} 
		catch (IOException e) 
		{
		    e.printStackTrace();
		}
		
		Mat templateImg = bufferedImageToMat(img2);
		//System.out.println("tempCols:"+templateImg.cols());
		//System.out.println("frameCols:"+input.cols());
		
		int match_method = Imgproc.TM_CCOEFF;
		List<templateMatch> matches = new ArrayList<>();
		//Initial rezise of image to be a little smaller than the frame image.
		templateImg = blur(templateImg);
	
		Imgproc.resize(templateImg, templateImg, new Size(input.width(),input.height()));
		
		//gray and to canny??
//		input = toGrayScale(input);
//		input = toCanny(input);
//		
//		templateImg = toGrayScale(templateImg);
//		templateImg = toCanny(templateImg);
		
		//
		int i = 0;
		//As long as the size of template image is bigger than a 10th of the image, we will look at finding the best match
		while(templateImg.width()> input.width()/30 || templateImg.height() > input.height()/30){
		    // / Create the result matrix
	        int result_cols = input.cols() - templateImg.cols() + 1;
	        int result_rows = input.rows() - templateImg.rows() + 1;
	        Mat result = new Mat(result_rows, result_cols, CvType.CV_32FC1);

	        // / Do the Matching and Normalize
	        Imgproc.matchTemplate(input, templateImg, result, match_method);
	        //Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
	        // / Localizing the best match with minMaxLoc
	        MinMaxLocResult mmr = Core.minMaxLoc(result);
	        Point matchLoc;
	        if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED) {
	            matchLoc = mmr.minLoc;
	         
	        } else {
	            matchLoc = mmr.maxLoc;
	            //System.out.println(mmr.maxVal);
	        
	        }

	        // / Show me what you got
	        //System.out.println(matchLoc);
	        
	        i++;
	        //System.out.println(templateImg.width()+":"+templateImg.height());
	        templateMatch tempMatch = new templateMatch(matchLoc,templateImg.width(),templateImg.height(),mmr.maxVal);
	        matches.add(tempMatch);
	        
	        try {
	            // retrieve image
	            BufferedImage bi = toBufferedImage(templateImg);
	            File outputfile = new File("saveImg"+i+".png");
	            ImageIO.write(bi, "png", outputfile);
	        } catch (IOException e) {
	          e.printStackTrace();
	        }
	        Imgproc.resize(templateImg, templateImg, new Size(input.width()-16*i,input.height()-12*i));
	        
		}
		//System.out.println(matches.size());
//		double highestValue = 0;
//		int matchNr = 0;
//		for(int z = 0; z < matches.size(); z++){
//			
//			
//			if(matches.get(z).getMatchValue() > highestValue){
//				highestValue =matches.get(z).getMatchValue();
//				matchNr = z;
//			}
//			
//		}
		//input = toColor(input);
//		//System.out.println(matchNr);
//		//Imgproc.rectangle(input, matchLoc, new Point(matchLoc.x + templateImg.cols(), matchLoc.y + templateImg.rows()), new Scalar(0, 255, 0));
//		Imgproc.rectangle(input, matches.get(matchNr).getCoordinate(), new Point(matches.get(matchNr).getCoordinate().x + matches.get(matchNr).getPicWidth(), 
//								matches.get(matchNr).getCoordinate().y + matches.get(matchNr).getPicHeight()), new Scalar(0, 255, 0));
		System.out.println("-----------");
		for(int x = 0; x < matches.size(); x++){
			
			if(matches.get(x).getMatchValue() > 5.1E8){
			Imgproc.rectangle(input, matches.get(x).getCoordinate(), new Point(matches.get(x).getCoordinate().x + matches.get(x).getPicWidth(), 
					matches.get(x).getCoordinate().y + matches.get(x).getPicHeight()), new Scalar(0, 255, 0));
			System.out.println(matches.get(x).getMatchValue());
			}
			
			
		}
		System.out.println("-----------");


      
     
        
        
		return input;
		
	}
	
	public Mat toColor(Mat input){
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
					 * By calculating an average in the distance between points
					 * in the picture, we can use this to remove Unwanted
					 * vectors, for example vectors that is longer than a
					 * certain threshold in the picture
					 * 
					 * We could also use a an estimation on the different
					 * vectors to see if the drone has moved or a single object
					 * in the frame has moved we can do this by looking at the
					 * average movement, if an area of vectors are moving much
					 * longer than the rest of the frame vectors, there is
					 * likely to be an object moving in that area.
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

}
