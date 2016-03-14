/**
 * 
 */
package dk.gruppe5.drone.webcamtest;

import org.opencv.core.Core;

/**
 * @author thomas
 *
 */
public class WebcamTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		PWindow window = new PWindow(1280, 720);
	}

}
