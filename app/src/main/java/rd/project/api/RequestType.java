package rd.project.api;

import org.json.simple.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public interface RequestType {
    URL getUrl();
    
    ArrayList<Movie> getData();
    
    void updateData(JSONObject data);
}
