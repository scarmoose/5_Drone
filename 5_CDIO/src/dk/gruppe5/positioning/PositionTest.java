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
	
	IPosition pos;
	Mathmagic mm;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new PositionTest().test2();
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
		
		System.out.println(pos.distance(p1, p2));
		System.out.println("float max: "+Float.MAX_VALUE);
		System.out.println("double max: "+Double.MAX_VALUE);
		System.out.println("integer max: "+Integer.MAX_VALUE);
		System.out.println("short max: "+Short.MAX_VALUE);
		System.out.println("long max: "+Long.MAX_VALUE);
		
	}
	
	public void test1() {
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
		
		Vector2[] vectors = pos.getIntersectionVectors(c1, c2);
		for(Vector2 v : vectors) 
			System.out.println(v);
		
		Vector2 vector = pos.getPositionVector(c1, c2, points);
		System.out.println("Position: "+vector);
	}

}
