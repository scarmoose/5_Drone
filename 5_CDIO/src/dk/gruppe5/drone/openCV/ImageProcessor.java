package dk.gruppe5.drone.openCV;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

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

public class ImageProcessor {
	
	public BufferedImage toBufferedImage(Mat matrix){
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( matrix.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = matrix.channels()*matrix.cols()*matrix.rows();
		byte [] buffer = new byte[bufferSize];
		matrix.get(0,0,buffer); // get all the pixels
		BufferedImage image = new BufferedImage(matrix.cols(),matrix.rows(), type);
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
	
	public Mat blur(Mat input, int numberOfTimes){
		Mat sourceImage = new Mat();
		Mat destImage = input.clone();
		for(int i=0;i<numberOfTimes;i++){
			sourceImage = destImage.clone();
			Imgproc.blur(sourceImage, destImage, new Size(3.0, 3.0));
		}
		return destImage;
	}
	
	public Mat erode(Mat input, int elementSize, int elementShape){
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.erode(input,outputImage, element);
		return outputImage;
	}

	

	public Mat dilate(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.dilate(input,outputImage, element);
		return outputImage;
	}

	public Mat open(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_OPEN, element);
		return outputImage;
	}

	public Mat close(Mat input, int elementSize, int elementShape) {
		Mat outputImage = new Mat();
		Mat element = getKernelFromShape(elementSize, elementShape);
		Imgproc.morphologyEx(input,outputImage, Imgproc.MORPH_CLOSE, element);
		return outputImage;
	}
	
	private Mat getKernelFromShape(int elementSize, int elementShape) {
		return Imgproc.getStructuringElement(elementShape, new Size(elementSize*2+1, elementSize*2+1), new Point(elementSize, elementSize) );
	}
	
	public BufferedImage toGrayScale(BufferedImage input){
		
		byte[] data = ((DataBufferByte) input.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(input.getHeight(), input.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);

        Mat mat1 = new Mat(input.getHeight(),input.getWidth(),CvType.CV_8UC3);
        Imgproc.cvtColor(mat, mat1, Imgproc.COLOR_RGB2GRAY);

        byte[] data1 = new byte[mat1.rows() * mat1.cols() * (int)(mat1.elemSize())];
        mat1.get(0, 0, data1);
        BufferedImage image1 = new BufferedImage(mat1.cols(),mat1.rows(), BufferedImage.TYPE_BYTE_GRAY);
        image1.getRaster().setDataElements(0, 0, mat1.cols(), mat1.rows(), data1);

		
		
		return image1;
		
	}
	
	
	

	public Mat toCanny(Mat img) {
		//First we grayscale the picture
		Mat imageGray = new Mat();
		Mat imageCny = new Mat();
		Imgproc.cvtColor(img, imageGray, Imgproc.COLOR_BGR2GRAY);		
		//martin Webcam settings
		Imgproc.Canny(imageGray, imageCny, 50, 100, 3, true);
		//Drone webcame settings?????
		//Imgproc.Canny(imageGray, imageCny, 10, 100, 3, true);
		//Thomas Webcam settings
		//Imgproc.Canny(imageGray, imageCny, 20, 200, 3, true);

	    
		return imageCny;
	}

	public Mat opticalFlow(Mat frameOne, Mat frameTwo) {
		// TODO Auto-generated method stub
		//Først finder vi de gode features at tracker
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
//		Imgproc.goodFeaturesToTrack(frameOne, corners1, 100, 0.1, 30);
//		Imgproc.goodFeaturesToTrack(frameTwo, corners2, 100, 0.1, 30);
		Imgproc.goodFeaturesToTrack(frameOne, corners1, 500, 0.1, 10);
		Imgproc.goodFeaturesToTrack(frameTwo, corners2, 500, 0.1, 10);
		//Now that we have found good features and added them to the corners1 and 2
		//we add colour back to the picture so that we can draw lovely lines
		Imgproc.cvtColor(frameOne, standIn, Imgproc.COLOR_BayerBG2RGB);	
		
		 //This draws the good features that we have found in the 2 frames.
		for(int x = 0; x < corners1.width(); x++){
			for(int y = 0; y < corners1.height(); y++){
				Imgproc.circle(standIn, new Point(corners1.get(y, x)), 3, new Scalar(0,250,0),2);
				Imgproc.circle(standIn, new Point(corners2.get(y, x)), 7, new Scalar(200,0,200),1);
				
			}
		}
		
		MatOfByte status = new MatOfByte();
		MatOfFloat err = new MatOfFloat();
		MatOfPoint2f corners1f = new MatOfPoint2f(corners1.toArray());
		MatOfPoint2f corners2f = new MatOfPoint2f(corners2.toArray());
		Video.calcOpticalFlowPyrLK(frameOne, frameTwo, corners1f, corners2f, status, err);
		Double averageUncalc = 0.0;
		for(int i = 0; i < corners1f.height(); i++){
			Point startP = new Point(corners1f.get(i, 0));
			Point endP = new Point(corners2f.get(i, 0));
			Double distance = Math.sqrt((startP.x-endP.x)*(startP.x-endP.x) + (startP.y-endP.y)*(startP.y-endP.y));
			//System.out.println("Distance:"+distance);
			averageUncalc = averageUncalc + distance;
			//System.out.println(err.get(i, 0)[0]);
			
		
		}
		System.out.println("Average:"+averageUncalc/corners1f.height());
		averageUncalc = averageUncalc/corners1f.height();
		
		for(int i = 0; i < corners1f.height(); i++){
			
		if(err.get(i, 0)[0]< 10){
			//Imgproc.line(standIn,startP,endP,new Scalar(0,250,0),5);
			Point startP = new Point(corners1f.get(i, 0));
			Point endP = new Point(corners2f.get(i, 0));
			Double distance = Math.sqrt((startP.x-endP.x)*(startP.x-endP.x) + (startP.y-endP.y)*(startP.y-endP.y));
			if(distance < 5*averageUncalc){
				Imgproc.arrowedLine(standIn,startP,endP,new Scalar(0,250,0));
			}
			
			//System.out.println("Point start:"+startP.x+","+startP.y+"-->"+endP.x+","+endP.y);
		}
		}
		//System.out.println(corners1f.height());
		//System.out.println(corners2f.height());
//		for(int x = 0; x <= status.height(); x++){
			
			
				//Imgproc.circle(standIn, new Point(corners1.get(y, x)), 3, new Scalar(0,250,0),5);
				
//			}
		
		
		return standIn;
	}

}
