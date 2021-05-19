package rd.project.fragments;

import android.app.Activity;
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
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import rd.project.Application;
import rd.project.R;
import rd.project.adapters.MessagesAdapter;
import rd.project.events.WSClientEvent;
import rd.project.events.WSServerEvent;
import rd.project.network.NetworkServiceDiscovery;
import rd.project.network.WSClient;
import rd.project.network.WSServer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    // List of messages
    private final List<String> messages = new ArrayList<>();
    // Keeps track and updates the list of chat messages in the user interface
    MessagesAdapter adapter;
    
    // Are we the server? If false, we are the client.
    private boolean isServer = false;
    
    private WSServer server;
    private WSClient client;
    
    private NetworkServiceDiscovery nsd;
    
    public ChatFragment() {
        super(R.layout.fragment_chat);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Initialize messages list
        adapter = new MessagesAdapter(messages);
        
        RecyclerView recyclerView = view.findViewById(R.id.messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        
        // Initialize NetworkServiceDiscovery
        // Service will be registered when WebSocket Server has started up
        nsd = new NetworkServiceDiscovery(this.getContext());
        
        // Initialize either client or server
        // Get data from previous fragment
        Bundle bundle = getArguments();
        // If this bundle contains an ip, we are a client, otherwise we are a server
        if (bundle != null && bundle.containsKey("ip")) { // Bundle contains ip, we are a client
            String ip = bundle.getString("ip");
            try {
                URI uri = new URI(ip);
                client = new WSClient(uri);
                client.connect();
                addMessage("System: connecting to " + ip);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else { // Bundle does not contain ip, we are a server
            isServer = true;
            
            server = new WSServer();
            server.start();
        }
        
        // Register onClickListeners
        Button disconnectButton = view.findViewById(R.id.disconnectButton);
        disconnectButton.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Removes the menu from the back button stack
            getParentFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container_view, MenuFragment.class, null)
                .commit();
        });
        
        Button sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(v -> {
            EditText messageField = view.findViewById(R.id.messageField);
            String message = messageField.getText().toString();
            if (isServer) {
                server.broadcast("Host: " + messageField.getText().toString());
                addMessage("Host: " + message);
            } else {
                try {
                    client.send(messageField.getText().toString());
                } catch (WebsocketNotConnectedException e) {
                    addMessage("System: Not connected to server");
                }
            }
            messageField.getText().clear();
        });
        
        // Register events
        EventBus.getDefault().register(this);
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Unregister events
        EventBus.getDefault().unregister(this);
        
        // Unregister NetworkServiceDiscovery
        // If we are a server: the server has stopped so there is no point in broadcasting that we are online anymore
        System.out.println("Attempting to unregister NetworkServiceDiscovery service...");
        nsd.unregisterService();
        
        if (isServer) {
            try {
                // Try to stop the server
                System.out.println("Stopping server...");
                server.stop();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            // Try to close the connection from the client to the server
            System.out.println("Closing client connection...");
            if (client != null) {
                client.close();
            } else {
                System.out.println("Client was null, nothing to close");
            }
        }
    }
    
    /*
     * SERVER EVENTS
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerOpen(WSServerEvent.Open event) {
        addMessage("System: " + event.getWebSocket().getRemoteSocketAddress() + " joined the chat.");
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerClose(WSServerEvent.Close event) {
        addMessage("System: " + event.getWebSocket().getRemoteSocketAddress() + " left the chat.");
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessage(WSServerEvent.Message event) {
        addMessage(event.getWebSocket().getRemoteSocketAddress() + ": " + event.getMessage());
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerError(WSServerEvent.Error event) {
        addMessage("System: Error occurred: " + event.getException().getLocalizedMessage());
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerStart(WSServerEvent.Start event) {
        System.out.println("Attempting to register service with port " + event.getPort());
        nsd.registerService(event.getPort(), ((Application) getContext().getApplicationContext()).getUsername());
        addMessage("System: Server started on port " + event.getPort() + ".");
    }
    
    /*
     * CLIENT EVENTS
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientOpen(WSClientEvent.Open event) {
        addMessage("System: Welcome to the chatroom!");
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientMessage(WSClientEvent.Message event) {
        addMessage(event.getMessage());
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientClose(WSClientEvent.Close event) {
        addMessage("System: Connection closed, reason: " + event.getReason());
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientError(WSClientEvent.Error event) {
        addMessage("System: Error occurred: " + event.getException().getLocalizedMessage());
    }
    
    /**
     * Adds a message to the list of chat messages in the user interface.
     *
     * @param message The message to add
     */
    public void addMessage(String message) {
        messages.add(message);
        if (getContext() != null) {
            ((Activity) getContext()).runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                
                if (getView() != null) {
                    RecyclerView recyclerView = getView().findViewById(R.id.messages);
                    recyclerView.scrollToPosition(messages.size() - 1);
                }
            });
        }
    }
}
