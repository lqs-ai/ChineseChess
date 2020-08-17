package ui;

import photo.Photos;

import javax.swing.*;
import java.awt.*;

public class JFrameUserInfo extends JFrame {

    public JFrameUserInfo(JPanel jpanel){
        //设置标题
        this.setTitle("对方用户信息");
        //设置默认关闭
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


        //设置窗口大小
        this.setSize(400,500);


        this.setIconImage(Photos.ICON);

        //窗口偏右
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = screen.width-this.getWidth()+(900-240)*2;
        int y = screen.height-this.getHeight()-60;
        this.setLocation(x>>1, y>>1);


        //不允许用户改变窗口大小
        this.setResizable(false);

        this.setContentPane(jpanel);


        //显示可见
        this.setVisible(true);
    }

    public static void main(String[] args) {

        new JFrameUserInfo(new JPanelUserInfo());
    }
}
