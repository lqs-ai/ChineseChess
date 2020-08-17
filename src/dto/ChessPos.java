package dto;

import java.awt.*;

/**
 * 该类用来表示棋子所在的位置
 */
public class ChessPos {
    public Point begin;

    public int value;

    public ChessPos(Point begin, int value) {
        this.begin = begin;
        this.value = value;
    }
}
