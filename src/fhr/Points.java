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
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

class Surface extends JPanel {

    private void doDrawing(Graphics g) throws IOException {
        //FHR f = new FHR();
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

        for (int i = 0; i < 19200; i++) {
            if (i % 60 == 0) {//1 minute
                g2d.setColor(Color.yellow);
                g2d.drawLine(i, 0, i, h);
            }
            if (i % 600 == 0) {//10 minute
                g2d.setColor(Color.red);
                g2d.drawLine(i - 1, 0, i - 1, h);
                g2d.drawLine(i, 0, i, h);
                g2d.drawLine(i + 1, 0, i + 1, h);
            }

            try {
                Double x = FHR.fhr[i] * 100;
                int x2 = x.intValue() / 100;
                Double y = FHR.fhr[i + 1] * 100;
                int y2 = y.intValue() / 100;
                if (y2 != 0 && x2 != 0) {
                    g2d.setColor(Color.blue);
                    if (FHR.zeros[i] != 0) {
                        g2d.setColor(Color.GREEN);
                    }
                    g2d.drawLine(i / 4, h - x2, (i + 1) / 4, h - y2);
                    //g2d.drawLine(i / 7, h - x2, (i + 1) / 7, h - y2);
                    //g2d.drawLine(i / 7, h - x2, i / 7, h - x2);
                }

            } catch (Exception e) {
            }
        }

        // BLV drawing
        for (int i = 0; i < FHR.BLVcount; i++) {
            int x1 = FHR.BLVstarts[i] / 4;
            int x2 = FHR.BLVends[i] / 4;
            int x = FHR.BLVpeaks[i];
            int y = FHR.fhr[x].intValue();
            g2d.setColor(Color.cyan);

            g2d.drawLine(x1, h - 199, x2, h - 199);
            g2d.drawLine(x1, h - 200, x2, h - 200);
            g2d.drawLine(x1, h - 201, x2, h - 201);
            //g2d.drawLine(x1, h - 75, x2, h - 75);
            g2d.drawLine(x1, h - 200, x1, h - 140);
            g2d.drawLine(x2, h - 200, x2, h - 140);

            g2d.setColor(Color.cyan);
            g2d.drawOval((x / 4) - 5, h - y - 5, 10, 10);

            g2d.drawLine(x/4, h - y, x/4, h - 140);
            g2d.drawLine(x/4, h - y, x/4, h - 140);
            g2d.drawLine(x/4, h - y, x/4, h - 140);

                        //g2d.drawOval(x/4, h-y, 9, 9);
            //g2d.drawLine( x ,FHR.fhr[x].intValue(), x, h-140);
            //g2d.drawLine(FHR.fhr[y].intValue() + 1, y, h - 140, FHR.fhr[y].intValue() + 1);
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        try {
            doDrawing(g);
        } catch (IOException ex) {
            Logger.getLogger(Surface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public class Points extends JFrame {

    public Points() {

        initUI();
    }

    private void initUI() {

        setTitle("Ammar & Mahmood");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Surface s = new Surface();
        s.setPreferredSize(new Dimension(19200 / 4, 300));
        s.setAutoscrolls(true);
        s.setBackground(Color.gray);
        JScrollPane scrollFrame = new JScrollPane(s);
        scrollFrame.setPreferredSize(new Dimension(1920, 800));
        add(scrollFrame);

        setSize(19200, 400);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                Points ps = new Points();
                ps.setVisible(true);
            }
        });
    }
}
