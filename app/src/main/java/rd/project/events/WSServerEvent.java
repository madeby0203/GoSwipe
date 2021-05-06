package rd.project.events;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

public class WSServerEvent {
    
    /**
     * Fired when a user connects to the WebSocket Server.
     */
    public static class Open {
        private final WebSocket conn;
        private final ClientHandshake handshake;
        
        public Open(WebSocket conn, ClientHandshake handshake) {
            this.conn = conn;
            this.handshake = handshake;
        }
        
        public WebSocket getWebSocket() {
            return this.conn;
        }
        
        public ClientHandshake getHandshake() {
            return this.handshake;
        }
    }
    
    /**
     * Fired when a user disconnects from the WebSocket server.
     */
    public static class Close {
        private final WebSocket conn;
        private final String reason;
        
        public Close(WebSocket conn, String reason) {
            this.conn = conn;
            this.reason = reason;
        }
        
        public WebSocket getWebSocket() {
            return this.conn;
        }
        
        public String getReason() {
            return this.reason;
        }
    }
    
    /**
     * Fired when the WebSocket server receives a message from a connected client.
     */
    public static class Message {
        private final WebSocket conn;
        private final String message;
        
        public Message(WebSocket conn, String message) {
            this.conn = conn;
            this.message = message;
        }
        
        public WebSocket getWebSocket() {
            return this.conn;
        }
        
        public String getMessage() {
            return this.message;
        }
    }
    
    /**
     * Fired when the WebSocket server encountered an error.
     */
    public static class Error {
        private final WebSocket conn;
        private final Exception ex;
        
        public Error(WebSocket conn, Exception ex) {
            this.conn = conn;
            this.ex = ex;
        }
        
        public WebSocket getWebSocket() {
            return this.conn;
        }
        
        public Exception getException() {
            return this.ex;
        }
    }
    
    /**
     * Fired when the WebSocket server has started successfully.
     */
    public static class Start {
        private final int port;
        
        public Start(int port) {
            this.port = port;
        }
        
        public int getPort() {
            return this.port;
        }
    }
    
}
