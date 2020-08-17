package ui;

import client.Client;
import database.CreateChessDb;
import database.UserDao;
import dto.ChessRunning;
import dto.GameDto;
import dto.ManualDto;
import listener.MyKeyListener;
import listener.MyMouseListener;
import music.BackGroundMusicPlay;
import photo.Photos;
import service.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

@SuppressWarnings("all")
/**
 * 游戏面板
 */
public class JPanelGame extends JPanel {


    public static Image BK = Photos.getAllBackGround()[Photos.bkNum];

    public GameDto dto;

    public Client client = null;
    public Server server = null;

    public JButton regretButton = null;
    public JButton drawBtn = null;
    public JButton giveUpBtn = null;

    public JButton newGame = null;

    public JButton controlMusic = null;

    public JButton playerMusic = null;

    public JButton next = null;

    public JButton before = null;

    public JButton readData = null;

    public JButton saveData = null;

    public JButton showInfo = null;




    public Layer layer = new ChessFrameLayer(10, 10, 600, 630);

    public Layer layer2 = new InformationLayer(630,10,200,80);



    /**
     * 执行sql语句的
     * @param dto
     */

    public JPanelGame(GameDto dto) {
        this.dto = dto;
        layer.setGameDto(dto);
        layer2.setGameDto(dto);
        this.addKeyListener(new MyKeyListener(this));
        MyMouseListener myMouseListener = new MyMouseListener(this);
        this.addMouseListener(myMouseListener);
        this.addMouseMotionListener(myMouseListener);
        this.setLayout(null);

        initButton();


    }

