package dk.gruppe5.positioning;

import java.awt.Point;

import dk.gruppe5.controller.Mathmagic;

public class Position {
	
	final static float TOTAL_PIXELS = 720.0f;
	final static float TOTAL_ANGLE = 67.7f;
	
	public Position() {}
	


	public float getAngleInDegreesFromPixelsOccupied(float pixels) {
		return TOTAL_ANGLE * (pixels/TOTAL_PIXELS);
	}

	
	public float getDistanceToPoints(float angle, float distanceBetweenPoints) {
		return (float) ((distanceBetweenPoints/2) / Math.tan(Math.toRadians(angle)));
	}

	/**
	 * 
	 * @param c1 Circle 1
	 * @param c2 Circle 2
	 * @param startPoints Points that was used to create the circles
	 * @return
	 */
	
	@Deprecated
	public Point getPosition(Circle c1, Circle c2, Vector2[] startPoints) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		Point[] points = Vector2.getPointArray(cci.getIntersectionVectors());
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
	
	public Vector2[] getIntersectionVectors(Circle c1, Circle c2) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		Vector2[] points = cci.getIntersectionVectors();
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
	
	public Vector2 getPositionVector(Circle c1, Circle c2, Vector2[] startPoints) {
		CircleCircleIntersection cci = new CircleCircleIntersection(c1, c2);
		Vector2[] vectors = cci.getIntersectionVectors();
		for(Vector2 v : vectors)
			System.out.println("getPositionVector - Skæringspunkt: "+v);
		if(vectors != null && vectors.length > 0) {
			if(vectors.length == 1) {
				System.out.println("Der var 1 point");
				return new Vector2(startPoints[0].x, startPoints[0].y);
			}
			if(vectors.length == 2) {
				System.out.println("Der var 2 points");
				int i = 0;
				for(Vector2 v : vectors) {
					System.out.println("----> vector "+i++ +" er: "+v);
				}
				for(Vector2 v : vectors) { // noget er galt
					if(isVectorSignificantlyDifferentFromAllPoints(v, startPoints, 2)) {// 2% afvigelse tilladt
						System.out.println("Position er: "+v);
						return v;
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param v punkt der skal tjekkes for
	 * @param startPoints punkter der skal tjekkes op imod
	 * @param thresholdPercent antal procent det må være fra, før der skal returneres true
	 * @return true hvis punktet @param v ligger tæt på et af punkterne i @param points
	 */
	
	public boolean isVectorSignificantlyDifferentFromAllPoints(Vector2 v, Vector2[] startPoints, float thresholdPercent) {
		System.out.println("isVector - v der tjekkes for: "+ v);
		boolean[] barray = new boolean[3];
		int i = 0;
		for(Vector2 vector : startPoints) {
			System.out.println("isVector - i: "+i);
			double x = vector.x;
			double y = vector.y;
			double x_upper = (x <= 0.001 && x >= -0.001) ? 0.1 : (x * (1 + thresholdPercent/100.0));
			double x_lower = (x <= 0.001 && x >= -0.001) ? -0.1 : (x * (1 - thresholdPercent/100.0));
			double y_upper = (y <= 0.001 && y >= -0.001) ? 0.1 : (y * (1 + thresholdPercent/100.0));
			double y_lower = (y <= 0.001 && y >= -0.001) ? -0.1 : (y * (1 - thresholdPercent/100.0));
			
			System.out.println("isVector - Der tjekkes mod x: "+x+" y: "+y);
			System.out.println("isVector - øvre grænse x: "+x_upper);
			System.out.println("isVector - nedre grænse x: "+x_lower);
			System.out.println("isVector - øvre grænse x: "+y_upper);
			System.out.println("isVector - nedre grænse x: "+y_lower);
			
			if(!(v.x <= x_upper && v.x >= x_lower
					&& v.y <= y_upper && v.y >= y_lower)) {
				// virker ikke med nul.............
				System.out.println("isVector - punktet "+v+" returnerer true mod "+ vector);
				barray[i] = true;
			} else {
				barray[i] = false;
				System.out.println("isVector - punktet "+v+" returnerer false mod "+ vector);
			}
			i++;
			
		}
		for(boolean b : barray) {
			if(!b)
				return false;
		}
		System.out.println("isVector - Der returneres true i isVetorDifferentFromAllPoints()");
		return true;
	}
	/*
	public boolean isVectorAlmostEqualToOneOfThePoints(Vector2 v, Vector2[] startPoints, float thresholdPercent) {
		for(Vector2 vector : startPoints) {
			double x = vector.x;
			double y = vector.y;
			//x = (x == 0)? 0.1 : x;
			//y = (y == 0)? 0.1 : y;
			double x_upper = (x <= 0.001 && x >= -0.001) ? 0.5 : (x * (1 + thresholdPercent/100.0));
			double x_lower = (x <= 0.001 && x >= -0.001) ? -0.5 : (x * (1 - thresholdPercent/100.0));
			double y_upper = (y <= 0.001 && y >= -0.001) ? 0.5 : (y * (1 + thresholdPercent/100.0));
			double y_lower = (y <= 0.001 && y >= -0.001) ? -0.5 : (y * (1 - thresholdPercent/100.0));
			System.out.println("Der tjekkes mod x: "+x+" y: "+y);
			System.out.println("øvre grænse x: "+x_upper);
			System.out.println("nedre grænse x: "+x_lower);
			System.out.println("øvre grænse x: "+y_upper);
			System.out.println("nedre grænse x: "+y_lower);
			if(!(v.x <= x_upper && v.x >= x_lower
					&& v.y <= y_upper && v.y >= y_lower)) {
				// virker ikke med nul.............
				System.out.println("-- punktet "+v+" returnerer true mod "+ vector);
				return true;
			} System.out.println("tjekket returnerede false");
		}
		return false;
	}
	*/
	
	
	/**
	 * giver en cirkel med <param>p1</param> og <param>p2</param>, samt iagtageren/dronen på periferien
	 * @param p1 punkt 1
	 * @param p2 punkt 2
	 * @param pixelsOccupiedByObject så mange pixels, der er i mellem p1 og p2. 
	 * @return cirkel med alle punkter i periferien
	 */
	
	public Circle getCircleFromPoints(Vector2 p1, Vector2 p2, float pixelsOccupiedByObject) {
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
	
	public Circle getCircleFromPointsWithAngle(Vector2 p1, Vector2 p2, float angle) {
		double 		x1 = p1.x,
					y1 = p1.y,
					x2 = p2.x,
					y2 = p2.y;

		System.out.println("getCircle - Angle: " + angle);
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
		
		Vector2 center = new Vector2(x_c, y_c);
		
		System.out.println("getCircle - alpha: "+ alpha);
		double radius = (1.0/2) * a/Math.sin(alpha);
		System.out.println("getCircle - radius: "+ radius);
		
		return new Circle(center, radius);
	}
	
	
	
	public Vector2 getPositionFromPoints(Point p1, Point p2, Point p3) {
		float pixelsFromP1toP2 = (float) p1.distance(p2);
		float pixelsFromP2toP3 = (float) p2.distance(p3);
		Vector2 v1 = new Vector2(p1);
		Vector2 v2 = new Vector2(p2);
		Vector2 v3 = new Vector2(p3);
		return getPositionFromPoints(v1, v2, v3, pixelsFromP1toP2, pixelsFromP2toP3);
	}
	
	@Deprecated
	public Vector2 getPositionFromPoints(Vector2 v1, Vector2 v2, Vector2 v3) {
		float pixelsFromP1toP2 = (float) v1.distance(v2); // .distance ikke testet
		float pixelsFromP2toP3 = (float) v2.distance(v3);
		return getPositionFromPoints(v1, v2, v3, pixelsFromP1toP2, pixelsFromP2toP3);
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
	
	public Vector2 getPositionFromPoints(Vector2 v1, Vector2 v2, Vector2 v3,
			float pixelsFromP1toP2, float pixelsFromP2toP3){
		
		Vector2[] points = new Vector2[]{v1, v2, v3}; // startpoints som cirkler er lavet ud fra
		Circle c1 = getCircleFromPoints(v1, v2, pixelsFromP1toP2);
		Circle c2 = getCircleFromPoints(v2, v3, pixelsFromP2toP3);
		return getPositionVector(c1, c2, points);
		 
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
	public org.opencv.core.Point getPositionFromPoints(Vector2 v1, Vector2 v2, Vector2 v3,
			org.opencv.core.Point p1, org.opencv.core.Point p2, org.opencv.core.Point p3) {
		Vector2[] realLocations = new Vector2[]{v1, v2, v3};
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
	public org.opencv.core.Point getPositionFromPoints(Vector2[] vectors,
			org.opencv.core.Point p1, org.opencv.core.Point p2, org.opencv.core.Point p3) {
	
		float pixelsFromP1toP2 = distance(p1, p2);
		float pixelsFromP2toP3 = distance(p2, p3);
		Circle c1 = getCircleFromPoints(vectors[0], vectors[1], pixelsFromP1toP2);
		Circle c2 = getCircleFromPoints(vectors[1], vectors[2], pixelsFromP2toP3);
		Vector2 position = getPositionVector(c1, c2, vectors);
		
		return new org.opencv.core.Point(position.x, position.y);
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
		Vector2[] vectors = new Vector2[] { 
			mm.spMap.get(names[0]),
			mm.spMap.get(names[1]),
			mm.spMap.get(names[2])
		};
		int i = 0;
		for(Vector2 v : vectors) {
			System.out.println("getPositionFromPoints - vector "+i++ +" er: "+v);
			
		}
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
	
	public static float getDistanceBetweenPoints(Vector2 p1, Vector2 p2) {
		return (float) p1.distance(p2);
	}


}
