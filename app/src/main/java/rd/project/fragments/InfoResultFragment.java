package rd.project.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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



        title.setText(movie.getString("title"));
        overview.setText(movie.getString("overview"));
        year.setText(movie.getString("year"));
        score.setText(movie.getString("score"));
        genre.setText(movie.getString("genre"));
        platform.setText(movie.getString("platform"));
        image.setImageBitmap(movie.getParcelable("bitmap"));

        OnBackPressedCallback back = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                        .replace(R.id.fragment_container_view, ResultsFragment.class, null)
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, back);

    }




//    Button joinButton = view.findViewById(R.id.joinButton);
//        joinButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                .setReorderingAllowed(true)
//                .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
//                .replace(R.id.fragment_container_view, JoinFragment.class, null)
//                .commit());
//}

}
