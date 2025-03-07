package KanapkaEngine.Net;

import KanapkaEngine.Components.TSLinkedList;

/**
 * Network class that allows for command execution per tick on the main thread
 */
public class NetworkOperation {
    private static TSLinkedList<NetworkOperation> operations = new TSLinkedList<>();
    private boolean completed = false;

    private final Runnable runnable;

    public NetworkOperation(Runnable runnable) {
        this.runnable = runnable;
        operations.addEnd(this);
    }

    public final void run() {
        if (!isCompleted()) {
            runnable.run();
            completed = true;
        }
    }

    /**
     * Ik this is kinda stupid but this only turns true if the runnable was executed, so that if anything does infact
     * get added to the operations list because of multithreading between the function call to execute all operations
     * and to clear all of them then it won't remove any not yet executed functions.
     * @return
     */
    public final boolean isCompleted() {
        return completed;
    }

    public static void ExecuteNetworkOperations() {
        operations.foreach(NetworkOperation::run);
        operations.removeIf(NetworkOperation::isCompleted);
    }

    public static void run(Runnable runnable) {
        new NetworkOperation(runnable);
    }
}
