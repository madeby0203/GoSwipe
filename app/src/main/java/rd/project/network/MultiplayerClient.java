package rd.project.network;

import android.content.Context;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rd.project.Application;
import rd.project.events.WSClientEvent;

import java.net.URI;

public class MultiplayerClient implements Multiplayer {
    
    private Context context;
    private WSClient client;
    
    private boolean closed;
    
    public MultiplayerClient(Context context, URI uri) {
        this.context = context;
        
        // Open client connection
        client = new WSClient(uri);
        client.addHeader("username", ((Application) context.getApplicationContext()).getUsername());
        client.connect();
        
        // Register events
        EventBus.getDefault().register(this);
    }
    
    @Override
    public void sendMessage(String message) throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        client.send(message);
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientClose(WSClientEvent.Close event) {
        System.out.println("Client connection closed: " + event.getReason());
        this.close();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientError(WSClientEvent.Error event) {
        System.out.println("Error occurred in client");
        event.getException().printStackTrace();
        this.close();
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    @Override
    public void close() {
        if (closed) {
            return;
        }
        
        // Unregister events
        EventBus.getDefault().unregister(this);
        
        // Close client connection
        client.close();
    
        closed = true;
    }
}
