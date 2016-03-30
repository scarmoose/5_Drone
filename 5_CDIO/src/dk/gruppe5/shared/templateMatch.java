package dk.gruppe5.shared;

import org.opencv.core.Point;

public class templateMatch {
	
	
	Point coordinate;
	


	int picWidth;
	int picHeight;
	double matchValue;
	
	
	public templateMatch(Point coordinate, int picWidth, int picHeight, double matchValue){
		
		this.coordinate = coordinate;
		this.picWidth = picWidth;
		this.picHeight = picHeight;
		this.matchValue = matchValue;
		
	}
	
	
	
	public Point getCoordinate() {
		return coordinate;
	}


	public int getPicWidth() {
		return picWidth;
	}


	public int getPicHeight() {
		return picHeight;
	}


	public double getMatchValue() {
		return matchValue;
	}

}
