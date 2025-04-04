package KanapkaEngine.Components;

import KanapkaEngine.Game.Time;

public final class TimeRun {
    private double m_LastRun;

    private double m_RunDelay;

    private final Runnable runnable;

    public TimeRun(double delay, Runnable runnable) {
        m_LastRun = Time.time();
        this.runnable = runnable;

        setRunDelay(delay);
    }

    public double getRunDelay() {
        return m_RunDelay;
    }

    public void setRunDelay(double delay) {
        this.m_RunDelay = delay;
    }

    public void run() {
        if (m_LastRun + m_RunDelay < Time.time()) {
            runnable.run();
            m_LastRun = Time.time();
        }
    }
}
