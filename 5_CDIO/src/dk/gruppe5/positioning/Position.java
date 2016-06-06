package dk.gruppe5.positioning;

import java.awt.Point;

public class Position implements IPosition {
	
	private Vector2 currentPos;
	final static int TOTAL_PIXELS = 720;
	final static float TOTAL_ANGLE = 67.7f;
	
	public Position() {}
	


	@Override
	public float getAngleInDegreesFromPixelsOccupied(int pixels) {
		return TOTAL_ANGLE * (pixels/TOTAL_PIXELS);
	}


	@Override
	public float getDistanceToPoints(float angle, float distanceBetweenPoints) {
		return (float) ((distanceBetweenPoints/2) / Math.tan(Math.toRadians(angle)));
	}


	@Override
	public Point getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Circle getCircle(Point p1, Point p2, int pixelsOccupiedByObject) {
		int 	x1 = p1.x,
				x2 = p2.x,
				y1 = p1.y,
				y2 = p2.y;
		
		double alpha = getAngleInDegreesFromPixelsOccupied(pixelsOccupiedByObject);
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
		
		Point center = new Point((int) x_c, (int) y_c);
		
		double radius = (1.0/2) * a/Math.sin(Math.toRadians(alpha));
		
		return new Circle(center, radius);
	}
	
	

}