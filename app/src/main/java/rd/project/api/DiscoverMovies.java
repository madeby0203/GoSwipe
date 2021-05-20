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

public class DiscoverMovies implements RequestType{

    private String region;
    private String providers;
    private String releaseDate;
    private int minVote;
    private URL url;
    private String apiKey;
    private ArrayList movies;
    private String genre; //TODO: API nog toevoegen

    public DiscoverMovies (String apiKey, String region, String providers, String releaseDate, int minVote) throws MalformedURLException {
        this.region = region;
        this.providers = providers;
        this.releaseDate = releaseDate;
        this.minVote = minVote;
        this.apiKey = apiKey;
        this.url = new URL(" https://api.themoviedb.org/3/discover/movie?api_key="+ apiKey +
                "&language=en-US&watch_region="+ region +
                "&sort_by=popularity.desc&include_adult=false&include_video=true&page=1&vote_average.gte=" + minVote +
                "&with_watch_providers=" + providers +
                "&release_date.lte=" + releaseDate);
    }

    @Override
    public URL GetUrl() {
        return url;
    }

    @Override
    public ArrayList GetData() {
        return movies;
    }

    /**
     * Process the data which was returned by the API
     * @param data
     * @return
     */

    @Override
    public boolean UpdateData(JSONObject data) {
        JSONArray jsonresults = (JSONArray) data.get("results");
        ArrayList movieList = new ArrayList();
        for(int i=0; i<20; i++) {
            JSONObject jsonMovie = (JSONObject) jsonresults.get(i);
            Movie movie = new Movie(
                    (String) jsonMovie.get("overview"),
                    (String) jsonMovie.get("title"),
                    (String) jsonMovie.get("poster_path"),
                    (Number) jsonMovie.get("vote_average"),
                    Math.toIntExact((Long) jsonMovie.get("id")),
                    (int) jsonMovie.get(5), //TODO: release date aanpassen naar int
                    (String) jsonMovie.get(genre),
                    (String) jsonMovie.get(providers)

            );
            movieList.add(movie);
        }
        movies = movieList;
        return true;
    }

    @Override
    public String getAPI() {
        return this.apiKey;
    }
}
