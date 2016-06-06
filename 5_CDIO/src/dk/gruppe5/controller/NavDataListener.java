package dk.gruppe5.controller;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
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
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void attitudeUpdated(float pitch, float roll, float yaw) {
				//System.out.println("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
			}
			
			@Override
			public void attitudeUpdated(float pitch, float roll) {
				// TODO Auto-generated method stub
				
			}
		});
		
		drone.getNavDataManager().addBatteryListener(new BatteryListener() {
			
			@Override
			public void voltageChanged(int vbat_raw) {
				// TODO Auto-generated method stub	
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
				System.out.println("Altitude: " + altitude);
				}
				// TODO Auto-generated method stub
			}

			@Override
			public void receivedExtendedAltitude(Altitude exAltitude) {
				// TODO Auto-generated method stub
				
			}
						
		});
		
	}

}
