package dk.gruppe5.ai;

import java.util.ArrayList;
import java.util.List;

import dk.gruppe5.app.App;
import dk.gruppe5.controller.DroneCommander;
import dk.gruppe5.framework.FrameGrabber;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.positioning.Movement;
import dk.gruppe5.positioning.Position;

public class DecisionMakerNr2 implements Runnable {

	List<String> targets = new ArrayList<>();
	String endTarget;
	String startSpot;
	DroneCommander dCommando;

	boolean runs = true;

	public void setupTargets() {
		targets.add("AF.09");

	}

	public DecisionMakerNr2(DroneCommander dCommando) {
		this.dCommando = dCommando;

	}

	public void run() {
		try {
		while (runs) {
			if(Position.isFlying){
			// check altitude
				
				if (checkHeight()) {
					// altitude is a OK now
					// check if QR code is spotted and get its distance to the
					// camera
					Values_cam.setMethod(6);
					long currentTime = System.currentTimeMillis();
					if (currentTime - Values_cam.timeOfFindingSingleQRCode < 2000) {

						// QR code detected, try to center it, small movements
						System.out.println("Recent qr code seen, cms away -->" + Values_cam.distanceToLastQr);

					} else {
						// Search for QR code
						searchForQrCode();
					}
				}
				
				
				
			}
		}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	private void searchForQrCode() throws InterruptedException {
		boolean search = true;
		int turnAround = 0;
		
			// if a qr code has been found, the time will now be less than 2
			// seconds
			// so the loop here will break
			long currentTime = System.currentTimeMillis();
			if (currentTime - Values_cam.timeOfFindingSingleQRCode < 2000) {

				search = false;
				
			}
			while(turnAround < 10 && search){
				
				System.out.println("Turning");
				if (currentTime - Values_cam.timeOfFindingSingleQRCode < 2000) {

					search = false;
					break;
				}
				if(checkHeight()){
					
				dCommando.getMovement().spinLeft(80, 50);
				Thread.sleep(1000);
				turnAround++;
				}
			}
		// If no qr code is visible in the frame, the we must find one, we
		// can first turn around ourselves. if none is visible, height is
		// okay. The go forward abit and try again

	}

	private boolean checkHeight() throws InterruptedException {
		int maxHeight = 1800;
		int minHeight = 800;
		if (Movement.currentAltitude < maxHeight && Movement.currentAltitude > minHeight) {
			return true;
		} else {
			// check if height is to low or to high, send a correction command,
			// and check again at some point
			if (Movement.currentAltitude > maxHeight) {
				dCommando.getMovement().down(100, 50);
				Thread.sleep(500);
				System.out.println("Too high");
				// Go down with drone until below 2500
			} else if (Movement.currentAltitude < minHeight) {
				// go up a little check again
				dCommando.getMovement().up(100, 50);
				Thread.sleep(500);
				System.out.println("Too low");

			}
			return false;
		}
	}

}
