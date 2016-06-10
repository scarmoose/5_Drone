package dk.gruppe5.controller;

import de.yadrone.apps.controlcenter.plugins.speed.SpeedPanel;
import de.yadrone.base.ARDrone;
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

public class NavDataListener {
	
	private final IARDrone drone;
	
	public NavDataListener(final ARDrone drone) {
		this.drone = drone;
		init();
	}
	
	private void init() {
		drone.getNavDataManager().addAttitudeListener(new AttitudeListener() {
			
			@Override
			public void windCompensation(float pitch, float roll) {
				//System.out.println("windCompensation - pitch: "+roll+", roll: "+roll);
			}
			
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw) {
				//System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
			}
			
			@Override
			public void attitudeUpdated(float pitch, float roll) {
				//System.out.println("attitudeUpdated - pitch: "+pitch+", roll: "+roll);
			}
		});
		
		drone.getNavDataManager().addBatteryListener(new BatteryListener() {
			
			@Override
			public void voltageChanged(int vbat_raw) {
				//System.out.println("voltageChanged - vbat_raw: "+vbat_raw);
			}
			
			@Override
			public void batteryLevelChanged(int percentage) {
				//System.out.println("Battery: " + percentage + " %");			
			}
			
		});
		drone.getNavDataManager().addAltitudeListener(new AltitudeListener() {

			@Override
			public void receivedAltitude(int altitude) {
				if (altitude > 0){
				//System.out.println("Altitude: " + altitude);
				}
			}

			@Override
			public void receivedExtendedAltitude(Altitude exAltitude) {
				//System.out.println("receivedExtendedAltitude - Altitude: "+exAltitude);
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
				if(!(vx == 0.0f && vy == 0.0f && vz == 0.0f) ){
				//System.out.println("velocity changed:"+ "x: "+vx + " y: "+ vy + " z: "+vz);
				// TODO Auto-generated method stub
			}
			}
		});
		
		drone.getNavDataManager().addGyroListener(new GyroListener() {
			
			@Override
			public void receivedRawData(GyroRawData data) {
				//System.out.println("GyroRayData"+ data);
				
			}
			
			@Override
			public void receivedPhysData(GyroPhysData PhysData) {
			//System.out.println("GyroPhysData"+ PhysData);
				
			}
			
			@Override
			public void receivedOffsets(float[] recievedOffsets) {
				//System.out.println("recievedOffsets"+ recievedOffsets);
				
			}
		});
		
		
		drone.getSpeed();
		//System.out.println("Speedlol:"+drone.getSpeed());

		drone.getNavDataManager().addAcceleroListener(new AcceleroListener() {
			
			@Override
			public void receivedRawData(AcceleroRawData arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void receivedPhysData(AcceleroPhysData arg0) {
				// TODO Auto-generated method stub
				
			}
		});

		
	}

}
