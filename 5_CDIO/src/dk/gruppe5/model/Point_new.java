package dk.gruppe5.model;

public class Point_new extends org.opencv.core.Point {

	public Point_new() {
	}

	public Point_new(double[] vals) {
		super(vals);
	}

	public Point_new(double x, double y) {
		super(x, y);
	}
	
	public Point_new(org.opencv.core.Point point){
		this.x = point.x;
		this.y = point.y;
	}

}
