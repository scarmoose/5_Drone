package CoordinateSystem;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
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
import dk.gruppe5.model.Airfield;
import dk.gruppe5.model.AirfieldList;

public class Lines {

	AirfieldList airfields = new AirfieldList();
	
    public Lines() {
        initUI();
        
        //Sætter airfields
        /*
        AirfieldList.addAirfield(new Airfield("Airfield1",new Point(350,700)));
        AirfieldList.addAirfield(new Airfield("Airfield2",new Point(530,200)));*/
                
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
				DronePosition.setYMirror(530+(530-DronePosition.getYPoint()));
			}
			if (DronePosition.getYPoint() > 530){
				DronePosition.setYMirror(530-(DronePosition.getYPoint()-530));
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
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
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
            
            for(int j = 0; j<AirfieldList.getArray().size();j++ ){
            	g2d.setStroke(bs2);
                g2d.setPaint(Color.MAGENTA);
                int pointy=0;
                
                if (AirfieldList.getArray().get(j).point.y < 530){
                	pointy = 530+(530-AirfieldList.getArray().get(j).point.y);
    			}
                else if (AirfieldList.getArray().get(j).point.y > 530){
                	pointy = 530-((AirfieldList.getArray().get(j).point.y)-530);
    			}
                g2d.drawRect((AirfieldList.getArray().get(j).point.x/2)-(DronePosition.getxLen()/4)+(40/2), (pointy)/4+(DronePosition.getyLen()/4)+(50/2), (DronePosition.getxLen())/2, DronePosition.getyLen()/2);
                g2d.drawString(AirfieldList.getArray().get(j).name, (AirfieldList.getArray().get(j).point.x/2)-(DronePosition.getxLen()/4)+(40/2)-7, (pointy)/4+(DronePosition.getyLen()/4)+(50/2)-2);
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
