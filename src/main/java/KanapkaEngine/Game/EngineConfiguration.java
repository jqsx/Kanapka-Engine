package KanapkaEngine.Game;

import javax.swing.*;
import java.awt.*;

public class EngineConfiguration {
    public int FPSLIMIT = 60;
    public String window_title = "GAME";
    public int width = 300;
    public int height = 300;
    public int WINDOW_CLOSE_OPERATION = JFrame.EXIT_ON_CLOSE;

    public boolean custom_title_bar = false;

    public Dimension target_size = new Dimension(600, 400);
}
