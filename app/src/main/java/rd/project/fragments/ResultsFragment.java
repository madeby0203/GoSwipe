package rd.project.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import rd.project.Application;
import rd.project.R;
import rd.project.adapters.ResultAdapter;
import rd.project.api.Movie;
import rd.project.network.Multiplayer;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

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


        //add elements to the list in order for the pager to show it
//        Movie temp = new Movie("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque eu eleifend odio, eget cursus neque. Proin ut massa ac tellus egestas placerat. Etiam condimentum pellentesque ligula ut tristique. Aliquam pretium convallis augue non interdum. Vestibulum tempus tellus ante, nec luctus ligula convallis quis. Ut placerat, turpis id varius iaculis, nisi sapien pulvinar metus, sed faucibus dui odio ac nisl. Nam a ultrices neque. Vestibulum scelerisque, turpis et bibendum dictum, quam mauris vehicula felis, in aliquam leo turpis suscipit nunc.","Lorem Ipsum","/j4tRuhwfAOBKCoZeY6PDMJHzdW9.jpg",10,5,"1000 BC","greek roman","stoneTablet");
//        Movie temp2 = new Movie("test2","test2","/j4tRuhwfAOBKCoZeY6PDMJHzdW9.jpg",2,52,"52","test2","test2");
//        list.add(temp);
//        list.add(temp2);
        //TODO remove in final product.
        
        Application application = (Application) getContext().getApplicationContext();
        
        if(application.getMultiplayerType() == Multiplayer.Type.NONE) {
            application.showErrorScreen(getParentFragmentManager(),
                    R.drawable.ic_baseline_error_outline_24,
                    "Error",
                    "Couldn't load results");
            
            return;
        }
        
        Map<Movie, Integer> results = application.getMultiplayer().getResults();
        list.addAll(results.keySet());


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
                        .replace(R.id.fragment_container_view, InfoResultFragment.class, tempBundle)
                        .commit();

            }).start();



        });
    }

}
