package dk.gruppe5.controller;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;

import dk.gruppe5.model.DPoint;
import dk.gruppe5.model.Wallmark;

public class Mathmagic {

	int pixels = 720;
	int cameraDegrees = 68;

	public final static Map<String, DPoint> spMap = new HashMap<String, DPoint>();
	public final static Map<Integer, String> isMap = new HashMap<Integer, String>();
	// public final static float[][] wallmarkDistances = new float[20][20];
	public final static Wallmark[] wallmarks = {
			new Wallmark("W00.00", new Point(94, 1055), new Point(188, 1055), new Point(264, 1055)),
			new Wallmark("W00.01",new Point(264,1055), new Point(338, 1060),new Point(426,1055)), 
			new Wallmark("W00.02",new Point(426,1055), new Point(515, 1055),new Point(604,1055)),
			new Wallmark("W00.03",new Point(604,1055), new Point(694, 1060),new Point(767,1060)), 
			new Wallmark("W00.04",new Point(767, 1060), new Point(840, 1055),new Point(926,980)),
			new Wallmark("W01.00",new Point(926,980), new Point(926, 904),new Point(926,813)), 
			new Wallmark("W01.01",new Point(926, 813), new Point(926, 721),new Point(926, 643)),
			new Wallmark("W01.02", new Point(926, 643),  new Point(926, 566), new Point(926, 445)),
			new Wallmark("W01.03", new Point(926, 445), new Point(926, 324), new Point(926 , 220)),
			new Wallmark("W01.04", new Point(926, 220), new Point(926, 115), new Point(926, 53)), 
			new Wallmark("W02.00", new Point(926, 53),new Point(847, -10), new Point(752, -10)),
			new Wallmark("W02.01", new Point(752, -10), new Point(656, -77), new Point(585, -77)), 
			new Wallmark("W02.02", new Point(585, -77), new Point(514, 0), new Point(421, 0)),
			new Wallmark("W02.03", new Point(421, 0), new Point(328, 0), new Point(235, 0)), 
			new Wallmark("W02.04", new Point(235, 0), new Point(143, 0), new Point(72, 0)),
			new Wallmark("W03.00", new Point(72, 0), new Point(0, 108), new Point(0, 249)), 
			new Wallmark("W03.01", new Point(0, 249), new Point(0, 357), new Point(0, 459)),
			new Wallmark("W03.02", new Point(0, 459), new Point(0, 561), new Point(0, 650)), 
			new Wallmark("W03.03", new Point(0, 650), new Point(0, 770), new Point(0, 868)),
			new Wallmark("W03.04", new Point(0, 868), new Point(0, 997), new Point(94, 1055)) };

	static {
		for (int i = 0; i < wallmarks.length; i++) {
			Wallmark m = wallmarks[i];
			isMap.put(i, m.getName());
			Point p = m.getPosition();
			spMap.put(m.getName(), new DPoint(p.x, p.y));
			// for(int j = 0; j < wallmarks.length; j++){
			// Wallmark m2 = wallmarks[j];
			// float distance = getDistanceBetweenPoints(new
			// Vector2(m.getPosition()), new Vector2(m2.getPosition()));
			// wallmarkDistances[i][j] = distance;
			// }
		}
	}

	public static Wallmark[] getArray() {
		return wallmarks;
	}

	public static DPoint getPointFromInt(int i) {
		return spMap.get(isMap.get(i));
	}

	public static DPoint getPointFromName(String name) {
		return spMap.get(name);
	}

	public static String getNameFromInt(int i) {
		return isMap.get(i);
	}

	private static float getDistanceBetweenPoints(DPoint p1, DPoint p2) {
		return (float) p1.distance(p2);
	}

}