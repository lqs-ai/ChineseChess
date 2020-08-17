package ui;

import photo.Photos;
import util.FrameUtil;

import javax.swing.*;

public class JFrameGame extends JFrame {

    public JPanel jpanel;

    public JFrameGame(JPanel jpanel) {
        //设置标题
        this.setTitle("中国象棋");
        //设置默认关闭
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        //设置窗口大小
        this.setSize(900,690);


        this.setIconImage(Photos.ICON);

        //窗口居中
        FrameUtil.setFrameCenter(this);

        //不允许用户改变窗口大小
        this.setResizable(false);

        this.setContentPane(jpanel);


        //显示可见
        this.setVisible(true);

    }

    public JPanel getJpanel() {
        return jpanel;
    }

    public void setJpanel(JPanel jpanel) {
        this.jpanel = jpanel;
    }
}
