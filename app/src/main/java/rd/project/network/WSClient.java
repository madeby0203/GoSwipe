package rd.project.network;

import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import rd.project.events.WSClientEvent;

import java.net.URI;

public class WSClient extends WebSocketClient {
    private final String TAG = "WSClient";
    
    public WSClient(URI serverURI) {
        super(serverURI, new Draft_6455(), null, 4000);
    }
    
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "Connection opened.");
        EventBus.getDefault().post(new WSClientEvent.Open());
    }
    
    @Override
    public void onMessage(String message) {
        Log.d(TAG, "Received message: " + message);
        EventBus.getDefault().post(new WSClientEvent.Message(message));
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "Connection closed. code: " + code + ", reason: " + reason + ", remote: " + remote);
        EventBus.getDefault().post(new WSClientEvent.Close(reason));
    }
    
    @Override
    public void onError(Exception ex) {
        Log.e(TAG, "Error opening connection.");
        ex.printStackTrace();
        EventBus.getDefault().post(new WSClientEvent.Error(ex));
    }
    
    @Override
    public void send(String text) {
        super.send(text);
        Log.d(TAG, "Message sent: " + text);
    }
}
