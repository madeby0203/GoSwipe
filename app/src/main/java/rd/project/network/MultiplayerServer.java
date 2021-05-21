package rd.project.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rd.project.Application;
import rd.project.R;
import rd.project.events.WSServerEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiplayerServer implements Multiplayer {
    
    private Context context;
    private WSServer server;
    private NetworkServiceDiscovery nsd;
    
    private boolean closed = false;
    
    public MultiplayerServer(Context context) {
        System.out.println("Starting multiplayer server...");
        
        this.context = context;
    
        // Register events
        EventBus.getDefault().register(this);
        
        // Start WebSocket server
        server = new WSServer();
        server.start();

        // Initialize Network Service Discovery
        nsd = new NetworkServiceDiscovery(context);
    }
    
    @Override
    public void sendMessage(String message) throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        server.broadcast(message);
        System.out.println("Broadcast message: " + message);
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
    public void onServerOpen(WSServerEvent.Open event) {
        // Send playerlist to newly connected client
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.PLAYER_LIST.toString());
            
            JSONArray jsonArray = new JSONArray(getConnectedUsernames());
            jsonObject.put(MessageParameter.USERLIST.toString(), jsonArray);
        
            event.getWebSocket().send(jsonObject.toString());
            System.out.println("Sending message to new user: " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerError(WSServerEvent.Error event) {
        close();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerStart(WSServerEvent.Start event) {
        System.out.println("Server started on port " + event.getPort() + ".");
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
    
    public List<String> getConnectedUsernames() {
        List<String> usernames = new ArrayList<>();
        usernames.add(((Application) context.getApplicationContext()).getUsername());
        usernames.addAll(server.getConnected().values());
        return usernames;
    }
    
    public String getJoinAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append("ws://");
        
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sb.append(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        
        sb.append(":");
        
        sb.append(server.getPort());
        
        return sb.toString();
    }
}
