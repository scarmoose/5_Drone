package dk.gruppe5.positioning;

import java.awt.Point;

public interface IPosition {
	
	float getDistanceToPoints(float angle, float distanceBetweenPoints);
	float getAngleInDegreesFromPixelsOccupied(int pixels);
	Circle getCircle(Point p1, Point p2, int pixelsOccupiedByObject);
	Point getPosition();

}
