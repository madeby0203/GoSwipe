package rd.project.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import rd.project.Application;
import rd.project.R;
import rd.project.adapters.ResultAdapter;
import rd.project.api.Movie;

import java.util.*;

public class ResultsFragment extends Fragment { //source: https://github.com/Rohitohlyan66/InstagramSuggestion
    ResultAdapter resultAdapter;
    ViewPager2 pager;
    
    public ResultsFragment() {
        super(R.layout.fragment_results);
    }
    
    private static Map<Movie, Integer> sortResults(Map<Movie, Integer> results) {
        Map<Movie, Integer> sortedResults = new LinkedHashMap<>();
        
        List<Integer> scores = new ArrayList<>(new HashSet<>(results.values())); // Remove duplicate scores
        scores.sort(Collections.reverseOrder()); // Sort scores form high to low
        
        // Get the movies for each score
        for (int score : scores) {
            // Get all movies for this specific score
            List<Movie> movies = getKeysFromValue(results, score);
            
            // Sort movies alphabetically
            Collections.sort(movies);
            
            // Add movies to sortedResults
            for (Movie movie : movies) {
                sortedResults.put(movie, score);
            }
        }
        
        return sortedResults;
    }
    
    private static LinkedList<Movie> getKeysFromValue(Map<Movie, Integer> map, int value) {
        LinkedList<Movie> keys = new LinkedList<>();
        
        for (Movie movie : map.keySet()) {
            if (map.get(movie) == value) {
                keys.add(movie);
            }
        }
        
        return keys;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set pager to the pager in the fragment_result.xml
        pager = view.findViewById(R.id.sliderMenu);
        
        Application application = (Application) getContext().getApplicationContext();
        
        if (application.results.isEmpty()) {
            application.showErrorScreen(getParentFragmentManager(),
                    R.drawable.ic_baseline_thumbs_up_down_24,
                    getString(R.string.results_error_empty_title),
                    getString(R.string.results_error_empty_description));
            
            return;
        }
        
        // Get results from Application and sort them
        Map<Movie, Integer> results = sortResults(application.results);
        
        // set the result adapter
        resultAdapter = new ResultAdapter(results, getContext());
        pager.setAdapter(resultAdapter);
        
        //set the amount of offscreen pages
        pager.setOffscreenPageLimit(1);
        
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer((page, position) -> {
        });
        
        pager.setPageTransformer(transformer);
        
        Button infoButton = view.findViewById(R.id.infoButton);
        
        infoButton.setOnClickListener(v -> {
            // Get movie at specified position
            Movie movie = new ArrayList<>(results.keySet()).get(pager.getCurrentItem());
            Bundle tempBundle = new Bundle();
            tempBundle.putString("overview", movie.getOverview());
            tempBundle.putString("title", movie.getTitle());
            tempBundle.putString("year", movie.getReleaseDate());
            tempBundle.putString("score", movie.getScore().toString());
            tempBundle.putString("genre", movie.getGenreString());
            tempBundle.putString("platform", movie.getPlatformString());
            
            
            Intent intent = new Intent();
            
            new Thread(() -> {
                Bitmap bmp = movie.getPosterBM();
                tempBundle.putParcelable("bitmap", bmp);
                intent.putExtra("bitmap", bmp);
                
                getParentFragmentManager().popBackStack();
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, ResultInfoFragment.class, tempBundle)
                        .commit();
            }).start();
        });
        
        // Define back button behaviour
        OnBackPressedCallback back = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, MenuFragment.class, null)
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), back);
    }
    
}
