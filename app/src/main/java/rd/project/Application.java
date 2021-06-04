package rd.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import rd.project.api.Movie;
import rd.project.api.Settings;
import rd.project.fragments.ErrorFragment;
import rd.project.network.Multiplayer;
import rd.project.network.MultiplayerClient;
import rd.project.network.MultiplayerServer;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class Application extends android.app.Application {
    
    public final Map<Movie, Integer> results = new HashMap<>(); // Integer contains amount of likes
    private Multiplayer multiplayer;
    private Settings settings;
    
    public Multiplayer getMultiplayer() {
        return multiplayer;
    }
    
    public void becomeHost() {
        if (multiplayer != null) {
            multiplayer.close();
        }
        results.clear();
        multiplayer = new MultiplayerServer(getApplicationContext());
    }
    
    public void becomeClient(URI uri) {
        if (multiplayer != null) {
            multiplayer.close();
        }
        results.clear();
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
     *
     * @return username
     */
    public String getUsername() {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        
        return prefs.getString(getString(R.string.username_preference), null);
    }
    
    /**
     * Sets username in SharedPreferences
     *
     * @param name username
     */
    public void setUsername(String name) {
        SharedPreferences prefs = getApplicationContext()
                .getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(getString(R.string.username_preference), name)
                .apply();
    }
    
    public void setLobbyPref(Settings newSettings) {
        settings = newSettings;
    }
    
    public Settings getSettings() {
        return settings;
    }
    
    /**
     * Switch screen to error screen
     *
     * @param fragmentManager fragment manager
     * @param icon            icon drawable
     * @param title           title of error
     * @param description     description of error
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
