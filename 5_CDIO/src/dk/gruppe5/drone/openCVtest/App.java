package dk.gruppe5.drone.openCVtest;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;
import dk.gruppe5.drone.openCV.ImageProcessor;
import dk.gruppe5.drone.yaDroneFeed.TutorialVideoListener;

public class App {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	}

	public IARDrone drone;
	CommandManager cmd;
	private JFrame frame;
	private JPanel panel;
	TutorialVideoListener tutVidList;
	ImageProcessor imgProce = new ImageProcessor(); 


	public void start() throws Exception {

		upStart();
		runVidCapture();
		initGUI();
		
		/*
		 * String filePath = "C:\\PictureManipulation\\test1.jpg"; Mat newImage
		 * = Imgcodecs.imread(filePath); if (newImage.dataAddr() == 0) {
		 * System.out.println("Couldn't open file " + filePath); } else {
		 * ImageViewer imageViewer = new ImageViewer();
		 * imageViewer.show(newImage, "Loaded image"); }
		 */

	}

	private void runVidCapture() {

		tutVidList = new TutorialVideoListener(drone);
		
	}

	private void initGUI() {

		// create window

		frame = new JFrame("Drone Camera feed");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);
		panel = new JPanel(new BorderLayout());
		tutVidList.setPreferredSize(new Dimension(600,400));
		panel.add(tutVidList,BorderLayout.PAGE_START);
		frame.add(panel);
		setupSlider(panel);
		
		/*
		
		try {
		    File img = new File("lena.png");
		    BufferedImage image = ImageIO.read(img); 
		    image = imgProce.toBufferedImage(imgProce.opticalFlow(imgProce.bufferedImageToMat(image), imgProce.bufferedImageToMat(image)));
		    
		    ImageIcon imgIcon = new ImageIcon(image);
		    JLabel label = new JLabel();
		    label.setIcon(imgIcon);
		    
		    panel.add(label);
		    System.out.println(image);
		} catch (IOException e) { 
		    e.printStackTrace(); 
		}
		
		*/
		frame.setVisible(true);
	

	}

	public void upStart() {

		drone = new ARDrone();
		drone.start();
		
	}

	private void setupSlider(JPanel frame) {
		JLabel sliderLabel = new JLabel("Blur level", JLabel.CENTER);
		sliderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		int minimum = 0;
		int maximum = 10;
		int initial = 0;
		JSlider levelSlider = new JSlider(JSlider.HORIZONTAL, minimum, maximum, initial);
		levelSlider.setMajorTickSpacing(2);
		levelSlider.setMinorTickSpacing(1);
		levelSlider.setPaintTicks(true);
		levelSlider.setPaintLabels(true);
		levelSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int level = (int) source.getValue();
				System.out.println(""+level);
				tutVidList.setBlurLevel(level);
				//Mat output = imgProce.blur(imgProce.bufferedImageToMat(tutVidList.getImage()), level);
				
			}
		});
		
		//frame.add(sliderLabel,BorderLayout.WEST);
		frame.add(sliderLabel,BorderLayout.CENTER);
		frame.add(levelSlider,BorderLayout.PAGE_END);
	}

}