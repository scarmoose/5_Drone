package dk.gruppe5.view;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManager;
import dk.gruppe5.ai.DecisionMaker;
import dk.gruppe5.ai.DecisionMakerNr2;
import dk.gruppe5.controller.DroneCommander;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.positioning.Position;

public class PWindow {

	Values_cam vall = Values_cam.getInstance();


	DroneCommander dCommando;
	DecisionMaker dm;


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
		droneOrWebcamPanel.setFocusable(false);
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
		leftPanel.setFocusable(false);
		innerLeftPanel.setFocusable(false);
		rightPanel.setFocusable(false);
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
				frame.toFront();
			}
		});

		btnEmergency.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("KILL ALL");
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
			}
		});

		btnTakeoff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TAKEOFF");
				dCommando.droneTakeOff();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Position.isFlying = true;
				new Thread(dm).start();
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
			}
		});

		btnLand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TAKEOFF");
				dCommando.getDrone().landing();
				SwingUtilities.updateComponentTreeUI(frame);
				frame.invalidate();
				frame.validate();
				frame.repaint();
			}
		});

		btnSelectDrone.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				dCommando = new DroneCommander();
				DecisionMakerNr2 dm2 = new DecisionMakerNr2(dCommando);
				DronePanel panel = new DronePanel(dCommando.getDrone());
				new Thread(dm2).start();
				new Thread(panel).start();
				frame.setVisible(true);
				frame.setFocusable(true);
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
				frame.addKeyListener(new KeyboardCommandManager((dCommando.getDrone())));
			
			}
		});

		btnSelectWebcam.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){

				WebcamPanel videoFeed = new WebcamPanel();
				Thread camThread = new Thread(videoFeed);
				camThread.start();
				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
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