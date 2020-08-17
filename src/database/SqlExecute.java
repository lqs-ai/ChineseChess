package database;

import java.sql.*;
import java.util.ArrayList;

public class SqlExecute {
    static{
        CreateChessDb.createChessDb();
    }
    /**
     * 1.从数据库中获取所有玩家信息
     */
    public ArrayList<UserDao> getAllUserInfo(){
        ArrayList<UserDao> users = new ArrayList<>();

        try {

            Connection coon = DriverManager.getConnection(CreateChessDb.url, CreateChessDb.user, CreateChessDb.password);

            PreparedStatement ps = coon.prepareStatement("select * from chessInfo");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String ip = rs.getString("ip");
                String userName = rs.getString("userName");
                int winNum = rs.getInt("winNum");
                int drawNum = rs.getInt("drawNum");
                int loserNum = rs.getInt("loserNum");
                int allgameNum = rs.getInt("allgameNum");

                UserDao ud = new UserDao(ip, userName, winNum, drawNum, loserNum, allgameNum);

                users.add(ud);
            }

            ps.close();
            coon.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public UserDao getOneUserByIp(String ip) {
        UserDao ud = null;

        ArrayList<UserDao> allUserInfo = null;
        allUserInfo = this.getAllUserInfo();

        for (UserDao userDao : allUserInfo) {
            if (userDao.getIp().equals(ip)) {
                ud = userDao;
                return ud;
            }
        }

        return ud;
    }

    public void inserOneuser(UserDao ud) throws SQLException{
        Connection coon = DriverManager.getConnection(CreateChessDb.url, CreateChessDb.user, CreateChessDb.password);

        String sql = "INSERT INTO chessInfo VALUES('"+ud.getIp()+"','"+ud.getUserName()+"',0,0,0,0)";
        PreparedStatement ps = coon.prepareStatement(sql);

        ps.executeUpdate();

        ps.close();
        coon.close();

    }

    /**
     * quaary -1代表无结果    1代表赢棋  2代表输棋  0 代表和棋
     */
    public void increaseGameNum(String ip,int quarryStatus) throws SQLException{

        Connection coon = DriverManager.getConnection(CreateChessDb.url, CreateChessDb.user, CreateChessDb.password);

        String sql = null;

        if (quarryStatus == 1) {
            sql = "update chessInfo set loserNum=loserNum+1 where ip='"+ip+"'";
        } else if (quarryStatus == 0) {
            sql = "update chessInfo set drawNum=drawNum+1 where ip='"+ip+"'";
        } else if (quarryStatus == 2) {
            sql = "update chessInfo set winNum=winNum+1 where ip='"+ip+"'";
        } else {
            coon.close();
            return;
        }

        PreparedStatement ps = coon.prepareStatement(sql);
        ps.executeUpdate();

        ps = coon.prepareStatement("update chessInfo set allgameNum=allgameNum+1 where ip='"+ip+"'");
        ps.executeUpdate();

        ps.close();

        coon.close();

    }


}
