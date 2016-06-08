package dk.gruppe5.controller;

import dk.gruppe5.framework.ImageProcessor;

public class distanceCalc {

	double focalLength = 163.5;
	
	ImageProcessor imgproc = new ImageProcessor();
	
	public double distanceFromCamera(double width, double focalLength, double perceivedPixels){
		return (width*focalLength)/perceivedPixels;
	}
	
	
	
}
