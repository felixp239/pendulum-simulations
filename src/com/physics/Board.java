package com.physics;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class Board extends JPanel implements ActionListener {
    private final float m2p = 100; //metres to pixels

    private Timer timer;
    private final int B_WIDTH;
    private final int B_HEIGHT;
    private final int DELAY = 1;
    private final int doublePendulumAmount = 20;

    private Pendulum pendulum;
    private DoublePendulum doublePendulum;
    private DoublePendulum[] doublePendulums;
    private MultiplePendulum multiplePendulum;

    private ArrayList<Point> trail1 = new ArrayList<>();
    private int maxPointCount1 = 150;
    private ArrayList<Point> trail2 = new ArrayList<>();
    private int maxPointCount2 = 150;
    private float x_0;
    private float a_x_0 = 0;
    private float y_0;
    private float a_y_0 = 0;
    private float period = 3;
    private float amplitude = 60;
    private float theta = (float) Math.toRadians(100);
    private long start_time;
    private long time;

    private int updateCount = 0;

    public Board(int b_WIDTH, int b_HEIGHT) throws Exception {
        B_WIDTH = b_WIDTH;
        B_HEIGHT = b_HEIGHT;
        x_0 = b_WIDTH / 2;
        y_0 = b_HEIGHT / 3;
        initBoard();
    }

    private void initBoard() throws Exception {
        //addKeyListener(new TAdapter());
        setFocusable(true);
        setBackground(Color.WHITE);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));

        //initPendulum();
        initDoublePendulum();
        //initDoublePendulums();
        //initMultiplePendulum();

        start_time = System.nanoTime();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void initDoublePendulums() {
        doublePendulums = new DoublePendulum[doublePendulumAmount];
        for (int i = 0; i < doublePendulumAmount; i++) {
            doublePendulums[i] = new DoublePendulum((float) Math.toRadians(80), 1f, 1f, (float) Math.toRadians(101 + i / 10f), 1f, 10f, 9.81f);
            doublePendulums[i].initDoublePendulum();
        }
    }

    public void initDoublePendulum() {
        doublePendulum = new DoublePendulum((float) Math.toRadians(80), 1, 1, (float) Math.toRadians(102.4f), 1,10, 9.81f);
        doublePendulum.initDoublePendulum();
    }

    public void initPendulum() {
        pendulum = new Pendulum((float) Math.toRadians(1));
        pendulum.initPendulum();
    }

    public void initMultiplePendulum() throws Exception {
        //multiplePendulum = new MultiplePendulum(new float[]{(float) Math.toRadians(80), (float) Math.toRadians(60), (float) Math.toRadians(100)}, 9.81f);
        //multiplePendulum = new MultiplePendulum(9);
        multiplePendulum = new MultiplePendulum(new float[]{0.8f, 0.6f, 0.4f, 0.2f}, new float[]{(float) Math.toRadians(89), (float) Math.toRadians(91), (float) Math.toRadians(89), (float) Math.toRadians(91)}, 9.81f);
        //multiplePendulum = new MultiplePendulum(new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f},
              //new float[]{0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f, 0.2f},
                //new float[]{3.14f, 3.14f, 3f, 3f, 3f, 1f, 1f, 1f, 1f, 1f, 1f, 1f}, new float[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},9.81f);
        multiplePendulum.initMultiplePendulum();
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
            //g.fillOval((int) (pendulumFirstEnd[0] - 12), (int) (pendulumFirstEnd[1] - 12), 24, 24);
            float[] pendulumSecondEnd = doublePendulum.calculateSecondCords();
            pendulumSecondEnd[0] *= m2p;
            pendulumSecondEnd[1] *= m2p;
            pendulumSecondEnd[0] += pendulumFirstEnd[0];
            pendulumSecondEnd[1] += pendulumFirstEnd[1];
            g.drawLine((int) pendulumFirstEnd[0], (int) pendulumFirstEnd[1], (int) pendulumSecondEnd[0], (int) pendulumSecondEnd[1]);
            trail1.add(new Point((int) pendulumSecondEnd[0], (int) pendulumSecondEnd[1]));
            if (trail1.size() > maxPointCount1) {
                trail1.remove(0);
            }
            if (trail1.size() > 1) {
                g.setColor(Color.blue);
                Point p1;
                Point p2;
                for (int i = 0; i < trail1.size() - 1; i++) {
                    p1 = trail1.get(i);
                    p2 = trail1.get(i + 1);
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
            //g.fillOval((int) (pendulumSecondEnd[0] - 12), (int) (pendulumSecondEnd[1] - 12), 24, 24);
            //g.setColor(Color.BLACK);
            //g.fillOval(B_WIDTH / 2 - 5, B_HEIGHT / 2 - 5, 10, 10);
        }

        if (doublePendulums != null) {
            for (int i = 0; i < doublePendulumAmount; i++) {
                float[] pendulumFirstEnd = doublePendulums[i].calculateFirstCords();
                pendulumFirstEnd[0] *= m2p;
                pendulumFirstEnd[1] *= m2p;
                pendulumFirstEnd[0] += B_WIDTH / 2;
                pendulumFirstEnd[1] += B_HEIGHT / 3;
                g.setColor(new Color(150, 150, 255, 100));
                g.drawLine(B_WIDTH / 2, B_HEIGHT / 3, (int) pendulumFirstEnd[0], (int) pendulumFirstEnd[1]);
                float[] pendulumSecondEnd = doublePendulums[i].calculateSecondCords();
                pendulumSecondEnd[0] *= m2p;
                pendulumSecondEnd[1] *= m2p;
                pendulumSecondEnd[0] += pendulumFirstEnd[0];
                pendulumSecondEnd[1] += pendulumFirstEnd[1];
                g.drawLine((int) pendulumFirstEnd[0], (int) pendulumFirstEnd[1], (int) pendulumSecondEnd[0], (int) pendulumSecondEnd[1]);
            }
        }

        if (multiplePendulum != null) {
            Point[] points = multiplePendulum.getPoints((int) x_0, (int) y_0, m2p);
            g.setColor(Color.WHITE);
            for (int i = 1; i < points.length; i++) {
                g.drawLine(points[i - 1].x, points[i - 1].y, points[i].x, points[i].y);
            }
            trail2.add(points[points.length - 1]);
            if (trail2.size() > maxPointCount2) {
                trail2.remove(0);
            }
            if (trail2.size() > 1) {
                g.setColor(Color.blue);
                Point p1;
                Point p2;
                for (int i = 0; i < trail2.size() - 1; i++) {
                    p1 = trail2.get(i);
                    p2 = trail2.get(i + 1);
                    g.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
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
        updateCount++;

        if (pendulum != null) {
            updatePendulum();
        }

        if (doublePendulum != null) {
            updateDoublePendulum();
        }

        if (doublePendulums != null) {
            updateDoublePendulums();
        }

        if (multiplePendulum != null) {
            updateMultiplePendulum();
        }

        //updateR0();

        repaint();
    }

    private void updateR0() {
        x_0 = B_WIDTH / 2 + (float) (Math.sin((System.nanoTime() - start_time) * 2 * Math.PI / 1000000000f / period) * amplitude * Math.sin(theta));
        y_0 = B_HEIGHT / 3 + (float) (Math.sin((System.nanoTime() - start_time) * 2 * Math.PI / 1000000000f / period) * amplitude * Math.cos(theta));
        a_x_0 = (float) (-Math.sin((System.nanoTime() - start_time) * 2 * Math.PI / 1000000000f / period) * amplitude * Math.sin(theta) * 4 * Math.PI * Math.PI / period / period);
        a_x_0 = (float) (-Math.sin((System.nanoTime() - start_time) * 2 * Math.PI / 1000000000f / period) * amplitude * Math.cos(theta) * 4 * Math.PI * Math.PI / period / period);
    }

    private void updatePendulum() {
        pendulum.update();
    }

    private void updateDoublePendulum() {
        doublePendulum.update();
    }

    private void updateDoublePendulums() {
        if (updateCount % 100 == 0) {
            updateCount += 0;
        }
        for (int i = 0; i < doublePendulumAmount; i++) {
            doublePendulums[i].update();
        }
    }

    private void updateMultiplePendulum() {
        multiplePendulum.update(a_x_0, a_y_0);
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