package music;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AudioPlayer{
    public Player player;
    public File music;

    public AudioPlayer(File file) {
        this.music = file;
    }


    public void play() throws FileNotFoundException, JavaLayerException {
        BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(music));
        player = new Player(buffer);
        player.play();
    }
}