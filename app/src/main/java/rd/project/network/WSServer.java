package rd.project.network;

import android.net.Uri;
import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import rd.project.events.WSServerEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class WSServer extends WebSocketServer {
    
    private final Map<WebSocket, String> connected = new HashMap<>();
    
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
        System.out.println("Adding client to list of connected clients: " + conn + ", " + username);
        connected.put(conn, username);
        
        // Send this message to all clients
        this.broadcast("System: " + username + " joined the chat.");
    
        // Send event so other parts of the app know a client has joined
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
        this.broadcast("System: " + username + " left the chat.");
    
        // Send event so other parts of the app know a client has left
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
