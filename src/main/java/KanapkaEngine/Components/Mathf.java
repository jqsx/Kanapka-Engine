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

    /**
     * Just an approximation,
     * This is a sqrt that uses 5 iterations to calculate the square root of a
     * @param a
     * @return square root approximate of a
     */
    public static double Sqrt(double a) {
        double out = a;
        for (int i = 0; i < 5; i++) {
            out = sqrt_iteration(a, out);
        }
        return out;
    }

    private static double sqrt_iteration(double a, double previous) {
        double m = (a / previous - previous) / 2.0;
        return previous + m;
    }
}
