package KanapkaEngine;

public class Time {
    private static double delta = 0;
    private static final long start = System.nanoTime();
    private static long last_game_update = System.nanoTime();

    private static final double Second = (long) Math.pow(10, 9);

    public final void GameUpdate() {
        delta = Math.min(Math.max((System.nanoTime() - last_game_update) / Second, 0.0), 1.0);
        last_game_update = System.nanoTime();
    }

    public static double deltaTime() {
        return delta;
    }

    public static double time() {
        return (System.nanoTime() - start) / Second;
    }
}
