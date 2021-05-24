package rd.project.network;

import rd.project.api.Movie;

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
    List<Movie> getMovies();
    
    enum Type {
        HOST, CLIENT, NONE
    }
    
    enum MessageParameter {
        TYPE, USERNAME, USERLIST, MOVIELIST
    }
    
    enum MessageType {
        PLAYER_LIST, PLAYER_JOIN, PLAYER_LEAVE, START_PREPARE, START_COUNTDOWN, MOVIE_LIST
    }
    
    public class ClosedException extends Exception {
    
    }
    
}
