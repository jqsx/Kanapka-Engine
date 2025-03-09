package KanapkaEngine.Net.DataSync;

import KanapkaEngine.Game.WIP;

/**
 * A class activated from network components (to be added) to allow for easier tracking of data in multiplayer
 * Should be able to support some utility classes and will attempt to serialize data into simplistic other components to be reconstructed later.
 */
@WIP
public class TrackedData<T> {
    private T stored;

    private boolean isAuthOnly = false;

    private TrackedData(boolean isAuthOnly) {
        stored = null;
        this.isAuthOnly = isAuthOnly;
    }
    private TrackedData(T init, boolean isAuthOnly) {
        this(isAuthOnly);
        stored = init;
    }

    public void set(T v) {
        this.stored = v;

        // Sync message
    }

    public T get() {
        return stored;
    }

    protected boolean isAuthOnly() {
        return isAuthOnly;
    }

    public static class AuthOnly<V> extends TrackedData<V> {

        public AuthOnly() {
            super(true);
        }

        public AuthOnly(V init) {
            super(init, true);
        }
    }

    public static class Any<V> extends TrackedData<V> {
        public Any() {
            super(false);
        }

        public Any(V init) {
            super(init, false);
        }
    }
}
