package rd.project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.Application;
import rd.project.R;
import rd.project.api.Settings;
import rd.project.api.Genres;
import rd.project.api.Providers;

import java.util.Arrays;

/**
 * Fragment for settings: genre, director, year, review score, running time, country
 */
public class SetupFragment extends Fragment {
    private final String TAG = "SetupFragment";
    
    public SetupFragment() {
        super(R.layout.fragment_host_setup);
    }
    
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Application application = (Application) getContext().getApplicationContext();
        
        // Links edit text and puts it in a string var
        Spinner platformDropdown = view.findViewById(R.id.platformSetting);
        String[] platformFromEnum = Arrays.stream(Providers.values()).map(Providers::getName).toArray(String[]::new);
        ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, platformFromEnum);
        platformDropdown.setAdapter(platformAdapter);
        
        Spinner genreDropdown = view.findViewById(R.id.genreSetting);
        String[] genreFromEnum = Arrays.stream(Genres.values()).map(Genres::getName).toArray(String[]::new);
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, genreFromEnum);
        genreDropdown.setAdapter(genreAdapter);
        
        EditText yearBox = view.findViewById(R.id.yearSetting);
        EditText scoreBox = view.findViewById(R.id.scoreSetting);
        Button joinButton = view.findViewById(R.id.applyButton);
        
        joinButton.setOnClickListener(v -> {
            Providers selectedProvider = Providers.Netflix; //default
            for (Providers providerSelection : Providers.values()) {
                if (providerSelection.getName().equals(platformDropdown.getSelectedItem().toString())) {
                    selectedProvider = providerSelection;
                }
            }
            Log.d(TAG, "Amount of Genres: " + Genres.values().length);
            Genres selectedGenre = Genres.Action; //default
            for (Genres genreSelection : Genres.values()) {
                if (genreSelection.getName().equals(genreDropdown.getSelectedItem().toString())) {
                    selectedGenre = genreSelection;
                }
            }
            
            Log.d(TAG, "Set year: " + yearBox.getText().toString());
            String score = scoreBox.getText().toString();
            String year = yearBox.getText().toString();
            if (score.isEmpty()) {
                Log.d(TAG, "Score is empty");
                score = "0";
            }
            if (year.isEmpty()) {
                Log.d(TAG, "Year is empty");
                year = "1950";
            }
            Settings settings = new Settings(
                    selectedGenre.getId(),
                    score,
                    year,
                    selectedProvider.getId()
            );
            application.setLobbyPref(settings);
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, LobbyFragment.class, null)
                    .commit();
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
