package dto;

import java.awt.*;
import java.io.Serializable;

/**
 * 棋子value从位置begin到位置end的走法,该类记录走的方法
 */
public class ChessRunning implements Serializable{
    public Point begin;

    public Point end;

    public int value;

    /**
     * 棋子end位置之前的值，可能为NOCHESS
     */
    public int missValue;

    public ChessRunning(Point begin, Point end, int value) {
        this.begin = begin;
        this.end = end;
        this.value = value;
    }

    public ChessRunning(Point begin, Point end, int value, int missValue) {
        this.begin = begin;
        this.end = end;
        this.value = value;
        this.missValue = missValue;
    }
}
