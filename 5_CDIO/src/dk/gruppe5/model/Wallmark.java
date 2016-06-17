package dk.gruppe5.model;

import java.awt.Point;

public class Wallmark {
	

	String name;


	Point position;
	
	Point leftTrianglePos;
	Point rightTrianglePos;
	
	
	public Wallmark(String name, Point position, Point leftTrianglePos, Point rightTrianglePos) {
		super();
		this.name = name;
		this.position = position;
		this.leftTrianglePos = leftTrianglePos;
		this.rightTrianglePos = rightTrianglePos;
	}
	
	public String getName() {
		return name;
	}

	public Point getPosition() {
		return position;
	}

	public Point getLeftTrianglePos() {
		return leftTrianglePos;
	}

	public Point getRightTrianglePos() {
		return rightTrianglePos;
	}
	


}
