package KanapkaEngine.Components;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.*;
import java.util.Random;

public class Mathf {
    private Mathf() {}

    /**
     * Noise with seed 15, if you can't be asked to instance it yourself.
     */
    public static final NoiseGenerator Noise = new NoiseGenerator(15);

    public static double Lerp(double a, double b, double t) {
        return a + (b - a) * Clamp(t, 0, 1);
    }

    public static Vector2D Lerp(Vector2D a, Vector2D b, double t) {
        double x = Lerp(a.getX(), b.getX(), t);
        double y = Lerp(a.getY(), b.getY(), t);

        return new Vector2D(x, y);
    }

    /**
     * <code>min(max(mod(a, 2), 0), 1) + floor(a/2)</code> <br><br>
     * I found it useful when procedurally generating terrain.
     * @param a
     * @return Step
     */
    public static double stepLerp(double a) {
        return Clamp01(a % 2.0) + Math.floor(a / 2.0);
    }

    public static double Clamp(double n, double min, double max) {
        return Math.max(Math.min(n, max), min);
    }
    public static double Clamp01(double n) {
        return Clamp(n, 0, 1);
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
        while (Math.round(out * out) != Math.round(a)) {
            out = sqrt_iteration(a, out);
        }
        return out;
    }

    /**
     * dx = abs(x1 - x0)<br>
     * dy = abs(y1 - y0)<br>
     * <br>
     * dist = 0.5 * (dx + dy + max(dx, dy))
     * @param one
     * @param two
     * @return
     */
    public static double Adistance(Vector2D one, Vector2D two) {
        double dx = Math.abs(two.getX() - one.getX());
        double dy = Math.abs(two.getY() - one.getY());

        return 0.5 * (dx + dy + Math.max(dx, dy));
    }

    public static double distance(Vector2D one, Vector2D two) {
        return Sqrt(Math.pow(one.getX() - two.getX(), 2) + Math.pow(one.getY() - two.getY(), 2));
    }

    private static double sqrt_iteration(double a, double previous) {
        double m = (a / previous - previous) / 2.0;
        return previous + m;
    }

    public static class NoiseGenerator {
        private double seed;
        private long default_size;
        private int[] p;
        private int[] permutation;

        public NoiseGenerator(double seed) {
            this.seed = seed;
            init();
        }

        public NoiseGenerator() {
            this.seed = new Random().nextGaussian() * 255;
            init();
        }

        private void init() {
            // Initialize the permutation array.
            this.p = new int[512];
            this.permutation = new int[] { 151, 160, 137, 91, 90, 15, 131, 13, 201,
                    95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99,
                    37, 240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26,
                    197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88,
                    237, 149, 56, 87, 174, 20, 125, 136, 171, 168, 68, 175, 74,
                    165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111,
                    229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55, 46, 245, 40,
                    244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76,
                    132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188, 159,
                    86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
                    124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207,
                    206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170,
                    213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155,
                    167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113,
                    224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
                    193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235,
                    249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184,
                    84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236,
                    205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66,
                    215, 61, 156, 180 };
            this.default_size = 35;

            // Populate it
            for (int i = 0; i < 256; i++) {
                p[256 + i] = p[i] = permutation[i];
            }
        }

        public void setSeed(double seed) {
            this.seed = seed;
        }

        public double getSeed() {
            return this.seed;
        }

        public double noise(double x, double y, double z, int size) {
            double value = 0.0;
            double initialSize = size;

            while (size >= 1) {
                value += smoothNoise((x / size), (y / size), (z / size)) * size;
                size /= 2.0;
            }

            return value / initialSize;
        }

        public double noise(double x, double y, double z) {
            double value = 0.0;
            double size = default_size;
            double initialSize = size;

            while (size >= 1) {
                value += smoothNoise((x / size), (y / size), (z / size)) * size;
                size /= 2.0;
            }

            return value / initialSize;
        }

        public double noise(double x, double y) {
            double value = 0.0;
            double size = default_size;
            double initialSize = size;

            while (size >= 1) {
                value += smoothNoise((x / size), (y / size), (0f / size)) * size;
                size /= 2.0;
            }

            return value / initialSize;
        }

        public double noise(double x) {
            double value = 0.0;
            double size = default_size;
            double initialSize = size;

            while (size >= 1) {
                value += smoothNoise((x / size), (0f / size), (0f / size)) * size;
                size /= 2.0;
            }

            return value / initialSize;
        }

        public double smoothNoise(double x, double y, double z) {
            // Offset each coordinate by the seed value
            x += this.seed;
            y += this.seed;
            x += this.seed;

            int X = (int) Math.floor(x) & 255; // FIND UNIT CUBE THAT
            int Y = (int) Math.floor(y) & 255; // CONTAINS POINT.
            int Z = (int) Math.floor(z) & 255;

            x -= Math.floor(x); // FIND RELATIVE X,Y,Z
            y -= Math.floor(y); // OF POINT IN CUBE.
            z -= Math.floor(z);

            double u = fade(x); // COMPUTE FADE CURVES
            double v = fade(y); // FOR EACH OF X,Y,Z.
            double w = fade(z);

            int A = p[X] + Y;
            int AA = p[A] + Z;
            int AB = p[A + 1] + Z; // HASH COORDINATES OF
            int B = p[X + 1] + Y;
            int BA = p[B] + Z;
            int BB = p[B + 1] + Z; // THE 8 CUBE CORNERS,

            return lerp(w, lerp(v, lerp(u, grad(p[AA], 		x, 		y, 		z		), 	// AND ADD
                                    grad(p[BA],		x - 1, 	y, 		z		)), // BLENDED
                            lerp(u, grad(p[AB], 	x, 		y - 1, 	z		), 	// RESULTS
                                    grad(p[BB], 	x - 1, 	y - 1, 	z		))),// FROM 8
                    lerp(v, lerp(u, grad(p[AA + 1], x, 		y, 		z - 1	), 	// CORNERS
                                    grad(p[BA + 1], x - 1, 	y, 		z - 1	)), // OF CUBE
                            lerp(u, grad(p[AB + 1], x, 		y - 1,	z - 1	),
                                    grad(p[BB + 1], x - 1, 	y - 1, 	z - 1	))));
        }

        private double fade(double t) {
            return t * t * t * (t * (t * 6 - 15) + 10);
        }

        private double lerp(double t, double a, double b) {
            return a + t * (b - a);
        }

        private double grad(int hash, double x, double y, double z) {
            int h = hash & 15; // CONVERT LO 4 BITS OF HASH CODE
            double u = h < 8 ? x : y, // INTO 12 GRADIENT DIRECTIONS.
                    v = h < 4 ? y : h == 12 || h == 14 ? x : z;
            return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
        }
    }
}
