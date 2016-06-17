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

	Values_cam vall = Values_cam.getInstance();


	DroneCommander dCommando;


	public PWindow(int w, int h) {

		JFrame droneOrWebcamFrame = new JFrame();
		droneOrWebcamFrame.setSize(w,h);
		droneOrWebcamFrame.setTitle("Choose a video source");

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
				dCommando.droneFlightControl();
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
				//frame.addKeyListener(new KeyboardCommandManagerAlternative(dCommando.getDrone()));

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