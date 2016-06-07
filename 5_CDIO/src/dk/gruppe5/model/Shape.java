package dk.gruppe5.model;

import org.opencv.core.Point;

public class Shape {

	int edges;
	double area;
	Point tlPoint;
	Point brPoint;

	public Shape(double area, Point tlPoint, Point brPoint, int edges) {
		this.area = area;
		this.tlPoint = tlPoint;
		this.brPoint = brPoint;
		this.edges = edges;

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

	public double getWidth() {
		return brPoint.x - tlPoint.x;
	}

	public double getHeight() {
		return brPoint.y - tlPoint.y;
	}

	public int getEdges() {
		return edges;
	}
	public Point getCenter(){
		return  new Point(tlPoint.x+getHeight()/2,tlPoint.y+getWidth()/2);
		
	}
}
