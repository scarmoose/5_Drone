package dk.gruppe5.drone.yaDroneFeed;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.videoio.VideoCapture;

import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.video.ImageListener;
import dk.gruppe5.drone.openCV.ImageProcessor;
import dk.gruppe5.shared.opticalFlowData;

public class TutorialVideoListener extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5575916801733831478L;

	ImageProcessor imgProc = new ImageProcessor();
	BufferedImage Image;
	VideoCapture capture;
	ImageProcessor imgproc;

	List<Point> startPoints;
	List<Point> endPoints;
	Point direction;
	Mat old_frame;

	public TutorialVideoListener(final IARDrone drone) {
		imgproc = new ImageProcessor();

		drone.getVideoManager().addImageListener(new ImageListener() {

			public void imageUpdated(BufferedImage newImage) {
			
				Mat frame = imgProc.bufferedImageToMat(newImage);
				if (old_frame == null) {
					old_frame = frame;
				}
				opticalFlowData flowData = imgproc.opticalFlow(frame, old_frame);
				
				Mat ofs_frame = flowData.getFrame();
				Image = imgproc.toBufferedImage(ofs_frame);
				old_frame = frame;
			
				
				startPoints = flowData.getStartPoints();
				endPoints = flowData.getEndPoints();
				double angletotal = 0;
				
				for (int i = 0; i < startPoints.size(); i++) {
					Point one = startPoints.get(i);
					Point two = endPoints.get(i);

					Double distance = Math.sqrt(Math.pow(one.x - two.x, 2) + Math.pow(one.y - two.y, 2));

					if (distance > 5) {
						// System.out.println("Distance: "+distance);
						double angle = Math.atan2(two.y - one.y, two.x - one.x);
						double angle2 = angle * (180 / Math.PI);
						// System.out.println(angle2);
						if (angle2 < 0) {
							// System.out.println(angle2);
							angletotal = angletotal + angle2 + 360;
						} else {
							// System.out.println(angle2);
							angletotal = angletotal + angle2;
						}
					}

				}
				
				// System.out.println("totalAngle:"+angletotal);
				// System.out.println("AverageAngle:"+angletotal/startPoints.size());
				double avAngle = angletotal / startPoints.size();

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
		if (Image != null) {
			g.drawImage(Image, 0, 0, Image.getWidth(), Image.getHeight(), null);
		}
	}

	public BufferedImage getImage() {
		return Image;
	}

}