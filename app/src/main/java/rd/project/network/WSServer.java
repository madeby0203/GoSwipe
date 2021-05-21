package rd.project.network;

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
    
    private final Map<WebSocket, String> connected = new LinkedHashMap<>();
    
    public WSServer() {
        // Create a WebSocket server, port 0 means a port will be automatically assigned
        super(new InetSocketAddress(0));
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connecting...");
        
        // Add client to list of connected clients
        String username = handshake.getFieldValue("username");;
        if (username.equals("")) { // No username set; kick user
            System.out.println("Username missing, kicking client.");
            conn.close(CloseFrame.NORMAL, "Username missing.");
            return;
        }
        if (connected.containsValue(username)) {
            System.out.println("Duplicate username (" + username + "), kicking client");
            conn.close(CloseFrame.NORMAL, "A user with name " + username + " is already connected to this lobby. Please change your username.");
            return;
        }
        System.out.println("Adding client to list of connected clients: " + conn + ", " + username);
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
            System.out.println("Broadcast message (except to new client): " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Send event so other parts of the app know a client has joined
        EventBus.getDefault().post(new MultiplayerEvent.PlayerJoin(username));
        EventBus.getDefault().post(new WSServerEvent.Open(conn, handshake));
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client disconnecting..., conn: " + conn + ", code: " + code + ", reason: " + reason + ", remote: " + remote);
        
        // Remove client from list of connected clients
        String username = connected.get(conn);
        System.out.println("Removing client to list of connected clients: " + conn + ", " + username);
        connected.remove(conn);
    
        // Send this message to all clients
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.PLAYER_LEAVE.toString());
            jsonObject.put(MessageParameter.USERNAME.toString(), username);
            this.broadcast(jsonObject.toString());
            System.out.println("Broadcast message: " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    
        // Send event so other parts of the app know a client has left
        EventBus.getDefault().post(new MultiplayerEvent.PlayerLeave(username));
        EventBus.getDefault().post(new WSServerEvent.Close(conn, reason));
    }
    
    @Override
    public void onMessage(WebSocket conn, String incomingMessage) {
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
