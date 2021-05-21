package rd.project.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rd.project.MainActivity;
import rd.project.R;
import rd.project.adapters.JoinAdapter;
import rd.project.events.JoinListClickEvent;
import rd.project.events.NetworkServiceDiscoveryEvent;
import rd.project.network.NetworkServiceDiscovery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class JoinFragment extends Fragment {
    // List of found servers
    private final List<JoinAdapter.ServerItem> servers = new ArrayList<>();
    // Keeps track and updates the host list in the user interface
    JoinAdapter adapter;
    private NetworkServiceDiscovery nsd;
    
    public JoinFragment() {
        super(R.layout.fragment_join);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Initialize join list adapter
        adapter = new JoinAdapter(servers);
        
        RecyclerView recyclerView = view.findViewById(R.id.serverList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        
        // Initialize and start NetworkServiceDiscovery
        // Used for detecting hosts
        nsd = new NetworkServiceDiscovery(this.getContext());
        nsd.discoverServices();
        
        // Initialize onClickListener
        Button manualJoinButton = view.findViewById(R.id.manualJoinButton);
        manualJoinButton.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showProgressDialog();
            
            EditText ipField = view.findViewById(R.id.editTextIP);
            Bundle bundle = new Bundle();
            bundle.putString("ip", ipField.getText().toString());
            getParentFragmentManager().beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .setReorderingAllowed(true)
                    .replace(R.id.fragment_container_view, ChatFragment.class, bundle)
                    .commit();
        });
        
        // Register events
        EventBus.getDefault().register(this);
    }
    
    @Override
    public void onDestroyView() {
        // onDestroyView is called when the fragment is exited
        super.onDestroyView();
        
        // Stop discovering new hosts; we aren't in the join fragment anymore
        nsd.stopDiscovering();
        
        // Unregister events
        EventBus.getDefault().unregister(this);
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJoinListClick(JoinListClickEvent event) {
        Bundle bundle = new Bundle();
        bundle.putString("ip", event.getURI().toString());
        getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, ChatFragment.class, bundle)
                .commit();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNsdEvent(NetworkServiceDiscoveryEvent event) {
        String host = "ws:/" + event.getServiceInfo().getHost() + ":" + event.getServiceInfo().getPort();
        try {
            URI uri = new URI(host);
            String name = new String(event.getServiceInfo().getAttributes().get("name"));
            JoinAdapter.ServerItem serverItem = new JoinAdapter.ServerItem(uri, name);
    
            // Was the service found or lost?
            if (event.isOnline()) { // found; add to the list
                servers.add(serverItem);
            } else { // lost; remove from the list
                List<JoinAdapter.ServerItem> remove = new ArrayList<>();
                for (JoinAdapter.ServerItem item : servers) {
                    if (item.getHost().equals(uri)) {
                        remove.add(item);
                    }
                }
                servers.removeAll(remove);
            }
    
            updateServerList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Refreshes server list with data from list servers
     */
    private void updateServerList() {
        if (getContext() != null) {
            ((Activity) getContext()).runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                
                if (getView() != null) {
                    RecyclerView recyclerView = getView().findViewById(R.id.serverList);
                    recyclerView.scrollToPosition(servers.size() - 1);
                }
            });
        }
    }
}
