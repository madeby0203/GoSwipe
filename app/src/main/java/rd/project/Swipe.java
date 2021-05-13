package rd.project;

import rd.project.api.*;

import java.net.MalformedURLException;
import java.util.Date;

/**
 * Main functional element of the swiping and rating (yes/no) of the individual movies
 */

public class Swipe {
    String api = "a443e45153a06c5830898cf8889fa27e";
    String region = "NL";
    String providers = Providers.Videoland.getId();
    Date release = new Date();
    //release.setTime();
    String releaseDate = "2020-01-01T00:00:00.000Z";
    int minVote = 8;

    RequestType request;
    {
        try {
            request = new DiscoverMovies(api,region,providers,releaseDate,minVote);
            Connect discover = new Connect(request);
            request.UpdateData(discover.Send());
            System.out.println(request.GetData().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
