package listener;

import client.Client;
import database.UserDao;
import dto.ChessNum;
import dto.ChessPos;
import dto.ChessRunning;
import dto.GameDto;
import music.MyMusic;
import service.Server;
import ui.JPanelGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

@SuppressWarnings("all")

public class MyMouseListener extends MouseAdapter {
    public JPanelGame jPanelGame;
    public static HashMap<Integer, String> methodMap = new HashMap<>();

    static {
        methodMap.put(4, "judgeMovePao");
        methodMap.put(11, "judgeMovePao");
        methodMap.put(3, "judgeMoveMa");
        methodMap.put(10, "judgeMoveMa");
        methodMap.put(2, "judgeMoveCar");
        methodMap.put(9, "judgeMoveCar");
        methodMap.put(7, "judgeMovePawn");
        methodMap.put(14, "judgeMovePawn");
        methodMap.put(6, "judgeMoveElephant");
        methodMap.put(13, "judgeMoveElephant");
        methodMap.put(5, "judgeMoveBiShop");
        methodMap.put(12, "judgeMoveBiShop");
        methodMap.put(1, "judgeMoveKing");
        methodMap.put(8, "judgeMoveKing");
    }

    public MyMouseListener(JPanelGame jPanelGame) {
        this.jPanelGame = jPanelGame;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();


        int posX = (y - 40) / 60;
        int posY = (x - 40) / 60;
        if (posX > 9 || posX < 0) {
            return;
        }
        if (posY > 8 || posY < 0) {
            return;
        }


        GameDto dto = jPanelGame.getDto();


        jPanelGame.repaintMap();

        if (dto.isNewGame() == true) {
            return;
        }
        boolean[][] pos = dto.getPos();
        int[][] map = dto.getMap();

        //System.out.println(getClickCount(pos));

        if (getClickCount(pos) == 0) {
            /*主方是黑棋*/
            if (dto.getFlag() == false) {
                if (GameDto.isBlack(map[posX][posY])) {
                    pos[posX][posY] = true;
                }
            }
            /*
             * 主方是红棋
             */
            if (dto.getFlag() == true) {
                if (GameDto.isRed(map[posX][posY])) {
                    pos[posX][posY] = true;
                }
            }
        }

        if (getClickCount(pos) == 1) {
            /*主方是黑棋*/
            if (dto.getFlag() == false) {

                /*第二次点击是黑棋*/
                if (GameDto.isBlack(map[posX][posY])) {
                    initPos(pos);
                    pos[posX][posY] = true;
                }
                /*第二次点击是红棋或者空位,判断能否吃子*/
                else if (GameDto.isRed(map[posX][posY]) || map[posX][posY] == 0) {
                    if (dto.getWhoMove() == false) {
                        return;
                    }
                    Point p = getFirstPos(pos);

                    if (p != null) {
                        try {
                            boolean boo = moveChess(map, p.x, p.y, posX, posY);
                            if (boo == false) {
                                return;
                            }
                            /*移动棋子后判断黑方是否被将能被将则不能移动棋子*/
                            if (judgeBlackBeGeneral(copyMap(map, p.x, p.y, posX, posY)) == false) {
                                JOptionPane.showMessageDialog(null, "黑将可能被将军,请重新走棋", "提示", JOptionPane.WARNING_MESSAGE);
                                return;
                            }
                            if (judgeMeetRedAndBlack(copyMap(map, p.x, p.y, posX, posY)) == true) {
                                JOptionPane.showMessageDialog(null, "将帅相遇，请重走", "提示", JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            HashMap<String,String> getSound = new HashMap<>();
                            if (GameDto.isRed(map[posX][posY])) {
                                if(dto.controlMusic == true)
                                     MyMusic.eat();
                                getSound.put("true","eat");
                            } else if(map[posX][posY] == 0) {
                                if(dto.controlMusic == true)
                                    MyMusic.move();
                                getSound.put("true", "move");
                            }



                            LinkedList<ChessRunning> history = dto.getHistory();
                            //Point begin, Point end, int value, int missValue
                            ChessRunning cr = new ChessRunning(new Point(p.x, p.y), new Point(posX, posY), map[p.x][p.y], map[posX][posY]);
                            history.addLast(cr);


                            int temp = map[p.x][p.y];
                            map[p.x][p.y] = 0;
                            map[posX][posY] = temp;



                            dto.setMap(map);

                            if (judgeRedBeGeneral(map) == false) {
                                if(dto.controlMusic == true)
                                    MyMusic.jiangJun();
                                getSound.put("true", "jiangJun");

                            }
                            dto.setHistory(history);

                            dto.whoMoveChanged();

                            initPos(pos);
                            StringBuffer sb = new StringBuffer();
                            sb.append("move:");//为传输的信息加入头信息
                            sb.append(p.x).append(" " + p.y).append(" " + posX).append(" " + posY);
                            write(sb.toString() + "&" + getSound.get("true"));
                            jPanelGame.addStepListener(false);
                            // 对方死棋判断 没有一种红棋走法可以缓解其红棋被将或者将帅相遇的局面即为死棋或者困毙
                            ArrayList<ChessRunning> allRedRunningMethod = getAllRedRunningMethod(map);

                            boolean gameOver = judgeRedChessGameOver(map, allRedRunningMethod);

                            if (gameOver == true) {
                                if (GameDto.databaseCoonect) {
                                    try {
                                        GameDto.se.increaseGameNum(GameDto.otherIp,1);
                                        UserDao ud = GameDto.jui.ud;
                                        ud.setLoserNum(ud.getLoserNum()+1);
                                        ud.setAllgameNum(ud.getAllgameNum()+1);
                                        GameDto.jui.repaint();
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                GameDto.gameOver = true;
                                dto.quarryStatus = 1;
                                dto.giveUpStatus = true;
                                jPanelGame.regretButton.setEnabled(false);
                                jPanelGame.drawBtn.setEnabled(false);
                                jPanelGame.giveUpBtn.setEnabled(false);
                                dto.setNewGame(true);
                                jPanelGame.setDto(dto);
                                JOptionPane.showMessageDialog(null, "红方已死棋", "提示", JOptionPane.WARNING_MESSAGE);
                                return;
                            }


                        } catch (NoSuchMethodException e1) {
                            e1.printStackTrace();
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        } catch (InstantiationException e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            }
            /*
             * 主方是红棋
             */
            else if (dto.getFlag() == true) {
                if (GameDto.isRed(map[posX][posY])) {
                    /*第二次点击还是红棋*/
                    initPos(pos);
                    pos[posX][posY] = true;
                }
                /*第二次点击是黑棋或者空位,判断能否吃子*/
                else {
                    if (dto.getWhoMove() == false) {
                        return;
                    }

                    /*移动棋子后判断红方是否被将能被将则不能移动棋子*/
                    Point p = getFirstPos(pos);


                    if (p != null) {
                        try {
                            boolean boo = moveChess(map, p.x, p.y, posX, posY);
                            if (boo == false) {
                                return;
                            }
                            if (judgeRedBeGeneral(copyMap(map, p.x, p.y, posX, posY)) == false) {
                                JOptionPane.showMessageDialog(null, "红帅可能被将军,请重新走棋", "提示", JOptionPane.WARNING_MESSAGE);
                                return;
                            }


                            if (judgeMeetRedAndBlack(copyMap(map, p.x, p.y, posX, posY)) == true) {
                                JOptionPane.showMessageDialog(null, "将帅相遇，请重走", "提示", JOptionPane.WARNING_MESSAGE);
                                return;
                            }

                            HashMap<String,String> getSound = new HashMap<>();

                            if (GameDto.isBlack(map[posX][posY])) {
                                if(dto.controlMusic == true)
                                    MyMusic.eat();
                                getSound.put("true", "eat");
                            } else if(map[posX][posY] == 0) {
                                if(dto.controlMusic == true)
                                    MyMusic.move();
                                getSound.put("true","move");
                            }


                            LinkedList<ChessRunning> history = dto.getHistory();
                            //Point begin, Point end, int value, int missValue
                            ChessRunning cr = new ChessRunning(new Point(p.x, p.y), new Point(posX, posY), map[p.x][p.y], map[posX][posY]);
                            history.addLast(cr);


                            int temp = map[p.x][p.y];
                            map[p.x][p.y] = 0;
                            map[posX][posY] = temp;
                            dto.setMap(map);

                            jPanelGame.setDto(dto);

                            if (judgeBlackBeGeneral(map) == false) {
                                if(dto.controlMusic == true)
                                    MyMusic.jiangJun();
                                getSound.put("true", "jiangJun");
                            }


                            initPos(pos);
                            StringBuffer sb = new StringBuffer();
                            sb.append("move:");//为传输的信息加入头信息
                            sb.append(p.x).append(" " + p.y).append(" " + posX).append(" " + posY);
                            write(sb.toString() + "&" + getSound.get("true"));

                            dto.whoMoveChanged();
                            dto.setHistory(history);
                            jPanelGame.addStepListener(false);



                            // 对方死棋判断 没有一种黑棋走法可以缓解其红棋被将或者将帅相遇的局面即为死棋或者困毙
                            ArrayList<ChessRunning> allBlackRunningMethod = getAllBlackRunningMethod(map);
/*
                            {
                                System.out.println("共"+allBlackRunningMethod.size()+"种走法");
                                for (ChessRunning chessRunning : allBlackRunningMethod) {
                                    Point begin = chessRunning.begin;
                                    Point end = chessRunning.end;
                                    String s = GameDto.numValue.get(chessRunning.value);
                                    System.out.println(begin +"---"+end+"---"+s);
                                }
                            }
*/

                            boolean gameOver = judgeBlackChessGameOver(map, allBlackRunningMethod);

                           // System.out.println(gameOver);

                            if (gameOver == true) {

                                if (GameDto.databaseCoonect) {
                                    try {
                                        GameDto.se.increaseGameNum(GameDto.otherIp,1);
                                        UserDao ud = GameDto.jui.ud;
                                        ud.setLoserNum(ud.getLoserNum()+1);
                                        ud.setAllgameNum(ud.getAllgameNum()+1);
                                        GameDto.jui.repaint();
                                    } catch (SQLException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                                dto.giveUpStatus = true;
                                dto.quarryStatus = 1;
                                GameDto.gameOver = true;
                                jPanelGame.regretButton.setEnabled(false);
                                jPanelGame.drawBtn.setEnabled(false);
                                jPanelGame.giveUpBtn.setEnabled(false);
                                dto.setNewGame(true);
                                jPanelGame.setDto(dto);
                                JOptionPane.showMessageDialog(null, "黑方已死棋", "提示", JOptionPane.WARNING_MESSAGE);

                                return;
                            }

                        } catch (NoSuchMethodException e1) {
                            e1.printStackTrace();
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        } catch (InstantiationException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        }

    }



    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();


        int posX = (y - 40) / 60;
        int posY = (x - 40) / 60;
        if (posX > 9 || posX < 0) {
            return;
        }
        if (posY > 8 || posY < 0) {
            return;
        }


        GameDto dto = jPanelGame.getDto();

        boolean[][] pos = dto.getPos();
        int[][] map = dto.getMap();

        //System.out.println(getClickCount(pos));

        if (getClickCount(pos) == 0) {
            if (dto.getFlag() == true) {
                if (!GameDto.isRed(map[posX][posY])) {
                    return;
                }
            } else {
                if (!GameDto.isBlack(map[posX][posY])) {
                    return;
                }
            }
        }

        dto.mouseX = posX;
        dto.mouseY = posY;

        jPanelGame.repaint();

    }

    private boolean judgeBlackChessGameOver(int[][] map, ArrayList<ChessRunning> allRedRunningMethod) {

        for (ChessRunning chessRunning : allRedRunningMethod) {
            Point begin = chessRunning.begin;
            Point end = chessRunning.end;
            int value = chessRunning.value;

            int missValue = map[end.x][end.y];

            map[end.x][end.y] = value;
            map[begin.x][begin.y] = ChessNum.NOCHESS;

            if (judgeBlackBeGeneral(map) == true && judgeMeetRedAndBlack(map) == false) {
               // System.out.println("Begin:"+"("+begin.x+","+begin.y+")"+"-->"+"("+end.x+","+end.y+")"+"value:"+GameDto.numValue.get(value));

                map[end.x][end.y] = missValue;
                map[begin.x][begin.y] = value;
                return false;
            }
            map[end.x][end.y] = missValue;
            map[begin.x][begin.y] = value;
        }
        return true;
    }

    private boolean judgeRedChessGameOver(int[][] map,ArrayList<ChessRunning> allRedRunningMethod) {

        for (ChessRunning chessRunning : allRedRunningMethod) {
            Point begin = chessRunning.begin;
            Point end = chessRunning.end;
            int value = chessRunning.value;

            int missValue = map[end.x][end.y];

            map[end.x][end.y] = value;
            map[begin.x][begin.y] = ChessNum.NOCHESS;

            if (judgeRedBeGeneral(map) == true && judgeMeetRedAndBlack(map) == false) {
                //System.out.println("Begin:"+"("+begin.x+","+begin.y+")"+"-->"+"("+end.x+","+end.y+")"+"value:"+GameDto.numValue.get(value));

                map[end.x][end.y] = missValue;
                map[begin.x][begin.y] = value;
                return false;
            }
            map[end.x][end.y] = missValue;
            map[begin.x][begin.y] = value;
        }
        return true;
    }

    public void write(String str) {
        Client client = jPanelGame.getClient();
        Server server = jPanelGame.getServer();

        if (client != null) {
            client.writeData(str);
        } else if (server != null) {
            server.writeData(str);
        }
    }

    public int getClickCount(boolean[][] pos) {
        int count = 0;
        for (int i = 0; i < pos.length; i++) {
            for (int j = 0; j < pos[i].length; j++) {
                if (pos[i][j] == true) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * 将pos 数组全部初始为false
     *
     * @param pos
     */
    public void initPos(boolean[][] pos) {
        for (int i = 0; i < pos.length; i++) {
            for (int j = 0; j < pos[i].length; j++) {
                pos[i][j] = false;
            }
        }
    }

    /**
     * 获取第一次点击的位置
     */
    public Point getFirstPos(boolean[][] pos) {
        Point p = new Point();

        for (int i = 0; i < pos.length; i++) {
            for (int j = 0; j < pos[i].length; j++) {
                if (pos[i][j] == true) {
                    p.x = i;
                    p.y = j;
                    return p;
                }
            }
        }

        return null;
    }

    /**
     * 判断能否走棋,利用反射和HashMap解决走棋代码冗余问题
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public boolean moveChess(int[][] map, int x1, int y1, int x2, int y2) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        String method = methodMap.get(map[x1][y1]);
        //System.out.println(method);
        if (method == null) {
            return false;
        }
        Class<? extends MyMouseListener> cls = this.getClass();
        Method method1 = cls.getMethod(method, int[][].class, int.class, int.class, int.class, int.class);
        Object res = method1.invoke(this, map, x1, y1, x2, y2);

        return (boolean) res;
    }

    /**
     * 判断炮能否行走
     *
     * @return
     */

    /**
     * 判断炮能否走
     *
     * @param map
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public boolean judgeMovePao(int[][] map, int x1, int y1, int x2, int y2) {
        if ((x1 != x2) && (y1 != y2))//炮只能横着走或者竖着走
            return false;
        if (map[x2][y2] == 0) {
            if (x1 == x2) {
                if (y1 < y2) {
                    for (int i = y1 + 1; i < y2; i++) {
                        if (map[x1][i] != 0)
                            return false;
                    }
                } else {
                    for (int i = y2 + 1; i < y1; i++) {
                        if (map[x1][i] != 0)
                            return false;
                    }
                }
            } else if (y1 == y2) {
                if (x1 < x2) {
                    for (int i = x1 + 1; i < x2; i++) {
                        if (map[i][y1] != 0)
                            return false;
                    }
                } else {
                    for (int i = x2 + 1; i < x1; i++) {
                        if (map[i][y1] != 0)
                            return false;
                    }
                }
            }
        } else if (map[x2][y2] != 0) {
            int count = 0;
            if (x1 == x2) {
                if (y1 < y2) {
                    for (int i = y1 + 1; i < y2; i++) {
                        if (map[x1][i] != 0)
                            count++;
                    }
                    if (count != 1)
                        return false;
                    if ((GameDto.isBlack(map[x1][y1]) && GameDto.isBlack(map[x2][y2])) || (GameDto.isRed(map[x1][y1]) && GameDto.isRed(map[x2][y2]))) {
                        return false;
                    }
                } else {
                    for (int i = y2 + 1; i < y1; i++) {
                        if (map[x1][i] != 0)
                            count++;
                    }
                    if (count != 1) return false;
                    if ((GameDto.isBlack(map[x1][y1]) && GameDto.isBlack(map[x2][y2])) || (GameDto.isRed(map[x1][y1]) && GameDto.isRed(map[x2][y2]))) {
                        return false;
                    }
                }
            } else if (y1 == y2) {
                if (x1 < x2) {
                    for (int i = x1 + 1; i < x2; i++) {
                        if (map[i][y1] != 0)
                            count++;
                    }
                    if (count != 1) return false;
                    if ((GameDto.isBlack(map[x1][y1]) && GameDto.isBlack(map[x2][y2])) || (GameDto.isRed(map[x1][y1]) && GameDto.isRed(map[x2][y2]))) {
                        return false;
                    }
                } else {
                    for (int i = x2 + 1; i < x1; i++) {
                        if (map[i][y1] != 0)
                            count++;
                    }
                    if (count != 1)
                        return false;
                    if ((GameDto.isBlack(map[x1][y1]) && GameDto.isBlack(map[x2][y2])) || (GameDto.isRed(map[x1][y1]) && GameDto.isRed(map[x2][y2]))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 判断马能否走
     *
     * @param map
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public boolean judgeMoveMa(int[][] map, int x1, int y1, int x2, int y2) {
        int difx = Math.abs(x1 - x2);
        int dify = Math.abs(y1 - y2);
        int i = 0;
        int j = 0;
        if (!((difx == 1 && dify == 2) || (difx == 2 && dify == 1)))//马走日
        {
            return false;
        }
        if (x1 - x2 == 2) {
            j = y1;
            i = x1 - 1;
        } else if (x2 - x1 == 2) {
            j = y1;
            i = x1 + 1;
        } else if (y1 - y2 == 2) {
            i = x1;
            j = y1 - 1;

        } else if (y2 - y1 == 2) {
            i = x1;
            j = y1 + 1;
        }
        if (i == x1 && j == y1)
            return false;
        if ((map[i][j] != 0)) {//拌马腿
            return false;
        } else
            return true;
    }

    /**
     * 判断车能否走
     *
     * @param map
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public boolean judgeMoveCar(int[][] map, int x1, int y1, int x2, int y2) {
        if ((x1 != x2) && (y1 != y2))//车只能横着走或者竖着走
            return false;
        if (x1 == x2)      //如果车横着走
        {
            if (y1 < y2) {
                for (int i = y1 + 1; i < y2; i++)//如果中间有棋子不能走
                {
                    if (map[x1][i] != 0)
                        return false;
                }
            } else if (y1 > y2)//如果中间有棋子不能走
            {
                for (int i = y2 + 1; i < y1; i++) {
                    if (map[x1][i] != 0)
                        return false;
                }
            }
        } else if (y1 == y2) //如果车竖着走
        {
            if (x1 < x2) {
                for (int i = x1 + 1; i < x2; i++) {
                    if (map[i][y1] != 0)
                        return false;
                }
            } else if (x2 < x1) {
                for (int i = x2 + 1; i < x1; i++) {
                    if (map[i][y1] != 0)
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断兵或卒能否走
     *
     * @param map
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public boolean judgeMovePawn(int[][] map, int x1, int y1, int x2, int y2) {
        if (x2 > x1)//兵不能回走
            return false;
        else if ((x1 > 4) && (x2 == x1))//兵在没过河之前不能横走
            return false;
        else if ((Math.abs(x2 - x1) + Math.abs(y2 - y1)) != 1)//兵只能走一个单位
            return false;
        else
            return true;
    }

    public boolean judgeMoveElephant(int[][] map, int x1, int y1, int x2, int y2) {
        if (x2 < 5)
            return false;
        else if ((Math.abs(y2 - y1) != 2) || (Math.abs(x2 - x1) != 2))//象的走法
            return false;
        else if (map[(x1 + x2) / 2][(y1 + y2) / 2] != 0)//象眼被填了不能走
            return false;
        else
            return true;
    }

    /*士的走法*/
    public boolean judgeMoveBiShop(int[][] map, int x1, int y1, int x2, int y2) {
        if ((y2 < 3) || (y2 > 5) || (x2 < 7))      //士在九宫格之外是不行的
            return false;
        else if ((Math.abs(y2 - y1) != 1) || (Math.abs(x2 - x1) != 1))
            return false;
        else
            return true;
    }

    public boolean judgeMoveKing(int[][] map, int x1, int y1, int x2, int y2) {
        Point p;
        if (GameDto.isBlack(map[x1][y1])) {
            p = getPositionChess(map, 8);
        } else {
            p = getPositionChess(map, 1);
        }

        if (y2 < 3 || y2 > 5 || x2 < 7) {
            return false;
        } else if ((Math.abs(y2 - y1) + Math.abs(x2 - x1)) > 1) {
            return false;
        }

        if (y2 == p.y) {
            int count = 0;
            for (int i = p.x + 1; i < x2; i++) {
                if (map[i][p.y] != 0) {
                    count++;
                }
            }
            if (count == 0) {
                JOptionPane.showMessageDialog(null, "将帅相遇，请重走", "提示", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    public boolean judgeRedBeGeneral(int[][] map) {
        Point redKing = getPositionChess(map, ChessNum.RED_SHUAI);

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int val = map[x][y];
                if (val == ChessNum.BLACK_CHE) {
                    if (judgeMoveCar(map, x, y, redKing.x, redKing.y) == true) {
                        return false;
                    }
                }
                if (val == ChessNum.BLACK_MA) {
                    if (judgeMoveMa(map, x, y, redKing.x, redKing.y) == true) {
                        return false;
                    }
                }
                if (val == ChessNum.BLACK_PAO) {
                    if (judgeMovePao(map, x, y, redKing.x, redKing.y) == true) {
                        return false;
                    }
                }
                if (val == ChessNum.BLACK_ZHU) {
                    if ((x == redKing.x) && (Math.abs(redKing.y - y) == 1)) {
                        return false;
                    }
                    if ((y == redKing.y) && (redKing.x - x) == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean judgeBlackBeGeneral(int[][] map) {
        Point blackKing = getPositionChess(map, ChessNum.BLACK_JIANG);

        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                int val = map[x][y];
                if (val == ChessNum.RED_CHE) {
                    if (judgeMoveCar(map, x, y, blackKing.x, blackKing.y) == true) {
                        return false;
                    }
                }
                if (val == ChessNum.RED_MA) {
                    if (judgeMoveMa(map, x, y, blackKing.x, blackKing.y) == true) {
                        return false;
                    }
                }
                if (val == ChessNum.RED_PAO) {
                    if (judgeMovePao(map, x, y, blackKing.x, blackKing.y) == true) {
                        return false;
                    }
                }
                if (val == ChessNum.RED_BING) {
                    if ((x == blackKing.x) && (Math.abs(y - blackKing.y) == 1)) {
                        return false;
                    }
                    if ((y == blackKing.y) && (blackKing.x - x) == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean judgeMeetRedAndBlack(int[][] map) {
        Point p1 = getPositionChess(map, ChessNum.RED_SHUAI);
        Point p2 = getPositionChess(map, ChessNum.BLACK_JIANG);

        if (p1.y != p2.y) {
            return false;
        }

        if (p1.y == p2.y) {
            int min, max;
            if (p1.x < p2.x) {
                min = p1.x;
                max = p2.x;
            } else {
                min = p2.x;
                max = p1.x;
            }

            for (int i = min + 1; i < max; i++) {
                if (map[i][p1.y] != ChessNum.NOCHESS) {
                    return false;
                }
            }
        }

        return true;
    }



    /**
     * 棋子从(x1,y1)移动到(x2,y2)之后的坐标
     *
     * @param map
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public int[][] copyMap(int[][] map, int x1, int y1, int x2, int y2) {
        int[][] map2 = new int[10][9];
        for (int i = 0; i < map.length; i++) {
            map2[i] = map[i].clone();
        }

        map2[x2][y2] = map2[x1][y1];
        map2[x1][y1] = ChessNum.NOCHESS;

        return map2;
    }


    public Point getPositionChess(int[][] map, int value) {
        Point p = new Point();
        for (int x = 0; x < map.length; x++) {
            for (int y = 0; y < map[x].length; y++) {
                if (map[x][y] == value) {
                    p.x = x;
                    p.y = y;

                    return p;
                }
            }
        }

        return null;
    }

    /**
     * 获得所在棋盘所有黑棋位置
     * @param map
     * @return
     */
    public ArrayList<ChessPos> getAllBlackChess(int[][] map){
        ArrayList<ChessPos> list = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for(int j=0;j<map[i].length;j++) {
                if (GameDto.isBlack(map[i][j])) {
                    list.add(new ChessPos(new Point(i,j),map[i][j]));
                }
            }
        }
        return list;
    }

    /**
     * 获得棋盘所有红棋位置
     * @param map
     * @return
     */
    public ArrayList<ChessPos> getAllRedChess(int[][] map) {
        ArrayList<ChessPos> list = new ArrayList<>();
        for (int i = 0; i < map.length; i++) {
            for(int j=0;j<map[i].length;j++) {
                if (GameDto.isRed(map[i][j])) {
                    list.add(new ChessPos(new Point(i,j),map[i][j]));
                }
            }
        }
        return list;
    }

    /**
     * 生成所有黑棋走棋方法
     * @return
     */
    public ArrayList<ChessRunning> getAllBlackRunningMethod(int[][] map) {

        ArrayList<ChessPos> allBlackChess = getAllBlackChess(map);

        ArrayList<ChessRunning> list = new ArrayList<>();

        for (ChessPos blackChess : allBlackChess) {
            Point begin = blackChess.begin;
            int value = blackChess.value;

            ArrayList<ChessRunning> oneRunningMethod = getOneRunningMethod(map, value, begin);

            list.addAll(oneRunningMethod);
        }


        return list;
    }

    /**
     * 生成所有红棋走棋方法
     */
    public ArrayList<ChessRunning> getAllRedRunningMethod(int[][] map) {
        ArrayList<ChessPos> allRedChess = getAllRedChess(map);

        ArrayList<ChessRunning> list = new ArrayList<>();

        for (ChessPos blackChess : allRedChess) {
            Point begin = blackChess.begin;
            int value = blackChess.value;

            ArrayList<ChessRunning> oneRunningMethod = getOneRunningMethod(map, value, begin);

            list.addAll(oneRunningMethod);
        }


        return list;
    }


    /**
     * 得到一种棋子的所有走法-
     * @param map
     * @param value
     * @param point
     * @return
     */
    private ArrayList<ChessRunning> getOneRunningMethod(int[][] map, int value, Point point) {
        ArrayList<ChessRunning> list = new ArrayList<>();
        switch(value) {
            case ChessNum.BLACK_JIANG: {
                Point[] points = new Point[] {
                  new Point(0,1),
                  new Point(0,-1),
                  new Point(-1,0),
                  new Point(1,0)
                };
                for (Point p : points) {
                    p.x = point.x+p.x;
                    p.y = point.y+p.y;
                    if ((p.x >= 0 && p.x <= 2) || (p.x >= 7 && p.x <= 9)) {
                        if (p.y >= 3 && p.y <= 5) {
                            if (GameDto.isRed(map[p.x][p.y]) || map[p.x][p.y] == ChessNum.NOCHESS) {
                                list.add(new ChessRunning(point, new Point(p.x, p.y), ChessNum.BLACK_JIANG));
                            }
                        }
                    }
                }
                break;
            }
            case ChessNum.RED_SHUAI: {
                Point[] points = new Point[] {
                        new Point(0,1),
                        new Point(0,-1),
                        new Point(-1,0),
                        new Point(1,0)
                };
                for (Point p : points) {
                    p.x = point.x+p.x;
                    p.y = point.y+p.y;
                    if ((p.x >= 0 && p.x <= 2) || (p.x >= 7 && p.x <= 9)) {
                        if (p.y >= 3 && p.y <= 5) {
                            if (GameDto.isBlack(map[p.x][p.y]) || map[p.x][p.y] == ChessNum.NOCHESS) {
                                list.add(new ChessRunning(point, new Point(p.x, p.y),  ChessNum.RED_SHUAI));
                            }
                        }
                    }
                }
                break;
            }

            case ChessNum.BLACK_CHE: {
                int x = point.x;
                int y = point.y;


                while (x>=1 && map[--x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_CHE));
                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_CHE));
                }

                x = point.x;
                y = point.y;
                while (x<=8 && map[++x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_CHE));
                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_CHE));
                }


                x = point.x;
                y = point.y;
                while (y>=1 && map[x][--y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_CHE));
                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_CHE));
                }


                x = point.x;
                y = point.y;
                while (y<=7 && map[x][++y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_CHE));
                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_CHE));
                }

                break;
            }

            case ChessNum.RED_CHE :{
                int x = point.x;
                int y = point.y;

                while (x>=1 && map[--x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_CHE));
                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_CHE));
                }


                x = point.x;
                y = point.y;
                while (x<=8 && map[++x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_CHE));
                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_CHE));
                }


                x = point.x;
                y = point.y;

                while (y>=1 && map[x][--y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_CHE));
                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_CHE));
                }


                x = point.x;
                y = point.y;

                while (y<=7 && map[x][++y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_CHE));
                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_CHE));
                }


                break;
            }

            case ChessNum.BLACK_PAO: {
                int x = point.x;
                int y = point.y;


                while (x>=1 && map[--x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_PAO));
                }

                while (x >= 1 && map[--x][y] == ChessNum.NOCHESS) {

                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_PAO));
                }

