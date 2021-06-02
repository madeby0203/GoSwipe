package rd.project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.Application;
import rd.project.R;
import rd.project.Settings;
import rd.project.api.Genres;
import rd.project.api.Providers;

public class SetupFragment extends Fragment { //fragment for settings: genre, director, year, review score, running time, country
    private final String TAG = "SetupFragment";

    private String genreSetting,yearSetting, scoreSetting, platformSetting;

    public SetupFragment() {
        super(R.layout.fragment_host_setup);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Application application = (Application) getContext().getApplicationContext();

        //links edit text and puts it in a string var.
        Spinner platformDropdown = view.findViewById(R.id.s_platformSetting);
        String[] platformItems = new String[]{"Netflix","Amazon video","DisneyPlus","Videoland"};
        ArrayAdapter<String> platformAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, platformItems);
        platformDropdown.setAdapter(platformAdapter);
        platformSetting = platformDropdown.getSelectedItem().toString();


        Spinner genreDropdown = view.findViewById(R.id.s_genreSetting);
        String[] genreItems = new String[]{"Action","Comedy","Drama","Fantasy", "Horror", "Mystery","Romance","Thriller","Western"};
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, genreItems);
        genreDropdown.setAdapter(genreAdapter);
        genreSetting = genreDropdown.getSelectedItem().toString();
        String genreValue = genreDropdown.getSelectedItem().toString();

        EditText yearBox = view.findViewById(R.id.s_yearSetting);
        yearSetting = yearBox.getText().toString();

        EditText scoreBox = view.findViewById(R.id.s_scoreSetting);
        scoreSetting = scoreBox.getText().toString();

        //collects all the settings and saves it to bundle in order to give it to the new fragment
        Button joinButton = view.findViewById(R.id.applyButton);
        Bundle bundle = new Bundle();
//        bundle.putString("platform", platformSetting);
//        bundle.putString("genre", genreSetting);
//        bundle.putString("year", yearSetting);
//        bundle.putString("score", scoreSetting);

        joinButton.setOnClickListener(v -> {
                Providers selectedProvider = Providers.Netflix; //default
                for(Providers providerSelection : Providers.values()) {
                    if(providerSelection.getName() == platformDropdown.getSelectedItem().toString()) {
                        selectedProvider = providerSelection;
                    }
                }
                Log.d(TAG,"Size of genres: " + Genres.values().length);
                Genres selectedGenre = Genres.Action; //default
                for(Genres genreSelection : Genres.values()) {
                    Log.d(TAG, "Genre " + genreSelection.getName() + " VS " + genreDropdown.getSelectedItem().toString());
                    if(genreSelection.getName() == genreDropdown.getSelectedItem().toString()) {
                        selectedGenre = genreSelection;
                    }
                }

                EditText yearvalue = view.findViewById(R.id.s_yearSetting);
                Log.d(TAG,"Set year: " + yearvalue.getText().toString());
                EditText scorevalue = view.findViewById(R.id.s_scoreSetting);
                Settings settings = new Settings(
                        selectedGenre.getId(),
                        scorevalue.getText().toString(),
                        yearvalue.getText().toString(),
                        selectedProvider.getId()
                );
                application.setLobbyPref(settings);
                getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                .replace(R.id.fragment_container_view, LobbyFragment.class, bundle)
                .commit();
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
}
