package util;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class FrameUtil {
    public static void setFrameCenter(JFrame jFrame) {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screen = toolkit.getScreenSize();
        int x = screen.width-jFrame.getWidth();
        int y = screen.height-jFrame.getHeight()-60;
        jFrame.setLocation(x>>1, y>>1);
    }
}
