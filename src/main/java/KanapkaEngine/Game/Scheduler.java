package KanapkaEngine.Game;

import KanapkaEngine.Components.TSLinkedList;

import java.util.ArrayList;
import java.util.List;

public final class Scheduler implements Plugin {
    private static final List<delayed> list = new ArrayList<>();

    Scheduler() {

    }

    @Override
    public void Apply(Engine engine) {

    }

    @Override
    public void Update() {
        list.removeIf(delayed::execute);
    }

    @Override
    public void Detach() {

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
