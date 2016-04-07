package dk.gruppe5.model;

import org.opencv.core.Point;

public class Rects {
	
	double area;
	Point tlPoint;
	Point brPoint;
	
	public Rects( double area, Point tlPoint, Point brPoint){
		this.area = area;
		this.tlPoint = tlPoint;
		this.brPoint = brPoint;
		
	}
	
	public double getArea() {
		return area;
	}

	public Point getTlPoint() {
		return tlPoint;
	}

	public Point getBrPoint() {
		return brPoint;
	}
	
	public double getWidth(){
		return brPoint.x-tlPoint.x;
	}
	public double getHeight(){
		return brPoint.y-tlPoint.y;
	}

}
