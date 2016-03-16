package dk.gruppe5.drone.webcamtest;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import dk.gruppe5.drone.window.CustomOutputStream;

public class PWindow {
	
	ImageIcon image = new ImageIcon("BillederTilDrone.png");
	JLabel imageLabel = new JLabel(image);
	
	private JTextArea textArea;
	private PrintStream standardOut;
	
	public PWindow(int w, int h) {
		textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        PrintStream printStream = new PrintStream(new CustomOutputStream(textArea));
         
        // keeps reference of standard output stream
        standardOut = System.out;
         
        // re-assigns standard output stream and error output stream
        System.setOut(printStream);
        System.setErr(printStream);
        
		JFrame frame = new JFrame();
		frame.setSize(w, h);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Super programmet");
		
		PPanel panel = new PPanel();
		panel.setSize(new Dimension(700, 400));
		//panel.setPreferredSize(new Dimension(700, 400));
		frame.setLayout(new GridLayout(0,2));
		
		frame.add(new JScrollPane(textArea));
		frame.add(new JButton("Button 3"));
        //frame.add(imageLabel);
        frame.add(panel);
		frame.setVisible(true);
		
		Thread thread = new Thread(panel);
		thread.start();
		
		
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	//Nødløsning, nu slukker programmet da :P
		        thread.stop();
		    }
		});
		
	}

}
