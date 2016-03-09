package dk.gruppe5.drone.window;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
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
		
	}

}
