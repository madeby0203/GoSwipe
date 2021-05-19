package rd.project;

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
            String username = ((Application) getApplicationContext()).getUsername();    // Retrieve username
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
}