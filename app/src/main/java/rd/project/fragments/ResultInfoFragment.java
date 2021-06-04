package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.R;

public class ResultInfoFragment extends Fragment {
    
    public ResultInfoFragment() {
        super(R.layout.fragment_result_info);
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Bundle movie = getArguments();
        TextView title = view.findViewById(R.id.resultInfoTitle);
        TextView overview = view.findViewById(R.id.resultInfoOverviewText);
        TextView year = view.findViewById(R.id.resultInfoProductionDate);
        TextView score = view.findViewById(R.id.resultInfoScore);
        TextView genre = view.findViewById(R.id.resultInfoGenre);
        TextView platform = view.findViewById(R.id.resultInfoPlatform);
        ImageView image = view.findViewById(R.id.resultInfoPoster);
        
        
        title.setText(movie.getString("title"));
        overview.setText(movie.getString("overview"));
        year.setText(movie.getString("year"));
        score.setText(String.format(getString(R.string.movie_details_score_format), movie.getString("score")));
        genre.setText(movie.getString("genre"));
        platform.setText(movie.getString("platform"));
        image.setImageBitmap(movie.getParcelable("bitmap"));
        
        // Define back button behaviour
        OnBackPressedCallback back = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, ResultsFragment.class, null)
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), back);
    }
    
}
