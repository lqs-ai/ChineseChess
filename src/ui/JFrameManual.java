package ui;

import dto.GameDto;
import photo.Photos;
import util.FrameUtil;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JFrameManual extends JFrame {
    public JFrameManual(JPanel jpanel) {
        //设置标题
        this.setTitle("复盘中");
        //设置默认关闭
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                GameDto.isCompound = false;
            }
        });

        //设置窗口大小
        this.setSize(900,690);
        this.setIconImage(Photos.ICON);

        FrameUtil.setFrameCenter(this);

        //不允许用户改变窗口大小
        this.setResizable(false);

        this.setContentPane(jpanel);


        //显示可见
        this.setVisible(true);

    }
}
