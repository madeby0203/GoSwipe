package rd.project.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Movie implements Comparable<Movie> {
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "Movie";
    
    private final String overview;
    private final String title;
    private final String poster;
    private final Number score;
    private final int id;
    private final String releaseDate;
    
    private final String genre;
    private final String platform;
    
    /**
     * Movie and its corresponding information and features
     *
     * @param overview    movie description
     * @param title       movie title
     * @param poster      url to movie poster image
     * @param score       movie score according to The Movie Database
     * @param id          movie id on The Movie Database
     * @param releaseDate movie release date, formatted yyyy-mm-dd
     * @param genre       movie genre, formatted as a number supplied by The Movie Database
     * @param platform    movie platform, formatted as a number supplied by The Movie Database
     */
    public Movie(String overview, String title, String poster, Number score, int id, String releaseDate, String genre, String platform) {
        this.overview = overview;
        this.title = title;
        this.poster = "https://image.tmdb.org/t/p/original" + poster;
        this.score = score;
        this.id = id;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.platform = platform;
    }
    
    @NonNull
    public String toString() {
        return "Title: " + title + "\nOverview: " + overview + "\nVote: " + score + "\nPoster: " + poster + "\nID: " + id + "\nYear: " + releaseDate + "\nGenre: " + genre + "\nPlatform: " + platform;
    }
    
    public Number getScore() {
        return score;
    }
    
    public int getId() {
        return id;
    }
    
    public String getOverview() {
        return overview;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getPoster() {
        return poster;
    }
    
    public Bitmap getPosterBM() {
        try {
            URL url = new URL(this.poster);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    public String getReleaseDate() {
        return this.releaseDate;
    }
    
    /**
     * Get movie genre.
     *
     * @return movie genre formatted as a number, as given by the API
     */
    public String getGenre() {
        return genre;
    }
    
    /**
     * Get movie genre.
     *
     * @return movie genre formatted as a string
     */

    public String getGenreString() {
        for(Genres genre:Genres.values()) {
            if(genre.getId()==this.genre) {
                return genre.getName();
            }
        }
        return "";
    }
    
    /**
     * Get movie platform.
     *
     * @return movie platform formatted as a number, as given by the API
     */
    public String getPlatform() {
        return platform;
    }
    
    /**
     * Get movie platform.
     *
     * @return movie platform formatted as a string
     */
    public String getPlatformString() {
        Log.d(TAG, "Platform: " + this.platform);
        for(Providers provider:Providers.values()) {
            if(provider.getId() == this.platform) {
                return provider.getName();
            }
        }
        return "Disney+";
    }


    
    @Override
    public int compareTo(Movie movie) {
        // Compare movie titles
        return this.getTitle().compareToIgnoreCase(movie.getTitle());
    }
}
