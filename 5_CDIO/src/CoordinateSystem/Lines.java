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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import dk.gruppe5.controller.Mathmagic;
import dk.gruppe5.model.Airfield;
import dk.gruppe5.model.AirfieldList;
import dk.gruppe5.model.DPoint;

public class Lines {

	public Lines() {
        initUI();
        
        //S�tter airfields
        
        /*AirfieldList.addAirfield(new Airfield("Airfield1",new Point(DronePosition.getXPoint(),DronePosition.getYMirror())));
        AirfieldList.addAirfield(new Airfield("Airfield2",new Point(530,200)));
        AirfieldList.addAirfield(new Airfield("Airfield3",new Point(800,800)));*/
                
    }

    private void initUI() {

        MutableModel model = new DefaultModel();
        Controller controller = new Controller(model);

        JFrame frame = new JFrame("Lokale 040");
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
            g2d.drawLine(20, 1060/2+50, 550/2+20, 1060/2+50);
            g2d.drawLine(550/2+20, 1060/2+50+(77/2), 550/2+20, 1060/2+50);
            g2d.drawLine(550/2+20, 1060/2+50+(77/2), 750/2+20, 1060/2+50+(77/2));
            g2d.drawLine(550/2+20, 1060/2+50+(77/2), 550/2+20, 1060/2+50);
            g2d.drawLine(900/2+20, 1060/2+50+(10/2), 750/2+20, 1060/2+50+(10/2));
            g2d.drawLine(750/2+20, 1060/2+50+(77/2), 750/2+20, 1060/2+50+(10/2));
            g2d.drawLine(900/2+20, 1060/2+50, 900/2+20, 1060/2+50+(10/2));
            g2d.drawLine(900/2+20, 1060/2+50, 930/2+20, 1060/2+50);
            g2d.drawLine(930/2+20, 1060/2+50, 930/2+20, 50);
            ArrayList<Integer> Xelements = new ArrayList<>();
    		ArrayList<Integer> Yelements = new ArrayList<>();
    		ArrayList<Integer> LeftXelements = new ArrayList<>();
    		ArrayList<Integer> LeftYelements = new ArrayList<>();
    		ArrayList<Integer> RightXelements = new ArrayList<>();
    		ArrayList<Integer> RightYelements = new ArrayList<>();
    		for (int i = 0 ; i < Mathmagic.getArray().length ; i++){
    			Xelements.add((int) Mathmagic.getArray()[i].getPosition().x);
    			Yelements.add((int) Mathmagic.getArray()[i].getPosition().y);
    			LeftXelements.add((int) Mathmagic.getArray()[i].getLeftTrianglePos().x);
    			LeftYelements.add((int) Mathmagic.getArray()[i].getLeftTrianglePos().y);
    			RightXelements.add((int) Mathmagic.getArray()[i].getRightTrianglePos().x);
    			RightYelements.add((int) Mathmagic.getArray()[i].getRightTrianglePos().y);
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
    		
    		for (int k = 0;k< LeftYelements.size();k++){
    			int yplace = 0;
    			int xvalue = LeftXelements.get(k);
    			int yvalue = LeftYelements.get(k);
    			if (yvalue < 530){
    				yplace = 530-(yvalue - 530);
    			}
    			if (yvalue > 530){
    				yplace= (530-yvalue)+530;
    			}
    			g2d.setStroke(bs2);
    			g2d.setPaint(Color.green);
    			
    			g2d.drawLine(xvalue/2+20-7, yplace/2+50+7, xvalue/2+20+7, yplace/2+50+7);
    			g2d.drawLine(xvalue/2+20-7, yplace/2+50+7, xvalue/2+20, yplace/2+50-7);
    			g2d.drawLine(xvalue/2+20+7, yplace/2+50+7, xvalue/2+20, yplace/2+50-7);
    		}
    		
    		for (int k = 0;k< RightYelements.size();k++){
    			int yplace = 0;
    			int xvalue = RightXelements.get(k);
    			int yvalue = RightYelements.get(k);
    			if (yvalue < 530){
    				yplace = 530-(yvalue - 530);
    			}
    			if (yvalue > 530){
    				yplace= (530-yvalue)+530;
    			}
    			g2d.setStroke(bs2);
    			g2d.setPaint(Color.green);
    			g2d.drawLine(xvalue/2+20-7, yplace/2+50+7, xvalue/2+20+7, yplace/2+50+7);
    			g2d.drawLine(xvalue/2+20-7, yplace/2+50+7, xvalue/2+20, yplace/2+50-7);
    			g2d.drawLine(xvalue/2+20+7, yplace/2+50+7, xvalue/2+20, yplace/2+50-7);
    		}
    		
            
    		File img = new File("rsz_1he291_av3.jpg");

            BufferedImage buffImg = 
            	    new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);

            	try { 
            	    buffImg = ImageIO.read(img ); 
            	} 
            	catch (IOException e) { }

            double rotationRequired = Math.toRadians (DronePosition.getDegree());
            double locationX = (buffImg.getHeight()) / 2;
            double locationY = (buffImg.getHeight()) / 2;
            AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

            // Drawing the rotated image at the required drawing locations
            g2d.drawImage(op.filter(buffImg, null), DronePosition.getXPoint()/2+20, DronePosition.getYMirror()/2+25, null);
            
            //g2d.drawImage(img1, DronePosition.getXPoint()/2+20, DronePosition.getYMirror()/2+25, this);
           
            
            for(int j = 0; j<AirfieldList.getArray().size();j++ ){
            	g2d.setStroke(bs2);
                g2d.setPaint(Color.MAGENTA);
                int pointy=0;
               
                g2d.drawRect((AirfieldList.getArray().get(j).point.x/2)-(50/4)+(60/2), (AirfieldList.getArray().get(j).point.y)/2+(50/4)+(20/2), (50)/2, 50/2);
                g2d.drawString(AirfieldList.getArray().get(j).name, (AirfieldList.getArray().get(j).point.x/2)-(50/4)+(60/2)-7, (AirfieldList.getArray().get(j).point.y)/2+(50/4)+(50/2)-17);
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
