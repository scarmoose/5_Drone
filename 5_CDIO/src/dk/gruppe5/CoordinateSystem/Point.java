package dk.gruppe5.CoordinateSystem;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Point {

	/**
	 * 
	 */
	private static double x;
	private static double y;

	
	public static double getXPoint(){return x;}
	public static void setXPoint(double input) {x = input;}

	public static double getYPoint(){return y;}
	public static void setYPoint(double input) {y = input;}
	
	
}

