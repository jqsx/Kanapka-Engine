package KanapkaEngine.Components;

import java.util.Objects;

public final class ANSI {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static String getAnsiColor(int r, int g, int b) {
        return "\033[38;2;" + r + ";" + g + ";" + b + "m";
    }

    private static int Clamp(int n, int min, int max) {
        return Math.max(Math.min(n, max), min);
    }

    public static String getAnsiColor(float r, float g, float b) {
        return getAnsiColor(Clamp((int) (255 * r), 0, 255), Clamp((int) (255 * g), 0, 255), Clamp((int) (255 * b), 0, 255));
    }

    private static float[] lerpColor(float[] a, float[] b, float t) {
        float[] result = new float[3];
        t = Math.max(Math.min(t, 1.f), 0.f);
        for (int i = 0; i < result.length; i++) {
            float x = a[i];
            float y = b[i];
            result[i] = (x + (y - x) * t);
        }

        return result;
    }

    public static float[] rgb(float r, float g, float b) {
        float[] result = new float[3];

        result[0] = Math.max(Math.min(r, 1.f), 0.f);
        result[1] = Math.max(Math.min(g, 1.f), 0.f);
        result[2] = Math.max(Math.min(b, 1.f), 0.f);

        return result;
    }

    public static int[] rgb(int r, int g, int b) {
        int[] result = new int[3];

        result[0] = Math.max(Math.min(r, 255), 0);
        result[1] = Math.max(Math.min(g, 255), 0);
        result[2] = Math.max(Math.min(b, 255), 0);

        return result;
    }

    /**
     * Gradient
     * @param text
     * @param a from
     * @param b to
     * @return formatted text as gradient
     */
    public static String g(String text, int[] a, int[] b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        if (a.length != 3 || b.length != 3)
            return text;

        float[] _a = new float[3];
        float[] _b = new float[3];

        for (int i = 0; i < 3; i++) {
            _a[i] = a[i] / 255.f;
            _b[i] = b[i] / 255.f;
        }

        return g(text, _a, _b);
    }

    /**
     * Gradient
     * @param text
     * @param a from
     * @param b to
     * @return formatted text as gradient
     */
    public static String g(String text, float[] a, float[] b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        Objects.requireNonNull(text);
        if (a.length != 3 || b.length != 3)
            return text;
        StringBuilder builder = new StringBuilder();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            float[] color = lerpColor(a, b, i / (float)len);
            builder.append(getAnsiColor(color[0], color[1], color[2]));
            builder.append(text.charAt(i));
        }
        return builder.toString();
    }
}
