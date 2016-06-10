package dk.gruppe5.model;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.imgproc.Imgproc;
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

	public List<Point> getCorners(int ratio) {

		double[] temp_double;
		temp_double = approxCurve.get(0, 0);
		Point p1 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
		temp_double = approxCurve.get(1, 0);
		Point p2 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
		temp_double = approxCurve.get(2, 0);
		Point p3 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
		temp_double = approxCurve.get(3, 0);
		Point p4 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
		List<Point> source = new ArrayList<Point>();
		source.add(p1);
		source.add(p2);
		source.add(p3);
		source.add(p4);
		return source;
	}

	public Point getCenter(int ratio) {
		Point center;
		double sumX = 0.0;
		double sumY = 0.0;
		List<Point> points = getCorners(ratio);
		for (Point point : points) {
			sumX += point.x;
			sumY += point.y;
		}
		center = new Point(sumX / points.size(), sumY / points.size());
		return center;
	}

	public Point getTlPoint(int ratio) {
		// find tl point
		// punktet med korteste afstand til 0,0

		List<Point> points = getCorners(ratio);
		Point tl = new Point(0, 0);
		int i = 0;
		int index = 100;
		double testDistance = 100000;

		for (Point point : points) {
			double dx = point.x;
			double dy = point.y;
			double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance < testDistance) {
				index = i;

			}
			i++;

		}
		tl = points.get(index);

		return tl;
	}

	public double getArea(int ratio) {

		List<Point> points = getCorners(ratio);

		double area = 0; // Accumulates area in the loop
		int j = points.size() - 1; // The last vertex is the 'previous' one to
									// the first

		for (int i = 0; i < points.size(); i++) {
			area = area + (points.get(j).x + points.get(i).x) * (points.get(j).y - points.get(i).x);
			j = i; // j is previous vertex to i
		}
		return area / 2;

	}

	public int getHeight() {
		RotatedRect r = Imgproc.fitEllipse(contour);
		Rect p = r.boundingRect();
		
		
		return p.height;
	}
	public int getWidth() {
		RotatedRect r = Imgproc.fitEllipse(contour);
		Rect p = r.boundingRect();
		
		
		return p.width;
	}

}