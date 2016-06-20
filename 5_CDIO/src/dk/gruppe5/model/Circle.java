package dk.gruppe5.model;

import org.opencv.core.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;

//tyvstjålet fra https://github.com/Lanchon/circle-circle-intersection

public final class Circle implements Serializable {

	private static final long serialVersionUID = 1L;

	public final DPoint c;
	public final double r;

	public Circle(DPoint c, double r) {
		if (!(r > 0)) throw new IllegalArgumentException("Radius must be positive");
		this.c = c;
		this.r = r;
	}
	
	public Circle(Point p, double r) {
		if (!(r > 0)) throw new IllegalArgumentException("Radius must be positive");
		this.c = new DPoint(p);
		this.r = r;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		long temp;
		temp = Double.doubleToLongBits(r);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	} 
	
	public boolean contains(Rect r) {
		List<DPoint> list = new ArrayList<DPoint>() {{
			add(new DPoint(r.tl()));
			add(new DPoint(r.br().x, r.tl().y));
			add(new DPoint(r.tl().x, r.br().y));
			add(new DPoint(r.br()));
		}};
		for(DPoint p : list) {
			if(p.distance(c) > this.r)
				return false;
		}
		return true;
	}
	
	public boolean contains(RotatedRect r) {
		Point[] points = new Point[4];
		r.points(points);
		for(Point p : points) {
			if(c.distance(new DPoint(p)) > this.r) {
				return false;
			}
		} return true;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Circle other = (Circle) obj;
		if (c == null) {
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		if (Double.doubleToLongBits(r) != Double.doubleToLongBits(other.r))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(c: " + c + ", r: " + r + ")";
	}

}
