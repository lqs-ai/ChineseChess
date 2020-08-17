package music;

import javazoom.jl.decoder.JavaLayerException;

import java.io.File;
import java.io.FileNotFoundException;

public class BackGroundMusicPlay {
    public static File[] files;
    public static int size;
    public static int point = 0;
    public static AudioPlayer ap;

    public static boolean flag;

    static {
        File file = new File("Chess\\music");
        files = file.listFiles();
        size  = files.length;
        ap = new AudioPlayer(files[point]);
        flag = true;
    }

    public static void play() {
        flag = true;
        Thread t = new Thread() {
            @Override
            public void run() {

                while (flag) {
                    synchronized(BackGroundMusicPlay.class) {
                        try {
                            ap.play();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (JavaLayerException e) {
                            e.printStackTrace();
                        }
                        if (point >= size - 1) {
                            point = -1;
                        }
                        point++;
                        ap = new AudioPlayer(files[point]);

                    }
                }

            }
        };

        t.start();
    }

    public static void close() {
        flag = false;
        ap.player.close();

    }

}
