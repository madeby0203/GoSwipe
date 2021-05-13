package rd.project.api;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Connect {

    private String apiKey;
    private HttpURLConnection conn;
    private boolean connected;
    private RequestType request;

    /**
     * Responsible for creating httpURLconnection for any set-up request type
     * @param requestType
     */
    public Connect(RequestType requestType) {
        this.request = requestType;
        String[] response = new String[20];
        this.apiKey = request.getAPI();
        this.conn = null;
        this.connected = false;
    }

    /**
     * Send request to API and return response in JSON
     * @return
     */

    public JSONObject Send() {
        String[] response = new String[10];
        String inline = "";
        try {
            URL url = request.GetUrl();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            String APIresponse = conn.getResponseMessage();
            InputStream inputStream = conn.getInputStream();
            System.out.println(inputStream.toString());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONParser parser = new JSONParser();
            JSONObject jsonContent = (JSONObject) parser.parse(content.toString());

            return jsonContent;
/*
            JSONArray jsonresults = (JSONArray) jsonContent.get("results");
            ArrayList movieList = new ArrayList();
            for(int i=0; i<20; i++) {
                JSONObject jsonMovie = (JSONObject) jsonresults.get(i);
                Movie movie = new Movie(
                        (String) jsonMovie.get("overview"),
                        (String) jsonMovie.get("title"),
                        (String) jsonMovie.get("poster_path"),
                        (Number) jsonMovie.get("vote_average"),
                        Math.toIntExact((Long) jsonMovie.get("id"))
                );
                movieList.add(movie);
            }
            for(int i = 0; i < 20; i++) {
                Movie test = (Movie) movieList.get(i);
                System.out.println(test.toString());
            }

            if (conn.getResponseCode() != 200) {
                System.out.println("Error connecting");
                throw new RuntimeException("Failed: " + conn.getResponseCode());
            } else {
                System.out.println("Connected");
                connected = true;
                response[0] = "1";
            }

 */
        } catch (MalformedURLException e) {
            System.out.println("Something wrong with the url: " + e.getMessage());
            response[0] = "0";
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            response[0] = "0";
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        response[0] = "0";
        return null;
    }
}
