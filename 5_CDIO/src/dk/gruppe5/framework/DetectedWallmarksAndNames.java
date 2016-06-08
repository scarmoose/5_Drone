package dk.gruppe5.framework;

import org.opencv.core.Point;

public class DetectedWallmarksAndNames {

	String[] qrNames;
	Point[] points;
	double distance;
	
	public DetectedWallmarksAndNames(String[] qrNames, Point[] points, double distance) {
		super();
		this.qrNames = qrNames;
		this.points = points;
		this.distance = distance;
	}

	public String[] getQrNames() {
		return qrNames;
	}

	public Point[] getPoints() {
		return points;
	}
	public double getDistance(){
		return distance;
	}
	
}
