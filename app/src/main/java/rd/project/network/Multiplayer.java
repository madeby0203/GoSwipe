package rd.project.network;

import android.util.Log;
import rd.project.api.Movie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Multiplayer {
    final String TAG = "Multiplayer";
    
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
    
    default Map<Movie, Integer> convertLikedIDsToLikedMovies(List<Movie> movies, Map<Integer, Integer> likedIDs) {
        Map<Movie, Integer> likedMovies = new HashMap<>();
        
        for(int id : likedIDs.keySet()) {
            Movie movie = findMovieById(movies, id);
            
            if (movie == null) {
                Log.w(TAG, "Movie with id " + id + " not found.");
            } else {
                likedMovies.put(movie, likedIDs.get(id));
            }
        }
        
        return likedMovies;
    }
    
    default Movie findMovieById(List<Movie> movies, int id) {
        for(Movie movie : movies) {
            if (movie.getId() == id) {
                return movie;
            }
        }
        
        return null;
    }
    
    enum Type {
        HOST, CLIENT, NONE
    }
    
    enum MessageParameter {
        TYPE,                       // Specifies type of message
        USERNAME,                   // Username of player, used in PLAYER_JOIN and PLAYER_LEAVE, send to clients by host
        USER_LIST,                  // List of connected users, send to clients by host
        MOVIE_LIST,                 // List of movies, sent to all clients by the host
        LIKES_LIST,                 // Used by client to send list of liked movies to host
        AMOUNT,                     // An integer containing an amount of something
        RESULTS_LIST
    }
    
    enum MessageType {
        PLAYER_LIST,                // List of connected players
        PLAYER_JOIN,                // Contains name of player that joined
        PLAYER_LEAVE,               // Contains name of player that leaved
        START_PREPARE,              // Tells clients to prepare for start, hides buttons in lobby
        START_COUNTDOWN,            // Starts countdown in lobby
        MOVIE_LIST,                 // List of movies, sent to all clients by the host
        LIKES_SAVE,                 // List of liked movie IDs by user, sent to host by client
        RESULTS_COMPLETED_AMOUNT,   // Sent if a user has completed the swipe section, contains amount of completions
        RESULTS                     // List of results containing a list of movie IDs and amount of likes
    }
    
    public class ClosedException extends Exception {
    
    }
    
}
