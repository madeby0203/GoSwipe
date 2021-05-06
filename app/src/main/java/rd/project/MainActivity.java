package rd.project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import rd.project.fragments.MenuFragment;

public class MainActivity extends AppCompatActivity {
    
    public MainActivity() {
        super(R.layout.activity_main);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Switch to the initial fragment: the menu fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, MenuFragment.class, null)
                    .commit();
        }
    }
}