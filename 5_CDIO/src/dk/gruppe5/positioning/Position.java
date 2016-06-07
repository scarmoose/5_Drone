package dk.gruppe5.positioning;

import java.awt.Point;

public class Position {
	
	private Vector2 currentPos;
	final static float TOTAL_PIXELS = 720.0f;
	final static float TOTAL_ANGLE = 67.7f;
	
	public Position() {}
	


	public float getAngleInDegreesFromPixelsOccupied(int pixels) {
		return TOTAL_ANGLE * (pixels/TOTAL_PIXELS);
	}


	public float getDistanceToPoints(float angle, float distanceBetweenPoints) {
		return (float) ((distanceBetweenPoints/2) / Math.tan(Math.toRadians(angle)));
	}

	/**
	 * 
	 * @param c1 Circle 1
	 * @param c2 Circle 2
	 * @param startPoints Points that was used to create the circles
	 * @return
	 */

	public Point getPosition(Circle c1, Circle c2, Vector2[] startPoints) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		Point[] points = Vector2.getPointArray(cci.getIntersectionVectors());
		if(points != null && points.length > 0) {
			if(points.length == 1) {
				System.out.println("Der var 1 point");
				return new Point((int) points[0].x, (int) points[0].y);
			}
			if(points.length == 2) {
				System.out.println("Der var 2 points");
				for(Point p : points) {
					if(!p.equals(startPoints[0]) && !p.equals(startPoints[1])
							&& !p.equals(startPoints[2]))
						return p;
				}
			}
		}
		return null;
	}
	
	public Vector2[] getIntersectionVectors(Circle c1, Circle c2) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		Vector2[] points = cci.getIntersectionVectors();
		if(points != null && points.length > 0) {
			return points;
		}
		return null;
	}
	
	public Vector2 getPositionVector(Circle c1, Circle c2, Vector2[] startPoints) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		Vector2[] vectors = cci.getIntersectionVectors();
		if(vectors != null && vectors.length > 0) {
			if(vectors.length == 1) {
				System.out.println("Der var 1 point");
				return new Vector2(startPoints[0].x, startPoints[0].y);
			}
			if(vectors.length == 2) {
				System.out.println("Der var 2 points");
				for(Vector2 v : vectors) {
					if(!isVectorAlmostEqualToOneOfThePoints(v, startPoints, 2))
						return v;
				}
			}
		}
		return null;
	}
	
	public boolean isVectorAlmostEqualToOneOfThePoints(Vector2 v, Vector2[] vectors, float thresholdPercent) {
		for(Vector2 vector : vectors) {
			double x = vector.x;
			double y = vector.y;
			if((v.x <= x * (1 + thresholdPercent/100.0) && v.x >= x * (1 - thresholdPercent/100.0)
					&& v.y <= y * (1 + thresholdPercent/100.0) && v.y >= y * (1 - thresholdPercent/100.0))) {
				return true;
			}
		}
		return false;
	}
	
	

	public Circle getCircleFromPoints(Vector2 p1, Vector2 p2, int pixelsOccupiedByObject) {
		float alpha = getAngleInDegreesFromPixelsOccupied(pixelsOccupiedByObject);
		return getCircleFromPointsWithAngle(p1, p2, alpha);
	}

	
	/**
	 * This creates a circle with the two observed points, and the observer points on the peripheral line
	 * @param p1
	 * @param p2
	 * @param angle
	 * @return
	 */
	public Circle getCircleFromPointsWithAngle(Vector2 p1, Vector2 p2, float angle) {
		double 	x1 = p1.x,
						y1 = p1.y,
						x2 = p2.x,
						y2 = p2.y;
		
		double alpha = Math.toRadians(angle);
		double a = Math . sqrt ( Math . pow ( x1 - x2 ,2) + Math . pow ( y1 - y2 , 2) ) ;
		double t1 = a * a ;
		double t2 = Math . sin ( alpha ) ;
		double t3 = t2 * t2 ;
		double t7 = Math . sqrt (1.0 / t3 * t1 - t1 ) ;
		double t8 = y1 - y2 ;
		double t10 = t8 * t8 ;
		double t12 = x1 - x2 ;
		double t13 = t12 * t12 ;
		double t15 = Math . sqrt (( t10 + t13 ) ) ;
		double x_c = - t8 / t15 * t7 / 2.0 + x1 / 2.0 + x2 / 2.0;
		double y_c = t12 / t15 * t7 / 2.0 + y1 / 2.0 + y2 / 2.0;
		
		Vector2 center = new Vector2(x_c, y_c);
		
		System.out.println("alpha: "+ alpha);
		double radius = (1.0/2) * a/Math.sin(alpha);
		System.out.println("radius: "+ radius);
		
		return new Circle(center, radius);
	}


}
