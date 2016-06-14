package dk.gruppe5.positioning;

import de.yadrone.base.IARDrone;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.navdata.AcceleroListener;
import de.yadrone.base.navdata.AcceleroPhysData;
import de.yadrone.base.navdata.AcceleroRawData;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.BatteryListener;
import de.yadrone.base.navdata.GyroListener;
import de.yadrone.base.navdata.GyroPhysData;
import de.yadrone.base.navdata.GyroRawData;
import de.yadrone.base.navdata.VelocityListener;
import dk.gruppe5.app.App;
import dk.gruppe5.model.Circle;
import dk.gruppe5.model.DPoint;

public class Movement {
	
	private int voltageChanged;
	private int batteryLevelChanged;
	private int altitude;
	private float velocityListener;
	private float windCompensation;
	private float attitudeUpdatedPRY;
	private float attitudeUpdatedPR;
	private float[] offset_g;
	private GyroPhysData gyroPhysData;
	private GyroRawData gyroRawData;
	private AcceleroPhysData acceleroPhysData;
	private AcceleroRawData acceleroRawData;
	private Altitude exAltitude;
	
	//Getter og setter for MyVelocityListener
	public float getVelocityListener() {
		return velocityListener;
	}
	public void setVelocityListener(float vx, float vy, float vz) {
		this.velocityListener = velocityListener;
	}
	
	//Getter og setter for MyGyroListener
	public float[] getOffset_g() {
		return offset_g;
	}
	public void setOffset_g(float[] currentOffset_g) {
		this.offset_g = currentOffset_g;
	}
	
	public GyroPhysData getGyroPhysData() {
		return gyroPhysData;
	}
	public void setGyroPhysData(GyroPhysData currentGyroPhysData) {
		this.gyroPhysData = currentGyroPhysData;
	}
	
	public GyroRawData getGyroRawData() {
		return gyroRawData;
	}
	public void setGyroRawData(GyroRawData currentGyroRawData) {
		this.gyroRawData = currentGyroRawData;
	}
	
	//Getter og setter for MyAcceleroListener
	public AcceleroPhysData getAcceleroPhysData() {
		return acceleroPhysData;
	}
	public void setAcceleroPhysData(AcceleroPhysData currentAcceleroPhysData) {
		this.acceleroPhysData = currentAcceleroPhysData;
	}
	
	public AcceleroRawData getAcceleroRawData() {
		return acceleroRawData;
	}
	public void setAcceleroRawData(AcceleroRawData currentAcceleroRawData) {
		this.acceleroRawData = currentAcceleroRawData;
	}

	//Getter og setter for MyAttitudeListener
	public float getWindCompensation() {
		return windCompensation;
	}
	public void setWindCompensation(float pitch,float roll) {
		this.windCompensation = windCompensation;
	}
	
	public float getAttitudeUpdatedPRY() {
		return attitudeUpdatedPRY;
	}
	public void setAttitudeUpdatedPRY(float pitch, float roll, float yaw) {
		this.attitudeUpdatedPRY = attitudeUpdatedPRY;
	}
	
	public float getAttitudeUpdatedPR() {
		return attitudeUpdatedPR;
	}
	public void setAttitudeUpdatedPR(float pitch, float roll) {
		this.attitudeUpdatedPR = attitudeUpdatedPR;
	}
	
	//Getter og setter for MyBatteryListener
	public int getVoltageChanged() {
		return voltageChanged;
	}
	public void setVoltageChanged(int currentVoltageChanged) {
		this.voltageChanged = currentVoltageChanged;
	}
	
	public int getVoltagePercentage() {
		return batteryLevelChanged;
	}
	public void setVoltagePercentage(int currentVoltagePercentage) {
		this.batteryLevelChanged = currentVoltagePercentage;
	}
	
	//Getter og setter for MyAltitudeListener
	public int getAltitudeList() {
		return altitude;
	}
	public void setAltitudeList(int currentAltitudeList) {
		this.altitude = currentAltitudeList;
	}

	public Altitude getExAltitude() {
		return exAltitude;
	}
	public void setExAltitude(Altitude currentExAltitude) {
		this.exAltitude = currentExAltitude;
	}
	
	

	
	private final IARDrone drone;
	private Position pos;
	

	Runnable rthread = new Runnable(){
		@Override 
		public void run() {
			
		}
	};

	public Movement(IARDrone drone) {
		this.drone = drone;
		init();
	}
	
	public Movement() {
		this(App.drone);
	}

	private void init() {
		drone.addExceptionListener(new MyExceptionListener());
		drone.getNavDataManager().addAltitudeListener(new MyAltitudeListener());
		drone.getNavDataManager().addAttitudeListener(new MyAttitudeListener());
		drone.getNavDataManager().addBatteryListener(new MyBatteryListener());
		drone.getNavDataManager().addAcceleroListener(new MyAcceleroListener());
		drone.getNavDataManager().addVelocityListener(new MyVelocityListener());
	}
	
	public void moveToPoint(DPoint p) {
		DPoint position = Position.currentPos;
		
	}

	/**
	 * Udregner gennemsnitsvektoren for et array vektorer
	 * @param vectors vektorer, der skal findes gennemsnit af
	 * @return gennemsnitsvektoren
	 */

