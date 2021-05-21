package rd.project.fragments;

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
import rd.project.R;
import rd.project.adapters.ResultAdapter;
import rd.project.api.Movie;

import java.util.ArrayList;

public class ResultsFragment extends Fragment { //source: https://github.com/Rohitohlyan66/InstagramSuggestion

    public ResultsFragment(){
        super(R.layout.fragment_results);
    }
    ResultAdapter resultAdapter;
    ViewPager2 pager;
    ArrayList<Movie> list = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;

        // Set pager to the pager in the fragment_result.xml
        pager = view.findViewById(R.id.sliderMenu);

        //add elements to the list in order for the pager to show it
        Movie temp = new Movie("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque eu eleifend odio, eget cursus neque. Proin ut massa ac tellus egestas placerat. Etiam condimentum pellentesque ligula ut tristique. Aliquam pretium convallis augue non interdum. Vestibulum tempus tellus ante, nec luctus ligula convallis quis. Ut placerat, turpis id varius iaculis, nisi sapien pulvinar metus, sed faucibus dui odio ac nisl. Nam a ultrices neque. Vestibulum scelerisque, turpis et bibendum dictum, quam mauris vehicula felis, in aliquam leo turpis suscipit nunc.","test","test",1,5,"5","test","test");
        Movie temp2 = new Movie("test2","test2","test2",2,52,"52","test2","test2");
        list.add(temp);
        list.add(temp2);



        // set the result adapter
        resultAdapter = new ResultAdapter(list);
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

            getParentFragmentManager().popBackStack();
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, InfoResultFragment.class, tempBundle)
                    .commit();
        });
    }

}
