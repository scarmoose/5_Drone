package dk.gruppe5.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.opencv.core.Mat;

public class Filterstates extends JPanel implements Runnable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1707981331137474391L;
	static int WEBCAM = 0;
	private static volatile BufferedImage image1;
	private static volatile BufferedImage image2;
	private static volatile BufferedImage image3;
	private static volatile BufferedImage image4;
	
	public Filterstates(){
		
	}
	
	public static void setImage1(BufferedImage image){
		image1 = image;
	}

	public static void setImage2(BufferedImage image){
		image2 = image;
	}

	public static void setImage3(BufferedImage image){
		image3 = image;
	}

	public static void setImage4(BufferedImage image){
		image4 = image;
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g);
		
		int x = this.getWidth();
		int y = this.getHeight();
			
		if(image1 != null){
			g.drawImage(image1, 0, 0,	x, y, null);			
		}
		
		if(image2 != null){
			g.drawImage(image2, x/2, 0, x/2,y/2, null);
		}
		
		if(image3 != null){
			g.drawImage(image3, 0, y/2, x/2, y/2, null);
		}
		
		if(image4 != null){
			g.drawImage(image4, x/2, y/2, x/2, y/2, null);
		}
	}
	
	public void run(){
		while(true){
			repaint();
		}
	}
}
