package dk.gruppe5.view;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManager;

import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManagerAlternative;
import de.yadrone.apps.tutorial.TutorialVideoListener;


import dk.gruppe5.controller.DroneCommander;
import dk.gruppe5.model.Values_cam;

public class PWindow {

	private JTextArea textArea;
	Values_cam vall = Values_cam.getInstance();

	DroneCommander dCommando;
	
	public PWindow(int w, int h) {

		textArea = new JTextArea(50, 10);
		textArea.setEditable(false);
		PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));


		JFrame droneOrWebcamFrame = new JFrame();
		droneOrWebcamFrame.setSize(w,h);
		droneOrWebcamFrame.setTitle("Chose a video source");

		JFrame frame = new JFrame();
		frame.setSize(w, h);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Super programmet");


		JButton btnSelectDrone = new JButton("Use drone");
		JButton btnSelectWebcam = new JButton("Use webcam");

		JPanel droneOrWebcamPanel = new JPanel();
		droneOrWebcamPanel.setLayout(new GridLayout(0,1));
		droneOrWebcamFrame.add(droneOrWebcamPanel);
		droneOrWebcamPanel.add(btnSelectDrone);
		droneOrWebcamPanel.add(btnSelectWebcam);

		Filterstates filters = new Filterstates();
		Thread filtersThread = new Thread(filters);
		filtersThread.start();

		frame.setLayout(new GridLayout(2, 2));

		JPanel leftPanel = new JPanel();
		JPanel innerLeftPanel = new JPanel();
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(0, 1));
		leftPanel.setLayout(new GridLayout(4, 1));
		innerLeftPanel.setLayout(new GridLayout(1, 2));

		JButton btnUpdate = new JButton("UPDATE");
		JButton btnTakeoff = new JButton("TAKEOFF");
		JButton btnEmergency = new JButton("KILL IT!");
		JButton btnLand = new JButton("LAND");

		JTextField txtMethod = new JTextField();
		txtMethod.setHorizontalAlignment(JTextField.CENTER);


		droneOrWebcamFrame.setVisible(true);

		leftPanel.add(innerLeftPanel);
		innerLeftPanel.add(btnUpdate);
		innerLeftPanel.add(txtMethod);
		leftPanel.add(btnTakeoff);
		leftPanel.add(btnLand);
		leftPanel.add(btnEmergency);
		btnEmergency.setForeground(Color.RED);
		/*
		 * Button-functionality
		 */

		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Values_cam.setMethod(Integer.parseInt(txtMethod.getText()));
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
			}
		});

		btnEmergency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				System.out.println("KILL IT");
				//				 dCommando.droneKillAll();
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
			}
		});

		btnTakeoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TAKEOFF");
				//				dCommando.droneFlightControl();
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
			}
		});

		btnLand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TAKEOFF");
				// dCommando.droneLanding();
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
			}
		});

		btnSelectDrone.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				dCommando = new DroneCommander();
				VideoListenerPanel panel = new VideoListenerPanel(dCommando.getDrone());
				new Thread(panel).start();
				frame.setFocusable(true);
				frame.addKeyListener(new KeyboardCommandManager((dCommando.getDrone())));
				frame.addKeyListener(new KeyboardCommandManagerAlternative(dCommando.getDrone()));

				leftPanel.add(innerLeftPanel);
				innerLeftPanel.add(btnUpdate);
				innerLeftPanel.add(txtMethod);
				leftPanel.add(btnTakeoff);
				leftPanel.add(btnLand);
				leftPanel.add(btnEmergency);
				btnEmergency.setForeground(Color.RED);

				frame.add(panel);
				rightPanel.add(filters);
				frame.add(rightPanel);
				frame.add(leftPanel);
				frame.setVisible(true);
			}
		});

		btnSelectWebcam.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){

				PPanel videoFeed = new PPanel();
				Thread camThread = new Thread(videoFeed);
				camThread.start();
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						//N�dl�sning, nu slukker programmet da :P
						camThread.stop();
					}
				});

				leftPanel.add(innerLeftPanel);
				innerLeftPanel.add(btnUpdate);
				innerLeftPanel.add(txtMethod);
				leftPanel.add(btnTakeoff);
				leftPanel.add(btnLand);
				leftPanel.add(btnEmergency);
				btnEmergency.setForeground(Color.RED);

				frame.add(videoFeed);
				rightPanel.add(filters);
				frame.add(rightPanel);
				frame.add(leftPanel);
				frame.setVisible(true);	
			}
		});
	}
}

// Values_cam valcam = Values_cam.getInstance();
// DroneCommander dc = new DroneCommander();
//
/// *
// * init window and frame
// */
// public PWindow(int w, int h) {
//
// JFrame frame = new JFrame();
// frame.setSize(w, h);
// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// frame.setTitle("Luftens Helte");
//
// PPanel mainPanel = new PPanel();
// Thread thread = new Thread(mainPanel);
// thread.start();
// frame.addWindowListener(new WindowAdapter() {
// @Override
// public void windowClosing(WindowEvent e) {
// thread.stop();
// }
// });
//
// mainPanel.setSize(new Dimension(1280, 720));
// frame.setLayout(new GridLayout(0,1));
// frame.add(mainPanel);
// mainPanel.setLayout(new GridLayout(0,2));
//
// JPanel leftpanel = new JPanel();
// JPanel rightpanel = new JPanel();
// mainPanel.add(leftpanel);mainPanel.add(rightpanel);
//
// JButton btnUpdate = new JButton("UPDATE");
// JButton btnTakeoff = new JButton("TAKEOFF");
// JButton btnLand = new JButton("LAND");
// JButton btnEmergency = new JButton("EMERGENCY");
// leftpanel.add(btnUpdate);leftpanel.add(btnTakeoff);leftpanel.add(btnLand);leftpanel.add(btnEmergency);
//
//
// }

