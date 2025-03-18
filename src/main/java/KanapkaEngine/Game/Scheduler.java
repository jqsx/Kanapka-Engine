package KanapkaEngine.Game;

import KanapkaEngine.Components.TSLinkedList;

import java.util.ArrayList;
import java.util.List;

public final class Scheduler implements Plugin {
    private static final List<delayed> list = new ArrayList<>();
    private static final List<RepeatingTask> repeat = new ArrayList<>();

    Scheduler() {

    }

    @Override
    public void Apply(Engine engine) {

    }

    @Override
    public void Update() {
        list.removeIf(delayed::execute);
        repeat.forEach(RepeatingTask::exec);
    }

    @Override
    public void Detach() {

    }

    public static void delay(Runnable task, double delay) {
        list.add(new delayed(task, delay));
    }

    public static RepeatingTask repeating(Runnable task, double every) {
        RepeatingTask rt;
        repeat.add(rt = new RepeatingTask(task, every));
        return rt;
    }

    public static void endRepeating(RepeatingTask repeatingTask) {
        repeat.remove(repeatingTask);
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

    public static class RepeatingTask {
        private final Runnable task;
        private final double every;

        private double last;

        private RepeatingTask(Runnable task, double every) {
            this.task = task;
            this.every = every;

            last = Time.time();
        }

        private void exec() {
            if (last + every < Time.time()) {
                task.run();
                last = Time.time();
            }
        }
    }
}
