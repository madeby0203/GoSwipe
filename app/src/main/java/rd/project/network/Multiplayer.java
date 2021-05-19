package rd.project.network;

public interface Multiplayer {
    
    default Type getType() {
        if (!isClosed()) {
            if (this instanceof MultiplayerServer) {
                return Type.HOST;
            } else if (this instanceof MultiplayerClient) {
                return Type.CLIENT;
            }
        }
        return Type.NONE;
    }
    
    void close();
    boolean isClosed();
    void sendMessage(String message) throws ClosedException;
    
    enum Type {
        HOST, CLIENT, NONE;
    }
    
    public class ClosedException extends Exception {
    
    }
    
}
