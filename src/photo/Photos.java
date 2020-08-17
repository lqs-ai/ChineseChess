package photo;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * 图片素材加载类
 */
@SuppressWarnings("all")
public class Photos {

    /**
     * 棋盘背景编号
     */
    public static int chessBkNum = 0;
    /**
     * 背景编号
     */
    public static int bkNum = 0;
    /**
     * 棋子类型编号
     */
    public static int chessNum = 0;

    private static int allChess = 6;

    /**
     * allChess种棋子供选择
     */
    public static Image[][] imgChess = new Image[allChess][14];

    static {
        for (int i = 0; i < allChess; i++) {
            imgChess[i][0] = new ImageIcon("Chess\\graphics\\chess" + i + "\\ba.png").getImage();
            imgChess[i][1] = new ImageIcon("Chess\\graphics\\chess" + i + "\\bb.png").getImage();
            imgChess[i][2] = new ImageIcon("Chess\\graphics\\chess" + i + "\\bc.png").getImage();
            imgChess[i][3] = new ImageIcon("Chess\\graphics\\chess" + i + "\\bk.png").getImage();
            imgChess[i][4] = new ImageIcon("Chess\\graphics\\chess" + i + "\\bn.png").getImage();
            imgChess[i][5] = new ImageIcon("Chess\\graphics\\chess" + i + "\\bp.png").getImage();
            imgChess[i][6] = new ImageIcon("Chess\\graphics\\chess" + i + "\\br.png").getImage();
            imgChess[i][7] = new ImageIcon("Chess\\graphics\\chess" + i + "\\ra.png").getImage();
            imgChess[i][8] = new ImageIcon("Chess\\graphics\\chess" + i + "\\rb.png").getImage();
            imgChess[i][9] = new ImageIcon("Chess\\graphics\\chess" + i + "\\rc.png").getImage();
            imgChess[i][10] = new ImageIcon("Chess\\graphics\\chess" + i + "\\rk.png").getImage();
            imgChess[i][11] = new ImageIcon("Chess\\graphics\\chess" + i + "\\rn.png").getImage();
            imgChess[i][12] = new ImageIcon("Chess\\graphics\\chess" + i + "\\rp.png").getImage();
            imgChess[i][13] = new ImageIcon("Chess\\graphics\\chess" + i + "\\rr.png").getImage();
        }

    }

    /**
     * 点击棋子代表选中状态的图
     */
    public static Image R_CHESS_FRAME = new ImageIcon("Chess\\graphics\\select\\r_box.png").getImage();
    public static Image B_CHESS_FRAME = new ImageIcon("Chess\\graphics\\select\\b_box.png").getImage();
    public static Image SELECT = new ImageIcon("Chess\\graphics\\select\\mask.png").getImage();
    public static Image TRANS = new ImageIcon("Chess\\graphics\\img\\trans.png").getImage();
    public static Image ICON = new ImageIcon("Chess\\graphics\\chess0\\rk.png").getImage();

    public static ImageIcon Next = new ImageIcon("Chess\\graphics\\img\\Next.png");
    public static ImageIcon Before = new ImageIcon("Chess\\graphics\\img\\Before.png");




    /**
     * 黑棋和红棋
     */
    public static Image BLACK_SHI = imgChess[chessNum][0];
    public static Image BLACK_XINAG = imgChess[chessNum][1];
    public static Image BLACK_PAO = imgChess[chessNum][2];
    public static Image BLACK_JIANG = imgChess[chessNum][3];
    public static Image BLACK_MA = imgChess[chessNum][4];
    public static Image BLACK_ZHU = imgChess[chessNum][5];
    public static Image BLACK_CHE = imgChess[chessNum][6];
    public static Image RED_SHI = imgChess[chessNum][7];
    public static Image RED_XINAG = imgChess[chessNum][8];
    public static Image RED_PAO = imgChess[chessNum][9];
    public static Image RED_SHUAI = imgChess[chessNum][10];
    public static Image RED_MA = imgChess[chessNum][11];
    public static Image RED_BING = imgChess[chessNum][12];
    public static Image RED_CHE = imgChess[chessNum][13];

    /**
     * 改变棋子类型的方法
     */
    public static void changeChess() {

        ++chessNum;
        BLACK_SHI = imgChess[chessNum%(imgChess.length)][0];
        BLACK_XINAG = imgChess[chessNum%(imgChess.length)][1];
        BLACK_PAO = imgChess[chessNum%(imgChess.length)][2];
        BLACK_JIANG = imgChess[chessNum%(imgChess.length)][3];
        BLACK_MA = imgChess[chessNum%(imgChess.length)][4];
        BLACK_ZHU = imgChess[chessNum%(imgChess.length)][5];
        BLACK_CHE = imgChess[chessNum%(imgChess.length)][6];
        RED_SHI = imgChess[chessNum%(imgChess.length)][7];
        RED_XINAG = imgChess[chessNum%(imgChess.length)][8];
        RED_PAO = imgChess[chessNum%(imgChess.length)][9];
        RED_SHUAI = imgChess[chessNum%(imgChess.length)][10];
        RED_MA = imgChess[chessNum%(imgChess.length)][11];
        RED_BING = imgChess[chessNum%(imgChess.length)][12];
        RED_CHE = imgChess[chessNum%(imgChess.length)][13];

    }


    /**
     * 棋盘图
     */
    public static Image[] getAllChessBackGround() {
        File file = new File("Chess\\graphics\\chessbackground");
        ArrayList<Image> list = new ArrayList<>();
        File[] files = file.listFiles();
        Image g = null;
        for (File f : files) {
            g = new ImageIcon(f.getPath()).getImage();
            list.add(g);
        }
        Object[] obj = list.toArray();
        Image[] img = new Image[obj.length];

        for (int i = 0; i < img.length; i++) {
            img[i] = (Image) obj[i];
        }
        return img;
    }

    /**
     * 背景图
     */
    public static Image[] getAllBackGround() {
        File file = new File("Chess\\graphics\\background");
        ArrayList<Image> list = new ArrayList<>();
        File[] files = file.listFiles();
        Image g = null;
        for (File f : files) {
            g = new ImageIcon(f.getPath()).getImage();
            list.add(g);
        }
        Object[] obj = list.toArray();
        Image[] img = new Image[obj.length];

        for (int i = 0; i < img.length; i++) {
            img[i] = (Image) obj[i];
        }
        return img;
    }


    /**
     * 方框图
     */
    public static Image Kuang = new ImageIcon("Chess\\graphics\\window\\Window.png").getImage();

}
