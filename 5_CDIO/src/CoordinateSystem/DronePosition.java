package CoordinateSystem;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

import dk.gruppe5.positioning.Vector2;

public class DronePosition {

	/**
	 * 
	 */
	private static int x=0;
	private static int y=0;

	
	public static int getXPoint(){return x;}
	public static void setXPoint(int input) {x = input;}

	public static int getYPoint(){return y;}
	public static void setYPoint(int input) {y = input;}
	
	public static void setCoordinates(Vector2 v){
		x = (int) v.x;
		y = (int) v.y;
	}
	
	
}

