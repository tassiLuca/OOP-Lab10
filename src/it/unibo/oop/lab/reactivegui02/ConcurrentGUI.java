package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = -8630968055862320453L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGH_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("Up");
    private final JButton down = new JButton("Down");
    private final JButton stop = new JButton("Stop");

    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGH_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();
        /* 
         * Handlers 
         */
        stop.addActionListener(e -> agent.stopCounting());
        up.addActionListener(e -> agent.increaseCounting());
        down.addActionListener(e -> agent.decreaseCounting());

    }

    private class Agent implements Runnable {

        private volatile boolean decrease;
        private volatile boolean stop;
        private volatile int counter;

        /**
         * Updates the count.
         */
        private void updateCounter() {
            if (!this.decrease) {
                this.counter++;
            } else {
                this.counter--;
            }
        }

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(Integer.toString(this.counter)));
                    updateCounter();
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        /*
         * External command to decrease counting.
         */
        public void decreaseCounting() {
            this.decrease = true;
        }

        /**
         * External command to increase counting.
         */
        public void increaseCounting() {
            this.decrease = false;
        }

    }

}
