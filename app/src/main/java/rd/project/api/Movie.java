package rd.project.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Movie implements Comparable<Movie> {
    private final String TAG = "Movie";
    
    private final String overview;
    private final String title;
    private final String poster;
    private final Number vote;
    private final int id;
    private final String year;

    private final String genre;
    private final String platform;

    /**
     * Movie and its corresponding information and features
     * @param overview
     * @param title
     * @param poster
     * @param vote
     * @param id
     */

    public Movie (String overview, String title, String poster, Number vote, int id, String year, String genre, String platform) {
        this.overview = overview;
        this.title = title;
        this.poster = "https://image.tmdb.org/t/p/original" + poster;
        this.vote = vote;
        this.id = id;
        this.year = year;
        this.genre = genre;
        this.platform = platform;
    }

    public String toString() {
        return "Title: " + title + "\nOverview: " + overview + "\nVote: " + vote + "\nPoster: " + poster + "\nID: " + id +"\nYear: " + year + "\nGenre: " + genre + "\nPlatform: " + platform;
    }

    public Number getVote() {
        return vote;
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
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (IOException e) {
            // Log exception
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public String getYear() {
        StringBuilder year = new StringBuilder(this.year);
        //year.delete(4,24);
        return year.toString();
    }
    
    /**
     * Get movie genre.
     * @return movie genre formatted as a number, as given by the API
     */
    public String getGenre() {
        return genre;
    }
    
    /**
     * Get movie genre.
     * @return movie genre formatted as a string
     */
    public String getGenreString() {
        String genre = "Unknown";
        switch (Integer.parseInt(this.getGenre())){
            case 28: genre = "Action"; break;
            case 12: genre = "Adventure"; break;
            case 15: genre = "Animation"; break;
            case 35: genre = "Comedy"; break;
            case 80: genre = "Crime"; break;
            case 99: genre = "Documentary"; break;
            case 10751: genre = "Family"; break;
            case 18: genre = "Drama"; break;
            case 14: genre = "Fantasy"; break;
            case 36: genre = "History"; break;
            case 27: genre = "Horror"; break;
            case 10402: genre = "Music"; break;
            case 9648: genre = "Mystery"; break;
            case 10749: genre = "Romance"; break;
            case 878: genre = "ScienceFiction"; break;
            case 10770: genre = "TV_Movie"; break;
            case 53: genre = "Thriller"; break;
            case 10752: genre = "War"; break;
            case 37: genre = "Western"; break;
        }
        return genre;
    }
    
    /**
     * Get movie platform.
     * @return movie platform formatted as a number, as given by the API
     */
    public String getPlatform() {
        return platform;
    }
    
    /**
     * Get movie platform.
     * @return movie platform formatted as a string
     */
    public String getPlatformString() {
        String platform = "Unknown";
        switch (Integer.parseInt(this.getPlatform())){
            case 8: platform = "Netflix"; break;
            case 9: platform = "Amazon Prime Video"; break;
            case 337: platform = "Disney Plus"; break;
            case 72: platform = "Videoland"; break;
        }
        return platform;
    }
    
    @Override
    public int compareTo(Movie movie) {
        // Compare movie titles
        return this.getTitle().compareToIgnoreCase(movie.getTitle());
    }
}
