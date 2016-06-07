/**
 * 
 */
package dk.gruppe5.positioning;

import java.awt.Point;

/**
 * @author Thomas
 *
 */
public class PositionTest {
	
	Position pos;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PositionTest().test();
	}
	
	public void test() {
		
		pos = new Position();
		Point p1 = new Point(0, 10);
		Point p2 = new Point(0, 0);
		Point p3 = new Point(10, 0);
		Point[] points = new Point[]{p1, p2, p3};
		Circle c1;
		Circle c2;
		
		c1 = pos.getCircle(p1, p2, 360);
		c2 = pos.getCircle(p2, p3, 360);
		
		Point p = pos.getPosition(c1, c2, points);
		System.out.println(p);
	}

}
