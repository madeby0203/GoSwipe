package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.R;

public class MenuFragment extends Fragment {
    public MenuFragment() {
        super(R.layout.fragment_menu);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Initialize onClickListeners; each button should switch to the corresponding fragment
        Button hostButton = view.findViewById(R.id.hostButton);
        hostButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                .replace(R.id.fragment_container_view, SetupFragment.class, null)
                .commit());
        
        Button joinButton = view.findViewById(R.id.joinButton);
        joinButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                .replace(R.id.fragment_container_view, JoinFragment.class, null)
                .commit());
    }
}
