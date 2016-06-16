package dk.gruppe5.positioning;

import java.awt.Point;

import CoordinateSystem.DronePosition;
import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.model.Circle;
import dk.gruppe5.model.CircleCircleIntersection;
import dk.gruppe5.model.DPoint;

public class Position {
	
	final static float TOTAL_PIXELS = 720.0f;
	final static float TOTAL_ANGLE = 67.7f;
	public static DPoint currentPos;
	public static float currentAngle;
	
	public Position() {}
	


	public float getAngleInDegreesFromPixelsOccupied(float pixels) {
		return TOTAL_ANGLE * (pixels/TOTAL_PIXELS);
	}
	
	/**
	 * same shit different method
	 * @param dronePos
	 * @param qrName
	 * @param pixelsFromMiddleToQr
	 * @return
	 */
	
	public float getDirectionAngleRelativeToYAxis(DPoint dronePos, String qrName, int pixelsFromMiddleToQr) {
		DPoint qrPos = Mathmagic.getPointFromName(qrName);
		return getDirectionAngleRelativeToYAxis(dronePos, qrPos, pixelsFromMiddleToQr);
	}
	
	/**
	 * Skal give vinkel fra dronens synsretning og til y-aksen.
	 * Der bliver vist kun returneret vinkler op til 180 grader
	 * @param dronePos dronens position
	 * @param qrPos et QR mærkes position
	 * @param pixelsFromMiddleToQr afstanden i pixels fra midten af skærmen til QR punktet.
	 * @return vinklen fra linjen mellem de to givne punkter, og y-aksen
	 */
	public float getDirectionAngleRelativeToYAxis(DPoint dronePos, DPoint qrPos, int pixelsFromMiddleToQr) {
		// vinkel fra midten af billedet, ud til den QR kode, der er givet med. 
		float alpha = getAngleInDegreesFromPixelsOccupied(pixelsFromMiddleToQr);
		return getDirectionAngleRelativeToYAxis(dronePos, qrPos, alpha);
	}
	
	/**
	 * Skal give vinkel fra dronens synsretning og til y-aksen.
	 * Der bliver vist kun returneret vinkler op til 180 grader
	 * @param dronePos dronens position
	 * @param qrPos et QR mærkes position
	 * @param angleToQr vinklen fra midten af dronens synsfelt, ud til QR koden
	 * @return vinklen fra linjen mellem de to givne punkter, og y-aksen
	 */
	public float getDirectionAngleRelativeToYAxis(DPoint dronePos, 
			DPoint qrPos, float angleToQr) {
		DPoint vector = qrPos.sub(dronePos);
		DPoint yaxis = new DPoint(0,1);
		float angleToY = getSignedAngleBetweenVectors(vector, yaxis);
		/*
		 * hvis angleToY er over 0, skal vinklen til QR-koden trækkes fra. 
		 * hvis under 0, skal vinklen til QR-koden lægges til. 
		 */
		float v = (float) ((angleToY > 0) ? (angleToY - angleToQr) 
				: (angleToY + angleToQr));
		currentAngle = v;
		return v;
	}
	
	public float getSignedAngleBetweenVectors(DPoint v1, DPoint v2) {
		float signed_angle = (float) 
				(Math.atan2(v1.x, v1.y) - Math.atan2(v2.x, v2.y));
		return signed_angle;
	}

	
//	public float getDistanceToPoints(float angle, float distanceBetweenPoints) {
//		return (float) ((distanceBetweenPoints/2) / Math.tan(Math.toRadians(angle)));
//	}

	/**
	 * OBS. VIRKER KUN MED INTEGERS, SÅ DEN VIRKER EGENTLIG IKKE
	 * @param c1 Circle 1
	 * @param c2 Circle 2
	 * @param startPoints Points that was used to create the circles
	 * @return
	 */
	
