/**
 * 
 */
package dk.gruppe5.test;

import org.opencv.core.Core;

import CoordinateSystem.Lines;
import dk.gruppe5.view.PWindow;

/**
 * @author thomas
 *
 */
public class WebcamTest {

	/**
	 * @param args
	 */
	
	Lines line = new Lines();
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		PWindow window = new PWindow(1280, 720);
		Lines.main(null);
		
		//test 123
	}

}
