package rd.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import rd.project.fragments.MenuFragment;
import rd.project.fragments.NameFragment;

public class MainActivity extends AppCompatActivity {
    
    public MainActivity() {
        super(R.layout.activity_main);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        new Swipe();
        // Switch to the initial fragment: the menu fragment
        if (savedInstanceState == null) {
            String username = getUsername();    // Retrieve username
            if(username == null) {  // Username not set, ask user for one
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_view, NameFragment.class, null)
                        .commit();
            } else {    // Username set, go to main menu
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.fragment_container_view, MenuFragment.class, null)
                        .commit();
            }
        }
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
}