package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.Application;
import rd.project.R;

public class ErrorFragment extends Fragment {


    public ErrorFragment() {
        super(R.layout.fragment_error);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("icon")) {
                ImageView icon = view.findViewById(R.id.errorIcon);
                icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        bundle.getInt("icon"),
                        getActivity().getTheme()));
            }
            if (bundle.containsKey("title")) {
                TextView title = view.findViewById(R.id.errorTitle);
                title.setText(bundle.getString("title"));
            }
            if (bundle.containsKey("description")) {
                TextView description = view.findViewById(R.id.errorDescription);
                description.setText(bundle.getString("description"));
            }
        }
        
        // Initialize onClickListeners; each button should switch to the corresponding fragment
        Button mainMenuButton = (Button) view.findViewById(R.id.mainMenuButton);
        mainMenuButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, MenuFragment.class, null)
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
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), back);
    }


}
