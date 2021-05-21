package rd.project.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rd.project.Application;
import rd.project.R;
import rd.project.adapters.PlayerListAdapter;
import rd.project.events.MultiplayerEvent;
import rd.project.events.WSServerEvent;
import rd.project.network.Multiplayer;
import rd.project.network.MultiplayerServer;

import java.util.ArrayList;
import java.util.List;

public class LobbyFragment extends Fragment {

    private final List<String> names = new ArrayList<>();
    // Keeps track and updates the list of players in the user interface
    PlayerListAdapter adapter;

    public LobbyFragment() {
        super(R.layout.fragment_lobby);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        Application application = (Application) getContext().getApplicationContext();
        
        adapter = new PlayerListAdapter(names, getContext());

        RecyclerView recyclerView = view.findViewById(R.id.playerList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        
        // Hide/change specific elements if not host
        if (application.getMultiplayerType() != Multiplayer.Type.HOST) {
            view.findViewById(R.id.startButton).setVisibility(View.GONE);
            view.findViewById(R.id.settingsButton).setVisibility(View.GONE);
            
            ((TextView) view.findViewById(R.id.lobbyJoinWith)).setText("");
            ((TextView) view.findViewById(R.id.lobbyJoinAddress)).setText("");
            
            ((Button) view.findViewById(R.id.lobbyCancelButton)).setText("Leave");
        }
        
        //Cancel and Leave button
        Button cancelButton = view.findViewById(R.id.lobbyCancelButton);
        cancelButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, MenuFragment.class, null)
                    .commit();
        });

        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, SwipeFragment.class, null)
                    .commit();
        });
        
        Button settingsButton = view.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {

        });
        
        // Update join details
        TextView joinAddress = view.findViewById(R.id.lobbyJoinAddress);
        if (application.getMultiplayerType() == Multiplayer.Type.HOST) {
            joinAddress.setText(((MultiplayerServer) application.getMultiplayer()).getJoinAddress());
        }
        
        // Update player list
        names.clear();
        names.addAll(application.getMultiplayer().getConnectedUsernames());
        updatePlayerList();
    
        // Register events
        EventBus.getDefault().register(this);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Unregister events
        EventBus.getDefault().unregister(this);
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMultiplayerPlayerList(MultiplayerEvent.PlayerList event) {
        names.clear();
        names.addAll(event.getPlayerList());
        updatePlayerList();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMultiplayerPlayerJoin(MultiplayerEvent.PlayerJoin event) {
        names.add(event.getUsername());
        updatePlayerList();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMultiplayerPlayerLeave(MultiplayerEvent.PlayerLeave event) {
        names.remove(event.getUsername());
        updatePlayerList();
    }
    
    /**
     * Updates player count and refreshes player list with data from the WebSocket server
     */
    private void updatePlayerList() {
        if (getContext() != null) {
            ((Activity) getContext()).runOnUiThread(() -> {
                // Update RecyclerView
                adapter.notifyDataSetChanged();
    
                if (getView() != null) {
                    RecyclerView recyclerView = getView().findViewById(R.id.playerList);
                    recyclerView.scrollToPosition(names.size() - 1);

                    // Update player count
                    TextView playerCount = getView().findViewById(R.id.playerCount);
                    playerCount.setText(String.valueOf(names.size()));
                }
            });
        }
    }


}
