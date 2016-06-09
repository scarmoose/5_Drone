package dk.gruppe5.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Airfield {

	public Point point;
	public String name;

	static List<Airfield> airfieldList = new ArrayList<Airfield>();
	
	
	public Airfield(String name, Point point){
		this.point = point;
		this.name = name;
	}
	
}
