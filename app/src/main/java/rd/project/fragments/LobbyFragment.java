package rd.project.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import rd.project.R;
import rd.project.adapters.MessagesAdapter;

import java.util.ArrayList;
import java.util.List;

public class LobbyFragment extends Fragment {

    private final List<String> names = new ArrayList<>();
    // Keeps track and updates the list of chat messages in the user interface
    MessagesAdapter adapter;

    public LobbyFragment() {
        super(R.layout.fragment_lobby);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        adapter = new MessagesAdapter(names);

        RecyclerView recyclerView = view.findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        Button disconnectButton = view.findViewById(R.id.startButton);
        disconnectButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Removes the menu from the back button stack
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, placeholderFragment.class, null)
                    .commit();
        });
    }


}
