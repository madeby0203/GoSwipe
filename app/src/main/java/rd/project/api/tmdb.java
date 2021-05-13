package rd.project.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class tmdb {

    private String apiKey;
    private HttpURLConnection conn;
    private boolean connected;

    public tmdb(String key) {
        this.apiKey = key;
        this.conn = null;
        this.connected = false;
    }

    public boolean connect() {
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/550?api_key=" + apiKey);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed: " + conn.getResponseCode());
            } else {
                connected = true;
            }
        } catch (MalformedURLException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean getStatus() {
        return connected;
    }

    public String[] getMovie(int id) {
        try {
            URL url = new URL("https://api.themoviedb.org/3/movie/"+ id + "?api_key=" + apiKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String[] result = new String[5];
        result[0] = "test";
        return result;
    }
}
