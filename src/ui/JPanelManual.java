package ui;

import dto.ChessRunning;
import dto.GameDto;
import dto.ManualDto;
import photo.Photos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class JPanelManual extends JPanel{
    Image select = Photos.SELECT;
    public static Image BK = Photos.getAllBackGround()[0];
    LinkedList<ChessRunning> history = null;
    Layer layer = new ChessFrameLayer(10, 10, 600, 630);
    public GameDto dto;
    public ManualDto mdto;

    public JButton next;
    public JButton before;

    public Point begin;
    public Point end;

    private int point = -1;
    private int allSize;




    public JPanelManual(ManualDto mdto) {
        this.mdto = mdto;



        before = new JButton(Photos.Before);
        before.setBounds(625,150,200,150);
        //before.setBackground(new Color(182,211,90));
        this.add(before);

        next = new JButton(Photos.Next);
        next.setBounds(625,330,200,150);
        //next.setBackground(new Color(182,211,90));
        this.add(next);

        before.setEnabled(false);
        if (allSize == 0) {
            next.setEnabled(false);
        }



        before.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (point < allSize) {
                    next.setEnabled(true);
                }
                point--;
                LinkedList<ChessRunning> history = dto.getHistory();
                int[][] map = dto.getMap();
                ChessRunning chessRunning = history.get(point+1);
                if (point == -1) {
                    before.setEnabled(false);
                }
                Point begin = chessRunning.begin;
                JPanelManual.this.begin = begin;
                Point end = chessRunning.end;
                JPanelManual.this.end = end;
                int value = chessRunning.value;

                map[begin.x][begin.y] = value;
                map[end.x][end.y] = chessRunning.missValue;

                JPanelManual.this.begin = begin;
                JPanelManual.this.end = end;

                repaint();

            }
        });

        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                point++;

                LinkedList<ChessRunning> history = dto.getHistory();
                int[][] map = dto.getMap();
                ChessRunning chessRunning = history.get(point);
                Point begin = chessRunning.begin;
                JPanelManual.this.begin = begin;
                Point end = chessRunning.end;
                JPanelManual.this.end = end;
                int value = chessRunning.value;

                map[begin.x][begin.y] = 0;
                map[end.x][end.y] = value;
                if (point > -1) {
                    before.setEnabled(true);
                }

                if (point+1 >= allSize) {
                    next.setEnabled(false);
                }

                repaint();


            }
        });



        layer.setGameDto(dto);

        this.setLayout(null);

        init();


    }

    public void init(){
        dto = new GameDto(mdto.flag);
        dto.history = mdto.history;
        allSize = dto.history.size();
        dto.quarryStatus = mdto.quarryStatus;
        layer.setGameDto(dto);
        before.setEnabled(false);
        begin = null;
        end = null;
        if (allSize == 0) {
            next.setEnabled(false);
        } else {
            next.setEnabled(true);
        }
        point = -1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(BK, 0, 0, this.getWidth(), this.getHeight(), 0, 0, BK.getWidth(null), BK.getHeight(null), null);

        layer.paint(g);

        if (begin != null && end != null) {
            putChess(g,select,begin.y,begin.x);
            putChess(g,select,end.y,end.x);
        }

        g.setColor(Color.YELLOW);

        g.setFont(new Font("楷体", Font.BOLD, 18));

        if (dto.getFlag() == true) {

            g.drawString("红方信息:",625,530);
            g.drawString("红方ip:"+mdto.myip,625,550);
            g.drawString("红方用户名:"+mdto.myUserName,625,570);

            g.drawString("黑方信息:",625,50);
            g.drawString("黑方ip:"+mdto.otherip,625,70);
            g.drawString("黑方用户名:"+mdto.otherUserName,625,90);
        } else {
            g.drawString("黑方信息:",625,530);
            g.drawString("黑方ip:"+mdto.myip,625,550);
            g.drawString("黑方用户名:"+mdto.myUserName,625,570);

            g.drawString("红方信息:",625,50);
            g.drawString("红方ip:"+mdto.otherip,625,70);
            g.drawString("红方用户名:"+mdto.otherUserName,625,90);
        }
        g.setColor(Color.red);
        String str = (dto.getFlag() == true) ? "红方":"黑方";
        if(dto.quarryStatus == -1) {
            g.drawString("本局红方与黑方输赢状态:无结果",615,120);
        } else if (dto.quarryStatus == 0) {
            g.drawString("本局红方与黑方输赢状态:和棋",615,120);
        } else if (dto.quarryStatus == 1) {
            g.drawString("本局红方与黑方输赢状态:"+str+"胜",615,120);
        } else if (dto.quarryStatus == 2) {
            g.drawString("本局红方与黑方输赢状态:"+str+"负",615,120);

        }

        this.requestFocus();
    }

    public void putChess(Graphics g,Image img, int x, int y) {
        g.drawImage(img,70+x*60-27,60+y*60-27,null);
    }

    public void setMdto(ManualDto mdto) {
        this.mdto = mdto;
        init();


    }


}
