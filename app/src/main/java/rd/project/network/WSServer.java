package rd.project.network;

import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONException;
import org.json.JSONObject;
import rd.project.events.MultiplayerEvent;
import rd.project.events.WSServerEvent;
import rd.project.network.Multiplayer.MessageType;
import rd.project.network.Multiplayer.MessageParameter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class WSServer extends WebSocketServer {
    private final String TAG = "WSServer";
    
    private final Map<WebSocket, String> connected = new LinkedHashMap<>();
    
    public WSServer() {
        // Create a WebSocket server, port 0 means a port will be automatically assigned
        super(new InetSocketAddress(0));
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Log.d(TAG, "Client connecting...");
        
        // Add client to list of connected clients
        String username = handshake.getFieldValue("username");
        if (username.equals("")) { // No username set; kick user
            Log.d(TAG, "Username missing, kicking client.");
            conn.close(CloseFrame.NORMAL, "Username missing.");
            return;
        }
        if (connected.containsValue(username)) {
            Log.d(TAG, "Duplicate username (" + username + "), kicking client.");
            conn.close(CloseFrame.NORMAL, "A user with name " + username + " is already connected to this lobby. Please change your username.");
            return;
        }
        Log.d(TAG, "Client connected with username " + username + ".");
        connected.put(conn, username);
        
        // Send this message to all clients except the newly connected one
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.PLAYER_JOIN.toString());
            jsonObject.put(MessageParameter.USERNAME.toString(), username);
            
            for(WebSocket c : getConnections()) {
                if (c != conn) {
                    c.send(jsonObject.toString());
                }
            }
            Log.d(TAG, "Broadcast message (except to new client): " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Send event so other parts of the app know a client has joined
        EventBus.getDefault().post(new MultiplayerEvent.PlayerJoin(username));
        EventBus.getDefault().post(new WSServerEvent.Open(conn, handshake));
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = connected.get(conn);
        Log.d(TAG, "Client disconnecting. username: " + username + ", code: " + code + ", reason: " + reason + ", remote: " + remote);
        
        // Remove client from list of connected clients
        connected.remove(conn);
    
        // Send this message to all clients
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.PLAYER_LEAVE.toString());
            jsonObject.put(MessageParameter.USERNAME.toString(), username);
            this.broadcast(jsonObject.toString());
            Log.d(TAG, "Broadcast message: " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    
        // Send event so other parts of the app know a client has left
        EventBus.getDefault().post(new MultiplayerEvent.PlayerLeave(username));
        EventBus.getDefault().post(new WSServerEvent.Close(conn, reason));
    }
    
    @Override
    public void onMessage(WebSocket conn, String incomingMessage) {
        Log.d(TAG, "Received message from " + connected.get(conn) + ": " + incomingMessage);
        
        // Send the incoming message to all clients
        this.broadcast(connected.get(conn) + ": " + incomingMessage);
    
        // Send event so other parts of the app know a client has sent a message
        EventBus.getDefault().post(new WSServerEvent.Message(conn, incomingMessage));
    }
    
    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Send event so other parts of the app know an error occured in the WebSocket server
        EventBus.getDefault().post(new WSServerEvent.Error(conn, ex));
    }
    
    @Override
    public void onStart() {
        // Send event so other parts of the app know the WebSocket server has started successfully
        EventBus.getDefault().post(new WSServerEvent.Start(this.getPort()));
    }
    
    @Override
    public void stop() throws IOException, InterruptedException {
        super.stop();
        
        // Remove all connections from connection list
        connected.clear();
    }
    
    public Map<WebSocket, String> getConnected() {
        return connected;
    }
}
