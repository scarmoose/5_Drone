package dk.gruppe5.legacy;

import de.yadrone.base.ARDrone.ISpeedListener;
import de.yadrone.base.IARDrone;
import de.yadrone.base.exception.ARDroneException;
import de.yadrone.base.exception.IExceptionListener;
import de.yadrone.base.navdata.Altitude;
import de.yadrone.base.navdata.AltitudeListener;
import de.yadrone.base.navdata.AttitudeListener;
import de.yadrone.base.navdata.BatteryListener;
import de.yadrone.base.navdata.ControlState;
import de.yadrone.base.navdata.DroneState;
import de.yadrone.base.navdata.GyroListener;
import de.yadrone.base.navdata.GyroPhysData;
import de.yadrone.base.navdata.GyroRawData;
import de.yadrone.base.navdata.StateListener;
import de.yadrone.base.navdata.VelocityListener;

public class NavDataListener {
	
	private final IARDrone drone;
	
	public NavDataListener(final IARDrone drone2) {
		this.drone = drone2;
		init();
	}
	
	private void init() {
	
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {
			
			@Override
			public void windCompensation(float pitch, float roll) {
			}
			
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw) {
			}
			
			@Override
			public void attitudeUpdated(float pitch, float roll) {
			}
		});
		
		drone.getNavDataManager().addBatteryListener(new BatteryListener() {
			
			@Override
			public void voltageChanged(int vbat_raw) {
			}
			
			@Override
			public void batteryLevelChanged(int percentage) {			
			}
			
		});
		drone.getNavDataManager().addAltitudeListener(new AltitudeListener() {

			@Override
			public void receivedAltitude(int altitude) {
				if (altitude > 0){
				System.out.println("Altitude: " + altitude);
				}
			}

			@Override
			public void receivedExtendedAltitude(Altitude exAltitude) {
			}
						
		});
		drone.addExceptionListener(new IExceptionListener() {
		    public void exeptionOccurred(ARDroneException exc)
		    {
		        exc.printStackTrace();
		    }
		});
		


	drone.getNavDataManager().addVelocityListener(new VelocityListener() {
			
			@Override
			public void velocityChanged(float vx, float vy, float vz) {
			}
		});


		drone.getNavDataManager().addGyroListener(new GyroListener() {
			
			@Override
			public void receivedRawData(GyroRawData data) {
			}
			
			@Override
			public void receivedPhysData(GyroPhysData PhysData) {
			}
			
			@Override
			public void receivedOffsets(float[] recievedOffsets) {		
			}
		});
		
		drone.getNavDataManager().addStateListener(new StateListener(){

			@Override
			public void controlStateChanged(ControlState arg0) {
				System.out.println("ControlState Changed: -->" + arg0);
				
			}

			@Override
			public void stateChanged(DroneState arg0) {
				System.out.println("state Changed: -->" + arg0);
			}
			
		});
		
		drone.getSpeed();
		System.out.println("Speedlol:"+ drone.getSpeed());
		drone.addSpeedListener(new ISpeedListener() {

			@Override
			public void speedUpdated(int speed) {
				System.out.println("Speed Updated:"+ speed);			
			}
		});		
	}

}
