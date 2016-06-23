package dk.gruppe5.positioning;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.model.Circle;
import dk.gruppe5.model.DPoint;

/**
 * @author Thomas
 *
 */
public class PositionTest {

	Position pos;
	Mathmagic mm;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new PositionTest().test4();
	}
	
	public void test5() {
		pos = new Position();
		DPoint 	v1 = new DPoint(0, 1),
				v2 = new DPoint(0.5, 12);
		
		float angle_rad = pos.getSignedAngleBetweenVectors(v1, v2);
		float angle_deg = (float) Math.toDegrees(angle_rad);
		System.out.println("angle: "+angle_deg);
		
		
	}
	
	public void test4() {
		Mat cameraMatrix = new Mat(3, 3, 5);
		printMat(cameraMatrix);
		cameraMatrix.put(0, 0, 1.1220e03);
		cameraMatrix.put(0, 1, 0.0);
		cameraMatrix.put(0, 2, 644.4117);
		cameraMatrix.put(1, 0, 0.0);
		cameraMatrix.put(1, 1, 1.1198e03);
		cameraMatrix.put(1, 2, 343.6528);
		cameraMatrix.put(2, 0, 0.0);
		cameraMatrix.put(2, 1, 0.0);
		cameraMatrix.put(2, 2, 1);
		printMat(cameraMatrix);
		Mat distCoeffs = new Mat(1,4,5);
		distCoeffs.put(0, 0, -0.5675);
		distCoeffs.put(0, 1, 0.4046);
		distCoeffs.put(0, 2, 0);
		distCoeffs.put(0, 3, 0);
		printMat(distCoeffs);
	}
	
	public void printMat(Mat m) {
		System.out.println("Mat: "+m.dump());
	}

	public void test3() {

		mm = new Mathmagic();
		pos = new Position();

		String[] testNames = {"W02.02","W02.03","W02.04"};

		org.opencv.core.Point[] testPoints = {new org.opencv.core.Point(68.0, 142.0),new org.opencv.core.Point(352.25, 149.25),new org.opencv.core.Point(622.5, 144.5)};

		System.out.println("position hentet er: "+pos.getPositionFromPoints(testNames, testPoints[0], testPoints[1], testPoints[2]));

	}

	public void test2() {

		mm = new Mathmagic();
		pos = new Position();
		int x = 39;
		int y = 84;
		org.opencv.core.Point p1 = new org.opencv.core.Point(x, y); 
		org.opencv.core.Point p2 = new org.opencv.core.Point(x+=3, y+=4);
		System.out.println("p1.x: "+p1.x+", p1.y: "+p1.y);
		System.out.println("p2.x: "+p2.x+", p2.y: "+p2.y);

	}

	public void test1() {

		mm = new Mathmagic();
		pos = new Position();

		DPoint p1 = new DPoint(0,0);
		DPoint p2 = new DPoint(1,2);
		DPoint p3 = new DPoint(0,4);

		DPoint[] points = new DPoint[]{p1, p2, p3};
		Circle c1;
		Circle c2;

		c1 = pos.getCircleFromPointsWithAngle(p1, p2, (float) Math.toDegrees(0.18));
		System.out.println(c1);
		c2 = pos.getCircleFromPointsWithAngle(p2, p3, (float) Math.toDegrees(0.2));
		System.out.println(c2);

		DPoint[] vectors = pos.getIntersectionPoints(c1, c2);
		for(DPoint v : vectors)  {
			//missing implementation
		}

		DPoint vector = pos.getPositionPoint(c1, c2, points);
		System.out.println("Position: "+vector);

	}


}
