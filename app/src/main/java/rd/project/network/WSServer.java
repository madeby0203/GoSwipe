package rd.project.network;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import rd.project.events.WSServerEvent;

import java.net.InetSocketAddress;

public class WSServer extends WebSocketServer {
    
    public WSServer() {
        // Create a WebSocket server, port 0 means a port will be automatically assigned
        super(new InetSocketAddress(0));
    }
    
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Send event so other parts of the app know a client has joined
        EventBus.getDefault().post(new WSServerEvent.Open(conn, handshake));
        // Send this message to all clients
        this.broadcast("System: " + conn.getRemoteSocketAddress() + " joined the chat.");
    }
    
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Send event so other parts of the app know a client has left
        EventBus.getDefault().post(new WSServerEvent.Close(conn, reason));
        // Send this message to all clients
        this.broadcast("System: " + conn.getRemoteSocketAddress() + " left the chat.");
    }
    
    @Override
    public void onMessage(WebSocket conn, String incomingMessage) {
        // Send event so other parts of the app know a client has sent a message
        EventBus.getDefault().post(new WSServerEvent.Message(conn, incomingMessage));
        // Send the incoming message to all clients
        this.broadcast(conn.getRemoteSocketAddress() + ": " + incomingMessage);
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
}
