package dk.gruppe5.test;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import dk.gruppe5.model.Contour;

public class CircleTest {

	public boolean findHoughCircles(Mat src, Mat destination) {

		int iCannyUpperThreshold = 100;
		int iMinRadius = 40; // ????
		int iMaxRadius = 350;
		int iAccumulator = 350;
		int iLineThickness = 10;


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
				
				Scalar pointsclr = new Scalar(255,255,255);
				Scalar radiussclr = new Scalar(255,255,255);

				Imgproc.circle(destination, pt, radius, pointsclr, iLineThickness);
				Imgproc.circle(destination, pt, 3, radiussclr, iLineThickness);
			}
			return true;
		} return false;
	}
	
	public boolean isContourInsideContour(Contour inner, Contour outer) {
		return false;
	}
}
