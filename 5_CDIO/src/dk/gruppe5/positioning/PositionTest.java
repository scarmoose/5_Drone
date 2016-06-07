/**
 * 
 */
package dk.gruppe5.positioning;

import java.awt.Point;

import dk.gruppe5.controller.Mathmagic;

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
		new PositionTest().test();
	}
	
	public void test() {
		mm = new Mathmagic();
		pos = new Position();
//		Point p1 = mm.map.get("W00.04");
//		Point p2 = mm.map.get("W01.00");
//		Point p3 = mm.map.get("W01.01");
		
		Point p1 = new Point(0,0);
		Point p2 = new Point(1,2);
		Point p3 = new Point(0,4);
		
		Point[] points = new Point[]{p1, p2, p3};
		Circle c1;
		Circle c2;
		
		c1 = pos.getCircleFromPoints(p1, p2, 72);
		System.out.println(c1);
		c2 = pos.getCircleFromPoints(p2, p3, 84);
		System.out.println(c2);
		
		Point p = pos.getPosition(c1, c2, points);
		System.out.println(p);
	}

}
