package ui;

import database.UserDao;
import photo.Photos;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class JPanelUserInfo extends JPanel {

    public static Image BK = Photos.getAllBackGround()[Photos.bkNum];

    public UserDao ud;
    //new UserDao("192.168.43.170","罗齐生",30,2,12,44);

    public JPanelUserInfo() {

    }

    public JPanelUserInfo(UserDao ud) {
        this.ud = ud;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(BK, 0, 0, this.getWidth(), this.getHeight(), 0, 0, BK.getWidth(null), BK.getHeight(null), null);

        g.setColor(Color.WHITE);
        g.setFont(new Font("宋体",Font.BOLD,20));

        //500/7 = 70; 30  90  150 210 270 330 390
        g.drawString("对方用户名:",70,30);
        g.drawString("对方ip:", 70, 90);
        g.drawString("对方胜局数:",70,150);
        g.drawString("对方输局数:",70,210);
        g.drawString("对方和棋数:",70,270);
        g.drawString("对方总对局数",70,330);
        g.drawString("胜率:",70,390);

        g.setFont(new Font("楷体",Font.BOLD,17));
        g.setColor(Color.yellow);
        g.drawString(ud.getUserName(),230,30);
        g.drawString(ud.getIp(),230,90);
        g.drawString(String.valueOf(ud.getWinNum()),230,150);
        g.drawString(String.valueOf(ud.getLoserNum()),230,210);
        g.drawString(String.valueOf(ud.getDrawNum()),230,270);
        g.drawString(String.valueOf(ud.getAllgameNum()),230,330);
        g.drawString(this.getWinRate(ud),230,390);


        g.setColor(Color.YELLOW);

        g.setFont(new Font("楷体", Font.BOLD, 18));



        this.requestFocus();
    }

    public String getWinRate(UserDao ud) {

        String s = null;

        if (ud == null)
            return s;

        int winNum = ud.getWinNum();
        int allgameNum = ud.getAllgameNum();

        if (allgameNum == 0) {
            return "0%";
        }

        double winRate = winNum*100.0/allgameNum;

        DecimalFormat df = new DecimalFormat("#.00");
        String d = df.format(winRate);

        s = d+"%";

        return s;

    }

    public void setUd(UserDao ud) {
        this.ud = ud;
    }
}
