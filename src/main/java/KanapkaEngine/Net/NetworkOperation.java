package KanapkaEngine.Net;

import KanapkaEngine.Components.TSLinkedList;

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

    public final boolean isCompleted() {
        return completed;
    }

    public static void ExecuteNetworkOperations() {
        operations.foreach(NetworkOperation::run);
        operations.removeIf(NetworkOperation::isCompleted);
    }
}
