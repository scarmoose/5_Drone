package dk.gruppe5.test;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class CircleTest {

	public void findHoughCircles(Mat src, Mat destination) {
		
		//for webcam
		int iCannyUpperThreshold = 120;
		int iMinRadius = 40; // ????
		int iMaxRadius = 350;
		int iAccumulator = 300;
		int iLineThickness = 2;
		
		//for drone:
//		int iCannyUpperThreshold = 30;
//		int iMinRadius = 20; // ????
//		int iMaxRadius = 250;
//		int iAccumulator = 200;
//		int iLineThickness = 2;		

		Mat dst = new Mat(src.width(), src.height(), 5);
		
		System.err.println("Hello");
		
		Imgproc.HoughCircles(src, dst, Imgproc.CV_HOUGH_GRADIENT, 
				2.0, src.rows() / 8, iCannyUpperThreshold, iAccumulator, 
				iMinRadius, iMaxRadius);
		
		System.err.println("Done");
		
		int num = dst.cols();
		
		if (num > 0) {
			System.out.println("- Fundne cirkler: "+num);
			for (int i = 0; i < num; i++) 
			{
				double vCircle[] = dst.get(0,i);

				if (vCircle == null) {
					System.err.println("No circles found");
					break;
				} else System.err.println("Circle apparently found");
				
				Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
				int radius = (int)Math.round(vCircle[2]);

				// draw the found circle

				Imgproc.circle(destination, pt, radius, new Scalar(0,255,0), iLineThickness);
				Imgproc.circle(destination, pt, 3, new Scalar(0,0,255), iLineThickness);
			}
		}
	}
}
