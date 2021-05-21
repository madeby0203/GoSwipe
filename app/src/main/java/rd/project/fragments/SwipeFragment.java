package rd.project.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import org.json.simple.JSONObject;
import rd.project.R;
import rd.project.api.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class SwipeFragment extends Fragment {

    private ArrayList<Movie> movies;
    private View view;

    public SwipeFragment (){
        super(R.layout.fragment_swipe);
    }

    public class updateMovie implements Runnable {

        private ArrayList movies;

        updateMovie() {
        }

        @Override
        public void run() {
            String api = "a443e45153a06c5830898cf8889fa27e";
            String region = "NL";
            String providers = Providers.Videoland.getId();
            Date release = new Date();
            //release.setTime();
            String releaseDate = "2020-01-01T00:00:00.000Z";
            int minVote = 8;


            RequestType request = null;
            try {
                request = new DiscoverMovies(api,region,providers,releaseDate,minVote);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Connect discover = new Connect(request);
            request.UpdateData(discover.Send());
            movies = request.GetData();
            request.GetData();
        }

        public ArrayList getMovies() {
            return movies;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        this.view = view;

        Button likeButton = view.findViewById(R.id.likeButton);
        //likeButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction());
        Button refreshButton = view.findViewById(R.id.refreshButton);
        Thread t = new Thread(() -> {
            String api = "a443e45153a06c5830898cf8889fa27e";
            String region = "NL";
            String providers = Providers.Videoland.getId();
            Date release = new Date();
            //release.setTime();
            String releaseDate = "2020-01-01T00:00:00.000Z";
            int minVote = 8;


            RequestType request = null;
            try {
                request = new DiscoverMovies(api,region,providers,releaseDate,minVote);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Connect discover = new Connect(request);
            JSONObject test = discover.Send();
            request.UpdateData(test);
            movies = request.GetData();
            request.GetData();
            RequestType finalRequest = request;
            new Thread(() -> updateMovies(finalRequest.GetData())).start();
        });
        t.start();
        refreshButton.setOnClickListener(v -> {
            try {
                updateView();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void updateMovies(ArrayList<Movie> movies) {
        this.movies = movies;
    }


    private void updateView() throws URISyntaxException, IOException {
        ImageView imageMovie = view.findViewById(R.id.imageView);
        URL url = new URL(movies.get(0).getPoster());
        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        imageMovie.setImageBitmap(bmp);
    }

}
