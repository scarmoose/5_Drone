package dk.gruppe5.controller;

import java.awt.Point;
import java.util.Arrays;

public class Mathmagic {

	Point[] wallmarks = {
			new Point(188,1055), new Point(338, 1060), new Point(515,1055), new Point(694, 1060), new Point(840, 1055),
			new Point(926, 904), new Point(926, 721), new Point(926, 566), new Point(926, 324), new Point(926, 115),
			new Point(847, -10), new Point(656, -77), new Point(420,0), new Point(350, 0), new Point(150,0),
			new Point(0,108), new Point(0, 357), new Point(0, 561), new Point(0,770), new Point(0,997) 
	};

	public float[][] wallmarkDistances = new float[20][20];

	public Mathmagic(){
		for(int i = 0; i < wallmarks.length; i++){
			for(int j  = 0; j < wallmarks.length; j++){
				float distance = getDistanceBetweenPoints(wallmarks[i], wallmarks[j]);
				wallmarkDistances[i][j] = distance;
			}
		}
	}

	private float getDistanceBetweenPoints(Point p1, Point p2) {
		return (float )p1.distance(p2);
	}
	
}