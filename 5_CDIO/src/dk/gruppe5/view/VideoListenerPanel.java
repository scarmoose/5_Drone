package dk.gruppe5.view;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.videoio.VideoCapture;

import com.google.zxing.Result;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;
import dk.gruppe5.framework.ImageProcessor;
import dk.gruppe5.model.Shape;
import dk.gruppe5.model.Values_cam;

public class VideoListenerPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5575916801733831478L;

	
	BufferedImage image;
	VideoCapture capture;
	ImageProcessor imgProc;

	List<Point> startPoints;
	List<Point> endPoints;
	Point direction;
	Mat old_frame;

	public VideoListenerPanel(final IARDrone drone) {
		imgProc = new ImageProcessor();

		drone.getVideoManager().addImageListener(new ImageListener() {

			public void imageUpdated(BufferedImage newImage) {
//				 try {
//					 Thread.sleep(200);
//					 } catch (InterruptedException e) {
//					 // TODO Auto-generated catch block
//					 e.printStackTrace();
//					 }
//				
				
				Mat frame = imgProc.bufferedImageToMat(newImage);
				if (old_frame == null) {
					old_frame = frame;
				}
				
				if(Values_cam.getMethod() == 0){
					image = imgProc.toBufferedImage(frame);
				} else 	if(Values_cam.getMethod() == 5){
					Mat backUp = new Mat();
					backUp = frame;
					//kig på whitebalancing og eventuelt at reducere området som vi kigger igennem for firkanter.
					//frame = imgProc.equalizeHistogramBalance(frame);
					
					//først gør vi det sort hvidt
					frame = imgProc.toGrayScale(frame);
					
					//
					frame = imgProc.equalizeHistogramBalance(frame);
					//Vi tester først med blur og ser hvor godt det bliver
					//prøv også uden
					//blur virker bedre
					frame = imgProc.blur(frame);
				
					//Til canny for at nemmere kunne finde contourer
					frame = imgProc.toCanny(frame);
					
					//Nu skal vi prøve at finde firkanter af en hvis størrelse
					List<Shape> shapes = imgProc.findQRsquares(frame);
					//vi finder de potentielle QR kode områder
					List<BufferedImage> potentialQRcodes = new ArrayList<BufferedImage>();
					BufferedImage source = imgProc.toBufferedImage(backUp);
					
					//place shapes on the backup image to test
					int z = 0;
					for (Shape rect : shapes) {
						int h = (int) rect.getHeight();
						int w = (int) rect.getWidth();
						BufferedImage dst = source.getSubimage((int)rect.getTlPoint().x, (int)rect.getTlPoint().y, w, h);
						potentialQRcodes.add(dst);
					}
					//Vi aflæser de potentielle QR koder og ser om vi har nogen matches, hvis vi har!
					//så marker dette og firkanter der har ca samme højde og størrelse!
					//skriv i disse hvilken en firkant de nok er ud fra dataene vi har.
					//tegn streg mellem dem og skriv pixel afstand
					//udregn afstand til QR kode via python afstands bestemmelse på papir
					
					List<Result> results = imgProc.readQRCodes(potentialQRcodes);
					//backUp = imgProc.markQrCodes(results, shapes, backUp);
					Point[] points = imgProc.markQrCodes(results, shapes, backUp);
					if(points != null){
						backUp = imgProc.drawLine( points[0],points[1],backUp);
						backUp = imgProc.drawLine(points[1], points[2], backUp);
						System.out.println("point1:" +points[0]+"   point 2:" +points[1]+ "   point 3:"+points[2]);	
					}else{
						image = imgProc.toBufferedImage(backUp);
					}
//				
//					
//					
//				
//					image = imgProc.toBufferedImage(backUp);
					image = imgProc.toBufferedImage(backUp);
				}
				
				
			
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						repaint();
					}
				});
			}

		
		});

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				drone.getCommandManager().setVideoChannel(VideoChannel.NEXT);
			}
		});
		

	}

	public synchronized void paint(Graphics g) {
		super.paintComponent(g);
		if (image != null) {
			g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
		}
	}

	public BufferedImage getImage() {
		return image;
	}

	
	public void directionGuess(double avAngle) {
		if (startPoints.size() > 30) {
			// System.out.println("Nr of vectors: "
			// +startPoints.size());

			if (avAngle > 315 && avAngle < 360) {
				System.out.println("Left - U");

			} else if (avAngle > 0 && avAngle < 45) {
				System.out.println("Left - D");

			} else if (avAngle > 45 && avAngle < 90) {
				System.out.println("Down - L");

			} else if (avAngle > 90 && avAngle < 135) {
				System.out.println("Down - R");

			} else if (avAngle > 135 && avAngle < 180) {
				System.out.println("Right - D");

			} else if (avAngle > 180 && avAngle < 225) {
				System.out.println("Right - U");

			} else if (avAngle > 225 && avAngle < 270) {
				System.out.println("Up - R");

			} else if (avAngle > 270 && avAngle < 315) {
				System.out.println("Up - L");

			}

		}
	}
}