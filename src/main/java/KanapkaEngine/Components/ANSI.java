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

    public static String getAnsiColor(float r, float g, float b) {
        return getAnsiColor((int)Mathf.Clamp(255 * r, 0, 255), (int)Mathf.Clamp(255 * g, 0, 255), (int)Mathf.Clamp(255 * b, 0, 255));
    }

    private static float[] lerpColor(float[] a, float[] b, float t) {
        float[] result = new float[3];
        for (int i = 0; i < result.length; i++) {
            result[i] = Mathf.Lerp(a[i], b[i], t);
        }

        return result;
    }

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

    public static String g(String text, float[] a, float[] b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
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
