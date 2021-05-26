package rd.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import rd.project.fragments.ErrorFragment;
import rd.project.network.Multiplayer;
import rd.project.network.MultiplayerClient;
import rd.project.network.MultiplayerServer;

import java.net.URI;

public class Application extends android.app.Application {
    
    private Multiplayer multiplayer;
    
    public Multiplayer getMultiplayer() {
        return multiplayer;
    }
    
    public void becomeHost() {
        if (multiplayer != null) {
            multiplayer.close();
        }
        multiplayer = new MultiplayerServer(getApplicationContext());
    }
    
    public void becomeClient(URI uri) {
        if (multiplayer != null) {
            multiplayer.close();
        }
        multiplayer = new MultiplayerClient(getApplicationContext(), uri);
    }
    
    public Multiplayer.Type getMultiplayerType() {
        if (multiplayer == null) {
            return Multiplayer.Type.NONE;
        }
        return multiplayer.getType();
    }
    
    /**
     * Retrieves username from SharedPreferences
     * @return username
     */
    public String getUsername() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        
        return prefs.getString(getString(R.string.username_preference), null);
    }
    
    /**
     * Sets username in SharedPreferences
     * @param name username
     */
    public void setUsername(String name) {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.username_preference), name)
                .apply();
    }

    final String Genre = "genreKey";
    final String Providers = "providersKey";
    final String Year = "yearKey";
    final String Rating = "ratingKey";

    public void setLobbyPref(String genre, String providerId, String year, String rating) {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);



        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Genre, genre);
        editor.putString(Providers, providerId)
                .putString(Year, year)
                .putString(Rating, rating)
                .apply();
    }

    public String getGenre() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);

        return prefs.getString(Genre, null);
    }

    public String getProviders() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);

        return prefs.getString(Providers, null);
    }

    public String getRating() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);

        return prefs.getString(Rating, null);
    }

    public String getYear() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);

        return prefs.getString(Year, null);
    }

    /**
     * Switch screen to error screen
     * @param fragmentManager fragment manager
     * @param icon icon drawable
     * @param title title of error
     * @param description description of error
     */
    public void showErrorScreen(FragmentManager fragmentManager, int icon, String title, String description) {
        // Put data in bundle
        Bundle bundle = new Bundle();
        bundle.putInt("icon", icon);
        bundle.putString("title", title);
        bundle.putString("description", description);
    
        // Clear back button history
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    
        // Open error screen
        fragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, ErrorFragment.class, bundle)
                .commit();
    }
}
