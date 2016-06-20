package dk.gruppe5.controller;

import dk.gruppe5.framework.ImageProcessor;

public class DistanceCalc {

	static double percievedPixelWidth = 480;
	static double centimeter = 100.0;
	static double paperHeight = 42.0;
	static double focalLength = (percievedPixelWidth*centimeter)/paperHeight;
	
	ImageProcessor imgproc = new ImageProcessor();
	
	public static double distanceFromCamera(double perceivedPixels){
		return (paperHeight*focalLength)/(perceivedPixels);
	}

	
	
	
}
