package KanapkaEngine.Game;

import KanapkaEngine.Time;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Scheduler extends Plugin {
    private static final LinkedList<delayed> list = new LinkedList<>();

    @Override
    public void Update() {
        super.Update();
        list.removeIf(delayed::execute);
    }

    public static void delay(Runnable task, double delay) {
        list.add(new delayed(task, delay));
    }

    static class delayed {
        private final Runnable task;
        private final double delay;

        private final double start = Time.time();

        private delayed(Runnable task, double delay) {
            this.task = task;
            this.delay = delay;
        }

        private boolean execute() {
            if (start + delay < Time.time()) {
                task.run();
                return true;
            }

            return false;
        }
    }
}
