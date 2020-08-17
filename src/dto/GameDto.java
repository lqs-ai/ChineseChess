package dto;

import database.SqlExecute;
import database.UserDao;
import photo.Photos;
import ui.JFrameManual;
import ui.JFrameUserInfo;
import ui.JPanelManual;
import ui.JPanelUserInfo;

import java.awt.*;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GameDto {

    //步时
    public static final int STEPTIME = 120;
    /**
     * 标志主方式红棋还是黑棋,false代表主方式黑棋，true代表主方式红棋
     */
    public boolean flag = false;
    /*
     *游戏地图 h
     */
    public int[][] map;

    /*
     *判断点击的位置
     */
    public boolean[][] pos = new boolean[10][9];

    /**
     * 为false表示主方是黑棋，要等待红方走棋了才能走棋
     */
    public boolean whoMove = false;


    /**
     * 请求悔棋状态，true代表已发送请求
     */
    public boolean regretStatus = false;

    /**
     * 请求和棋状态，true代表已发送请求
     */
    public boolean drawStatus = false;

    /**
     * 请求认输状态,true代表已发送请求
     */
    public boolean giveUpStatus = false;

    /**
     * newGame按钮状态，true代表已发送请求
     */
    public boolean newGameStatus = false;

    /**
     * 为true时说明游戏是因为和棋或者认输导致棋局结束的
     */
    public boolean regretOrDrawStatus = false;

    public int goValue;
    public int endValue;
    public Point p1 = new Point(-1,-1);
    public Point p2 = new Point(-1,-1);

    /**
     * 新局判断,newGame为true表示需要开始新局了
     */
    public boolean newGame = false;

    /**
     *走棋一些音效，true代表打开状态，false代表关闭状态
     */
    public boolean controlMusic = true;

    /**
     * 下棋时的背景音乐,true代表打开状态，false代表关闭状态
     */
    public boolean backgroundMusic = false;

    /**
     * 是否在复盘中
     */
    public static boolean isCompound = false;


    /*
     *根据值得到棋子图片
     */
    public static HashMap<Integer,Image> allChessImg;

    /**
     * 步时
     */
    public static int stepTime = STEPTIME;


    /**
     * 记录走棋数据，用于悔棋
     */
    public LinkedList<dto.ChessRunning> history;

    /**
     * 鼠标移动的x坐标
     */
    public static int mouseX = -100;

    /**
     * 鼠标移动的y坐标
     */
    public static int mouseY = -100;

    /**
     * gameOver
     */
    public static boolean gameOver = false;

    /**
     * 为false代表已断开连接
     */
    public static boolean isCoonect = true;

    /**
     * ip:本机 ip
     * userName:本机 userName
     */
    public static String myIp;
    public static String myUserName;

    /**
     * otherIP:对方的ip
     * otherUserName:对方的用户名
     */
    public static String otherIp;
    public static String otherUserName;

    /**
     * 本方棋局状态 -1代表无结果    1代表赢棋  2代表输棋  0 代表和棋
     */
    public int quarryStatus = -1;



    /**
     * 执行sql语句的对象
     */
    public static SqlExecute se;

    /**
     * 对方信息的 实体对象
     */
    public static UserDao ud;

    /**
     *数据库为true代表连接成功
     */
    public static boolean databaseCoonect = false;

    /**
     * 显示对方信息的窗口
     * @param dto
     */
    public static JFrameUserInfo userInfo;
    public static JPanelUserInfo jui;

    public static ManualDto mdto;      //复盘时的mdto
    public static JPanelManual jPaenlManual;
    public static JFrameManual jFrameManual;


    static {

        InetAddress ia=null;
        try {
            ia=ia.getLocalHost();
            String localip=ia.getHostAddress();
            myIp = localip;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Map<String, String> map = System.getenv();
        String userName = map.get("USERNAME");// 获取用户名
        myUserName = userName;


    }

    public GameDto(boolean flag){

        this.flag = flag;
        initChessImg();
        history = new LinkedList<>();
        if(flag == false)
            initPlayer2();
        else {
            initPlayer1();
        }
    }

    public static void initChessImg() {
        allChessImg = new HashMap<>();
        allChessImg.put(dto.ChessNum.NOCHESS, null);
        allChessImg.put(dto.ChessNum.BLACK_JIANG, Photos.BLACK_JIANG);
        allChessImg.put(dto.ChessNum.BLACK_CHE, Photos.BLACK_CHE);
        allChessImg.put(dto.ChessNum.BLACK_MA, Photos.BLACK_MA);
        allChessImg.put(dto.ChessNum.BLACK_PAO, Photos.BLACK_PAO);
        allChessImg.put(dto.ChessNum.BLACK_SHI, Photos.BLACK_SHI);
        allChessImg.put(dto.ChessNum.BLACK_XINAG, Photos.BLACK_XINAG);
        allChessImg.put(dto.ChessNum.BLACK_ZHU, Photos.BLACK_ZHU);
        allChessImg.put(dto.ChessNum.RED_SHUAI, Photos.RED_SHUAI);
        allChessImg.put(dto.ChessNum.RED_CHE, Photos.RED_CHE);
        allChessImg.put(dto.ChessNum.RED_MA, Photos.RED_MA);
        allChessImg.put(dto.ChessNum.RED_PAO, Photos.RED_PAO);
        allChessImg.put(dto.ChessNum.RED_SHI, Photos.RED_SHI);
        allChessImg.put(dto.ChessNum.RED_XINAG, Photos.RED_XINAG);
        allChessImg.put(dto.ChessNum.RED_BING, Photos.RED_BING);
    }

    /**
     * 红方
     */
    public void initPlayer1() {
        map = new int[][]{
                {2, 3, 6, 5, 1, 5, 6, 3, 2},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 4, 0, 0, 0, 0, 0, 4, 0},
                {7, 0, 7, 0, 7, 0, 7, 0, 7},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {14, 0, 14, 0, 14, 0, 14, 0, 14},
                {0, 11, 0, 0, 0, 0, 0, 11, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {9, 10, 13, 12, 8, 12, 13, 10, 9}
        };
    }

    /**
     * 黑方
     */
    public void initPlayer2() {
        map = new int[][] {
                {9, 10, 13, 12, 8, 12, 13, 10, 9},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 11, 0, 0, 0, 0, 0, 11, 0},
                {14, 0, 14, 0, 14, 0, 14, 0, 14},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {7, 0, 7, 0, 7, 0, 7, 0, 7},
                {0, 4, 0, 0, 0, 0, 0, 4, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0},
                {2, 3, 6, 5, 1, 5, 6, 3, 2}

        };
    }

    public void initPos(boolean[][] pos) {
        for (int i = 0; i < pos.length; i++) {
            for (int j = 0; j < pos[i].length; j++) {
                pos[i][j] = false;
            }
        }
    }


    public static boolean isBlack(int value) {
        return (value >= 1 && value <= 7);
    }

    public static boolean isRed(int value) {
        return (value >= 8 && value <= 14);
    }



    public HashMap<Integer, Image> getAllChessImg() {
        return allChessImg;
    }


    public int[][] getMap() {
        return map;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean[][] getPos() {
        return pos;
    }

    public void setPos(boolean[][] pos) {
        this.pos = pos;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setMap(int[][] map) {
        this.map = map;
    }

    public boolean getWhoMove() {
        return whoMove;
    }

    public void setWhoMove(boolean whoMove) {
        this.whoMove = whoMove;
    }

    public void whoMoveChanged() {
        this.whoMove = !this.whoMove;
    }

    //regretStatus
    public void regretStatusChanged() {
        this.regretStatus = !regretStatus;
    }

    public void drawStatusChanged() {
        this.drawStatus = !drawStatus;
    }

    public boolean isNewGame() {
        return newGame;
    }

    public void setNewGame(boolean newGame) {
        this.newGame = newGame;
    }

    public LinkedList<dto.ChessRunning> getHistory() {
        return history;
    }

    public void setHistory(LinkedList<dto.ChessRunning> history) {
        this.history = history;
    }

    public boolean isControlMusic() {
        return controlMusic;
    }

    public void setControlMusic(boolean controlMusic) {
        this.controlMusic = controlMusic;
    }

    public boolean isDrawStatus() {
        return drawStatus;
    }

    public void setDrawStatus(boolean drawStatus) {
        this.drawStatus = drawStatus;
    }

    public boolean getCompound() {
        return isCompound;
    }

    public void setCompound(boolean compound) {
        isCompound = compound;
    }
}
