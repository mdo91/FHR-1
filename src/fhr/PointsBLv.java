/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fhr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Surface2 extends JPanel {

    private void doDrawing(Graphics g) throws IOException {
        FHR f = new FHR();
        Graphics2D g2d = (Graphics2D) g;

        Dimension size = getSize();
        Insets insets = getInsets();

        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;

        g2d.setColor(Color.red);
        g2d.drawLine(0, h - 140, w, h - 140);
        g2d.setColor(Color.gray);
        g2d.drawLine(0, h - 150, w, h - 150);
        g2d.drawLine(0, h - 160, w, h - 160);
        g2d.drawLine(0, h - 130, w, h - 130);
        g2d.drawLine(0, h - 120, w, h - 120);
        g2d.setColor(Color.blue);

        for (int i = 0; i < 19200; i++) {
            if (i % 16 == 0) {
                g2d.setColor(Color.white);
                g2d.drawLine(i, 0, i, h);
                g2d.setColor(Color.blue);
            }

            try {
                Double x = FHR.fhr[i] * 100;
                int x2 = x.intValue() / 100;
                Double y = FHR.fhr[i + 1] * 100;
                int y2 = y.intValue() / 100;
                if (y2 != 0 && x2 != 0) {
                    g2d.drawLine(i / 7, h - x2, (i + 1) / 7, h - y2);
                    //g2d.drawLine(i / 7, h - x2, i / 7, h - x2);
                }

            } catch (Exception e) {
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        try {
            doDrawing(g);
        } catch (IOException ex) {
            Logger.getLogger(Surface2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public class PointsBLv extends JFrame {

    public PointsBLv() {

        initUI();
    }

    private void initUI() {

        setTitle("Points");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(new Surface2());

        setSize(1920, 300);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                PointsBLv ps = new PointsBLv();
                ps.setVisible(true);
            }
        });
    }
}
