package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import rd.project.R;
import rd.project.adapters.ResultAdapter;

public class ResultsFragment extends Fragment {

    public ResultsFragment(){
        super(R.layout.fragment_results);
    }
    ResultAdapter resultAdapter;
    ViewPager2 pager;
    int[] list;
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;

        pager = view.findViewById(R.id.sliderMenu);
        list = new int[5];
        list[0] = R.drawable.ic_launcher_background;
        list[1] = R.drawable.ic_launcher_background;

        resultAdapter = new ResultAdapter(list);
        pager.setAdapter(resultAdapter);

        pager.setClipToPadding(false);
        pager.setClipToPadding(false);
        pager.setOffscreenPageLimit(3);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float a = 1 - Math.abs(position);

            }
        });

        pager.setPageTransformer(transformer);
    }

}
