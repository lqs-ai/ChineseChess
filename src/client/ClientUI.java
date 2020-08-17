package client;

import photo.Photos;
import ui.Layer;
import util.FrameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class MyJPanel extends JPanel {
    public static Image BK = Photos.getAllBackGround()[0];
    JButton button = new JButton("确定");
    JTextField jf = new JTextField();

    private int port;

    private String ip;

    Client client;

    public MyJPanel(Client client) {
        this.setLayout(null);
        this.client = client;
        init();
        this.add(jf);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(BK, 0, 0, this.getWidth(), this.getHeight(), 0, 0, BK.getWidth(null), BK.getHeight(null), null);
        Layer layer = new Layer(50, 90, 160, 30) {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("楷体", Font.BOLD, 15));
                g.drawString("请输入对方ip地址", this.getX() + 10, this.getY() + 20);
                createWindow(g);
            }
        };
        layer.paint(g);
        this.requestFocus();

    }

    public void init() {
        button.setBounds(240,200,100,30);
        jf.setBounds(220,90,200,30);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = jf.getText();
                //[0-2][0-5][0-5] | \\d\\d | \\d
                //String regex = "(([0-1][0-9][0-9]|[2][0-5][0-5]|\\d\\d|\\d)\\.){3}([0-1][0-9][0-9]|[2][0-5][0-5]|\\d\\d|\\d){1}";
                String regex = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))";
                boolean matches = str.matches(regex);
                if(matches == false) {
                    JOptionPane.showMessageDialog(null, "您输入的ip地址为空或有误，请重新输入", "ip地址错误",JOptionPane.WARNING_MESSAGE);
                }


                if (matches == true) {
                    setPort(9999);
                    setIp(str);
                    new Thread() {
                        @Override
                        public void run() {
                            System.out.println("正在连接服务器");

                            client.connectServer(ip,port);

                        }
                    }.start();
                }

            }
        });

        this.add(button);
    }

    public void setPort(int port) {
        this.port = port;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }

}


public class ClientUI {

    JFrame frame = null;

    public ClientUI(MyJPanel jPanel) {
        JFrame f = new JFrame("连接服务器");

        f.setBounds(500,200,600,400);

        //窗口居中
        FrameUtil.setFrameCenter(f);

        f.setIconImage(Photos.ICON);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.setResizable(false);

        f.setContentPane(jPanel);

        this.frame = f;

        f.setVisible(true);
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }
}

