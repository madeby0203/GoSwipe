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
    
    void saveLikes(List<Integer> movieIDs);
    void saveLikes(String username, List<Integer> movieIDs);
    
    int getResultsCompletedAmount();
    
    enum Type {
        HOST, CLIENT, NONE
    }
    
    enum MessageParameter {
        TYPE,                       // Specifies type of message
        USERNAME,                   // Username of player, used in PLAYER_JOIN and PLAYER_LEAVE, send to clients by host
        USER_LIST,                  // List of connected users, send to clients by host
        MOVIE_LIST,                 // List of movies, sent to all clients by the host
        LIKES_LIST,                 // Used by client to send list of liked movies to host
        AMOUNT                      // An integer containing an amount of something
    }
    
    enum MessageType {
        PLAYER_LIST,                // List of connected players
        PLAYER_JOIN,                // Contains name of player that joined
        PLAYER_LEAVE,               // Contains name of player that leaved
        START_PREPARE,              // Tells clients to prepare for start, hides buttons in lobby
        START_COUNTDOWN,            // Starts countdown in lobby
        MOVIE_LIST,                 // List of movies, sent to all clients by the host
        LIKES_SAVE,                 // List of liked movie IDs by user, sent to host by client
        RESULTS_COMPLETED_AMOUNT    // Sent if a user has completed the swipe section, contains amount of completions
    }
    
    public class ClosedException extends Exception {
    
    }
    
}
