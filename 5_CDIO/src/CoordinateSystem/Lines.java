package CoordinateSystem;

import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dk.gruppe5.controller.Mathmagic;

public class Lines {

    public Lines() {
        initUI();
    }

    private void initUI() {

        MutableModel model = new DefaultModel();
        Controller controller = new Controller(model);

        JFrame frame = new JFrame("Drone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new View(model));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        controller.start();
    }

    public interface Model {

        public Rectangle getBounds();
        public Dimension getSize();
        public void addChangeListener(ChangeListener listener);
        public void removeChangeListener(ChangeListener listener);

    }

    public interface MutableModel extends Model {

        public void update();

    }
    
    public class Controller extends Thread {

        private MutableModel model;

        public Controller(MutableModel model) {
            this.model = model;
            setDaemon(true);
        }


        public void run() {
            while (true) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException ex) {
                }
                model.update();
            }
        }

    }
    
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
			@Override
            public void run() {
                Lines ex = new Lines();
            }
        });
    }
    
    public class DefaultModel implements MutableModel {

        private final Dimension size = new Dimension(900/2+80, 1100/2+80);
        private final Rectangle bounds = new Rectangle(900/2+80, 1100/2+80, 10, 10);

        //private int xDelta = ((int) (Math.random() * 5)) + 1;
        //private int yDelta = ((int) (Math.random() * 5)) + 1;

        private List<ChangeListener> changeListeners;

        public DefaultModel() {
            changeListeners = new ArrayList<>(25);
        }

        @Override
        public void addChangeListener(ChangeListener listener) {
            changeListeners.add(listener);
        }

        @Override
        public void removeChangeListener(ChangeListener listener) {
            changeListeners.remove(listener);
        }

        protected void fireStateChanged() {
            if (changeListeners.size() > 0) {
                ChangeEvent evt = new ChangeEvent(this);
                Iterator<ChangeListener> it = changeListeners.iterator();
                while (it.hasNext()) {
                    ChangeListener listener = it.next();
                    listener.stateChanged(evt);
                }
            }
        }

        @Override
        public Dimension getSize() {
            return size;
        }

        @Override
        public Rectangle getBounds() {
            return bounds;
        }

        @Override
        public void update() {
            DronePosition.setXPoint(DronePosition.getXPoint());
            DronePosition.setYPoint(DronePosition.getYPoint());
            
            if (DronePosition.getYPoint() < 530){
				DronePosition.setYMirror(530-(DronePosition.getYPoint() - 530));
			}
			if (DronePosition.getYPoint() > 530){
				DronePosition.setYMirror((530-DronePosition.getYPoint())+530);
			}
            
            
            if (DronePosition.getXPoint() < 0) {
            	DronePosition.setXPoint(0);
                //xDelta *= -1;
            } else if (DronePosition.getXPoint() + 20 > 930) {
                //bounds.x = size.width - bounds.width;
                DronePosition.setXPoint(930 - 20);
                //xDelta *= -1;
            }
            
            if (DronePosition.getYPoint() < 0) {
                DronePosition.setYPoint(0);
                //yDelta *= -1;
            } else if (DronePosition.getYPoint() + 20 > 1060) {
            	DronePosition.setYPoint(1060 - 20);
                //yDelta *= -1;
            }
            
            if (((DronePosition.getYPoint() > 345 - 50 && DronePosition.getYPoint() < 705 - 50 && DronePosition.getXPoint() > 345 + 50 && DronePosition.getXPoint() < 355 + 50) || (DronePosition.getYPoint() > 345 - 50 && DronePosition.getYPoint() < 705 - 50 && DronePosition.getXPoint() > 695 + 50 && DronePosition.getXPoint() < 705 + 50)) || ((DronePosition.getXPoint() > 345 + 50 && DronePosition.getXPoint() < 705 + 50 && DronePosition.getYPoint() > 345 - 50 && DronePosition.getYPoint() < 355 - 50) || (DronePosition.getXPoint() > 345 + 50 && DronePosition.getXPoint() < 705 + 50 && DronePosition.getYPoint() > 695 - 50 && DronePosition.getYPoint() < 705 - 50)) ) {
                DronePosition.setFound(true);
            }
            
            fireStateChanged();
        }

    }
    
    public class View extends JComponent implements ChangeListener {

        private Model model;

        BasicStroke bs1 = new BasicStroke(8, BasicStroke.CAP_BUTT,
		        BasicStroke.JOIN_BEVEL);
        BasicStroke bs2 = new BasicStroke(1, BasicStroke.CAP_BUTT,
		        BasicStroke.JOIN_BEVEL);

        
        public View(Model model) {
            this.model = model;
            this.model.addChangeListener(this);
            setBackground(Color.WHITE);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(model.getSize());
        }

        @Override
        protected void paintComponent(Graphics g) {
            //super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            //Rectangle bounds = model.getBounds();
            g2d.setColor(Color.BLUE);
            //The four walls
            g2d.drawLine(20, 50, 930/2+20, 50);
            g2d.drawLine(20, 50, 20, 1060/2+50);
            g2d.drawLine(20, 1060/2+50, 450/2+20, 1060/2+50);
            g2d.drawLine(450/2+20, 1060/2+50+(77/2), 450/2+20, 1060/2+50);
            g2d.drawLine(450/2+20, 1060/2+50+(77/2), 750/2+20, 1060/2+50+(77/2));
            g2d.drawLine(450/2+20, 1060/2+50+(77/2), 450/2+20, 1060/2+50);
            g2d.drawLine(900/2+20, 1060/2+50+(10/2), 750/2+20, 1060/2+50+(10/2));
            g2d.drawLine(750/2+20, 1060/2+50+(77/2), 750/2+20, 1060/2+50+(10/2));
            g2d.drawLine(900/2+20, 1060/2+50, 900/2+20, 1060/2+50+(10/2));
            g2d.drawLine(900/2+20, 1060/2+50, 930/2+20, 1060/2+50);
            g2d.drawLine(930/2+20, 1060/2+50, 930/2+20, 50);
            
            ArrayList<Integer> Xelements = new ArrayList<>();
    		ArrayList<Integer> Yelements = new ArrayList<>();
    		for (int i = 0 ; i < Mathmagic.getArray().length ; i++){
    			Xelements.add((int) Mathmagic.getArray()[i].getPosition().getX());
    			Yelements.add((int) Mathmagic.getArray()[i].getPosition().getY());
    		}
        	
    		for (int k = 0;k< Yelements.size();k++){
    			int yplace = 0;
    			int xvalue = Xelements.get(k);
    			int yvalue = Yelements.get(k);
    			if (yvalue < 530){
    				yplace = 530-(yvalue - 530);
    			}
    			if (yvalue > 530){
    				yplace= (530-yvalue)+530;
    			}
    			g2d.setStroke(bs1);
    			g2d.setPaint(Color.red);
    			g2d.drawRoundRect(xvalue/2+20, yplace/2+50, 4, 4, 4, 4);
    			g2d.drawString(Mathmagic.getArray()[k].getName(), xvalue/2+20, yplace/2+70);
    		}
            
            Image img1 = Toolkit.getDefaultToolkit().getImage("rsz_he291.jpg");
            g2d.drawImage(img1, DronePosition.getXPoint()/2+20, DronePosition.getYMirror()/2+25, this);
            if(DronePosition.getXPoint() < 930 && DronePosition.getYPoint() < 1060){
            
        	g2d.setStroke(bs2);
            g2d.setPaint(Color.blue);
            int pointy=0;
            if (DronePosition.getyCorn() < 530){
            	pointy = 530-(DronePosition.getyCorn() - 530);
			}
            else if (DronePosition.getyCorn() > 530){
            	pointy = (530-DronePosition.getyCorn())+530;
			}
            
            g2d.drawRect((DronePosition.getXPoint()/2)-(DronePosition.getxLen()/4)+(40/2), (DronePosition.getYMirror())/2+(DronePosition.getyLen()/4)+(50/2), (DronePosition.getxLen())/2, DronePosition.getyLen()/2);

            }
            g2d.dispose();
        }
        
        public void isAirfieldAvailable(){
            
        	ArrayList<Integer> airfield = new ArrayList<Integer>(3);
        	for(int i = 0; i < DronePosition.getXPoint(); i++){
        		airfield.add(i);
        		for (int j = 0; i < DronePosition.getYPoint(); i++)
        			airfield.add(j);
        	} 
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            repaint();
        }

    }
}
