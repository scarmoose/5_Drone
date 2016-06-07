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
		
		Vector2 p1 = new Vector2(0,0);
		Vector2 p2 = new Vector2(1,2);
		Vector2 p3 = new Vector2(0,4);
		
		Vector2[] points = new Vector2[]{p1, p2, p3};
		Circle c1;
		Circle c2;
		
		c1 = pos.getCircleFromPointsWithAngle(p1, p2, (float) Math.toDegrees(0.18));
		System.out.println(c1);
		c2 = pos.getCircleFromPointsWithAngle(p2, p3, (float) Math.toDegrees(0.2));
		System.out.println(c2);
		
		Point p = pos.getPosition(c1, c2, points);
		System.out.println(p);
	}

}
