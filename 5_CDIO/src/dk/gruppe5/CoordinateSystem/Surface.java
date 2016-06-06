package dk.gruppe5.CoordinateSystem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;
import dk.gruppe5.controller.Mathmagic;

class Surface extends JPanel {
	
	Mathmagic math = new Mathmagic();

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        //The four walls
        g2d.drawLine(20, 50, 930/2+20, 50);
        g2d.drawLine(20, 50, 20, 1060/2+50);
        g2d.drawLine(20, 1060/2+50, 930/2+20, 1060/2+50);
        g2d.drawLine(930/2+20, 1060/2+50, 930/2+20, 50);
      
        
        ArrayList<Integer> Xelements = new ArrayList<>();
		ArrayList<Integer> Yelements = new ArrayList<>();
		for (int i = 0 ; i < Mathmagic.getArray().length ; i++){
			Xelements.add((int) Mathmagic.getArray()[i].getX());
			Yelements.add((int) Mathmagic.getArray()[i].getY());
		}
    	
		for (int k = 0;k< Xelements.size();k++){
			int xvalue = Xelements.get(k);
			int yvalue = Yelements.get(k);
			BasicStroke bs1 = new BasicStroke(8, BasicStroke.CAP_BUTT,
			        BasicStroke.JOIN_BEVEL);
			g2d.setStroke(bs1);
			g2d.setPaint(Color.red);
			g2d.drawRoundRect(xvalue/2+20, yvalue/2+50, 2, 2, 2, 2);
			System.out.println("x = " + xvalue + ", y= "+yvalue);
		}
        
        g2d.setPaint(Color.blue);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}
