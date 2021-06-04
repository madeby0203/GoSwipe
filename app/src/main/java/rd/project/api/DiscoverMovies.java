package rd.project.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Implementation of RequestType, in this case requesting a list of movies based on certain criteria:
 * - Region
 * - Watch providers (see Providers)
 * - Releasedate (year)
 * - Minimum vote (IMDB rating)
 */

public class DiscoverMovies implements RequestType {
    
    private final String providers;
    private final URL url;
    private final String genre;
    private ArrayList<Movie> movies;
    
    public DiscoverMovies(String apiKey, String region, String providers, String genres, String releaseDate, Double minVote) throws MalformedURLException {
        this.genre = genres;
        this.providers = providers;
        this.url = new URL(" https://api.themoviedb.org/3/discover/movie?api_key=" + apiKey +
                "&language=en-US&watch_region=" + region +
                "&sort_by=popularity.desc&include_adult=false&include_video=true&page=1&vote_average.gte=" + minVote +
                "&with_genres=" + genres +
                "&with_watch_providers=" + providers +
                "&release_date.gte=" + releaseDate);
    }
    
    @Override
    public URL getUrl() {
        return url;
    }
    
    @Override
    public ArrayList<Movie> getData() {
        return movies;
    }
    
    /**
     * Process the data which was returned by the API
     *
     * @param data data returned by the API
     */
    
    @Override
    public void updateData(JSONObject data) {
        JSONArray jsonresults = (JSONArray) data.get("results");
        ArrayList<Movie> movieList = new ArrayList<>();
        if (jsonresults != null) {
            for (Object jsonresult : jsonresults) {
                JSONObject jsonMovie = (JSONObject) jsonresult;
                Movie movie = new Movie(
                        (String) jsonMovie.get("overview"),
                        (String) jsonMovie.get("title"),
                        (String) jsonMovie.get("poster_path"),
                        (Number) jsonMovie.get("vote_average"),
                        Math.toIntExact((Long) jsonMovie.get("id")),
                        (String) jsonMovie.get("release_date"),
                        this.genre,
                        this.providers
                
                );
                movieList.add(movie);
            }
        }
        movies = movieList;
    }
}
