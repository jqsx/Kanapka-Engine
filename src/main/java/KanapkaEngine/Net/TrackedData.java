package KanapkaEngine.Net;

import KanapkaEngine.Game.WIP;

/**
 * A class activated from network components (to be added) to allow for easier tracking of data in multiplayer
 * Should be able to support some utility classes and will attempt to serialize data into simplistic other components to be reconstructed later.
 */
@WIP
public final class TrackedData<T> {
    private T stored;

    public TrackedData() {
        stored = null;
    }
    public TrackedData(T init) {
        stored = init;
    }

    public void set(T v) {
        this.stored = v;

        // Sync message
    }

    public T get() {
        return stored;
    }
}
