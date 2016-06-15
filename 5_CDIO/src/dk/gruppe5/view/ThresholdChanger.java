package dk.gruppe5.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dk.gruppe5.model.Values_cam;

public class ThresholdChanger {
	
	public ThresholdChanger(int x, int y){
		
		JFrame frame = new JFrame();
		frame.setSize(x,y);
		frame.setTitle("Thresholds");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1,2));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(4,1));
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridLayout(4,1));
		
		JButton btnUpdate = new JButton("UPDATE");
		
		JTextField txtCannyThreshold = new JTextField();
		txtCannyThreshold.setHorizontalAlignment(JTextField.CENTER);
		JTextField txtCannyThreshold2 = new JTextField();
		txtCannyThreshold2.setHorizontalAlignment(JTextField.CENTER);
		JTextField txtCannyAperture = new JTextField();
		txtCannyAperture.setHorizontalAlignment(JTextField.CENTER);
		
		JLabel lblCannyThreshold = new JLabel("Canny Threshold");
		JLabel lblCannyThreshold2 = new JLabel("Canny2 Threshold");
		JLabel lblCannyAperture = new JLabel("Aperture Threshold"); 
		
		frame.add(mainPanel);
		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		
		leftPanel.add(txtCannyThreshold);
		leftPanel.add(txtCannyThreshold2);
		leftPanel.add(txtCannyAperture);
		leftPanel.add(btnUpdate);
		
		rightPanel.add(lblCannyThreshold);
		rightPanel.add(lblCannyThreshold2);
		rightPanel.add(lblCannyAperture);
		
		frame.setVisible(true);
		
		btnUpdate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				
				if(!(txtCannyThreshold.getText().equals(""))){					
					Values_cam.setCanTres1(Double.parseDouble(txtCannyThreshold.getText()));
				}
				
				if(!(txtCannyThreshold2.getText().equals(""))){
					Values_cam.setCanTres2(Double.parseDouble(txtCannyThreshold2.getText()));
				}
				
				if(Integer.parseInt(txtCannyAperture.getText()) % 2 == 1 && Integer.parseInt(txtCannyAperture.getText()) != 1){
					Values_cam.setCanAp(Integer.parseInt(txtCannyAperture.getText()));
					System.out.println("aperture changed");
				}
				
				if(Integer.parseInt(txtCannyAperture.getText()) % 2 == 0 || Integer.parseInt(txtCannyAperture.getText()) == 0  || Integer.parseInt(txtCannyAperture.getText()) == 1){
					JOptionPane.showMessageDialog(frame, "Aperture must be an odd number!");
				}
			}
		});
	}
}