    private void initButton() {
        //request repent
        regretButton = new JButton("悔棋");
        regretButton.setBounds(625,100,80,20);
        regretButton.setBackground(new Color(182,211,90));
        this.add(regretButton);

        drawBtn = new JButton("求和");
        drawBtn.setBounds(710,100,80,20);
        drawBtn.setBackground(new Color(182,211,90));
        this.add(drawBtn);

        giveUpBtn = new JButton("认输");
        giveUpBtn.setBounds(795,100,80,20);
        giveUpBtn.setBackground(new Color(182,211,90));
        this.add(giveUpBtn);

        newGame = new JButton("NewGame");
        newGame.setBounds(655,150,140,20);
        newGame.setBackground(new Color(182,211,90));

        this.add(newGame);

        controlMusic = new JButton("open/close sound");
        controlMusic.setBounds(655, 230, 140, 20);
        controlMusic.setBackground(new Color(182,211,90));
        this.add(controlMusic);

        playerMusic = new JButton("打开/关闭背景音乐");
        playerMusic.setBounds(625, 300, 150, 20);//775
        playerMusic.setBackground(new Color(182,211,90));
        this.add(playerMusic);

        showInfo = new JButton("查看对方信息");
        showInfo.setBounds(655,450,200,40);
        showInfo.setBackground(new Color(182,211,90));
        this.add(showInfo);

        next = new JButton("下一首");
        next.setBounds(780,315,100,20);
        next.setBackground(new Color(182,211,90));
        this.add(next);

        before = new JButton("上一首");
        before.setBounds(780,285,100,20);
        before.setBackground(new Color(182,211,90));
        this.add(before);

        readData = new JButton("打开棋谱");
        readData.setBounds(655,395,100,20);
        readData.setBackground(new Color(182,211,90));
        this.add(readData);

        saveData = new JButton("保存棋谱");
        saveData.setBounds(760,395,100,20);
        saveData.setBackground(new Color(182,211,90));
        this.add(saveData);

        next.setEnabled(false);
        before.setEnabled(false);

        readData.setEnabled(false);
        saveData.setEnabled(false);




        regretButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LinkedList<ChessRunning> history = dto.getHistory();
                if(history.size() != 0 && dto.regretStatus == false) {

                    dto.regretStatusChanged();
                    write("regret:regret");

                    repaint();
                }
            }
        });

        drawBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dto.regretStatus == false && dto.drawStatus == false) {
                    dto.drawStatusChanged();
                    write("draw:draw");
                }
                repaint();
            }
        });

        giveUpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dto.giveUpStatus) {
                    return;
                }
                int result = JOptionPane.showConfirmDialog(null, "你确定要认输吗?", "提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    write("giveUp:giveUp");
                    dto.quarryStatus = 2;
                    dto.setNewGame(true);
                    readData.setEnabled(true);
                    saveData.setEnabled(true);
                    drawBtn.setEnabled(false);
                    regretButton.setEnabled(false);
                    giveUpBtn.setEnabled(false);
                    dto.giveUpStatus = true;
                    dto.regretOrDrawStatus = true;
                    setDto(dto);
                    if (GameDto.databaseCoonect) {
                        try {
                            GameDto.se.increaseGameNum(GameDto.otherIp,2);
                            UserDao ud = GameDto.jui.ud;
                            ud.setWinNum(ud.getWinNum()+1);
                            ud.setAllgameNum(ud.getAllgameNum()+1);
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }

                    repaint();
                }
            }
        });

        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (dto.newGameStatus == false) {
                    if (dto.regretStatus == true) {
                        JOptionPane.showMessageDialog(null,"您已经发送过请求悔棋信息，请先等待对方回应!");
                        return;
                    }
                    if (dto.drawStatus == true) {
                        JOptionPane.showMessageDialog(null,"您已经发送过请求和棋信息，请先等待对方回应!");
                        return;
                    }
                    write("newGame:newGame");
                    dto.newGameStatus = true;

                    regretButton.setEnabled(false);
                    drawBtn.setEnabled(false);
                    giveUpBtn.setEnabled(false);

                    repaint();
                }
            }
        });

        controlMusic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dto.controlMusic = !dto.controlMusic;
                repaint();
            }
        });

        playerMusic.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dto.backgroundMusic = !dto.backgroundMusic;
                if (dto.backgroundMusic) {
                    next.setEnabled(true);
                    before.setEnabled(true);
                    repaint();
                    BackGroundMusicPlay.play();
                } else {
                    next.setEnabled(false);
                    before.setEnabled(false);
                    repaint();
                    BackGroundMusicPlay.close();
                }
            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dto.backgroundMusic) {
                    BackGroundMusicPlay.close();

                    BackGroundMusicPlay.play();
                }
            }
        });

        before.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dto.backgroundMusic) {
                    BackGroundMusicPlay.close();
                    int point = BackGroundMusicPlay.point;
                    int size = BackGroundMusicPlay.size;
                    if (point == 0) {
                        BackGroundMusicPlay.point = size -2;
                    } else if(point == 1) {
                        BackGroundMusicPlay.point = size -1;
                    } else {
                        BackGroundMusicPlay.point = BackGroundMusicPlay.point-2;
                    }

                    BackGroundMusicPlay.play();
                }
            }
        });

        readData.addActionListener(new ActionListener() {
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

                GameDto.isCompound = true;

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

        saveData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                File f = new File("Chess\\ChessManual");
                jf.setCurrentDirectory(f);
                while (true) {
                    int option = jf.showSaveDialog(null);
                    if (option == JFileChooser.APPROVE_OPTION) {


                        File file = jf.getSelectedFile();


                        try {
                            if (file.exists() == false) {
                                file.createNewFile();
                            } else {
                                JOptionPane.showMessageDialog(null,"该文件已存在,请重新命名!");
                                continue;
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                        try {
                            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
                            LinkedList<ChessRunning> history = dto.getHistory();
                            boolean flag = dto.getFlag();
                            ManualDto mdto = new ManualDto(history,flag,GameDto.myIp,GameDto.otherIp,GameDto.myUserName,GameDto.otherUserName,dto.quarryStatus);
                            oos.writeObject(mdto);
                            oos.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        saveData.setEnabled(false);
                    }
                    break;
                }
            }
        });

        showInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //String otherIp = dto.otherIp;
                Connection connection = null;
                try {
                    connection = DriverManager.getConnection(CreateChessDb.url, CreateChessDb.user, CreateChessDb.password);
                    dto.databaseCoonect = true;
                } catch (SQLException e1) {
                    JOptionPane.showMessageDialog(null,"登录数据库有误,请检查mysql数据库安装是否成功,检查databaseInfo下的jdbc.properties文件内容是否正确");
                    dto.databaseCoonect = false;
                    return;
                } finally {
                    try {
                        if(connection != null)
                            connection.close();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }


                // GameDto.ud.setLoserNum(GameDto.ud.getLoserNum()+1);

                GameDto.jui.repaint();
                GameDto.userInfo.setVisible(true);



            }
        });
    }


    /**
     *
     * @param flag 是我方调用时传入false，对方调用时传入true
     */
    public void addStepListener(boolean flag) {
        new Thread(()->{
            GameDto.stepTime = GameDto.STEPTIME;
            while(true) {
                if (dto.getWhoMove() == flag) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GameDto.stepTime--;
                    if (GameDto.stepTime < 0) {
                        dto.setNewGame(true);
                        this.regretButton.setEnabled(false);
                        this.drawBtn.setEnabled(false);
                        this.giveUpBtn.setEnabled(false);
                        dto.giveUpStatus = true;
                        saveData.setEnabled(true);
                        readData.setEnabled(true);
                        repaint();
                        if (GameDto.databaseCoonect) {
                            try {
                                if (dto.getWhoMove()) {
                                    GameDto.se.increaseGameNum(GameDto.otherIp,2);
                                } else {
                                    GameDto.se.increaseGameNum(GameDto.otherIp,1);
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                        if(!dto.regretOrDrawStatus)
                            JOptionPane.showMessageDialog(null, "已超步时，棋局已结束", "提示", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (dto.newGame) {
                        return;
                    }
                    this.repaint();
                } else {
                    return;
                }
            }
        }).start();
    }

    public void write(String str) {

        if (client != null) {
            client.writeData(str);
        } else if (server != null) {
            server.writeData(str);
        }
    }

    public JPanelGame(GameDto dto,Client client) {
        this(dto);
        this.client = client;
    }

    public JPanelGame(GameDto dto,Server server) {
        this(dto);
        this.server = server;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(BK, 0, 0, this.getWidth(), this.getHeight(), 0, 0, BK.getWidth(null), BK.getHeight(null), null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("楷体", Font.BOLD, 15));

        g.setColor(Color.yellow);
        if (dto.regretStatus == true) {
            g.drawString("已发送请求悔棋信息", 655, 140);
        }

        if (dto.drawStatus == true) {
            g.drawString("已发送请求和棋信息",655,140);
        }

        if (dto.newGameStatus == true) {
            g.drawString("已发送请求开始新局信息",655,140);
        }

        g.setColor(Color.WHITE);

        g.setFont(new Font("楷体", Font.BOLD, 18));
        if (dto.controlMusic == true) {
            g.drawString("游戏音效处于    状态", 655, 270);
            g.setColor(Color.red);
            g.drawString("打开", 655+6*19, 270);
        } else {
            g.drawString("游戏音效处于    状态", 655, 270);
            g.setColor(Color.red);
            g.drawString("关闭", 655+6*19, 270);
        }

        g.setColor(Color.WHITE);

        if (dto.backgroundMusic == true) {
            g.drawString("游戏背景音乐处于    状态", 655, 360);
            g.setColor(Color.red);
            g.drawString("打开",655+8*19,360);

        } else {
            g.drawString("游戏背景音乐处于    状态", 655, 360);
            g.setColor(Color.red);
            g.drawString("关闭",655+8*19,360);
        }

        g.setColor(Color.yellow);

        g.drawString("已走回合数:"+(dto.getHistory().size()/2),700,600);

        if (dto.isCoonect) {
            g.drawString("状态:已与对方连接",700,570);
        } else {
            g.drawString("状态:对方断开连接了",700,570);
        }


/*
        if (!dto.newGame) {
            g.setColor(Color.pink);
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd号 HH:mm:ss");
            g.drawString(""+df.format(System.currentTimeMillis()),640,630);
        }

*/

        g.setColor(Color.WHITE);
        if (dto.getWhoMove() == true && !dto.newGame) {
            g.setFont(new Font("楷体", Font.BOLD, 20));
            g.drawString("你的剩余走棋时间:",655,200);
            if (GameDto.stepTime < 60) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.white);
            }
            g.drawString(String.valueOf(GameDto.stepTime),835,200);
        } else if(!dto.newGame){
            g.setFont(new Font("楷体", Font.BOLD, 20));
            g.setColor(Color.white);
            g.drawString("对方剩余走棋时间:",655,200);
            g.setColor(Color.red);
            g.drawString(String.valueOf(GameDto.stepTime),835,200);
        } else {
            g.setFont(new Font("楷体", Font.BOLD, 20));
            g.setColor(Color.RED);
            g.drawString("newGame开始新局",655,200);
        }



        layer.paint(g);
        layer2.paint(g);
        if (GameDto.gameOver) {
            write("gameover:");
            dto.quarryStatus = 1;
            readData.setEnabled(true);
            saveData.setEnabled(true);
            GameDto.gameOver = false;
        }
        this.requestFocus();

    }

    public void repaintMap() {
        this.repaint();
    }

    public GameDto getDto() {
        return dto;
    }

    public void setDto(GameDto dto) {
        this.dto = dto;
    }


    public Server getServer() {
        return server;
    }

    public Client getClient() {
        return client;
    }
}

@SuppressWarnings("all")
class InformationLayer extends Layer{



    public InformationLayer(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public InformationLayer(int x, int y, int w, int h, GameDto dto) {
        super(x, y, w, h);
        this.dto = dto;
    }

    @Override
    public void paint(Graphics g) {
        Image select = Photos.SELECT;

        boolean flag = super.dto.getFlag();
        boolean whoMove = super.dto.getWhoMove();
        g.setColor(Color.WHITE);
        g.setFont(new Font("楷体", Font.BOLD, 15));

        if (flag == true) {
            if (whoMove == true) {
                g.drawString("提示:现在轮到红方走棋", this.getX() + 10, this.getY() + 20);
                if (!(super.dto.goValue == 0 && super.dto.endValue == 0)) {
                    if (!(super.dto.goValue == 0 && super.dto.endValue == 0)) {
                        putChess(g,select,super.dto.p1.y,super.dto.p1.x);
                        putChess(g,select,super.dto.p2.y,super.dto.p2.x);
                    }
                }
            } else {
                g.drawString("提示:现在轮到黑方走棋", this.getX() + 10, this.getY() + 20);
            }

        } else if (flag == false) {
            if (whoMove == true) {
                g.drawString("提示:现在轮到黑方走棋",this.getX()+10,this.getY()+20);
                if (!(super.dto.goValue == 0 && super.dto.endValue == 0)) {
                    putChess(g,select,super.dto.p1.y,super.dto.p1.x);
                    putChess(g,select,super.dto.p2.y,super.dto.p2.x);
                }
            } else {
                g.drawString("提示:现在轮到红方走棋",this.getX()+10,this.getY()+20);
            }


        }
        g.drawString("J:棋子切换；K:棋盘切换",this.getX()+10,this.getY()+40);
        g.drawString("L:背景图切换",this.getX()+10,this.getY()+60);

        createWindow(g);
    }


    public void putChess(Graphics g,Image img, int x, int y) {
        g.drawImage(img,70+x*60-27,60+y*60-27,null);
    }
}
