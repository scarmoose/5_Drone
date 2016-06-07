package dk.gruppe5.positioning;

import java.awt.Point;

public interface IPosition {
	
	float getDistanceToPoints(float angle, float distanceBetweenPoints);
	float getAngleInDegreesFromPixelsOccupied(int pixels);
	Point getPosition(Circle c1, Circle c2, Point[] startPoints);
	Circle getCircleFromPoints(Vector2 p1, Vector2 p2, int pixelsOccupiedByObject);

}
