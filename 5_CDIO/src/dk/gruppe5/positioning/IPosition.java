package dk.gruppe5.positioning;

import java.awt.Point;

public interface IPosition {
	
	float getDistanceToPoints(float angle, float distanceBetweenPoints);
	Point getPositionCoordinates();
	float getAngleInDegreesFromPixelsOccupied(int pixels);
	Circle getCircleFromPoints(Point p1, Point p2, int pixelsOccupiedByObject);
	Circle getCircle(Point p1, Point p2, int pixelsOccupiedByObject);
	Point getPosition(Circle c1, Circle c2, Point[] startPoints);

}
