package rd.project.api;

import android.util.Log;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Connect {
    @SuppressWarnings("FieldCanBeLocal")
    private final String TAG = "Connect";
    private final RequestType request;
    private HttpURLConnection conn;
    
    /**
     * Responsible for creating httpURLconnection for any set-up request type
     *
     * @param requestType request parameters
     */
    public Connect(RequestType requestType) {
        this.request = requestType;
        this.conn = null;
    }
    
    /**
     * Send request to API and return response in JSON.
     *
     * @return API response as a JSON object
     */
    
    public JSONObject Send() {
        try {
            URL url = request.getUrl();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            InputStream inputStream = conn.getInputStream();
            Log.v(TAG, "inputStream: " + inputStream.toString());
            
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(content.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Something wrong with the url: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
