package music;


import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MyMusic {

    /**
     * 将军的声音
     */
    public static void jiangJun() {
        new Thread() {
            @Override
            public void run() {
                playMusic("Chess\\mic\\jiangjun.wav");
            }
        }.start();
    }


    /**
     * 选择
     */
    public static void select() {
        new Thread() {
            @Override
            public void run() {
                playMusic("Chess\\mic\\select.wav");
            }
        }.start();
    }


    /**
     * 吃子声音
     */
    public static void eat() {
        new Thread() {
            @Override
            public void run() {
                playMusic("Chess\\mic\\eat.WAV");
            }
        }.start();
    }

    /**
     * 游戏结束或者死棋的声音
     */
    public static void gameover() {
        new Thread() {
            @Override
            public void run() {
                playMusic("Chess\\mic\\gameover.wav");
            }
        }.start();
    }


    public static void move() {
        new Thread() {
            @Override
            public void run() {
                playMusic("Chess\\mic\\move.WAV");
            }
        }.start();
    }


    /**
     * 开始新棋局的声音
     */
    public static void newGame() {
        new Thread() {
        @Override
        public void run() {
            playMusic("Chess\\mic\\newGame.WAV");
        }
    }.start();
}





    public static void playMusic(String fileName) {
        File soundFile = new File(fileName);
        AudioInputStream audioInputStream=null;
        try{
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
        }catch(Exception e1){
            e1.printStackTrace();
            return;
        }
        AudioFormat format = audioInputStream.getFormat();
        SourceDataLine auline = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
        try{
            auline=(SourceDataLine)AudioSystem.getLine(info);
            auline.open(format);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        auline.start();
        int nBytesRead=0;
        byte[] abData = new byte[1024];
        try{
            while(nBytesRead!=-1){
                nBytesRead=audioInputStream.read

                        (abData,0,abData.length);
                if(nBytesRead>=0)
                    auline.write(abData,0,nBytesRead);
            }
        }catch(IOException e){
            e.printStackTrace();
            return;
        }finally{
            auline.drain();
            auline.close();
        }
    }
}