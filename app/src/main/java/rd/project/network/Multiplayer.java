package rd.project.network;

import java.util.List;

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
    List<String> getConnectedUsernames();
    
    enum Type {
        HOST, CLIENT, NONE
    }
    
    enum MessageParameter {
        TYPE, USERNAME, USERLIST
    }
    
    enum MessageType {
        PLAYER_LIST, PLAYER_JOIN, PLAYER_LEAVE
    }
    
    public class ClosedException extends Exception {
    
    }
    
}
