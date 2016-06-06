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

public class Lines extends JFrame {

    public Lines() {
        initUI();
    }

    private void initUI() {

        add(new Surface());

/*        setTitle("Lines");
        //Size of window
        setSize(900/2+80, 1100/2+80);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 */       
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

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(40);
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
                ex.setVisible(true);
            }
        });
    }
    
    public class DefaultModel implements MutableModel {

        private final Dimension size = new Dimension(900/2+80, 1100/2+80);
        private final Rectangle bounds = new Rectangle(900/2+80, 1100/2+80, 10, 10);

        private int xDelta = ((int) (Math.random() * 5)) + 1;
        private int yDelta = ((int) (Math.random() * 5)) + 1;

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
            Points.setXPoint(Points.getXPoint()+xDelta);
            Points.setYPoint(Points.getYPoint()+yDelta);
            
            if (Points.getXPoint() < 0) {
            	Points.setXPoint(0);
                xDelta *= -1;
            } else if (Points.getXPoint() + 20 > 930) {
                //bounds.x = size.width - bounds.width;
                Points.setXPoint(930 - 20);
                xDelta *= -1;
            }
            
            if (Points.getYPoint() < 0) {
                Points.setYPoint(0);
                yDelta *= -1;
            } else if (Points.getYPoint() + 20 > 1060) {
            	Points.setYPoint(1060 - 20);
                yDelta *= -1;
            }
            fireStateChanged();
        }

    }
    
    public class View extends JComponent implements ChangeListener {

        private Model model;

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
            int x = this.getWidth() / 2;
            int y = this.getHeight() / 2;
            g2d.rotate(Math.toRadians(180.0), x, y);
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth(), getHeight());
            //Rectangle bounds = model.getBounds();
            g2d.setColor(Color.BLUE);
            //The four walls
            g2d.drawLine(20, 50, 30/2+20, 50);
            g2d.drawLine(30/2+20, 50-(10/2), 150/2+20, 50-(10/2));
            g2d.drawLine(150/2+20, 50-(77/2), 480/2+20, 50-(77/2));
            g2d.drawLine(480/2+20, 50, 930/2+20, 50);
            
            g2d.drawLine(150/2+20, 50-(10/2), 150/2+20, 50-(77/2));
            g2d.drawLine(30/2+20, 50-(10/2), 30/2+20, 50);
            g2d.drawLine(480/2+20, 50-(77/2), 480/2+20, 50);
            
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
    			g2d.drawRoundRect(xplace/2+20, yvalue/2+50, 4, 4, 4, 4);
    			//System.out.println("x = " + xplace + ", y= "+yvalue);
    		}
            g2d.setPaint(Color.blue);
            
            //g2d.drawRoundRect(Points.getXPoint()/2+20, Points.getYPoint()/2+50, 2, 2, 2, 2);
            
            Image img1 = Toolkit.getDefaultToolkit().getImage("rsz_he291.jpg");
            g2d.drawImage(img1, Points.getXPoint()/2+20, Points.getYPoint()/2+50, this);
            
            System.out.println(Points.getXPoint()+" + "+Points.getYPoint());
            //g2d.drawOval(bounds.x, bounds.y, bounds.width, bounds.height);
            g2d.dispose();
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            repaint();
        }

    }
}
