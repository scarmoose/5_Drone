package dk.gruppe5.controller;

import dk.gruppe5.framework.ImageProcessor;

public class DistanceCalc {

	static double percievedPixelWidth = 420;
	static double centimeter = 100.0;
	static double paperWidth = 42.0;
	static double focalLength = (percievedPixelWidth*centimeter)/paperWidth;
	
	ImageProcessor imgproc = new ImageProcessor();
	
	public static double distanceFromCamera(double perceivedPixels){
		return (paperWidth*focalLength)/perceivedPixels;
	}
	
	
	
}
