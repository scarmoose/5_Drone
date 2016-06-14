package dk.gruppe5.view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dk.gruppe5.model.Values_cam;

public class ThresholdChanger {

	public ThresholdChanger(int x, int y){
		
		JFrame frame = new JFrame();
		frame.setSize(x,y);
		frame.setTitle("Thresholds");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(3,2));
		
		JButton btnUpdate = new JButton("UPDATE");
		
		JTextField txtCannyThreshold = new JTextField();
		JTextField txtCannyThreshold2 = new JTextField();
		JTextField txtCannyAperature = new JTextField();
		
		frame.add(mainPanel);
		mainPanel.add(txtCannyThreshold);
		mainPanel.add(txtCannyThreshold2);
		mainPanel.add(txtCannyAperature);
		mainPanel.add(btnUpdate);
		frame.setVisible(true);
		
		btnUpdate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				Values_cam.setCanTres1(Double.parseDouble(txtCannyThreshold.getText()));
				Values_cam.setCanTres2(Double.parseDouble(txtCannyThreshold2.getText()));
				Values_cam.setCanAp(Integer.parseInt(txtCannyAperature.getText()));
			}
		});
	}
}
