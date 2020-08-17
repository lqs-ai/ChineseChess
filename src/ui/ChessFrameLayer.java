package ui;

import dto.GameDto;
import photo.Photos;

import java.awt.*;
import java.util.HashMap;

@SuppressWarnings("all")
public class ChessFrameLayer extends Layer {

    private static final int WID = 70;
    private static final int HIG = 60;
    private static final int BOARD = 60;

    public static Image CHESS_BK = Photos.getAllChessBackGround()[Photos.chessBkNum];



    public ChessFrameLayer(int x, int y, int w, int h) {
        super(x, y, w, h);
    }


    @Override
    public void paint(Graphics g) {
        super.createWindow(g);
        g.drawImage(CHESS_BK, 10,10,610,640,0,0,CHESS_BK.getWidth(null),CHESS_BK.getHeight(null),null);

        Graphics2D g2 = (Graphics2D)g;  //g是Graphics对象

        drawFrom(g2);

        printMap(g,super.dto.getMap(),super.dto.getPos());

        if(GameDto.isCompound == false) {
            if (GameDto.isCoonect == false) {
                g.setColor(Color.RED);
                g.setFont(new Font("楷体", Font.BOLD, 30));
                g.drawString("对方网络连接已断开!",140,350);
            } else if (super.dto.isNewGame() == true) {
                g.setColor(Color.RED);
                g.setFont(new Font("楷体", Font.BOLD, 40));
                g.drawString("棋局已结束!",200,350);
            } else if (GameDto.stepTime <= 60&&dto.getWhoMove()) {
                g.setColor(Color.YELLOW);
                g.setFont(new Font("楷体", Font.BOLD, 25));
                g.drawString("剩余时间:"+GameDto.stepTime,230,340);
            }
            putChess(g,Photos.TRANS,super.dto.mouseY,super.dto.mouseX);
        }

        //drawNumber(g);

    }

    public void drawNumber(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("楷体", Font.BOLD, 15));
        for(int i=0;i<9;i++) {
            g.drawString(String.valueOf(i),65+i*60,25);
        }

