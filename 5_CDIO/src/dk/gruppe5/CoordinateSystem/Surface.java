package dk.gruppe5.CoordinateSystem;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

class Surface extends JPanel {

    private void doDrawing(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        //The four walls
        g2d.drawLine(0, 0, 1100/2, 0);
        g2d.drawLine(0, 0, 0, 1000/2);
        g2d.drawLine(0, 1000/2, 1100/2, 1000/2);
        g2d.drawLine(1100/2, 1000/2, 1100/2, 0);
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        doDrawing(g);
    }
}
