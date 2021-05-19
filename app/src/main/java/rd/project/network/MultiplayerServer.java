package rd.project.network;

import android.content.Context;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rd.project.Application;
import rd.project.events.WSServerEvent;

import java.io.IOException;

public class MultiplayerServer implements Multiplayer {
    
    private Context context;
    private WSServer server;
    private NetworkServiceDiscovery nsd;
    
    private boolean closed = false;
    
    public MultiplayerServer(Context context) {
        this.context = context;
        
        // Start WebSocket server
        server = new WSServer();
        server.start();
        
        // Initialize Network Service Discovery
        nsd = new NetworkServiceDiscovery(context);
    
        // Register events
        EventBus.getDefault().register(this);
    }
    
    @Override
    public void sendMessage(String message) throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        server.broadcast(message);
    }
    
    public void startDiscoveryBroadcast(int port) throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        nsd.registerService(port, ((Application) context.getApplicationContext()).getUsername());
    }
    
    public void stopDiscoveryBroadcast() throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        nsd.unregisterService();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerError(WSServerEvent.Error event) {
        close();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerStart(WSServerEvent.Start event) {
        System.out.println("Attempting to register service with port " + event.getPort());
        try {
            startDiscoveryBroadcast(event.getPort());
        } catch (ClosedException e) {
            e.printStackTrace();
        }
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
        
        // Stop WebSocket server
        System.out.println("Stopping WebSocket server...");
        try {
            server.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        server = null;
        
        // Stop NetworkServiceDiscovery
        try {
            stopDiscoveryBroadcast();
        } catch (ClosedException e) {
            e.printStackTrace();
        }
        nsd = null;
    
        closed = true;
    }
}
