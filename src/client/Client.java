package client;

import database.SqlExecute;
import database.UserDao;
import dto.ChessRunning;
import dto.GameDto;
import music.MyMusic;
import ui.JFrameGame;
import ui.JFrameUserInfo;
import ui.JPanelGame;
import ui.JPanelUserInfo;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.LinkedList;

@SuppressWarnings("all")

public class Client {
    private String ip;//服务器地址
    public int port;//服务器端口号

    private Socket socket;

    public boolean isConnect = false;

    public ClientUI clientUI;
    public MyJPanel jPanel;

    public Client() {
        jPanel = new MyJPanel(this);
        clientUI = new ClientUI(jPanel);
    }

    public void connectServer(String ip,int port) {
        Socket socket = null;
            try {
                socket = new Socket(ip, port);
                this.socket = socket;
                this.ip = ip;
                this.port = port;
                this.setConnect(true);
                writeData("true:"+GameDto.myIp+"-"+GameDto.myUserName);
                while (true) {
                    String s = readData();
                    if (s != null) {
                        if (s.startsWith("true:")) {
                            String str = s.substring(5);
                            String[] split = str.split("-");
                            GameDto.otherIp = split[0];
                            GameDto.otherUserName = split[1];

                            /**
                             * 数据库登录成功了
                             */
                            if (GameDto.databaseCoonect) {
                                if (GameDto.jui == null && GameDto.userInfo == null) {
                                    SqlExecute se = new SqlExecute();
                                    GameDto.se = se;
                                    UserDao ud = null;

                                    ud = se.getOneUserByIp(GameDto.otherIp);



                                    if (ud == null) {
                                        ud = new UserDao(GameDto.otherIp,GameDto.otherUserName,0,0,0,0);
                                        try {
                                            se.inserOneuser(ud);
                                        } catch (SQLException e1) {
                                            e1.printStackTrace();
                                        }
                                    }

                                    GameDto.ud = ud;
                                    GameDto.jui = new JPanelUserInfo(GameDto.ud);
                                    GameDto.userInfo = new JFrameUserInfo(GameDto.jui);
                                    GameDto.userInfo.setVisible(false);

                                }
                            }

                            notifyChange();

                            break;
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "主机ip地址有误或者主机还没有启动服务器!", "提示",JOptionPane.WARNING_MESSAGE);
                return;
            }
    }

    private void notifyChange() {
        //红方
        clientUI.getFrame().setVisible(false);
        GameDto dto = new GameDto(true);
        dto.setWhoMove(true);
        JPanelGame jPanelGame = new JPanelGame(dto,this);
        new JFrameGame(jPanelGame);


        jPanelGame.addStepListener(true);
        new Thread(()->{
            while (true) {
                String s = readData();
                if (s != null) {
                    if (s.startsWith("move:")) {
                        String str = null;
                        if (s.endsWith("&move")) {
                            str = s.substring(5, s.length() - 5);
                            if (dto.controlMusic) {
                                MyMusic.move();
                            }
                        } else if(s.endsWith("&eat")){
                            str = s.substring(5,s.length()-4);
                            if (dto.controlMusic) {
                                MyMusic.eat();
                            }
                        } else if(s.endsWith("&jiangJun")){
                            str = s.substring(5, s.length() - 9);
                            if (dto.controlMusic) {
                                MyMusic.jiangJun();
                            }
                        } else {
                            str = s.substring(5);
                        }
                        GameDto.stepTime = GameDto.STEPTIME;
                        moveChess(str, dto, jPanelGame);
                        jPanelGame.addStepListener(dto.getWhoMove());
                    } else if (s.startsWith("gameover:")) {
                        dto.setNewGame(true);
                        dto.quarryStatus = 2;

                        dto.giveUpStatus = true;

                        if (GameDto.databaseCoonect) {
                            try {
                                GameDto.se.increaseGameNum(GameDto.otherIp,2);
                                UserDao ud = GameDto.jui.ud;
                                ud.setWinNum(ud.getWinNum()+1);
                                ud.setAllgameNum(ud.getAllgameNum()+1);
                                GameDto.jui.repaint();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        jPanelGame.readData.setEnabled(true);
                        jPanelGame.saveData.setEnabled(true);
                        jPanelGame.regretButton.setEnabled(false);
                        jPanelGame.drawBtn.setEnabled(false);
                        jPanelGame.setDto(dto);
                        jPanelGame.repaint();
                    } else if (s.startsWith("regret:")) {
                        String str = s.substring(7);
                        if (str.equals("regret")) {
                            int result = JOptionPane.showConfirmDialog(null, "对方请求悔棋", "提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (result == JOptionPane.YES_OPTION) {
                                LinkedList<ChessRunning> history = dto.getHistory();
                                ChessRunning chessRunning = history.removeLast();
                                Point begin = chessRunning.begin;
                                Point end = chessRunning.end;
                                int value = chessRunning.value;
                                int missValue = chessRunning.missValue;
                                int[][] map = dto.getMap();
                                map[begin.x][begin.y] = value;
                                map[end.x][end.y] = missValue;
                                dto.whoMoveChanged();
                                jPanelGame.addStepListener(true);
                                jPanelGame.repaint();
                                writeData("regret:true");

                            } else {
                                writeData("regret:false");
                            }
                        } else if(str.equals("false")) {
                            dto.regretStatusChanged();
                            JOptionPane.showMessageDialog(null, "对方拒绝悔棋", "提示", JOptionPane.WARNING_MESSAGE);
                        } else if (str.equals("true")) {
                            dto.regretStatusChanged();
                            LinkedList<ChessRunning> history = dto.getHistory();
                            ChessRunning chessRunning = history.removeLast();
                            Point begin = chessRunning.begin;
                            Point end = chessRunning.end;
                            int value = chessRunning.value;
                            int missValue = chessRunning.missValue;
                            int[][] map = dto.getMap();
                            map[begin.x][begin.y] = value;
                            map[end.x][end.y] = missValue;
                            dto.whoMoveChanged();
                            jPanelGame.addStepListener(dto.getWhoMove());
                            jPanelGame.repaint();
                        }
                    } else if (s.startsWith("newGame:")) {
                        String str = s.substring(8);
                        if (str.equals("newGame")) {
                            int result = JOptionPane.showConfirmDialog(null, "对方请求新开局，再来一局?", "提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (result == JOptionPane.YES_OPTION) {
                                if (dto.getFlag() == true) {
                                    dto.setFlag(false);
                                    dto.initPlayer2();
                                    dto.whoMove = false;

                                } else {
                                    dto.setFlag(true);
                                    dto.initPlayer1();
                                    dto.whoMove = true;
                                }
                                dto.regretStatus = false;
                                dto.giveUpStatus = false;
                                LinkedList<ChessRunning> history = dto.history;
                                while (history.size() != 0) {
                                    history.removeLast();
                                }
                                dto.stepTime = dto.STEPTIME;
                                dto.initPos(dto.getPos());
                                dto.quarryStatus = -1;
                                jPanelGame.readData.setEnabled(false);
                                jPanelGame.saveData.setEnabled(false);
                                dto.goValue = 0;
                                dto.endValue = 0;
                                dto.setNewGame(false);
                                dto.gameOver = false;
                                jPanelGame.regretButton.setEnabled(true);
                                jPanelGame.drawBtn.setEnabled(true);
                                jPanelGame.giveUpBtn.setEnabled(true);
                                jPanelGame.addStepListener(dto.getWhoMove());
                                jPanelGame.repaint();
                                dto.regretOrDrawStatus = false;
                                writeData("newGame:true");

                            } else {
                                writeData("newGame:false");
                            }


                        } else if (str.equals("true")) {
                            if (dto.getFlag() == true) {
                                dto.setFlag(false);
                                dto.initPlayer2();
                                dto.whoMove = false;


                            } else {
                                dto.setFlag(true);
                                dto.initPlayer1();
                                dto.whoMove = true;
                            }
                            dto.regretOrDrawStatus = false;
                            dto.regretStatus = false;
                            dto.drawStatus = false;
                            dto.quarryStatus = -1;
                            dto.giveUpStatus = false;
                            jPanelGame.readData.setEnabled(false);
                            jPanelGame.saveData.setEnabled(false);
                            dto.initPos(dto.getPos());
                            LinkedList<ChessRunning> history = dto.history;
                            while (history.size() != 0) {
                                history.removeLast();
                            }
                            dto.stepTime = dto.STEPTIME;
                            dto.goValue = 0;
                            dto.endValue = 0;
                            dto.newGameStatus = false;
                            dto.setNewGame(false);
                            dto.gameOver = false;
                            jPanelGame.regretButton.setEnabled(true);
                            jPanelGame.drawBtn.setEnabled(true);
                            jPanelGame.giveUpBtn.setEnabled(true);
                            jPanelGame.addStepListener(dto.getWhoMove());
                            jPanelGame.repaint();
                        } else if (str.equals("false")) {
                            dto.newGameStatus = false;
                            jPanelGame.regretButton.setEnabled(true);
                            jPanelGame.drawBtn.setEnabled(true);
                            jPanelGame.giveUpBtn.setEnabled(true);
                            JOptionPane.showMessageDialog(null, "对方拒绝开始新游戏", "提示", JOptionPane.WARNING_MESSAGE);
                        }
                    }else if (s.startsWith("draw:")) {
                        String str = s.substring(5);
                        if (str.equals("draw")) {
                            int result = JOptionPane.showConfirmDialog(null, "对方请求和棋", "提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                            if (result == JOptionPane.YES_OPTION) {

                                dto.setNewGame(true);
                                dto.quarryStatus = 0;
                                dto.giveUpStatus = true;
                                if (GameDto.databaseCoonect) {
                                    try {
                                        GameDto.se.increaseGameNum(GameDto.otherIp,0);
                                        UserDao ud = GameDto.jui.ud;
                                        ud.setAllgameNum(ud.getAllgameNum()+1);
                                        ud.setDrawNum(ud.getDrawNum()+1);
                                        GameDto.jui.repaint();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }

                                jPanelGame.readData.setEnabled(true);
                                jPanelGame.saveData.setEnabled(true);
                                dto.regretOrDrawStatus = true;
                                jPanelGame.regretButton.setEnabled(false);
                                jPanelGame.drawBtn.setEnabled(false);
                                jPanelGame.giveUpBtn.setEnabled(false);
                                jPanelGame.setDto(dto);
                                jPanelGame.repaint();

                                writeData("draw:true");

                            } else {
                                writeData("draw:false");
                            }
                        } else if (str.equals("true")) {
                            dto.setNewGame(true);
                            dto.regretOrDrawStatus = true;
                            dto.quarryStatus = 0;
                            dto.giveUpStatus = true;
                            if (GameDto.databaseCoonect) {
                                try {
                                    GameDto.se.increaseGameNum(GameDto.otherIp,0);
                                    UserDao ud = GameDto.jui.ud;
                                    ud.setAllgameNum(ud.getAllgameNum()+1);
                                    ud.setDrawNum(ud.getDrawNum()+1);
                                    GameDto.jui.repaint();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                            jPanelGame.readData.setEnabled(true);
                            jPanelGame.saveData.setEnabled(true);
                            jPanelGame.drawBtn.setEnabled(false);
                            jPanelGame.regretButton.setEnabled(false);
                            jPanelGame.giveUpBtn.setEnabled(false);
                            dto.drawStatus = false;
                            jPanelGame.setDto(dto);
                            jPanelGame.repaint();
                        } else if (str.equals("false")) {
                            dto.drawStatus = false;
                            JOptionPane.showMessageDialog(null, "对方拒绝和棋", "提示", JOptionPane.WARNING_MESSAGE);

                        }
                    } else if (s.startsWith("giveUp:")) {
                        dto.setNewGame(true);
                        dto.quarryStatus = 1;
                        if (GameDto.databaseCoonect) {
                            try {
                                GameDto.se.increaseGameNum(GameDto.otherIp,1);
                                UserDao ud = GameDto.jui.ud;
                                ud.setAllgameNum(ud.getAllgameNum()+1);
                                ud.setLoserNum(ud.getLoserNum()+1);
                                GameDto.jui.repaint();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }

                        jPanelGame.readData.setEnabled(true);
                        jPanelGame.saveData.setEnabled(true);
                        dto.regretOrDrawStatus = true;
                        jPanelGame.drawBtn.setEnabled(false);
                        jPanelGame.regretButton.setEnabled(false);
                        jPanelGame.giveUpBtn.setEnabled(false);
                        dto.drawStatus = true;
                        dto.giveUpStatus = true;
                        jPanelGame.setDto(dto);
                        jPanelGame.repaint();
                        JOptionPane.showMessageDialog(null, "对方认输", "提示", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }).start();
    }


    public void dateChange(String str,JPanelGame jPanelGame) {
        String[] split = str.split(" ");
        Point p1 = new Point();
        Point p2 = new Point();
        p1.x = 9-Integer.parseInt(split[0]);
        p1.y = 8 - Integer.parseInt(split[1]);
        p2.x = 9 - Integer.parseInt(split[2]);
        p2.y = 8 - Integer.parseInt(split[3]);

        GameDto dto = jPanelGame.getDto();
        int[][] map = dto.getMap();

        LinkedList<ChessRunning> history = dto.getHistory();
        //Point begin, Point end, int value, int missValue
        ChessRunning cr = new ChessRunning(new Point(p1.x, p1.y), new Point(p2.x, p2.y), map[p1.x][p1.y], map[p2.x][p2.y]);
        history.addLast(cr);
        int temp = map[p1.x][p1.y];
        dto.goValue = temp;
        dto.p1.x = p1.x;
        dto.p1.y = p1.y;
        dto.p2.x = p2.x;
        dto.p2.y = p2.y;
        dto.endValue = map[p2.x][p2.y];
        map[p1.x][p1.y] = 0;
        map[p2.x][p2.y] = temp;
        dto.setMap(map);
        dto.setHistory(history);
        jPanelGame.setDto(dto);
    }

    /**
     * 通过网络传输数据得到对方的移动棋子
     * @param str
     * @param dto
     * @param jPanelGame
     */
    public void moveChess(String str,GameDto dto, JPanelGame jPanelGame) {
        dateChange(str,jPanelGame);
        dto.whoMoveChanged();
        jPanelGame.setDto(dto);
        jPanelGame.repaintMap();
    }

    public void writeData(String str) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(str.getBytes());
        } catch (IOException e) {
            System.out.println("客户端写入数据失败!");
        }
    }

    public String readData() {
        String str = null;
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
            byte[] bys = new byte[1024];
            int len = inputStream.read(bys);
            str = new String(bys,0,len);
        } catch (IOException e) {
            System.out.println("与对方断开连接了");
            GameDto.isCoonect = false;
            try {
                socket.close();

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return str;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public boolean getConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }
}
