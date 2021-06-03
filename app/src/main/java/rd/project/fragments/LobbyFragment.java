package rd.project.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rd.project.Application;
import rd.project.MainActivity;
import rd.project.R;
import rd.project.adapters.PlayerListAdapter;
import rd.project.events.MultiplayerEvent;
import rd.project.network.Multiplayer;
import rd.project.network.MultiplayerServer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LobbyFragment extends Fragment {

    private final List<String> names = new ArrayList<>();
    // Keeps track and updates the list of players in the user interface
    PlayerListAdapter adapter;
    // Keeps track of if the lobby is currently starting
    private boolean starting;

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
            view.findViewById(R.id.startButton).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.settingsButton).setVisibility(View.INVISIBLE);
            
            view.findViewById(R.id.lobbyJoinWith).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.lobbyJoinAddress).setVisibility(View.INVISIBLE);
            
            ((Button) view.findViewById(R.id.lobbyCancelButton)).setText(getString(R.string.lobby_leave));
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
            ((MultiplayerServer) application.getMultiplayer()).startPrepare();
            ((MultiplayerServer) application.getMultiplayer()).fetchMovies();
        });
        
        Button settingsButton = view.findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                    .replace(R.id.fragment_container_view, SetupFragment.class, null)
                    .commit();
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


        OnBackPressedCallback back = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!starting) {
                    getParentFragmentManager().beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .setReorderingAllowed(true)
                            .addToBackStack(null) // Pressing the back button in the next fragments makes it return to this one
                            .replace(R.id.fragment_container_view, MenuFragment.class, null)
                            .commit();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), back);
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
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrepareStart(MultiplayerEvent.StartPrepare event) {
        starting = true;
        
        // Disable buttons
        getView().findViewById(R.id.lobbyCancelButton).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.startButton).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.settingsButton).setVisibility(View.INVISIBLE);
        
        // Hide text on top
        getView().findViewById(R.id.lobbyJoinWith).setVisibility(View.INVISIBLE);
        getView().findViewById(R.id.lobbyJoinAddress).setVisibility(View.INVISIBLE);
        
        // Show loading spinner
        getView().findViewById(R.id.lobbyLoading).setVisibility(View.VISIBLE);
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCancelStart(MultiplayerEvent.StartPrepare event) {
        starting = false;
        
        // Enable buttons
        getView().findViewById(R.id.lobbyCancelButton).setVisibility(View.VISIBLE);
        
        if (((Application) getContext().getApplicationContext()).getMultiplayerType() == Multiplayer.Type.HOST) {
            getView().findViewById(R.id.startButton).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.settingsButton).setVisibility(View.VISIBLE);
    
            // Show text on top
            getView().findViewById(R.id.lobbyJoinWith).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.lobbyJoinAddress).setVisibility(View.VISIBLE);
        }
        
        // Hide loading spinner
        getView().findViewById(R.id.lobbyLoading).setVisibility(View.INVISIBLE);
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startCountdown(MultiplayerEvent.StartCountdown event) {
        // Hide loading spinner
        getView().findViewById(R.id.lobbyLoading).setVisibility(View.INVISIBLE);
        
        // Prepare counter
        AtomicInteger counter = new AtomicInteger(3);
        
        // Show countdown
        TextView countdownText = getView().findViewById(R.id.lobbyCountdown);
        countdownText.setVisibility(View.VISIBLE);
        
        Thread thread = new Thread(() -> {
            try {
                while (counter.intValue() >= 0) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            if (counter.intValue() > 0) { // Counter is higher than 0, update counter text
                                countdownText.setText(String.valueOf(counter.get()));
                            } else { // Counter is 0, go to SwipeFragment
                                getParentFragmentManager().beginTransaction()
                                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                        .setReorderingAllowed(true)
                                        .replace(R.id.fragment_container_view, SwipeFragment.class, null)
                                        .commit();
                            }
                        });
                    }
                    
                    // Wait 1 second
                    TimeUnit.SECONDS.sleep(1);
    
                    // Decrement counter by 1
                    counter.getAndDecrement();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
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
