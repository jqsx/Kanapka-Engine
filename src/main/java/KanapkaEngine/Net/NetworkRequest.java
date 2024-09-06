package KanapkaEngine.Net;

import KanapkaEngine.Components.TSLinkedList;

public class NetworkRequest {
    private static TSLinkedList<NetworkRequest> requests = new TSLinkedList<>();
    private boolean completed = false;

    private final Runnable runnable;

    public NetworkRequest(Runnable runnable) {
        this.runnable = runnable;
        requests.addEnd(this);
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

    public static void ExecuteNetworkRequests() {
        requests.foreach(NetworkRequest::run);
        requests.removeIf(NetworkRequest::isCompleted);
    }
}
