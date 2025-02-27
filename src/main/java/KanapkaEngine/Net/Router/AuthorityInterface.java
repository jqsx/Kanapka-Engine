package KanapkaEngine.Net.Router;

import KanapkaEngine.Net.NetworkConnectionToClient;
import KanapkaEngine.Net.NetworkIdentity;
import KanapkaEngine.Net.NetworkOperation;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AuthorityInterface extends Route {
    private static final HashMap<Integer, BiConsumer<NetworkIdentity, Boolean>> callBackStorage = new HashMap<>();

    @Override
    public void ServerClient_IN(NetworkConnectionToClient conn, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int identityID = buffer.getInt();

        NetworkIdentity identity = NetworkIdentity.find(identityID);

        byte hasAuth = (byte) (identity != null && identity.hasAuthority(conn) ? 1 : (identity == null ? -1 : 0));

        respond(conn, identityID, hasAuth);
    }

    @Override
    public void Client_IN(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);

        int identityID = buffer.getInt();

        byte hasAuth = buffer.get();

        if (hasAuth == -1) {
            // error code
            callBackStorage.remove(identityID);
            return;
        }

        boolean auth = hasAuth == 1;

        BiConsumer<NetworkIdentity, Boolean> cb = callBackStorage.get(identityID);

        if (cb != null)
            new NetworkOperation(() -> cb.accept(NetworkIdentity.find(identityID), auth));
    }

    public void AskAuth(NetworkIdentity identity, BiConsumer<NetworkIdentity, Boolean> hasAuthority) {
        int identityID = identity.getNetID();

        callBackStorage.put(identityID, hasAuthority);

        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);

        buffer.putInt(identityID);

        sendToServer(buffer.array());
    }

    private void respond(NetworkConnectionToClient client, int id, byte hasAuth) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + 1);

        buffer.putInt(id);
        buffer.put(hasAuth);

        sendToClient(client, buffer.array());
    }
}
