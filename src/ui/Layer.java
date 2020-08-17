package ui;

import dto.GameDto;
import photo.Photos;

import java.awt.*;

@SuppressWarnings("all")

public abstract class Layer {
    private static final Image WINDOW_IMG = Photos.Kuang;
    protected static final int SIZE = 3;

    protected GameDto dto;
    /**
     * 窗口左上角x坐标
     */
    private int x;
    /**
     * 窗口左上角y坐标
     */
    private int y;
    /**
     * 窗口宽度
     */
    private int w;
    /**
     * 窗口高度
     */
    private int h;

    public Layer(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    /**
     * 绘制窗口用
     */


    public void createWindow(Graphics g) {
        int img_w = WINDOW_IMG.getWidth(null);
        int img_h = WINDOW_IMG.getHeight(null);
        //左上
        g.drawImage(WINDOW_IMG, x, y, x + SIZE, y + SIZE, 0, 0, SIZE, SIZE, null);
        //中上
        g.drawImage(WINDOW_IMG, x + SIZE, y, x+w - SIZE, y + SIZE, SIZE, 0,img_w - SIZE, SIZE, null);
        //右上
        g.drawImage(WINDOW_IMG, x + w - SIZE, y, x + w, y+SIZE, img_w - SIZE, 0, img_w, SIZE,null);
        //左
        g.drawImage(WINDOW_IMG, x, y + SIZE, x + SIZE, y + h - SIZE, 0, SIZE, SIZE, img_h - SIZE, null);
        //右
        g.drawImage(WINDOW_IMG, x+w-SIZE, y + SIZE, x + w, y + h - SIZE, img_w-SIZE, SIZE, img_w, img_h - SIZE, null);
        //左下
        g.drawImage(WINDOW_IMG, x, y + h - SIZE, x + SIZE, y + h, 0, img_h - SIZE, SIZE, img_h, null);
        //左中
        g.drawImage(WINDOW_IMG, x + SIZE, y+h-SIZE, x+w - SIZE, y + h, SIZE, img_h-SIZE,img_w - SIZE, img_h, null);
        //左右
        g.drawImage(WINDOW_IMG, x + w - SIZE, y + h - SIZE, x + w, y + h, img_w - SIZE, img_h - SIZE, img_w, img_h, null);
        g.drawImage(WINDOW_IMG, x + SIZE, y + SIZE, x + w - SIZE, y + h - SIZE, SIZE, SIZE, img_w - SIZE, img_h - SIZE, null);

    }

    public void setGameDto(GameDto dto) {
        this.dto = dto;
    }

    public abstract void paint(Graphics g);

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }
}
