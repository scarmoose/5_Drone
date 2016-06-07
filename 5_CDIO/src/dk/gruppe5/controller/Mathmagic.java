package dk.gruppe5.controller;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.HashMap;

import dk.gruppe5.model.Wallmark;
import dk.gruppe5.positioning.Vector2;

public class Mathmagic {
	
	int pixels = 720;
	int cameraDegrees = 68;
	
	public final static Map<String, Point> spMap = new HashMap<String, Point>();
	public final static Map<Integer, String> isMap = new HashMap<Integer, String>();
	{spMap.get(isMap.get(1)); }

	static Wallmark[] wallmarks = {
			new Wallmark("W00.00",new Point(188,1055)), new Wallmark("W00.01",new Point(338, 1060)), new Wallmark("W00.02",new Point(515,1055)), new Wallmark("W00.03",new Point(694, 1060)), new Wallmark("W00.04",new Point(840, 1055)),
			new Wallmark("W01.00",new Point(926, 904)), new Wallmark("W01.01",new Point(926, 721)), new Wallmark("W01.02",new Point(926, 566)), new Wallmark("W01.03",new Point(926, 324)), new Wallmark("W01.04",new Point(926, 115)),
			new Wallmark("W02.00",new Point(847, -10)), new Wallmark("W02.01",new Point(656, -77)), new Wallmark("W02.02",new Point(420,0)), new Wallmark("W02.03",new Point(350, 0)), new Wallmark("W02.04",new Point(150,0)),
			new Wallmark("W03.00",new Point(0,108)), new Wallmark("W03.01",new Point(0, 357)), new Wallmark("W03.02",new Point(0, 561)),new Wallmark("W03.03", new Point(0,770)), new Wallmark("W03.04",new Point(0,997)) 
	};
	
	public static Wallmark[] getArray(){
		return wallmarks;
	}
	
	public static Point getPointFromInt(int i) {
		return spMap.get(isMap.get(i));
	}
	
	public static Point getPointFromName(String name) {
		return spMap.get(name);
	}
	
	public static String getNameFromInt(int i) {
		return isMap.get(i);
	}

	public static float[][] wallmarkDistances = new float[20][20];

	static{
		for(int i = 0; i < wallmarks.length; i++){
			isMap.put(i, wallmarks[i].getName());
			spMap.put(wallmarks[i].getName(), wallmarks[i].getPosition());
			for(int j  = 0; j < wallmarks.length; j++){
				float distance = getDistanceBetweenPoints(new Vector2(wallmarks[i].getPosition()), new Vector2(wallmarks[j].getPosition()));
				wallmarkDistances[i][j] = distance;
				
			}
		}	
	}

	private static float getDistanceBetweenPoints(Vector2 p1, Vector2 p2) {
		return (float )p1.distance(p2);
	}
	
	
	
	
}