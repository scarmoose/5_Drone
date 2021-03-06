
package dk.gruppe5.model;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
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

	public Contour(MatOfPoint2f contour) {
		this.contour = contour;
	}

	public Contour() {
	}

	public MatOfPoint2f getContour() {
		return contour;
	}

	public MatOfPoint2f getApproxCurve() {
		return approxCurve;
	}

	public void setContour(MatOfPoint2f contour) {
		this.contour = contour;
	}

	public void setApproxCurve(MatOfPoint2f approxCurve) {
		this.approxCurve = approxCurve;
	}

	public List<Point> getCorners(int ratio) {
		List<Point> source = new ArrayList<Point>();

		double[] temp_double;
		try {
			temp_double = approxCurve.get(0, 0);
			Point p1 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
			temp_double = approxCurve.get(1, 0);
			Point p2 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
			temp_double = approxCurve.get(2, 0);
			Point p3 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
			temp_double = approxCurve.get(3, 0);
			Point p4 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);

			source.add(p1);
			source.add(p2);
			source.add(p3);
			source.add(p4);

		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("getCorners exception -> no enoguh corners fix method?");
			return null;
		}
		return source;
	}

	public Point getCenter(int ratio) {
		Point center;
		double sumX = 0.0;
		double sumY = 0.0;
		List<Point> points = getBoundingRectPoints(ratio);
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

		List<Point> points = getAllContourPoints(ratio);

		double area = 0; // Accumulates area in the loop
		int j = points.size() - 1; // The last vertex is the 'previous' one to
		// the first

		for (int i = 0; i < points.size(); i++) {
			area = area + (points.get(j).x + points.get(i).x) * (points.get(j).y - points.get(i).x);
			j = i; // j is previous vertex to i
		}
		return Math.abs(area / 2);

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

	public Point getBrPoint(int ratio) {

		List<Point> points = getCorners(ratio);
		Point tl = new Point(0, 0);
		int i = 0;
		int index = 100;
		double testDistance = 0;

		for (Point point : points) {
			double dx = point.x;
			double dy = point.y;
			double distance = Math.sqrt(dx * dx + dy * dy);
			if (distance > testDistance) {
				index = i;

			}
			i++;

		}
		tl = points.get(index);

		return tl;
	}

	public Rect getBoundingRect(int ratio) {
		MatOfPoint matOfPoint = new MatOfPoint();
		contour.convertTo(matOfPoint, CvType.CV_32S);

		Rect p = Imgproc.boundingRect(matOfPoint);
		Rect realRect = new Rect(p.x * ratio, p.y * ratio, p.width * ratio, p.height * ratio);
		return realRect;
	}

	/**
	 * Returns 4 points from the bounding rect, tl and then clockwise around the
	 * square
	 * 
	 * @param ratio
	 * @return
	 */
	public List<Point> getBoundingRectPoints(int ratio) {
		Rect r = getBoundingRect(ratio);
		List<Point> points = new ArrayList<>();
		// top left
		points.add(new Point(r.tl().x, r.tl().y));
		// top right
		points.add(new Point(r.br().x, r.tl().y));
		// bottom right
		points.add(new Point(r.br().x, r.br().y));
		// bottom left
		points.add(new Point(r.tl().x, r.br().y));

		return points;
	}

	/**
	 * Returns every single point on the contour, multiplied by the ratio
	 * 
	 * @param ratio
	 * @return
	 */
	public List<Point> getAllContourPoints(int ratio) {
		List<Point> source = new ArrayList<Point>();
		int i = 0;
		while (contour.get(i, 0) != null) {
			double[] temp_double;
			temp_double = contour.get(i, 0);
			Point p1 = new Point(temp_double[0] * ratio, temp_double[1] * ratio);
			source.add(p1);
			i++;
		}

		return source;
	}

	public Point[] allCornerPointsInOrderFromTheLeft(int ratio) {
		Point[] points = new Point[4];
		List<Point> UnorderedPoint = getCorners(ratio);
		List<Point> boundingRectPoints = getBoundingRectPoints(ratio);
		for (int z = 0; z < 4; z++) {

			double distancePointTl = 0.0;
			for (int i = 0; i < 4; i++) {
				double distance = getDistanceBetweenPoints(UnorderedPoint.get(i), boundingRectPoints.get(z));
				if (distancePointTl == 0.0) {
					distancePointTl = distance;
					points[z] = UnorderedPoint.get(i);

				} else if (distance < distancePointTl) {
					points[z] = UnorderedPoint.get(i);

				}

			}
		}

		return points;

	}

	public double getDistanceBetweenPoints(Point pointOne, Point pointTwo) {
		double px = pointOne.x - pointTwo.x;
		double py = pointOne.y - pointTwo.y;
		return Math.sqrt(px * px + py * py);

	}
}