package dk.gruppe5.drone.webcamtest;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.PrintStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import dk.gruppe5.drone.window.CustomOutputStream;

public class PWindow {
	
	ImageIcon image = new ImageIcon("BillederTilDrone.png");
	JLabel imageLabel = new JLabel(image);
	
	private JTextArea textArea;
	private PrintStream standardOut;
	
	Values_cam vall = new Values_cam();
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	
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

        JPanel jp = new JPanel();
        jp.setLayout(new GridLayout(0,4));
        JLabel lblNumber1 = new JLabel("Canny");
        JLabel lblNumber2 = new JLabel("Good Features To Track 1");
        JLabel lblNumber3 = new JLabel("Good Features To Track 2");
        JTextField textField_1 = new JTextField();
        textField_1.setText(""+vall.getCanTres1());
        JTextField textField_2 = new JTextField();
        textField_2.setText(""+vall.getCanTres2());
        JTextField textField_3 = new JTextField();
        textField_3.setText(""+vall.getCanAp());
        JTextField textField_4 = new JTextField();
        textField_4.setText(""+vall.getCorn1());
        JTextField textField_5 = new JTextField();
        textField_5.setText(""+vall.getQual1());
        JTextField textField_6 = new JTextField();
        textField_6.setText(""+vall.getDist1());
        JTextField textField_7 = new JTextField();
        textField_7.setText(""+vall.getCorn2());
        JTextField textField_8 = new JTextField();
        textField_8.setText(""+vall.getQual2());
        JTextField textField_9 = new JTextField();
        textField_9.setText(""+vall.getDist2());
        JButton updateBtn = new JButton("UPDATE");
        jp.add(new JLabel(""));
        jp.add(new JLabel("Treshold1"));
        jp.add(new JLabel("Treshold2"));
        jp.add(new JLabel("Aperture"));
        jp.add(lblNumber1);
        jp.add(textField_1);
        jp.add(textField_2);
        jp.add(textField_3);
        jp.add(new JLabel(""));
        jp.add(new JLabel("Max Corners"));
        jp.add(new JLabel("Quality Level"));
        jp.add(new JLabel("Min Distance"));
        jp.add(lblNumber2);
        jp.add(textField_4);
        jp.add(textField_5);
        jp.add(textField_6);
        jp.add(lblNumber3);
        jp.add(textField_7);
        jp.add(textField_8);
        jp.add(textField_9);
        jp.add(new JLabel(""));
        jp.add(updateBtn);
        
		frame.add(jp);
		
		frame.setVisible(true);
		
		Thread thread = new Thread(panel);
		thread.start();
		
		
		frame.addWindowListener(new WindowAdapter() {
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	//N�dl�sning, nu slukker programmet da :P
		        thread.stop();
		    }
		});
		
		updateBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
            	vall.setCanTres1(Integer.parseInt(textField_1.getText()));
            	vall.setCanTres2(Integer.parseInt(textField_2.getText()));
            	vall.setCanAp(Integer.parseInt(textField_3.getText()));
            	vall.setCorn1(Integer.parseInt(textField_4.getText()));
            	vall.setQual1(Double.parseDouble(textField_5.getText()));
            	vall.setDist1(Double.parseDouble(textField_6.getText()));
            	vall.setCorn2(Integer.parseInt(textField_7.getText()));
            	vall.setQual2(Double.parseDouble(textField_8.getText()));
            	vall.setDist2(Double.parseDouble(textField_9.getText()));
            	panel.repaint();
            }
        });
		
	}

}
