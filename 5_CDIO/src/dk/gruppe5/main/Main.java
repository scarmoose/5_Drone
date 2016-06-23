/**
 * 
 */
package dk.gruppe5.main;

import org.opencv.core.Core;

import CoordinateSystem.Lines;
import dk.gruppe5.view.PWindow;
import dk.gruppe5.view.ThresholdChanger;

/**
 * @author thomas
 *
 */
public class Main {

	/**
	 * @param args
	 */
	
	Lines line = new Lines();
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		PWindow window = new PWindow(1280, 720);
		Lines.main(null);
		ThresholdChanger changer = new ThresholdChanger(250,150);
	}

}
