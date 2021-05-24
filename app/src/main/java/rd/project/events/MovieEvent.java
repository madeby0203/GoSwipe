package rd.project.events;

import android.util.Log;
import rd.project.api.Movie;

import java.util.ArrayList;

public class MovieEvent {
    
    public static class FetchSuccess {
        private final ArrayList<Movie> movies;
        
        public FetchSuccess(ArrayList<Movie> movies) {
            this.movies = movies;
        }
        
        public ArrayList<Movie> getMovies() {
            return this.movies;
        }
    }
    
    public static class FetchError {
    
    }
    
}
