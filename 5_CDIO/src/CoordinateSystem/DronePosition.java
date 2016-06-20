package CoordinateSystem;

import java.awt.Point;

import dk.gruppe5.model.Airfield;
import dk.gruppe5.model.DPoint;

public class DronePosition {

	/**
	 * 
	 */
	private static int x=630;
	private static int y=-70;
	private static double degrees = 0.0;
	private static long time_last_pos = 0;
	
	private static boolean found=false;

	
	private static int ymirror = 0;
	
	public static int getXPoint(){return x;}
	public static void setXPoint(int input) {x = input;}

	public static int getYPoint(){return y;}
	public static void setYPoint(int input) {y = input;}
	
	public static void setCoordinates(DPoint v){
		x = (int) v.x;
		y = (int) v.y;
	}
	
	public static long getTimeLastPos() {
		return time_last_pos;
	}
	
	public static int getYMirror(){
		if (y < 530){
			ymirror = (530+(530-y));
		}
		else if (y > 530){
			ymirror = 530-(y-530);
		}
		
		return ymirror;
	}
	
	public static double getDegree(){return degrees;}
	public static void setDegree(double input) {
		degrees = input;
		System.out.println(degrees);
	}
	
	public static void setPosition(Point p) {
		time_last_pos = System.currentTimeMillis();
		x = p.x;
		y = p.y;
	}
	
	public static void setPosition(org.opencv.core.Point p) {
		time_last_pos = System.currentTimeMillis();
		x = (int) p.x;
		y = (int) p.y;
	}

	
	
}



