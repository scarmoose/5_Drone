package dk.gruppe5.model;

import java.awt.Point;

public class Wallmark {
	
	String name;
	Point position;

	public Wallmark(String name, Point position) {
		super();
		this.name = name;
		this.position = position;
	}
	
	public String getName() {
		return name;
	}
	public Point getPosition() {
		return position;
	}

}
