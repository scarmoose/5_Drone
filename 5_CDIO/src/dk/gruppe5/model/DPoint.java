package dk.gruppe5.model;

import static java.lang.Math.*;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.Serializable;

//tyvstjålet fra https://github.com/Lanchon/circle-circle-intersection

public final class DPoint extends org.opencv.core.Point
									implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final DPoint NULL = new DPoint(0, 0);
	public static final DPoint X = new DPoint(1, 0);
	public static final DPoint Y = new DPoint(0, 1);

	public DPoint(double x, double y) {
		super(x, y);
	}
	
	public DPoint(Point p) {
		super(p.x, p.y);
	}
	public DPoint(org.opencv.core.Point point){
		super(point.x, point.y);
	}

	public DPoint add(DPoint a) {
		return new DPoint(x + a.x, y + a.y);
	}

	public DPoint sub(DPoint a) {
		return new DPoint(x - a.x, y - a.y);
	}

	public DPoint neg() {
		return new DPoint(-x, -y);
	}

	public DPoint scale(double a) {
		return new DPoint(a * x, a * y);
	}

	public double dot(DPoint a) {
		return x * a.x + y * a.y;
	}

	public double modSquared() {
		return dot(this);
	}

	public double mod() {
		return sqrt(modSquared());
	}

	public DPoint normalize() {
		return scale(1 / mod());
	}

	public DPoint rotPlus90() {
		return new DPoint(-y, x);
	}

	public DPoint rotMinus90() {
		return new DPoint(y, -x);
	}

	public double angle() {
		return atan2(y, x);
	}

	public static DPoint fromAngle(double ang) {
		return new DPoint(cos(ang), sin(ang));
	}

	public static DPoint fromPolar(double ang, double mod) {
		return new DPoint(mod * cos(ang), mod * sin(ang));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DPoint other = (DPoint) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + x + ", " + y + ")";
	}
	
	public static Point[] getPointArray(DPoint[] vectors) {
		Point[] points = new Point[vectors.length];
		int i = 0;
		for(DPoint v : vectors) {
			points[i++] = new Point((int) v.x, (int) v.y);
		}
		return points;
	}
	
    public float distance(DPoint pt) {
        double px = pt.x - this.x;
        double py = pt.y - this.y;
        return (float) Math.sqrt(px * px + py * py);
    }


}
