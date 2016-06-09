package dk.gruppe5.model;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.utils.Converters;

public class Contour {

	MatOfPoint2f contour;
	MatOfPoint2f approxCurve;

	public Contour(MatOfPoint2f contour, MatOfPoint2f approxCurve) {
		super();
		this.contour = contour;
		this.approxCurve = approxCurve;
	}

	public MatOfPoint2f getContour() {
		return contour;
	}

	public MatOfPoint2f getApproxCurve() {
		return approxCurve;
	}

	public List<Point> getCorners() {

		double[] temp_double;
		temp_double = approxCurve.get(0, 0);
		Point p1 = new Point(temp_double[0], temp_double[1]);
		temp_double = approxCurve.get(1, 0);
		Point p2 = new Point(temp_double[0], temp_double[1]);
		temp_double = approxCurve.get(2, 0);
		Point p3 = new Point(temp_double[0], temp_double[1]);
		temp_double = approxCurve.get(3, 0);
		Point p4 = new Point(temp_double[0], temp_double[1]);
		List<Point> source = new ArrayList<Point>();
		source.add(p1);
		source.add(p2);
		source.add(p3);
		source.add(p4);
		return source;
	}

}