                x = point.x;
                y = point.y;

                while (x<=8 && map[++x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_PAO));
                }

                while (x <= 8 && map[++x][y] == ChessNum.NOCHESS) {

                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_PAO));
                }

                x = point.x;
                y = point.y;

                while (y>=1 && map[x][--y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_PAO));
                }

                while (y >= 1 && map[x][--y] == ChessNum.NOCHESS) {

                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_PAO));
                }

                x = point.x;
                y = point.y;

                while (y<=7 && map[x][y++] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.BLACK_PAO));
                }

                while (y<=7 && map[x][y++] == ChessNum.NOCHESS) {

                }

                if (GameDto.isRed(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.BLACK_PAO));
                }

                x = point.x;
                y = point.y;


                break;
            }

            case ChessNum.RED_PAO : {
                int x = point.x;
                int y = point.y;


                while (x>=1 && map[--x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_PAO));
                }

                while (x >= 1 && map[--x][y] == ChessNum.NOCHESS) {

                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_PAO));
                }

                x = point.x;
                y = point.y;

                while (x<=8 && map[++x][y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_PAO));
                }

                while (x <= 8 && map[++x][y] == ChessNum.NOCHESS) {

                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_PAO));
                }

                x = point.x;
                y = point.y;

                while (y>=1 && map[x][--y] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_PAO));
                }

                while (y >= 1 && map[x][--y] == ChessNum.NOCHESS) {

                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_PAO));
                }

                x = point.x;
                y = point.y;

                while (y<=7 && map[x][y++] == ChessNum.NOCHESS) {
                    list.add(new ChessRunning(point, new Point(x,y), ChessNum.RED_PAO));
                }

                while (y<=7 && map[x][y++] == ChessNum.NOCHESS) {

                }

                if (GameDto.isBlack(map[x][y])) {
                    list.add(new ChessRunning(point, new Point(x, y), ChessNum.RED_PAO));
                }

                x = point.x;
                y = point.y;

                break;
            }

            case ChessNum.RED_MA:
            case ChessNum.BLACK_MA: {
                Point[] points = new Point[] {
                  new Point(-2,-1),
                  new Point(-1,-2),
                  new Point(-2,1),
                  new Point(-1,2),
                  new Point(1,-2),
                  new Point(2,-1),
                  new Point(2,1),
                  new Point(1,2)
                };

                boolean b = GameDto.isBlack(map[point.x][point.y]);

                for (Point p : points) {
                    int x = point.x + p.x;
                    int y = point.y + p.y;

                    if (p.x == -2) {
                        p.x = -1;
                        p.y = 0;
                    } else if (p.x == 2) {
                        p.x = 1;
                        p.y = 0;
                    } else if (p.y == -2) {
                        p.y = -1;
                        p.x = 0;
                    } else {
                        p.y = 1;
                        p.x = 0;
                    }

                    int posX = point.x + p.x;
                    int posY = point.y + p.y;

                    if (x >= 0 && x<=9 && y >= 0 && y<=8 && posX >=0 && posX <= 9 && posY>=0 && posY<=8&& map[posX][posY] == ChessNum.NOCHESS) {
                        if((b == true && GameDto.isRed(map[x][y])) || (b== false && GameDto.isBlack(map[x][y])) || map[x][y] == ChessNum.NOCHESS)
                             list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));
                    }
                }

                break;
            }

            case ChessNum.BLACK_ZHU: {

                GameDto dto = jPanelGame.getDto();
                boolean flag = dto.getFlag();
                int x = point.x;
                int y = point.y;
                if (flag == false) {
                    if (x>=1&&!GameDto.isBlack(map[x-1][y])) {
                        list.add(new ChessRunning(point, new Point(x-1, y), map[point.x][point.y]));
                    }
                    if (x < 5) {
                        if (y>=1&&!GameDto.isBlack(map[x][y-1])) {
                            list.add(new ChessRunning(point, new Point(x, y-1), map[point.x][point.y]));
                        }
                        if (y <= 7 && !GameDto.isBlack(map[x][y + 1])) {
                            list.add(new ChessRunning(point, new Point(x, y+1), map[point.x][point.y]));
                        }
                    }
                } else {
                    if (x<=8&&!GameDto.isBlack(map[x+1][y])) {
                        list.add(new ChessRunning(point, new Point(x+1, y), map[point.x][point.y]));
                    }
                    if (x >= 5) {
                        if (y>=1&&!GameDto.isBlack(map[x][y-1])) {
                            list.add(new ChessRunning(point, new Point(x, y-1), map[point.x][point.y]));
                        }
                        if (y <= 7 && !GameDto.isBlack(map[x][y + 1])) {
                            list.add(new ChessRunning(point, new Point(x, y+1), map[point.x][point.y]));
                        }
                    }
                }
                break;
            }

            case ChessNum.RED_BING: {

                GameDto dto = jPanelGame.getDto();
                boolean flag = dto.getFlag();
                int x = point.x;
                int y = point.y;
                if (flag == true) {
                    if (x>=1&&!GameDto.isRed(map[x-1][y])) {
                        list.add(new ChessRunning(point, new Point(x-1, y), map[point.x][point.y]));
                    }
                    if (x < 5) {
                        if (y>=1&&!GameDto.isRed(map[x][y-1])) {
                            list.add(new ChessRunning(point, new Point(x, y-1), map[point.x][point.y]));
                        }
                        if (y <= 7 && !GameDto.isRed(map[x][y + 1])) {
                            list.add(new ChessRunning(point, new Point(x, y+1), map[point.x][point.y]));
                        }
                    }
                } else {
                    if (x<=8&&!GameDto.isRed(map[x+1][y])) {
                        list.add(new ChessRunning(point, new Point(x+1, y), map[point.x][point.y]));
                    }
                    if (x >= 5) {
                        if (y>=1&&!GameDto.isRed(map[x][y-1])) {
                            list.add(new ChessRunning(point, new Point(x, y-1), map[point.x][point.y]));
                        }
                        if (y <= 7 && !GameDto.isBlack(map[x][y + 1])) {
                            list.add(new ChessRunning(point, new Point(x, y+1), map[point.x][point.y]));
                        }
                    }
                }
                break;
            }

            case ChessNum.BLACK_SHI:{
                GameDto dto = jPanelGame.getDto();
                boolean flag = dto.getFlag();


                Point[] points = new Point[] {
                    new Point(-1,-1),
                    new Point(-1,1),
                    new Point( 1,-1),
                    new Point( 1, 1)
                };
                if (flag == true) {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 0 && x <= 2) && (y >= 3 && y <= 5)) {
                            if (!GameDto.isBlack(map[x][y])) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));

                            }
                        }
                    }
                } else {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 7 && x <= 9) && (y >= 3 && y <= 5)) {
                            if (!GameDto.isBlack(map[x][y])) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));

                            }
                        }
                    }
                }

                break;
            }

            case ChessNum.RED_SHI: {
                GameDto dto = jPanelGame.getDto();
                boolean flag = dto.getFlag();


                Point[] points = new Point[] {
                        new Point(-1,-1),
                        new Point(-1,1),
                        new Point( 1,-1),
                        new Point( 1, 1)
                };
                if (flag == true) {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 7 && x <= 9) && (y >= 3 && y <= 5)) {
                            if (!GameDto.isRed(map[x][y])) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));

                            }
                        }
                    }
                } else {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 0 && x <= 2) && (y >= 3 && y <= 5)) {
                            if (!GameDto.isRed(map[x][y])) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));

                            }
                        }
                    }
                }

                break;
            }

            case ChessNum.BLACK_XINAG : {

                GameDto dto = jPanelGame.getDto();
                boolean flag = dto.getFlag();


                Point[] points = new Point[] {
                        new Point(-2,-2),
                        new Point(-2,2),
                        new Point( 2,-2),
                        new Point( 2, 2)
                };
                if (flag == true) {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 0 && x <= 4) && (y >= 0 && y <= 8)) {
                            if (!GameDto.isRed(map[x][y]) && map[point.x+p.x/2][point.y+p.y/2]==ChessNum.NOCHESS) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));
                            }
                        }
                    }
                } else {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 5 && x <= 9) && (y >= 0 && y <= 8)) {
                            if (!GameDto.isRed(map[x][y]) &&  map[point.x+p.x/2][point.y+p.y/2]==ChessNum.NOCHESS) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));

                            }
                        }
                    }
                }


                break;
            }

            case ChessNum.RED_XINAG : {
                GameDto dto = jPanelGame.getDto();
                boolean flag = dto.getFlag();


                Point[] points = new Point[] {
                        new Point(-2,-2),
                        new Point(-2,2),
                        new Point( 2,-2),
                        new Point( 2, 2)
                };
                if (flag == false) {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 0 && x <= 4) && (y >= 0 && y <= 8)) {
                            if (!GameDto.isBlack(map[x][y]) && map[point.x+p.x/2][point.y+p.y/2]==ChessNum.NOCHESS) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));
                            }
                        }
                    }
                } else {
                    for (Point p : points) {
                        int x = point.x + p.x;
                        int y = point.y + p.y;

                        if ((x >= 5 && x <= 9) && (y >= 0 && y <= 8)) {
                            if (!GameDto.isBlack(map[x][y]) &&  map[point.x+p.x/2][point.y+p.y/2]==ChessNum.NOCHESS) {
                                list.add(new ChessRunning(point, new Point(x, y), map[point.x][point.y]));

                            }
                        }
                    }
                }


                break;


            }

        }

        return list;
    }

}
