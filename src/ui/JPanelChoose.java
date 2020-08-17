package ui;

import client.Client;
import database.CreateChessDb;
import dto.GameDto;
import dto.ManualDto;
import photo.Photos;
import service.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 客户端，服务端选择面板
 */
@SuppressWarnings("all")
public class JPanelChoose extends JPanel{
    public static Image BK = Photos.getAllBackGround()[0];
    public JFrame jframe;

    public Button START_SERVICE;
    public Button CONNECT_SERVICE;

    public JButton openManual;

    public JPanelChoose() {
        this.setLayout(null);
        initButton();
        registDatabase();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);


        g.drawImage(BK, 0, 0, this.getWidth(), this.getHeight(), 0, 0, BK.getWidth(null), BK.getHeight(null), null);
        Layer layer = new Layer(80, 20, 220, 60) {
            @Override
            public void paint(Graphics g) {
                g.setColor(Color.WHITE);
                g.setFont(new Font("楷体", Font.BOLD, 30));
                g.drawString("联网中国象棋", this.getX() + 10, this.getY() + 40);

                g.setFont(new Font("楷体", Font.BOLD, 20));
                g.setColor(Color.yellow);
                g.drawString("提示:需一方启动服务器一方启动客户端",10,250);

                createWindow(g);
            }
        };

        layer.paint(g);


        this.requestFocus();

    }

    public void initButton() {
        START_SERVICE = new Button("start Server");
        CONNECT_SERVICE = new Button("start Client");
        openManual = new JButton("打开棋谱");

        START_SERVICE.setBounds(145,110,100,30);
        CONNECT_SERVICE.setBounds(145,160,100,30);
        openManual.setBounds(120,210,150,20);

        this.add(START_SERVICE);
        this.add(CONNECT_SERVICE);
        this.add(openManual);

        //启动服务器
        this.START_SERVICE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jframe.setVisible(false);
                new Server();
            }
        });

        //连接服务器
        this.CONNECT_SERVICE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jframe.setVisible(false);
                new Client();
            }
        });

        this.openManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                File file = new File("Chess\\ChessManual");
                jf.setCurrentDirectory(file);
                jf.showOpenDialog(null);//显示打开的文件对话框
                File f =  jf.getSelectedFile();//使用文件类获取选择器选择的文件
                String s = f.getAbsolutePath();//返回路径名
                ManualDto mdto = null;
                try {
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                    mdto = (ManualDto)ois.readObject();
                    ois.close();
                } catch (IOException e1) {
                    System.out.println("打开的文件不是Chess类型的文件!");
                    return;
                } catch (ClassNotFoundException e1) {
                    System.out.println("ClassNotFoundException");
                }

                /**
                 * public static ManualDto mdto;      //复盘时的mdto
                 * public static JPanelManual jPaenlManual;
                 * public static JFrameManual jFrameManual;
                 */
                //

                if (GameDto.jPaenlManual == null && GameDto.jFrameManual == null) {
                    JPanelManual jPanelManual = new JPanelManual(mdto);

                    JFrameManual jFrameManual = new JFrameManual(jPanelManual);

                    GameDto.jPaenlManual = jPanelManual;
                    GameDto.jFrameManual = jFrameManual;
                } else {
                    GameDto.jPaenlManual.setMdto(mdto);
                    GameDto.jPaenlManual.repaint();
                    GameDto.jFrameManual.setVisible(true);
                }

            }
        });
    }

    /**
     * 登录数据库
     */
    public void registDatabase() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(CreateChessDb.url, CreateChessDb.user, CreateChessDb.password);
            GameDto.databaseCoonect = true;
        } catch (SQLException e1) {
            System.out.println("登录数据库有误,请检查mysql数据库安装是否成功,检查databaseInfo下的jdbc.properties文件内容是否正确，否则游戏将无法自动保存您与玩家的对战胜率");
            GameDto.databaseCoonect = false;
            return;
        } finally {
            try {
                if(connection != null)
                    connection.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        }
        System.out.println("mysql数据库登录成功");


    }

    public void setJframe(JFrame jframe) {
        this.jframe = jframe;
    }
}
