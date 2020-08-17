package dto;

import java.io.Serializable;
import java.util.LinkedList;

public class ManualDto implements Serializable{
    public LinkedList<ChessRunning> history;

    /**
     * 标志主方式红棋还是黑棋,false代表主方式黑棋，true代表主方式红棋
     */
    public boolean flag;

    public String myip;
    public String otherip;

    public String myUserName;
    public String otherUserName;

    public int quarryStatus;

    public ManualDto(LinkedList<ChessRunning> history, boolean flag) {
        this.history = history;
        this.flag = flag;
    }

    public ManualDto(LinkedList<ChessRunning> history, boolean flag, String myip, String otherip, String myUserName, String otherUserName) {
        this.history = history;
        this.flag = flag;
        this.myip = myip;
        this.otherip = otherip;
        this.myUserName = myUserName;
        this.otherUserName = otherUserName;
    }

    public ManualDto(LinkedList<ChessRunning> history, boolean flag, String myip, String otherip, String myUserName, String otherUserName, int quarryStatus) {
        this.history = history;
        this.flag = flag;
        this.myip = myip;
        this.otherip = otherip;
        this.myUserName = myUserName;
        this.otherUserName = otherUserName;
        this.quarryStatus = quarryStatus;
    }
}
