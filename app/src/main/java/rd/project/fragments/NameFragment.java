package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import rd.project.Application;
import rd.project.R;

public class NameFragment extends Fragment {
    public NameFragment() {
        super(R.layout.fragment_name);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Application application = (Application) getContext().getApplicationContext();
        
        EditText editTextName = view.findViewById(R.id.editTextName);
        String name = application.getUsername();
        if (name != null) {
            editTextName.setText(name);
        }
        
        // Initialize onClickListeners; each button should switch to the corresponding fragment
        Button nameSaveButton = view.findViewById(R.id.nameSaveButton);
        nameSaveButton.setOnClickListener(v -> {
            String newName = editTextName.getText().toString();
            
            if (newName.equals("")) {
                editTextName.setError(getString(R.string.name_empty_error));
            } else {
                application.setUsername(newName);
                
                getParentFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .setReorderingAllowed(true)
                        .replace(R.id.fragment_container_view, MenuFragment.class, null)
                        .commit();
            }
        });
        
        // Make the enter key on the keyboard press the join button
        editTextName.setOnEditorActionListener((textView, keyCode, keyEvent) -> {
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                nameSaveButton.callOnClick();
                return true;
            }
            return false;
        });
        
        // Make back button return to the main menu if a name has already been set
        OnBackPressedCallback back = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (application.getUsername() != null) {
                    getParentFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .setReorderingAllowed(true)
                            .replace(R.id.fragment_container_view, MenuFragment.class, null)
                            .commit();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), back);
    }
}
