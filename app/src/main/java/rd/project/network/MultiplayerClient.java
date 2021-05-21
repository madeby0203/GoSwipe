package rd.project.network;

import android.content.Context;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.framing.CloseFrame;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rd.project.Application;
import rd.project.R;
import rd.project.events.MultiplayerEvent;
import rd.project.events.WSClientEvent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerClient implements Multiplayer {
    
    private Context context;
    private WSClient client;
    
    private boolean closed;
    
    private final List<String> playerList = new ArrayList<>();
    
    public MultiplayerClient(Context context, URI uri) {
        this.context = context;
        
        // Open client connection
        client = new WSClient(uri);
        client.addHeader("username", ((Application) context.getApplicationContext()).getUsername());
        client.connect();
        
        // Register events
        EventBus.getDefault().register(this);
    }
    
    @Override
    public void sendMessage(String message) throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        client.send(message);
    }
    
    @Override
    public List<String> getConnectedUsernames() {
        return playerList;
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientMessage(WSClientEvent.Message event) {
        System.out.println("Client message: " + event.getMessage());
        try {
            JSONObject jsonObject = new JSONObject(event.getMessage());
            if (jsonObject.has(MessageParameter.TYPE.toString())) {
                String typeString = jsonObject.getString(MessageParameter.TYPE.toString());
                
                MessageType type = MessageType.valueOf(typeString);
                
                switch (type) {
                    case PLAYER_LIST:
                        JSONArray jsonArray = jsonObject.getJSONArray(MessageParameter.USERLIST.toString());
                        
                        List<String> playerList = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++) {
                            playerList.add(jsonArray.getString(i));
                        }
                        
                        this.playerList.clear();
                        this.playerList.addAll(playerList);
                        
                        EventBus.getDefault().post(new MultiplayerEvent.PlayerList(playerList));
                        
                        break;
                    case PLAYER_JOIN:
                        String username = jsonObject.getString(MessageParameter.USERNAME.toString());
                        EventBus.getDefault().post(new MultiplayerEvent.PlayerJoin(username));
                        
                        break;
                    case PLAYER_LEAVE:
                        username = jsonObject.getString(MessageParameter.USERNAME.toString());
                        EventBus.getDefault().post(new MultiplayerEvent.PlayerLeave(username));
                        
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientClose(WSClientEvent.Close event) {
        System.out.println("Client connection closed: " + event.getReason());
        this.close();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onClientError(WSClientEvent.Error event) {
        System.out.println("Error occurred in client");
        event.getException().printStackTrace();
        this.close();
    }
    
    @Override
    public boolean isClosed() {
        return closed;
    }
    
    @Override
    public void close() {
        if (closed) {
            return;
        }
        
        // Unregister events
        EventBus.getDefault().unregister(this);
        
        // Close client connection
        client.close(CloseFrame.NORMAL, context.getString(R.string.multiplayerClient_close));
    
        closed = true;
    }
}
