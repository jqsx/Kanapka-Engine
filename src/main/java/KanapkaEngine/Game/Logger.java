package KanapkaEngine.Game;

import KanapkaEngine.Components.ANSI;
import KanapkaEngine.Components.Mathf;
import KanapkaEngine.Components.ResourceLoader;
import org.apache.logging.log4j.LogManager;

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

    private final org.apache.logging.log4j.Logger logger;

    public static boolean useLog4jDefault = true;

    private boolean useLog4j = useLog4jDefault;

    public final String NameSpace;

    public Logger(String nameSpace) {
        this.NameSpace = nameSpace.toUpperCase();
        logger = LogManager.getLogger(nameSpace.toUpperCase());
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm:ss");

    private String getTime() {
        LocalTime time = LocalTime.now();
        return time.format(formatter);
    }

    public void log(Object text) {
        if (ignoreInfo)
            return;
        if (useLog4j) {
            logger.info(text);
        }
        else {
            String message = ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] " + INFO_PREFIX + ANSI_RESET + " [ " + ANSI_YELLOW + getFrom() + ANSI_RESET + " ] " + text.toString() + ANSI_RESET;
            System.out.println(message);
        }
    }

    private String getFrom() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();

        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < elements.length; index++) {
            builder.append(elements[index].getMethodName());
        }
        return builder.toString();
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
            System.out.println(builder.toString() + ANSI_RESET);
        }
        logo.flush();
    }

    public void warn(Object text) {
        if (ignoreWarn)
            return;
        if (useLog4j) {
            logger.info(text);
        }
        else {
            String message = ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] " + WARN_PREFIX + ANSI_RESET + " [ " + ANSI_YELLOW + NameSpace + ANSI_RESET + " ] " + ANSI_YELLOW + text.toString() + ANSI_RESET;
            System.out.println(message);
        }
    }

    public void error(Object text) {
        if (ignoreError)
            return;
        if (useLog4j) {
            logger.error(text);
        }
        else {
            String message = ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] " + ERROR_PREFIX + ANSI_RESET + " [ " + ANSI_YELLOW + NameSpace + ANSI_RESET + " ] " + ANSI_RED + text.toString() + ANSI_RESET;
            System.out.println(message);
            if (text instanceof Exception) {
                Exception e = (Exception) text;

                e.printStackTrace();
            }
        }
    }

    public void error(Object text, String context) {
        if (ignoreError)
            return;
        if (useLog4j) {
            logger.error("CONTEXT: " + context + " | " + text);
            if (text instanceof Throwable)
                logger.info(context, text);
        }
        else {
            String message = ERROR_PREFIX + ANSI_RESET + "[ " + ANSI_YELLOW + getTime() + ANSI_RESET + " ] [ " + ANSI_YELLOW + NameSpace + ANSI_RESET + " ] " + ANSI_RED + text.toString() + ANSI_RESET;
            System.out.println(message);
            error("CONTEXT: " + context);
            if (text instanceof Exception) {
                Exception e = (Exception) text;

                e.printStackTrace();
            }
        }
    }

    public Logger setUseLog4j(boolean b) {
        this.useLog4j = b;
        return this;
    }
}
