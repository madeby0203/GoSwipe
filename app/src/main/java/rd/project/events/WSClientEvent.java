package rd.project.events;

public class WSClientEvent {
    
    /**
     * Fired when the client has succesfully connected to the server.
     */
    public static class Open {
    
    }
    
    /**
     * Fired when the client receives a message from the server.
     */
    public static class Message {
        private final String message;
        
        public Message(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return this.message;
        }
    }
    
    /**
     * Fired when the connection from the client to the server was closed.
     */
    public static class Close {
        private final String reason;
        
        public Close(String reason) {
            this.reason = reason;
        }
        
        public String getReason() {
            return this.reason;
        }
    }
    
    /**
     * Fired when an error has occurred in the client.
     */
    public static class Error {
        private final Exception ex;
        
        public Error(Exception ex) {
            this.ex = ex;
        }
        
        public Exception getException() {
            return this.ex;
        }
    }
    
}
