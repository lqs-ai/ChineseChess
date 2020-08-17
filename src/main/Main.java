package main;

import photo.Photos;
import ui.JPanelChoose;
import util.FrameUtil;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JPanelChoose jpanelChoose = new JPanelChoose();
        jpanelChoose.setJframe(new InitFrame(jpanelChoose));
    }
}


class InitFrame extends JFrame {
    public InitFrame(JPanel panel) {
        this.setTitle("中国象棋");
        this.setSize(400,300);

        this.setIconImage(Photos.ICON);
        //窗口居中
        FrameUtil.setFrameCenter(this);

        this.setContentPane(panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        //显示可见
        this.setVisible(true);
    }
}
