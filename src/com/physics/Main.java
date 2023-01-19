package com.physics;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private final static int WIDTH = 1200;
    private final static int HEIGHT = 800;


    public Main() {
        initUI();
    }

    private void initUI() {
        add(new Board(WIDTH, HEIGHT));

        setSize(WIDTH, HEIGHT);
        setResizable(false);

        setTitle("Pendulum");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {
            Main ex = new Main();
            ex.setVisible(true);
        });
    }
}