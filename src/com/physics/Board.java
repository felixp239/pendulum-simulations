package com.physics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;


public class Board extends JPanel implements ActionListener {
    private final float m2p = 150; //metres to pixels

    private Timer timer;
    private final int B_WIDTH;
    private final int B_HEIGHT;
    private final int DELAY = 1;
    private final int doublePendulumAmount = 20000;

    private Pendulum pendulum;
    private DoublePendulum doublePendulum;
    private DoublePendulum[] doublePendulums = new DoublePendulum[doublePendulumAmount];

    public Board(int b_WIDTH, int b_HEIGHT) {
        B_WIDTH = b_WIDTH;
        B_HEIGHT = b_HEIGHT;
        initBoard();
    }

    private void initBoard() {
        //addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        //initPendulum();
        //initDoublePendulum();
        initDoublePendulums();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void initDoublePendulums() {
        for (int i = 0; i < doublePendulumAmount; i++) {
            doublePendulums[i] = new DoublePendulum((float) Math.toRadians(100), 1.2f, 1f, (float) Math.toRadians(101 + i / 100000000f), 0.8f, 10f, 9.81f);
            doublePendulums[i].initDoublePendulum();
        }
    }

    public void initDoublePendulum() {
        doublePendulum = new DoublePendulum((float) Math.toRadians(60), 1.5f, 1, (float) Math.toRadians(20), 1,1, 9.81f);
        doublePendulum.initDoublePendulum();
    }

    public void initPendulum() {
        pendulum = new Pendulum((float) Math.toRadians(1));
        pendulum.initPendulum();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawObjects(g);

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawObjects(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, B_WIDTH, B_HEIGHT);

        if (pendulum != null) {
            float[] pendulumEnd = pendulum.calculateCoords();
            pendulumEnd[0] *= m2p;
            pendulumEnd[1] *= m2p;
            pendulumEnd[0] += B_WIDTH / 2;
            pendulumEnd[1] += 50;
            g.setColor(Color.red);
            g.drawLine(B_WIDTH / 2, 50, (int) pendulumEnd[0], (int) pendulumEnd[1]);
            g.fillOval((int) (pendulumEnd[0] - 25), (int) (pendulumEnd[1] - 25), 50, 50);

            BufferedImage bi = drawText(String.valueOf(pendulum.getTheta()), "Helvetica", 70, Color.BLACK);
            g.drawImage(bi, 40, B_HEIGHT - 150, this);
        }

        if (doublePendulum != null) {
            float[] pendulumFirstEnd = doublePendulum.calculateFirstCords();
            pendulumFirstEnd[0] *= m2p;
            pendulumFirstEnd[1] *= m2p;
            pendulumFirstEnd[0] += B_WIDTH / 2;
            pendulumFirstEnd[1] += B_HEIGHT / 2;
            g.setColor(Color.red);
            g.drawLine(B_WIDTH / 2, B_HEIGHT / 2, (int) pendulumFirstEnd[0], (int) pendulumFirstEnd[1]);
            g.fillOval((int) (pendulumFirstEnd[0] - 12), (int) (pendulumFirstEnd[1] - 12), 24, 24);
            float[] pendulumSecondEnd = doublePendulum.calculateSecondCords();
            pendulumSecondEnd[0] *= m2p;
            pendulumSecondEnd[1] *= m2p;
            pendulumSecondEnd[0] += pendulumFirstEnd[0];
            pendulumSecondEnd[1] += pendulumFirstEnd[1];
            g.drawLine((int) pendulumFirstEnd[0], (int) pendulumFirstEnd[1], (int) pendulumSecondEnd[0], (int) pendulumSecondEnd[1]);
            g.fillOval((int) (pendulumSecondEnd[0] - 12), (int) (pendulumSecondEnd[1] - 12), 24, 24);
            g.setColor(Color.BLACK);
            g.setColor(Color.BLACK);
            g.fillOval(B_WIDTH / 2 - 5, B_HEIGHT / 2 - 5, 10, 10);
        }

        if (doublePendulums != null) {
            for (int i = 0; i < doublePendulumAmount; i++) {
                float[] pendulumFirstEnd = doublePendulums[i].calculateFirstCords();
                pendulumFirstEnd[0] *= m2p;
                pendulumFirstEnd[1] *= m2p;
                pendulumFirstEnd[0] += B_WIDTH / 2;
                pendulumFirstEnd[1] += B_HEIGHT / 4;
                g.setColor(new Color(150, 150, 255, 4));
                g.drawLine(B_WIDTH / 2, B_HEIGHT / 4, (int) pendulumFirstEnd[0], (int) pendulumFirstEnd[1]);
                float[] pendulumSecondEnd = doublePendulums[i].calculateSecondCords();
                pendulumSecondEnd[0] *= m2p;
                pendulumSecondEnd[1] *= m2p;
                pendulumSecondEnd[0] += pendulumFirstEnd[0];
                pendulumSecondEnd[1] += pendulumFirstEnd[1];
                g.drawLine((int) pendulumFirstEnd[0], (int) pendulumFirstEnd[1], (int) pendulumSecondEnd[0], (int) pendulumSecondEnd[1]);
            }
        }
    }

    private BufferedImage drawText(String text, String fontName, int size, Color color) {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        Font font = new Font(fontName, Font.BOLD, size);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int width = fm.stringWidth(text);
        int height = fm.getHeight();
        g2d.dispose();

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g2d.setFont(font);
        fm = g2d.getFontMetrics();
        g2d.setColor(color);
        g2d.drawString(text, 0, fm.getAscent());
        g2d.dispose();

        return img;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (pendulum != null) {
            updatePendulum();
        }

        if (doublePendulum != null) {
            updateDoublePendulum();
        }

        if (doublePendulums != null) {
            updateDoublePendulums();
        }

        repaint();
    }

    private void updatePendulum() {
        pendulum.update();
    }

    private void updateDoublePendulum() {
        doublePendulum.update();
    }

    private void updateDoublePendulums() {
        for (int i = 0; i < doublePendulumAmount; i++) {
            doublePendulums[i].update();
        }
     }
    /*
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            player.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            player.keyPressed(e);
            if (e.getKeyCode() == KeyEvent.VK_R) {
                restart();
            }
        }
    }

     */
}