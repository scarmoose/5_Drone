package dk.gruppe5.model;

public class Point_new extends org.opencv.core.Point {

	public Point_new() {
		// TODO Auto-generated constructor stub
	}

	public Point_new(double[] vals) {
		super(vals);
		// TODO Auto-generated constructor stub
	}

	public Point_new(double x, double y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}
	
	public Point_new(org.opencv.core.Point point){
		this.x = point.x;
		this.y = point.y;
	}

}
