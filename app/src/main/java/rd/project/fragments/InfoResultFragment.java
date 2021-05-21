package rd.project.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import org.w3c.dom.Text;
import rd.project.R;
import rd.project.api.Movie;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoResultFragment extends Fragment {

    public InfoResultFragment (){
        super(R.layout.fragment_resultinfo);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Bundle movie = getArguments();
        TextView title = view.findViewById(R.id.r_titleText);
        TextView overview = view.findViewById(R.id.r_overviewText);
        TextView year = view.findViewById(R.id.r_yearText);
        TextView score = view.findViewById(R.id.r_score);
        TextView genre = view.findViewById(R.id.r_genre);
        TextView platform = view.findViewById(R.id.r_platform);
        ImageView image = view.findViewById(R.id.r_image);




//        URL url = null;
//        try {
//            url = new URL("https://www.themoviedb.org/t/p/original/s9zJFEy4usXVFAa52AWT4ZCwR27.jpg");
//            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//            image.setImageBitmap(bmp);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        title.setText(movie.getString("title"));
        overview.setText(movie.getString("overview"));
        year.setText(movie.getString("year"));
        score.setText(movie.getString("score"));
        genre.setText(movie.getString("genre"));
        platform.setText(movie.getString("platform"));
        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher_background));


    }


}
