package KanapkaEngine.Components;

import java.awt.*;

public class Mathf {
    private Mathf() {}

    public static double Lerp(double a, double b, double t) {
        return a + (b - a) * Clamp(t, 0, 1);
    }

    public static double Clamp(double n, double min, double max) {
        return Math.max(Math.min(n, max), min);
    }

    public static int Clamp(int n, int min, int max) {
        return Math.max(Math.min(n, max), min);
    }

    public static Point Clamp(Point n, int x_max, int y_max) {
        return new Point(Math.min(Math.abs(n.x), x_max), Math.min(Math.abs(n.y), y_max));
    }
}
