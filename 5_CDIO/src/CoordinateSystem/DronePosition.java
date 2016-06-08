package CoordinateSystem;

import java.awt.Point;

import dk.gruppe5.positioning.Vector2;

public class DronePosition {

	/**
	 * 
	 */
	private static int x=0;
	private static int y=0;
	private static boolean found=false;

	private static int xcorn=350;
	private static int ycorn=700;
	private static int xlen=350;
	private static int ylen=350;
	
	
	public static int getXPoint(){return x;}
	public static void setXPoint(int input) {x = input;}

	public static int getYPoint(){return y;}
	public static void setYPoint(int input) {y = input;}
	
	public static boolean getFound(){return found;}
	public static void setFound(boolean input) {found = input;}
	
	public static void setCoordinates(Vector2 v){
		x = (int) v.x;
		y = (int) v.y;
	}
	
	public static int getxCorn(){return xcorn;}
	public static void setxCorn(int input) {xcorn = input;}
	public static int getyCorn(){return ycorn;}
	public static void setyCorn(int input) {ycorn = input;}
	
	public static int getxLen(){return xlen;}
	public static void setxLen(int input) {xlen = input;}
	public static int getyLen(){return ylen;}
	public static void setyLen(int input) {ylen = input;}
	
	public static void setPosition(Point p) {
		x = p.x;
		y = p.y;
	}
	
	public static void setPosition(org.opencv.core.Point p) {
		x = (int) p.x;
		y = (int) p.y;
	}
	
}



