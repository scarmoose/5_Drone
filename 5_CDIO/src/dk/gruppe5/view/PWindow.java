package dk.gruppe5.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import de.yadrone.apps.controlcenter.plugins.keyboard.KeyboardCommandManager;

import de.yadrone.apps.tutorial.TutorialVideoListener;

import dk.gruppe5.controller.DroneCommander;
import dk.gruppe5.model.Values_cam;
import dk.gruppe5.test.WebcamTest;

public class PWindow {
	
	//ImageIcon image = new ImageIcon("BillederTilDrone.png");
	//JLabel imageLabel = new JLabel(image);
	
	private JTextArea textArea;
	private PrintStream standardOut;
	
	Values_cam vall = Values_cam.getInstance();
	public JTextField textField;
	public JTextField textField_1;
	public JTextField textField_2;
	public JTextField textField_3;
	public JTextField textField_4;
	public JTextField textField_5;
	
	DroneCommander dCommando = new DroneCommander();
	
	public PWindow(int w, int h) {

		textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
        
        // keeps reference of standard output stream
        standardOut = System.out;
         
        // re-assigns standard output stream and error output stream
//        System.setOut(printStream);
//        System.setErr(printStream);

        
		JFrame frame = new JFrame();
		frame.setSize(w, h);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Super programmet");
		
		
		/*
		 * Indkommenternedenst�ende for at bruge webcam
		 */

		PPanel panel = new PPanel();
		Thread thread = new Thread(panel);
		thread.start();
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	//N�dl�sning, nu slukker programmet da :P
		        thread.stop();
		    }
		});
		
		/*
		 * nedenst�ende bruger dronen.
		 */


	
//
//		VideoListenerPanel panel = new VideoListenerPanel(dCommando.getDrone());
//		new Thread(panel).start();;
//		frame.setFocusable(true);
//		frame.addKeyListener(new KeyboardCommandManager((dCommando.getDrone())));
		

//		panel.setSize(new Dimension(700, 400));
		
		//panel.setPreferredSize(new Dimension(700, 400));
		frame.setLayout(new GridLayout(0,1));
		
		//frame.add(new JScrollPane(textArea));
	//	frame.add(new JButton("Button 3"));
        //frame.add(imageLabel);
        frame.add(panel);        
        
        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(0,4));
        
        JPanel jButz = new JPanel();
        jButz.setLayout(new GridLayout(0,1));
        JButton Button1 = new JButton("Standard");jButz.add(Button1);
        JButton Button2 = new JButton("Take Off");
        
        JPanel jButs = new JPanel(); 
        jButs.setLayout(new GridLayout(0,2));
        
        JButton Button3 = new JButton("Opticalflow");jButs.add(Button3);
        JButton Button4 = new JButton("Find Contours");jButs.add(Button4);
        JButton Button5 = new JButton("Template Matching");jButs.add(Button5);
        JButton Button6 = new JButton("Find Airfields");jButs.add(Button6);
        JButton Button7 = new JButton("Kill Switch");jButs.add(Button7); Button7.setForeground(Color.RED);
       
        
        
        JLabel lblNumber1 = new JLabel("Canny");
        JLabel lblNumber2 = new JLabel("Good Features To Track");
        
        //Oprettelse af input felter til Canny
        
        JTextField textField_1 = new JTextField();textField_1.setText(""+Values_cam.getCanTres1());
        JTextField textField_2 = new JTextField();textField_2.setText(""+Values_cam.getCanTres2());
        JTextField textField_3 = new JTextField();textField_3.setText(""+Values_cam.getCanAp());
        //Oprettelse af input felter til Good Features to Track
        JTextField textField_4 = new JTextField();textField_4.setText(""+Values_cam.getCorn());
        JTextField textField_5 = new JTextField();textField_5.setText(""+Values_cam.getQual());
        JTextField textField_6 = new JTextField();textField_6.setText(""+Values_cam.getDist());
        JTextField textField_7 = new JTextField();textField_7.setText(""+Values_cam.getCirclePrecision());
        JTextField textField_8 = new JTextField();textField_8.setText(""+Values_cam.getMethod());
        //Oprettelse af update og reset knap
        JButton updateBtn = new JButton("UPDATE");
        JButton repaintBtn = new JButton("Repaint");

        //Overskift for Canny
        jp.add(new JLabel(""));jp.add(new JLabel("Method"));jp.add(new JLabel("Circle Precision"));jp.add(new JLabel(""));
        jp.add(jButz);jp.add(jButs);jp.add(textField_7);jp.add(Button2);
        jp.add(new JLabel(""));jp.add(new JLabel("Treshold1"));jp.add(new JLabel("Treshold2"));jp.add(new JLabel("Aperture"));
        //Input til Canny
        jp.add(lblNumber1);jp.add(textField_1);jp.add(textField_2);jp.add(textField_3);
        //Overskrift for Good Features to Track
        jp.add(new JLabel(""));jp.add(new JLabel("Max Corners"));jp.add(new JLabel("Quality Level"));jp.add(new JLabel("Min Distance"));
        //Input til Good Features to Track
        jp.add(lblNumber2);jp.add(textField_4);jp.add(textField_5);jp.add(textField_6);
        //Update knap
        jp.add(new JLabel(""));jp.add(updateBtn);jp.add(repaintBtn);jp.add(Button7);
        
        
		frame.add(jp);
		
		frame.setVisible(true);
		
		updateBtn.addActionListener(new ActionListener() {

            @SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
            	Values_cam.setCanTres1(Double.parseDouble(textField_1.getText()));
            	Values_cam.setCanTres2(Double.parseDouble(textField_2.getText()));
            	Values_cam.setCanAp(Integer.parseInt(textField_3.getText()));
            	Values_cam.setCorn(Integer.parseInt(textField_4.getText()));
            	Values_cam.setQual(Double.parseDouble(textField_5.getText()));
            	Values_cam.setDist(Double.parseDouble(textField_6.getText()));
            	Values_cam.setCirclePrecision(Double.parseDouble(textField_7.getText()));
            	SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            	//panel.clear();
            }
            
            
        });

		repaintBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Values_cam.setCanTres2(1000);
	            frame.removeAll();
	            frame.validate();
	            frame.setVisible(false);
	            // thread.stop();
	            Values_cam.setCanTres1(50);
	            Values_cam.setCanTres2(100);
	            Values_cam.setCanAp(3);
	            Values_cam.setCorn(500);
	            Values_cam.setQual(0.1);
	            Values_cam.setDist(10);
	            Values_cam.setCirclePrecision(0.2);
	            
	            WebcamTest.main(null);
            }
            
        });
		Button1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	            Values_cam.setMethod(0);
	            SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            }
        });
		Button2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Takeoff er presset");
				dCommando.droneFlightControl();
	            SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            }
        });		
		Button3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	            Values_cam.setMethod(1);
	            SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            }
        });
		Button4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	            Values_cam.setMethod(2);
	            SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            }
        });
		Button5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	            Values_cam.setMethod(3);
	            SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            }
        });
		Button6.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
	            Values_cam.setMethod(4);
	            SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            }
        });
		Button7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("KILL IT");
//				dCommando.droneKillAll();

	            SwingUtilities.updateComponentTreeUI(frame);
            	frame.invalidate();
            	frame.validate();
            	frame.repaint();
            }
        });
	}

}
