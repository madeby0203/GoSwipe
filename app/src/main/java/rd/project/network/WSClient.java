package rd.project.network;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import rd.project.events.WSClientEvent;

import java.net.URI;

public class WSClient extends WebSocketClient {
    
    public WSClient(URI serverURI) {
        super(serverURI, new Draft_6455(), null, 4000);
    }
    
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        EventBus.getDefault().post(new WSClientEvent.Open());
    }
    
    @Override
    public void onMessage(String message) {
        EventBus.getDefault().post(new WSClientEvent.Message(message));
    }
    
    @Override
    public void onClose(int code, String reason, boolean remote) {
        EventBus.getDefault().post(new WSClientEvent.Close(reason));
    }
    
    @Override
    public void onError(Exception ex) {
        EventBus.getDefault().post(new WSClientEvent.Error(ex));
    }
}