        for(int i=0;i<10;i++) {
            g.drawString(String.valueOf(i),20,65+i*60);
        }

    }

    public void drawFrom(Graphics2D g2) {
        g2.setStroke(new BasicStroke(3.0f));

        g2.setColor(Color.BLACK);

        g2.drawRect(65,55,490,550);

        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRect(70,60,480,540);
        for(int i=1;i<=8;i++) {
            g2.drawLine(70,60+60*i,550,60+60*i);
        }

        for(int i=1;i<=7;i++) {
            g2.drawLine(70+60*i,60,70+60*i,300);
            g2.drawLine(70 + 60 * i, 360, 70 + 60 * i, 600);
        }

        g2.drawLine(WID + 5 * BOARD, HIG + 7 * BOARD, WID + 3 * BOARD, HIG + 9 * BOARD);
        g2.drawLine(WID + 3 * BOARD, HIG, WID + 5 * BOARD, HIG + 2 * BOARD);
        g2.drawLine(WID + 3 * BOARD, HIG + 2 * BOARD, WID + 5 * BOARD, HIG);
        g2.drawLine(WID + 3 * BOARD, HIG + 7 * BOARD, WID + 5 * BOARD, HIG + 9 * BOARD);

        for (int i = 0; i < 2; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                g2.drawLine(WID + 1 * BOARD - 3 + i * 6 * BOARD, HIG + 2 * BOARD - 3 + j * 5 * BOARD, WID + 1 * BOARD - 10 + i * 6 * BOARD, HIG + 2 * BOARD - 3 + j * 5 * BOARD);
                g2.drawLine(WID + 1 * BOARD + 3 + i * 6 * BOARD, HIG + 2 * BOARD - 3 + j * 5 * BOARD, WID + 1 * BOARD + 10 + i * 6 * BOARD, HIG + 2 * BOARD - 3 + j * 5 * BOARD);
                g2.drawLine(WID + 1 * BOARD - 3 + i * 6 * BOARD, HIG + 2 * BOARD - 3 + j * 5 * BOARD, WID + 1 * BOARD - 3 + i * 6 * BOARD, HIG + 2 * BOARD - 10 + j * 5 * BOARD);
                g2.drawLine(WID + 1 * BOARD + 3 + i * 6 * BOARD, HIG + 2 * BOARD - 3 + j * 5 * BOARD, WID + 1 * BOARD + 3 + i * 6 * BOARD, HIG + 2 * BOARD - 10 + j * 5 * BOARD);
                g2.drawLine(WID + 1 * BOARD - 3 + i * 6 * BOARD, HIG + 2 * BOARD + 3 + j * 5 * BOARD, WID + 1 * BOARD - 3 + i * 6 * BOARD, HIG + 2 * BOARD + 10 + j * 5 * BOARD);
                g2.drawLine(WID + 1 * BOARD + 3 + i * 6 * BOARD, HIG + 2 * BOARD + 3 + j * 5 * BOARD, WID + 1 * BOARD + 3 + i * 6 * BOARD, HIG + 2 * BOARD + 10 + j * 5 * BOARD);
                g2.drawLine(WID + 1 * BOARD - 3 + i * 6 * BOARD, HIG + 2 * BOARD + 3 + j * 5 * BOARD, WID + 1 * BOARD - 10 + i * 6 * BOARD, HIG + 2 * BOARD + 3 + j * 5 * BOARD);
                g2.drawLine(WID + 1 * BOARD + 3 + i * 6 * BOARD, HIG + 2 * BOARD + 3 + j * 5 * BOARD, WID + 1 * BOARD + 10 + i * 6 * BOARD, HIG + 2 * BOARD + 3 + j * 5 * BOARD);
            }
        }
        for (int i = 1; i <= 4; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                g2.drawLine(WID + 2 * i*BOARD - 3, HIG + 3 * BOARD + 3 * j*BOARD - 3, WID + 2 * i*BOARD - 10, HIG + 3 * BOARD + 3 * j*BOARD - 3);
                g2.drawLine(WID + 2 * i*BOARD - 3, HIG + 3 * BOARD + 3 * j*BOARD - 3, WID + 2 * i*BOARD - 3, HIG + 3 * BOARD + 3 * j*BOARD - 10);
                g2.drawLine(WID + 2 * i*BOARD - 3, HIG + 3 * BOARD + 3 * j*BOARD + 3, WID + 2 * i*BOARD - 3, HIG + 3 * BOARD + 3 * j*BOARD + 10);
                g2.drawLine(WID + 2 * i*BOARD - 3, HIG + 3 * BOARD + 3 * j*BOARD + 3, WID + 2 * i*BOARD - 10, HIG + 3 * BOARD + 3 * j*BOARD + 3);
            }
        }


        for (int i = 0; i < 4; i++)
        {
            for (int j = 0; j < 2; j++)
            {
                g2.drawLine(WID + 2 * i*BOARD + 3, HIG + 3 * BOARD + 3 * j*BOARD - 3, WID + 2 * i*BOARD + 10, HIG + 3 * BOARD + 3 * j*BOARD - 3);
                g2.drawLine(WID + 2 * i*BOARD + 3, HIG + 3 * BOARD + 3 * j*BOARD - 3, WID + 2 * i*BOARD + 3, HIG + 3 * BOARD + 3 * j*BOARD - 10);
                g2.drawLine(WID + 2 * i*BOARD + 3, HIG + 3 * BOARD + 3 * j*BOARD + 3, WID + 2 * i*BOARD + 3, HIG + 3 * BOARD + 3 * j*BOARD + 10);
                g2.drawLine(WID + 2 * i*BOARD + 3, HIG + 3 * BOARD + 3 * j*BOARD + 3, WID + 2 * i*BOARD + 10, HIG + 3 * BOARD + 3 * j*BOARD + 3);
            }
        }

    }

    public void putChess(Graphics g,Image img, int x, int y) {
        g.drawImage(img,WID+x*BOARD-27,HIG+y*BOARD-27,null);
    }

    public void printMap(Graphics g,int[][] map,boolean[][] pos) {
        for(int x = 0;x<map.length;x++) {
            for(int y=0;y<map[x].length;y++) {
                HashMap<Integer, Image> allChessImg = dto.getAllChessImg();
                Image image = allChessImg.get(map[x][y]);

                if (image != null) {
                    putChess(g,image,y,x);
                }
            }
        }

        Image r_img = Photos.R_CHESS_FRAME;
        Image b_img = Photos.B_CHESS_FRAME;
        for(int x=0;x<pos.length;x++) {
            for(int y=0;y<pos[x].length;y++) {
                if (pos[x][y] == true) {
                    if(GameDto.isBlack(map[x][y]))
                        putChess(g,b_img,y,x);
                    else if (GameDto.isRed(map[x][y])) {
                        putChess(g,r_img,y,x);
                    }
                }
            }
        }

    }

}