// JButton Button3 = new JButton("Opticalflow");rightPanel.add(Button3);
// JButton Button4 = new JButton("Find Contours");rightPanel.add(Button4);
// JButton Button5 = new JButton("Template Matching");rightPanel.add(Button5);
// JButton Button6 = new JButton("Find Airfields");rightPanel.add(Button6);
//
// JLabel lblNumber1 = new JLabel("Canny");
// JLabel lblNumber2 = new JLabel("Good Features To Track");

// Oprettelse af input felter til Canny

// JPanel jp = new JPanel();
// jp.setLayout(new GridLayout(0,2));
//
// JTextField textField_1 = new
// JTextField();textField_1.setText(""+Values_cam.getCanTres1());
// JTextField textField_2 = new
// JTextField();textField_2.setText(""+Values_cam.getCanTres2());
// JTextField textField_3 = new
// JTextField();textField_3.setText(""+Values_cam.getCanAp());
//// Oprettelse af input felter til Good Features to Track
// JTextField textField_4 = new
// JTextField();textField_4.setText(""+Values_cam.getCorn());
// JTextField textField_5 = new
// JTextField();textField_5.setText(""+Values_cam.getQual());
// JTextField textField_6 = new
// JTextField();textField_6.setText(""+Values_cam.getDist());
// JTextField textField_7 = new
// JTextField();textField_7.setText(""+Values_cam.getCirclePrecision());
// JTextField textField_8 = new
// JTextField();textField_8.setText(""+Values_cam.getMethod());
//// Oprettelse af update og reset knap
// JButton updateBtn = new JButton("UPDATE");
// JButton repaintBtn = new JButton("Repaint");
//
//// Overskift for Canny
// jp.add(new JLabel(""));jp.add(new JLabel("Method"));jp.add(new JLabel("Circle
// Precision"));jp.add(new JLabel(""));
// jp.add(leftPanel);jp.add(rightPanels);jp.add(textField_7);jp.add(btnTakeoff);
// jp.add(new JLabel(""));jp.add(new JLabel("Treshold1"));jp.add(new
// JLabel("Treshold2"));jp.add(new JLabel("Aperture"));
//// Input til Canny
// jp.add(lblNumber1);jp.add(textField_1);jp.add(textField_2);jp.add(textField_3);
//// Overskrift for Good Features to Track
// jp.add(new JLabel(""));jp.add(new JLabel("Max Corners"));jp.add(new
// JLabel("Quality Level"));jp.add(new JLabel("Min Distance"));
//// Input til Good Features to Track
// jp.add(lblNumber2);jp.add(textField_4);jp.add(textField_5);jp.add(textField_6);
//// Update knap
// jp.add(new
// JLabel(""));jp.add(updateBtn);jp.add(repaintBtn);jp.add(btnEmergency);
//
//
// frame.add(jp);

// updateBtn.addActionListener(new ActionListener() {
//
// @SuppressWarnings("deprecation")
// public void actionPerformed(ActionEvent arg0) {
// Values_cam.setCanTres1(Double.parseDouble(textField_1.getText()));
// Values_cam.setCanTres2(Double.parseDouble(textField_2.getText()));
// Values_cam.setCanAp(Integer.parseInt(textField_3.getText()));
// Values_cam.setCorn(Integer.parseInt(textField_4.getText()));
// Values_cam.setQual(Double.parseDouble(textField_5.getText()));
// Values_cam.setDist(Double.parseDouble(textField_6.getText()));
// Values_cam.setCirclePrecision(Double.parseDouble(textField_7.getText()));
// SwingUtilities.updateComponentTreeUI(frame);
// frame.invalidate();
// frame.validate();
// frame.repaint();
// //panel.clear();
// }
//
//
// });
//
// repaintBtn.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent arg0) {
// Values_cam.setCanTres2(1000);
// frame.removeAll();
// frame.validate();
// frame.setVisible(false);
// // thread.stop();
// Values_cam.setCanTres1(50);
// Values_cam.setCanTres2(100);
// Values_cam.setCanAp(3);
// Values_cam.setCorn(500);
// Values_cam.setQual(0.1);
// Values_cam.setDist(10);
// Values_cam.setCirclePrecision(0.2);
//
// WebcamTest.main(null);
// }
//
// });
// btnUpdate.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent arg0) {
// Values_cam.setMethod(0);
// SwingUtilities.updateComponentTreeUI(frame);
// frame.invalidate();
// frame.validate();
// frame.repaint();
// }
// });
// btnTakeoff.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent arg0) {
// System.out.println("Takeoff er presset");
// dCommando.droneFlightControl();
// SwingUtilities.updateComponentTreeUI(frame);
// frame.invalidate();
// frame.validate();
// frame.repaint();
// }
// });
// Button3.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent arg0) {
// Values_cam.setMethod(1);
// SwingUtilities.updateComponentTreeUI(frame);
// frame.invalidate();
// frame.validate();
// frame.repaint();
// }
// });
// Button4.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent arg0) {
// Values_cam.setMethod(2);
// SwingUtilities.updateComponentTreeUI(frame);
// frame.invalidate();
// frame.validate();
// frame.repaint();
// }
// });
// Button5.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent arg0) {
// Values_cam.setMethod(3);
// SwingUtilities.updateComponentTreeUI(frame);
// frame.invalidate();
// frame.validate();
// frame.repaint();
// }
// });
// Button6.addActionListener(new ActionListener() {
// public void actionPerformed(ActionEvent arg0) {
// Values_cam.setMethod(4);
// SwingUtilities.updateComponentTreeUI(frame);
// frame.invalidate();
// frame.validate();
// frame.repaint();
// }
// });
