package database;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

@SuppressWarnings("all")
public class CreateChessDb {
    public static String url;
    public static String user;
    public static String password;
    public static String driver;

    /**
     * 文件的读取，只需要读取一次即可拿到这些值。使用静态代码块
     */
    static{
        //读取资源文件，获取值。

        try {
            //1. 创建Properties集合类。
            Properties pro = new Properties();

            String path = "Chess\\databaseInfo\\jdbc.properties";
             //2. 加载文件
            pro.load(new FileReader(path));

            //3. 获取数据，赋值
            url = pro.getProperty("url");
            user = pro.getProperty("user");
            password = pro.getProperty("password");
            driver = pro.getProperty("driver");
            //4. 注册驱动
            Class.forName(driver);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void createChessDb() {
        Connection coon = null;
        PreparedStatement ps = null;
        try {
            coon = DriverManager.getConnection(url, user, password);
            ps = coon.prepareStatement("CREATE DATABASE IF NOT EXISTS ChessDb");
            ps.executeUpdate();

            ps.close();
            coon.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        CreateChessDb.url = "jdbc:mysql:///ChessDb";

        try {
            coon = DriverManager.getConnection(url,user,password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "CREATE TABLE IF NOT EXISTS chessInfo(\n" +
                "\tip VARCHAR(30),\n" +
                "\tuserName VARCHAR(40),\n" +
                "\twinNum INT,\n" +
                "\tdrawNum INT,\n" +
                "\tloserNum INT,\n" +
                "\tallgameNum INT\n" +
                ");\n" +
                "\n";

        try {
            ps = coon.prepareStatement(sql);
            ps.executeUpdate();

            ps.close();
            coon.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
