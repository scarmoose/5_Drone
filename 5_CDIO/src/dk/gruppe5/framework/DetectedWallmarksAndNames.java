package dk.gruppe5.framework;

import org.opencv.core.Point;

public class DetectedWallmarksAndNames {

	String[] qrNames;
	Point[] points;
	
	public DetectedWallmarksAndNames(String[] qrNames, Point[] points) {
		super();
		this.qrNames = qrNames;
		this.points = points;
	}

	public String[] getQrNames() {
		return qrNames;
	}

	public Point[] getPoints() {
		return points;
	}
	
}
