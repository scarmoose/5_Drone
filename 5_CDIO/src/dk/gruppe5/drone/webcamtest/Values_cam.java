package dk.gruppe5.drone.webcamtest;

public class Values_cam {

	private static double cannyTres1 = 50;
	private static double cannyTres2 = 100;
	private static int cannyAp = 3;
	private static int goodCorner = 500;
	private static double goodQualLvl = 0.1;
	private static double goodMinDist = 10;
	
	public static double getCanTres1() {return cannyTres1;}	
	public static void setCanTres1(double input) {cannyTres1 = input;}
	
	public static double getCanTres2() {return cannyTres2;} 
	public static void setCanTres2(double input) {cannyTres2 = input;}
	
	public static int getCanAp() {return cannyAp;}
	public static void setCanAp(int input) {cannyAp = input;}
	
	public static int getCorn(){return goodCorner;}	
	public static void setCorn(int input) {goodCorner = input;}
	
	public static double getQual(){return goodQualLvl;}
	public static void setQual(double input) {goodQualLvl = input;}
	
	public static double getDist(){return goodMinDist;}
	public static void setDist(double input) {goodMinDist = input;}
}
