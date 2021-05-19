package rd.project;

import android.content.Context;
import android.content.SharedPreferences;

public class Application extends android.app.Application {
    
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
