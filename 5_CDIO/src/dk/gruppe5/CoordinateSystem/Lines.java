package dk.gruppe5.CoordinateSystem;

import java.awt.EventQueue;

import javax.swing.JFrame;

public class Lines extends JFrame {

    public Lines() {
        initUI();
    }

    private void initUI() {

        add(new Surface());

        setTitle("Lines");
        //Size of window
        setSize(1150/2, 1100/2);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
}