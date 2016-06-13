/**
 * 
 */
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
	
	public void test4() {
		Mat k = new Mat(3, 3, 0);
		printMat(k);
		k.get(0, 0)[0] = 2;
		printMat(k);
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

		//		Point p1 = mm.map.get("W00.04");
		//		Point p2 = mm.map.get("W01.00");
		//		Point p3 = mm.map.get("W01.01");

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
			//System.out.println(v);
		}

		DPoint vector = pos.getPositionPoint(c1, c2, points);
		System.out.println("Position: "+vector);

	}


}
