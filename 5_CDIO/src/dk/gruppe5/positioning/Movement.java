package dk.gruppe5.positioning;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
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
import dk.gruppe5.exceptions.Fejl40;
import dk.gruppe5.model.Circle;
import dk.gruppe5.model.DPoint;

public class Movement {

	private final IARDrone drone;
	private final CommandManager cmd;
	private Position pos;


	Runnable rthread = new Runnable(){
		@Override 
		public void run() {

		}
	};

	public Movement(IARDrone drone) {
		this.drone = drone;
		this.cmd = drone.getCommandManager();
		this.pos = new Position();
		init();
	}

	public Movement() {
		this(App.drone);
	}

	private void init() {
		drone.addExceptionListener(new MyExceptionListener());
//		drone.getNavDataManager().addAltitudeListener(new MyAltitudeListener());
//		drone.getNavDataManager().addAttitudeListener(new MyAttitudeListener());
//		drone.getNavDataManager().addBatteryListener(new MyBatteryListener());
//		drone.getNavDataManager().addAcceleroListener(new MyAcceleroListener());
//		drone.getNavDataManager().addVelocityListener(new MyVelocityListener());
	}


	public void moveToPoint(DPoint p) throws Fejl40 {
		DPoint position = Position.currentPos;
		throw new Fejl40();
	}

	/**
	 * Skal gerne centrere et point i billedet, ved at rykke på dronen.
	 * @param p
	 * @param frameSize
	 */
	public void centerPointInFrame(DPoint p, DPoint frameSize) {
		int speed = 100;
		int interval = 10;
		int landIfLower = 15;
		DPoint point = p.clone();
		double cx = frameSize.x/2;
		double cy = frameSize.y/2;
		DPoint center = new DPoint(cx, cy);
		DPoint centerToPoint = point.sub(center);
		double vlength = centerToPoint.length();
		double vx = centerToPoint.x;
		double vy = centerToPoint.y;
		System.out.println("Length of vector: "+vlength);
		if(vlength < landIfLower) {
			land();
		} else if(Math.abs(vy) > Math.abs(vx)) {
			if(vy > 0) { // hvis punktet er over centrum
				forward(speed, interval);
			} else backward(speed, interval);
		} else {
			if(vx > 0) { // hvis punkter er til højre for centrum
				right(speed, interval);
			} else left(speed, interval);
		}

	}

	public void left(int speed, int interval) {
		cmd.goLeft(speed).doFor(interval);
	}

	public void right(int speed, int interval) {
		cmd.goRight(speed).doFor(interval);
	}

	public void forward(int speed, int interval) {
		cmd.forward(speed).doFor(interval);
	}

	public void backward(int speed, int interval) {
		cmd.backward(speed).doFor(interval);
	}

	public void land() {
		cmd.landing();
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
	
	class MyVelocityListener implements VelocityListener {
		@Override
		public void velocityChanged(float vx, float vy, float vz) {
			System.out.println("Velocity - vx: "+vx+", vy: "+vy+", vz: "+vz);
		}
	}

	class MyGyroListener implements GyroListener {
		@Override
		public void receivedOffsets(float[] offset_g) {
			System.out.println("Gyro - offset_g: "+offset_g);
		}

		@Override
		public void receivedPhysData(GyroPhysData arg0) {
			System.out.println(arg0);	
		}

		@Override
		public void receivedRawData(GyroRawData arg0) {
			System.out.println(arg0);
		}	
	}

	class MyAcceleroListener implements AcceleroListener {
		@Override
		public void receivedPhysData(AcceleroPhysData d) {
			System.out.println(d);
		}

		@Override
		public void receivedRawData(AcceleroRawData d) {
			System.out.println(d);
		}
	}

	class MyAttitudeListener implements AttitudeListener {
		@Override
		public void windCompensation(float pitch, float roll) {
			System.out.println("windCompensation - pitch: "+roll+", roll: "+roll);
		}

		@Override
		public void attitudeUpdated(float pitch, float roll, float yaw) {
			System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);

		}

		@Override
		public void attitudeUpdated(float pitch, float roll) {
			System.out.println("attitudeUpdated - pitch: "+pitch+", roll: "+roll);
		}

	}

	class MyBatteryListener implements BatteryListener {
		@Override
		public void voltageChanged(int vbat_raw) {
			System.out.println("voltageChanged - vbat_raw: "+vbat_raw);
		}

		@Override
		public void batteryLevelChanged(int percentage) {
			System.out.println("Battery: " + percentage + " %");			

		}

	}
	
	class MyAltitudeListener implements AltitudeListener {
		@Override
		public void receivedAltitude(int altitude) {
			if (altitude > 0){
				System.out.println("Altitude: " + altitude);
			}
		}

		@Override
		public void receivedExtendedAltitude(Altitude exAltitude) {
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

}








