package CoordinateSystem;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;

import dk.gruppe5.controller.Mathmagic;

public class Lines extends JFrame {

    public Lines() {
        initUI();
    }
    
    

    private void initUI() {

        add(new Surface());

        setTitle("Lines");
        //Size of window
        setSize(900/2+80, 1100/2+80);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
                
				/*ArrayList<Integer> Xelements = new ArrayList<>();
				ArrayList<Integer> Yelements = new ArrayList<>();
				for (int i = 0 ; i < Mathmagic.getArray().length ; i++){
					Xelements.add((int) Mathmagic.getArray()[i].getX());
					Yelements.add((int) Mathmagic.getArray()[i].getY());
				}
            	
				for (int k = 0;k< Xelements.size();k++){
					int xvalue = Xelements.get(k);
					int yvalue = Yelements.get(k);
					System.out.println("x = " + xvalue + ", y= "+yvalue);
				}*/
				
            	//System.out.println(Mathmagic.getArray()[0].getX());
                Lines ex = new Lines();
                ex.setVisible(true);
            }
        });
    }
}
