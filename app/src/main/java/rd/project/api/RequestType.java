package rd.project.api;

import org.json.simple.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public interface RequestType {
    URL GetUrl();
    ArrayList GetData();
    boolean UpdateData(JSONObject data);
    String getAPI();
}
