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
    private Object Movie;

    public ResultsFragment(){
        super(R.layout.fragment_results);
    }
    ResultAdapter resultAdapter;
    ViewPager2 pager;
    ArrayList<Movie> list = new ArrayList<>(); //TODO link swipe game in order to get those into here

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;

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
        
        Map<Movie, Integer> results = application.results;
        results = sortByValue(results);
        list.addAll(results.keySet());
        Collections.reverse(list);

        // set the result adapter
        resultAdapter = new ResultAdapter(list, getContext());
        pager.setAdapter(resultAdapter);

        //set the amount of offscreen pages
        pager.setOffscreenPageLimit(1);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float a = 1 - Math.abs(position);
            }
        });

        pager.setPageTransformer(transformer);


        Button infoButton = view.findViewById(R.id.infoButton);

        infoButton.setOnClickListener(v -> {
            Movie tempMovie = list.get(pager.getCurrentItem());
            Bundle tempBundle = new Bundle();
            tempBundle.putString("overview", tempMovie.getOverview());
            tempBundle.putString("title", tempMovie.getTitle());
            tempBundle.putString("year", tempMovie.getYear());
            tempBundle.putString("score", tempMovie.getVote().toString());
            tempBundle.putString("genre", tempMovie.getGenre());
            tempBundle.putString("platform",tempMovie.getPlatform());


            Intent intent = new Intent();

            new Thread(() -> {
                Bitmap bmp = tempMovie.getPosterBM();
                tempBundle.putParcelable("bitmap", bmp);
                intent.putExtra("bitmap", bmp);

                getParentFragmentManager().popBackStack();
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .replace(R.id.fragment_container_view, ResultInfoFragment.class, tempBundle)
                        .commit();

            }).start();



        });


        OnBackPressedCallback back = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setReorderingAllowed(true)
                        .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                        .replace(R.id.fragment_container_view, MenuFragment.class, null)
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, back);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) { //https://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values?page=1&tab=votes#tab-top
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
