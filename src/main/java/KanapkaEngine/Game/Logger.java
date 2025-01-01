package KanapkaEngine.Game;

import KanapkaEngine.Components.ANSI;
import KanapkaEngine.Components.Mathf;
import KanapkaEngine.Components.ResourceLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public final class Logger {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static boolean VERBOSE = true;

    public static boolean ignoreInfo = false;
    public static boolean ignoreWarn = false;
    public static boolean ignoreError = false;

    private static final String INFO_PREFIX = ANSI_GREEN + " [INFO] " + ANSI_RESET;
    private static final String WARN_PREFIX = ANSI_YELLOW + " [WARN] " + ANSI_RESET;
    private static final String ERROR_PREFIX = ANSI_RED + "[ERROR] " + ANSI_RESET;

    public final String NameSpace;

    public Logger(String nameSpace) {
        this.NameSpace = nameSpace.toUpperCase();
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");

    private String getTime() {
        LocalTime time = LocalTime.now();
        return time.format(formatter);
    }

    public void log(Object text) {
        if (ignoreInfo)
            return;
        String message = ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] " + INFO_PREFIX + ANSI_RESET + " [ " + ANSI_YELLOW + NameSpace + ANSI_RESET + " ] " + text.toString() + ANSI_RESET;
        System.out.println(message);
    }

    public void log(BufferedImage image, int width, int height) {
        if (ignoreInfo)
            return;
        BufferedImage logo = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = logo.createGraphics();

        AffineTransform at = new AffineTransform();

        at.scale(logo.getWidth() / (double)image.getWidth(), logo.getHeight() / (double)image.getHeight());

        g.drawImage(image, at, null);

        g.dispose();

        for (int y = 0; y < logo.getHeight(); y++) {
            StringBuilder builder = new StringBuilder();
            for (int x = 0; x < logo.getWidth(); x++) {
                Color color = new Color(logo.getRGB(x, y));

                builder.append(ANSI.getAnsiColor(color.getRed(), color.getGreen(), color.getBlue()) + "█");
            }
            log(builder.toString());
        }
        logo.flush();
    }

    public void warn(Object text) {
        if (ignoreWarn)
            return;
        String message = ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] " + WARN_PREFIX + ANSI_RESET + " [ " + ANSI_YELLOW + NameSpace + ANSI_RESET + " ] " + ANSI_YELLOW + text.toString() + ANSI_RESET;
        System.out.println(message);
    }

    public void error(Object text) {
        if (ignoreError)
            return;
        String message = ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] " + ERROR_PREFIX + ANSI_RESET + " [ " + ANSI_YELLOW + NameSpace + ANSI_RESET + " ] " + ANSI_RED + text.toString() + ANSI_RESET;
        System.out.println(message);
        if (text instanceof Exception) {
            Exception e = (Exception) text;

            e.printStackTrace();
        }
    }

    public void error(Object text, String context) {
        if (ignoreError)
            return;
        String message = ERROR_PREFIX + ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] [ " + ANSI_YELLOW + NameSpace + ANSI_RESET + " ] " + ANSI_RED + text.toString() + ANSI_RESET;
        System.out.println(message);
        error("CONTEXT: " + context);
        if (text instanceof Exception) {
            Exception e = (Exception) text;

            e.printStackTrace();
        }
    }
}
