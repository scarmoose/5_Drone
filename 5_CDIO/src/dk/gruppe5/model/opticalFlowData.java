package dk.gruppe5.model;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;

public class opticalFlowData {
	
	
	Mat frame;
	

	List<Point> startPoints = new ArrayList<>();
	List<Point> endPoints = new ArrayList<>();
	
	public opticalFlowData(Mat frame,List<Point> startPoints,List<Point> endPoints ){
		
		this.frame = frame;
		this.startPoints = startPoints;
		this.endPoints = endPoints;
		
		//asd
		
	}
	
	
	public Mat getFrame() {
		return frame;
	}

	public List<Point> getStartPoints() {
		return startPoints;
	}

	public List<Point> getEndPoints() {
		return endPoints;
	}
	
	
	

}
