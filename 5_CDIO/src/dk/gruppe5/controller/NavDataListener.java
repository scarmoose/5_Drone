package dk.gruppe5.controller;

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
