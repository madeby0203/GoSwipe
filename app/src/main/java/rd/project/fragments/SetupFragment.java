package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.Application;
import rd.project.R;
import rd.project.api.Providers;

import java.util.Arrays;
import java.util.List;

public class SetupFragment extends Fragment { //fragment for settings: genre, director, year, review score, running time, country

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
        platformSetting = platformDropdown.getTransitionName();

        String providerID = application.getProviders();
        if(providerID != null) {
            platformDropdown.setSelection(Integer.getInteger(providerID));
        }


        Spinner genreDropdown = view.findViewById(R.id.s_genreSetting);
        String[] genreItems = new String[]{"Action","Comedy","Drama","Fantasy", "Horror", "Mystery","Romance","Thriller","Western"};
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, genreItems);
        genreDropdown.setAdapter(genreAdapter);
        genreSetting = genreDropdown.getTransitionName();

        EditText yearBox = view.findViewById(R.id.s_yearSetting);
        yearSetting = yearBox.getText().toString();

        EditText scoreBox = view.findViewById(R.id.s_scoreSetting);
        scoreSetting = scoreBox.getText().toString();

        String provider = application.getProviders();
        String genre = application.getGenre();
        String rating = application.getRating();
        String year = application.getYear();

        if(provider != null && genre != null && rating !=null && year != null) {
            platformDropdown.setSelection(Integer.valueOf(provider));
            List<String> genreList = Arrays.asList(genreItems);
            genreDropdown.setSelection(genreList.indexOf(genre));
            yearBox.setText(year);
        }

        //collects all the settings and saves it to bundle in order to give it to the new fragment
        Button joinButton = view.findViewById(R.id.createButton);
        Bundle bundle = new Bundle();
        bundle.putString("platform", platformSetting);
        bundle.putString("genre", genreSetting);
        bundle.putString("year", yearSetting);
        bundle.putString("score", scoreSetting);

        Providers selectedProvider = Providers.Netflix;
        for(Providers providerSelection : Providers.values()) {
            if(providerSelection.getName() == platformSetting) {
                selectedProvider = providerSelection;
            }
        }

        Providers finalSelectedProvider = selectedProvider;
        joinButton.setOnClickListener(v -> {
            application.setLobbyPref(genreSetting, finalSelectedProvider.getId(),yearSetting,scoreSetting);
        });
        joinButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                .replace(R.id.fragment_container_view, LobbyFragment.class, bundle)
                .commit());


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
