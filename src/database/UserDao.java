package database;

import java.util.Objects;

public class UserDao {
    /**
     * 用户ip地址
     */
    private String ip;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 胜局数
     */
    private int winNum;
    /**
     * 平局数
     */
    private int drawNum;
    /**
     * 输局数
     */
    private int loserNum;
    /**
     * 总对局数
     */
    private int allgameNum;


    public UserDao(String ip, String userName, int winNum, int drawNum, int loserNum, int allgameNum) {
        this.ip = ip;
        this.userName = userName;
        this.winNum = winNum;
        this.drawNum = drawNum;
        this.loserNum = loserNum;
        this.allgameNum = allgameNum;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getWinNum() {
        return winNum;
    }

    public void setWinNum(int winNum) {
        this.winNum = winNum;
    }

    public int getDrawNum() {
        return drawNum;
    }

    public void setDrawNum(int drawNum) {
        this.drawNum = drawNum;
    }

    public int getLoserNum() {
        return loserNum;
    }

    public void setLoserNum(int loserNum) {
        this.loserNum = loserNum;
    }

    public int getAllgameNum() {
        return allgameNum;
    }

    public void setAllgameNum(int allgameNum) {
        this.allgameNum = allgameNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDao userDao = (UserDao) o;
        return winNum == userDao.winNum &&
                drawNum == userDao.drawNum &&
                loserNum == userDao.loserNum &&
                allgameNum == userDao.allgameNum &&
                Objects.equals(ip, userDao.ip) &&
                Objects.equals(userName, userDao.userName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ip, userName, winNum, drawNum, loserNum, allgameNum);
    }
}