	public DPoint getAverageVector(DPoint[] vectors) {		
		double sumx = 0;
		double sumy = 0;
		for(DPoint v : vectors) {
			sumx += v.x;
			sumy += v.y;
		}

		int n = vectors.length;
		double avgx = sumx/n;
		double avgy = sumy/n;

		return new DPoint(avgx, avgy);		
	}

	/**
	 * Afgør om en linje, der går gennem <param>lStart</param> og <param>lEnd</param>, 
	 * skærer en cirkel 
	 * @param lStart startpunkt på linjen
	 * @param lEnd slutpunkt på linjen
	 * @param circle den cirkel der skal tjekkes for
	 * @return
	 */

	public boolean doesLineAndCircleIntersect(DPoint lStart, DPoint lEnd, Circle circle) {		
		DPoint closestOnLine = closestPointOnLine(lStart, lEnd, circle.c);
		if(closestOnLine.distance(circle.c) < circle.r) {
			return true;
		}
		return false;
	}

	/**
	 * Giver det punkt på linjen, der går gennem <param>lStart</param> og <param>lEnd</param>, 
	 * som ligger tættest på cirklens centrum.
	 * @param lStart startpunkt på vektoren på linjen
	 * @param lEnd slutpunkt på vektoren på linjen
	 * @param circleC centrum på cirklen
	 * @return det punkt på linjen, der er tættest på cirklens centrum
	 */

	public DPoint closestPointOnLine(DPoint lStart, 
			DPoint lEnd, DPoint circleC){
		double 	ly2 = lEnd.y,
				ly1 = lStart.y,
				lx2 = lEnd.x,
				lx1 = lStart.x,
				x0 = circleC.x,
				y0 = circleC.y;
		double A1 = ly2 - ly1; 
		double B1 = lx1 - lx2; 
		double C1 = (ly2 - ly1)*lx1 + (lx1 - lx2)*ly1; 
		double C2 = -B1*x0 + A1*y0; 
		double det = A1*A1 - -B1*B1; 
		double cx = 0; 
		double cy = 0; 
		if(det != 0){ 
			cx = (float)((A1*C1 - B1*C2)/det); 
			cy = (float)((A1*C2 - -B1*C1)/det); 
		}else{ 
			cx = x0; 
			cy = y0; 
		} 
		return new DPoint(cx, cy); 
	}

}

class MyVelocityListener implements VelocityListener {
	private Movement move;
	@Override
	public void velocityChanged(float vx, float vy, float vz) {
		move.setVelocityListener(vx,vy,vz);
		System.out.println("Velocity - vx: "+vx+", vy: "+vy+", vz: "+vz);
	}
	
}

class MyGyroListener implements GyroListener {
	private Movement move;
	@Override
	public void receivedOffsets(float[] offset_g) {
		move.setOffset_g(offset_g);
		System.out.println("Gyro - offset_g: "+offset_g);
	}

	@Override
	public void receivedPhysData(GyroPhysData arg0) {
		move.setGyroPhysData(arg0);
		System.out.println(arg0);	
	}

	@Override
	public void receivedRawData(GyroRawData arg0) {
		move.setGyroRawData(arg0);
		System.out.println(arg0);
	}	
}

class MyAcceleroListener implements AcceleroListener {
	private Movement move;
	@Override
	public void receivedPhysData(AcceleroPhysData d) {
		move.setAcceleroPhysData(d);
		System.out.println(d);
		
	}

	@Override
	public void receivedRawData(AcceleroRawData d) {
		move.setAcceleroRawData(d);
		System.out.println(d);
		
	}
	
}

class MyAttitudeListener implements AttitudeListener {
	private Movement move;
	@Override
	public void windCompensation(float pitch, float roll) {
		move.setWindCompensation(pitch, roll);
		System.out.println("windCompensation - pitch: "+roll+", roll: "+roll);
	}

	@Override
	public void attitudeUpdated(float pitch, float roll, float yaw) {
		move.setAttitudeUpdatedPRY(pitch, roll, yaw);
		System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
	}

	@Override
	public void attitudeUpdated(float pitch, float roll) {
		move.setAttitudeUpdatedPR(pitch, roll);
		System.out.println("attitudeUpdated - pitch: "+pitch+", roll: "+roll);
	}

}

class MyBatteryListener implements BatteryListener {
	private Movement move;

	@Override
	public void voltageChanged(int vbat_raw) {
		move.setVoltageChanged(vbat_raw);
		System.out.println("voltageChanged - vbat_raw: "+vbat_raw);
	}

	@Override
	public void batteryLevelChanged(int percentage) {
		move.setVoltagePercentage(percentage);
		System.out.println("Battery: " + percentage + " %");			
	}

}

class MyAltitudeListener implements AltitudeListener {
private Movement move;

	@Override
	public void receivedAltitude(int altitude) {
		move.setAltitudeList(altitude);
		if (altitude > 0){
			System.out.println("Altitude: " + altitude);
		}
	}

	@Override
	public void receivedExtendedAltitude(Altitude exAltitude) {
		move.setExAltitude(exAltitude);
		System.out.println("receivedExtendedAltitude - Altitude: "+exAltitude);
	}

}

class MyExceptionListener implements IExceptionListener {
	
	@Override
	public void exeptionOccurred(ARDroneException exc)
	{
		exc.printStackTrace();
	}
	
}




