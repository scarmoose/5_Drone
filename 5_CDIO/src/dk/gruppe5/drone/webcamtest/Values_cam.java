package dk.gruppe5.drone.webcamtest;

public class Values_cam {

	int cannyTres1 = 50;
	int cannyTres2 = 100;
	int cannyAp = 3;
	int goodCorner1 = 500;
	int goodCorner2 = 500;
	double goodQualLvl1 = 0.1;
	double goodQualLvl2 = 0.1;
	double goodMinDist1 = 10;
	double goodMinDist2 = 10;
	
	public int getCanTres1() {return cannyTres1;}	
	public void setCanTres1(int input) {this.cannyTres1 = input;}
	
	public int getCanTres2() {return cannyTres2;} 
	public void setCanTres2(int input) {this.cannyTres2 = input;}
	
	public int getCanAp() {return cannyAp;}
	public void setCanAp(int input) {this.cannyAp = input;}
	
	public int getCorn1(){return goodCorner1;}	
	public void setCorn1(int input) {this.goodCorner1 = input;}
	
	public int getCorn2(){return goodCorner2;}
	public void setCorn2(int input) {this.goodCorner2 = input;}
	
	public double getQual1(){return goodQualLvl1;}
	public void setQual1(double input) {this.goodQualLvl1 = input;}
	
	public double getQual2(){return goodQualLvl2;}
	public void setQual2(double input) {this.goodQualLvl2 = input;}
	
	public double getDist1(){return goodMinDist1;}
	public void setDist1(double input) {this.goodMinDist1 = input;}
	
	public double getDist2(){return goodMinDist2;}	
	public void setDist2(double input) {this.goodMinDist2 = input;}
}
