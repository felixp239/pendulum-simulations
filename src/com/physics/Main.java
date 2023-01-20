package com.physics;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private final static int WIDTH = 1200;
    private final static int HEIGHT = 800;


    public Main() throws Exception {
        initUI();
    }

    private void initUI() throws Exception {
        add(new Board(WIDTH, HEIGHT));

        setSize(WIDTH, HEIGHT);
        setResizable(false);

        setTitle("Pendulum");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws Exception {

        EventQueue.invokeLater(() -> {
            Main ex = null;
            try {
                ex = new Main();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ex.setVisible(true);
        });

    }
}