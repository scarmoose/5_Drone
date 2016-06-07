package CoordinateSystem;

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
			Xelements.add((int) Mathmagic.getArray()[i].getPosition().getX());
			Yelements.add((int) Mathmagic.getArray()[i].getPosition().getY());
		}
    	
		for (int k = 0;k< Xelements.size();k++){
			int xplace = 0;
			int xvalue = Xelements.get(k);
			int yvalue = Yelements.get(k);
			if (xvalue < 465){
				xplace = 465-(xvalue - 465);
			}
			if (xvalue > 465){
				xplace= (465-xvalue)+465;
			}
			BasicStroke bs1 = new BasicStroke(8, BasicStroke.CAP_BUTT,
			        BasicStroke.JOIN_BEVEL);
			g2d.setStroke(bs1);
			g2d.setPaint(Color.red);
			g2d.drawRoundRect(xplace/2+20, yvalue/2+50, 2, 2, 2, 2);
			System.out.println("x = " + xplace + ", y= "+yvalue);
		}
        g2d.setPaint(Color.blue);
        super.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {

    	Graphics2D g2d = (Graphics2D) g;
        int x = this.getWidth() / 2;
        int y = this.getHeight() / 2;
        g2d.rotate(Math.toRadians(180.0), x, y);
    	
        super.paintComponent(g);
        doDrawing(g);
        
    }
}
