package service;

import photo.Photos;
import util.FrameUtil;

import javax.swing.*;
import java.awt.*;


class MyJPanel extends JPanel{
    public static Image BK = Photos.getAllBackGround()[0];


    private int port;
    /**
     * 判断是否点击了连接按钮
     */
    public Server server;

    public MyJPanel(Server server) {
        this.setLayout(null);
        this.server = server;
        init();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(BK, 0, 0, this.getWidth(), this.getHeight(), 0, 0, BK.getWidth(null), BK.getHeight(null), null);
        g.setColor(Color.WHITE);
        g.setFont(new Font("楷体", Font.BOLD, 30));

        g.drawString("已启动服务器", 150, 100);
        g.drawString("客户端连接即可开始游戏", 150, 200);
        g.drawString("正在等待客户端连接...", 150, 300);

        this.requestFocus();
    }

    public void init() {

        setPort(9999);
        //TODO
        new Thread(){
            @Override
            public void run() {
                server.startServer(port);
            }
        }.start();

    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}


public class ServiceUI {

    public JFrame frame;

    public ServiceUI(MyJPanel jPanel) {

        JFrame f = new JFrame("启动服务器");
        f.setBounds(500,200,600,400);

        //窗口居中
        FrameUtil.setFrameCenter(f);

        f.setIconImage(Photos.ICON);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.setResizable(false);

        f.setContentPane(jPanel);
        f.setVisible(true);

        this.frame = f;

    }


    public Component getFrame() {
        return frame;
    }
}
