package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rd.project.R;
import rd.project.adapters.MessagesAdapter;

public class SetupFragment extends Fragment { //fragment for settings: genre, director, year, review score, running time, country

    private String genreSetting,yearSetting, scoreSetting, platformSetting;

    public SetupFragment() {
        super(R.layout.fragment_host_setup);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //links edit text and puts it in a string var.
        EditText platformBox = view.findViewById(R.id.platformSetting);
        platformSetting = platformBox.getText().toString();

        EditText genreBox = view.findViewById(R.id.genreSettings);
        genreSetting = genreBox.getText().toString();

        EditText yearBox = view.findViewById(R.id.yearSetting);
        yearSetting = yearBox.getText().toString();

        EditText scoreBox = view.findViewById(R.id.scoreSetting);
        scoreSetting = scoreBox.getText().toString();

        //collects all the settings and saves it to bundle in order to give it to the new fragment
        Button joinButton = view.findViewById(R.id.createButton);
        Bundle bundle = new Bundle();
        bundle.putString("platform", platformSetting);
        bundle.putString("genre", genreSetting);
        bundle.putString("year", yearSetting);
        bundle.putString("score", scoreSetting);
        joinButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                .replace(R.id.fragment_container_view, ChatFragment.class, bundle)
                .commit());
    }

    public String getGenreSetting(){
        return genreSetting;
    }

    public String getYearSetting(){
        return yearSetting;
    }

    public String getScoreSetting(){
        return scoreSetting;
    }

    public String getPlatformSetting(){
        return platformSetting;
    }


}