	@Deprecated
	public Point getPosition(Circle c1, Circle c2, DPoint[] startPoints) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		Point[] points = DPoint.getPointArray(cci.getIntersectionVectors());
		if(points != null && points.length > 0) {
			if(points.length == 1) {
				System.out.println("Der var 1 point");
				return new Point((int) points[0].x, (int) points[0].y);
			}
			if(points.length == 2) {
				System.out.println("Der var 2 points");
				for(Point p : points) {
					if(!p.equals(startPoints[0]) && !p.equals(startPoints[1])
							&& !p.equals(startPoints[2]))
						return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * giver skæringspunkterne for de to cirkler, hvis der er nogen. 
	 * @param c1 cirkel 1
	 * @param c2 cirkel 2
	 * @return skæringspunkterne. <code>null</code> hvis der ikke er nogen
	 */
	
	public DPoint[] getIntersectionPoints(Circle c1, Circle c2) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		DPoint[] points = cci.getIntersectionVectors();
		if(points != null && points.length > 0) {
			return points;
		}
		return null;
	}
	
	/**
	 * giver det af skæringspunkterne for de cirkler, der ikke er et at de givne startpoints.
	 * Altså de 3 punkter, de 2 cirkler blevet lavet med. 
	 * @param c1 cirkel 1
	 * @param c2 cirkel 2
	 * @param startPoints punkter der skal tjekkes op imod
	 * @return returnerer <code>null</code>, 1 punkt, eller 2 punkter. Ved henholdsvis 0, 1 og 2 skæringspunkter.
	 */
	
	public DPoint getPositionPoint(Circle c1, Circle c2, DPoint[] startPoints) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		DPoint[] vectors = cci.getIntersectionVectors();
		if(vectors != null && vectors.length > 0) {
			if(vectors.length == 1) {
				return new DPoint(startPoints[0].x, startPoints[0].y);
			}
			if(vectors.length == 2) {
				for(DPoint v : vectors) { 
					if(isPointSignificantlyDifferentFromAllPoints(v, startPoints, 2)) { // mindst 2% afvigelse
						return v;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param p punkt der skal tjekkes for
	 * @param startPoints punkter der skal tjekkes op imod
	 * @param thresholdPercent antal procent det må være fra, før der skal returneres true
	 * @return true hvis punktet @param v ligger tæt på et af punkterne i @param points
	 */
	public boolean isPointSignificantlyDifferentFromAllPoints(DPoint p, DPoint[] startPoints, float thresholdPercent) {
		for(DPoint _p : startPoints) {
			double x = _p.x;
			double y = _p.y;
			// virker ikke med nul..
			// da x eller y værdier på 0, ikke vil give noget upper-lower interval, ændres 0 til 0.1/-0.1
			// for sjov sættes meget lave værdier til +- 0.1, da det ikke behøver at være så præcist
			// 
			double x_upper = (x <= 0.001 && x >= -0.001) ? 0.1 : (x * (1 + thresholdPercent/100.0));
			double x_lower = (x <= 0.001 && x >= -0.001) ? -0.1 : (x * (1 - thresholdPercent/100.0));
			double y_upper = (y <= 0.001 && y >= -0.001) ? 0.1 : (y * (1 + thresholdPercent/100.0));
			double y_lower = (y <= 0.001 && y >= -0.001) ? -0.1 : (y * (1 - thresholdPercent/100.0));
			
			if((p.x <= x_upper && p.x >= x_lower
					&& p.y <= y_upper && p.y >= y_lower)) {
				return false;
			} 
		}
		return true;
	}

	/**
	 * giver en cirkel med <param>p1</param> og <param>p2</param>, samt iagtageren/dronen på periferien
	 * @param p1 punkt 1
	 * @param p2 punkt 2
	 * @param pixelsOccupiedByObject så mange pixels, der er i mellem p1 og p2. 
	 * @return cirkel med alle punkter i periferien
	 */
	public Circle getCircleFromPoints(DPoint p1, DPoint p2, float pixelsOccupiedByObject) {
		float alpha = getAngleInDegreesFromPixelsOccupied(pixelsOccupiedByObject);
		return getCircleFromPointsWithAngle(p1, p2, alpha);
	}

	/**
	 * This creates a circle with the two observed points, and the observer point on the peripheral line
	 * @param p1 point number 1
	 * @param p2 point number 2
 	 * @param angle observed angle between the objects
	 * @return
	 */
	public Circle getCircleFromPointsWithAngle(DPoint p1, DPoint p2, float angle) {
		double 		x1 = p1.x,
					y1 = p1.y,
					x2 = p2.x,
					y2 = p2.y;

		double alpha = Math.toRadians(angle);
		double a = Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
		double t1 = a * a;
		double t2 = Math.sin(alpha);
		double t3 = t2 * t2;
		double t7 = Math.sqrt(1.0/t3 * t1 - t1 ) ;
		double t8 = y1 - y2;
		double t10 = t8 * t8;
		double t12 = x1 - x2;
		double t13 = t12 * t12;
		double t15 = Math.sqrt((t10 + t13));
		double x_c = -t8/t15 * t7/2.0 + x1/2.0 + x2/2.0;
		double y_c = t12/t15 * t7/2.0 + y1/2.0 + y2/2.0;
		
		DPoint center = new DPoint(x_c, y_c);
		double radius = (1.0/2) * a/Math.sin(alpha);
		
		return new Circle(center, radius);
	}
	
	/**
	 * Giver et <code>Vector2</code> punkt med positionen bestemt ud fra tre rumkoordinater, 
	 * og afstandene derimellem
	 * @param v1
	 * @param v2
	 * @param v3
	 * @param pixelsFromP1toP2
	 * @param pixelsFromP2toP3
	 * @return
	 */
	public DPoint getPositionFromPoints(DPoint v1, DPoint v2, DPoint v3,
			float pixelsFromP1toP2, float pixelsFromP2toP3){
		
		DPoint[] points = new DPoint[]{v1, v2, v3}; // startpoints som cirkler er lavet ud fra
		Circle c1 = getCircleFromPoints(v1, v2, pixelsFromP1toP2);
		Circle c2 = getCircleFromPoints(v2, v3, pixelsFromP2toP3);
		return getPositionPoint(c1, c2, points);
		 
	}
	
	/**
	 * Giver et <code>org.opencv.core.Point</code>, der repræsenterer positionen, 
	 * som udregnet fra de givne punkter i rummet, og pixelkoordinaterne fra kameraet. 
	 * @param v1 rumkoordinater for punkt 1
	 * @param v2 rumkoordinater for punkt 2
	 * @param v3 rumkoordinater for punkt 3
	 * @param p1 pixelkoordinater for punkt 1
	 * @param p2 pixelkoordinater for punkt 2
	 * @param p3 pixelkoordinater for punkt 3
	 * @return opencv.core.Point med positionskoordinater
	 */
	public org.opencv.core.Point getPositionFromPoints(DPoint v1, DPoint v2, DPoint v3,
			org.opencv.core.Point p1, org.opencv.core.Point p2, org.opencv.core.Point p3) {
		DPoint[] realLocations = new DPoint[]{v1, v2, v3};
		return getPositionFromPoints(realLocations, p1, p2, p3);
	}
	
	/**
	 * Giver et <code>org.opencv.core.Point</code>, der repræsenterer positionen, 
	 * som udregnet fra de givne punkter i rummet, og pixelkoordinaterne fra kameraet. 
	 * @param vectors array af rumkoordinater for punkterne. I rækkefølge.
	 * @param p1 pixelkoordinater for punkt 1
	 * @param p2 pixelkoordinater for punkt 2
	 * @param p3 pixelkoordinater for punkt 3
	 * @return opencv.core.Point med positionskoordinater
	 */
	public org.opencv.core.Point getPositionFromPoints(DPoint[] vectors,
			org.opencv.core.Point p1, org.opencv.core.Point p2, org.opencv.core.Point p3) {
	
		float pixelsFromP1toP2 = distance(p1, p2);
		float pixelsFromP2toP3 = distance(p2, p3);
		Circle c1 = getCircleFromPoints(vectors[0], vectors[1], pixelsFromP1toP2);
		Circle c2 = getCircleFromPoints(vectors[1], vectors[2], pixelsFromP2toP3);
		DPoint position = getPositionPoint(c1, c2, vectors);
		
		DPoint pos = new DPoint(position.x, position.y);
		currentPos = pos;
		DronePosition.setPosition(pos);
		return pos;
	}
	
	/**
	 * Giver et <code>org.opencv.core.Point</code>, der repræsenterer positionen, 
	 * som udregnet fra de givne punkter i rummet, og pixelkoordinaterne fra kameraet. 
	 * @param names array af navne på punkterne der er fundet. I rækkefølge.
	 * @param p1 pixelkoordinater for punkt 1
	 * @param p2 pixelkoordinater for punkt 2
	 * @param p3 pixelkoordinater for punkt 3
	 * @return opencv.core.Point med positionskoordinater
	 */
	public org.opencv.core.Point getPositionFromPoints(String[] names,
			org.opencv.core.Point p1, org.opencv.core.Point p2, org.opencv.core.Point p3) {
		Mathmagic mm = new Mathmagic();
		DPoint[] vectors = new DPoint[] { 
			mm.spMap.get(names[0]),
			mm.spMap.get(names[1]),
			mm.spMap.get(names[2])
		};
		return getPositionFromPoints(vectors, p1, p2, p3);
	}
	
	/**
	 * Giver afstanden i mellem to punkter
	 * @param p1 Punkt 1
	 * @param p2 Punkt 2
	 * @return Afstanden i mellem de to punkter.
	 */
	
	public float distance(org.opencv.core.Point p1, org.opencv.core.Point p2) {
		return (float) Math.abs(Math.sqrt(
						sq((float) (p2.x-p1.x)) +
								sq((float) (p2.y-p1.y))));
	}
	
	public float sq(float a) {
		return a * a;
	}
	
	public static float getDistanceBetweenPoints(DPoint p1, DPoint p2) {
		return (float) p1.distance(p2);
	}


}
