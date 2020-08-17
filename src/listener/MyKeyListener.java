package listener;

import dto.GameDto;
import photo.Photos;
import ui.ChessFrameLayer;
import ui.JPanelGame;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyKeyListener extends KeyAdapter {

    public JPanelGame jPanelGame;

    public MyKeyListener(JPanelGame jPanelGame) {
        this.jPanelGame = jPanelGame;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        /**
         * 按下k键时，换棋盘图片
         */
        if (KeyEvent.VK_K == keyCode) {
            Image[] imgs = Photos.getAllChessBackGround();
            ChessFrameLayer.CHESS_BK = Photos.getAllChessBackGround()[(++Photos.chessBkNum)%imgs.length];
            jPanelGame.repaintMap();
        }
        /**
         * 按下L键时，换背景图片
         */
        if (KeyEvent.VK_L == keyCode) {
            Image[] imgs = Photos.getAllBackGround();
            JPanelGame.BK = Photos.getAllBackGround()[(++Photos.bkNum)%imgs.length];
            jPanelGame.repaintMap();
        }

        /**
         * 按下J键时，换棋子类型
         */
        if (KeyEvent.VK_J == keyCode) {
            Photos.changeChess();
            GameDto.initChessImg();//重新将HashMap地址改变
            jPanelGame.repaintMap();
        }



    }
}
