package rd.project.network;

import android.content.Context;
import android.util.Log;
import androidx.annotation.Nullable;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.java_websocket.framing.CloseFrame;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rd.project.Application;
import rd.project.R;
import rd.project.api.Movie;
import rd.project.events.MultiplayerEvent;
import rd.project.events.WSClientEvent;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MultiplayerClient implements Multiplayer {
    private final String TAG = "MultiplayerClient";
    
    private Context context;
    private WSClient client;
    
    private final List<String> playerList = new ArrayList<>();
    private List<Movie> movies;
    private int resultsCompletedAmount = 0;
    
    private boolean closed;
    
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
        try {
            JSONObject jsonObject = new JSONObject(event.getMessage());
            if (jsonObject.has(MessageParameter.TYPE.toString())) {
                String typeString = jsonObject.getString(MessageParameter.TYPE.toString());
                
                MessageType type = MessageType.valueOf(typeString);
                
                switch (type) {
                    case PLAYER_LIST:
                        JSONArray jsonArray = jsonObject.getJSONArray(MessageParameter.USER_LIST.toString());
                        
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
                    case START_PREPARE:
                        EventBus.getDefault().post(new MultiplayerEvent.StartPrepare());
                        
                        break;
                    case START_COUNTDOWN:
                        EventBus.getDefault().post(new MultiplayerEvent.StartCountdown());
        
                        break;
                    case MOVIE_LIST:
                        Log.v(TAG, "Received movie list.");
                        
                        JSONArray moviesJSONArray = jsonObject.getJSONArray(MessageParameter.MOVIE_LIST.toString());
                        this.movies = new ArrayList<>();
                        for(int i = 0; i < moviesJSONArray.length(); i++) {
                            JSONObject movieJSON = moviesJSONArray.getJSONObject(i);
                            String overview = movieJSON.getString("overview");
                            String title = movieJSON.getString("title");
                            String poster = movieJSON.getString("poster");
                            Number vote = (Number) movieJSON.get("vote");
                            int id = movieJSON.getInt("id");
                            String year = movieJSON.getString("year");
                            String genre = movieJSON.has("genre") ? movieJSON.getString("genre") : null;
                            String platform = movieJSON.getString("platform");
                            Movie movie = new Movie(overview, title, poster, vote, id, year, genre, platform);
                            this.movies.add(movie);
                        }
                        
                        Log.v(TAG, "Movie list size: " + movies.size());
                        
                        break;
                    case RESULTS_COMPLETED_AMOUNT:
                        int amount = jsonObject.getInt(MessageParameter.AMOUNT.toString());
                        this.resultsCompletedAmount = amount;
                        
                        EventBus.getDefault().post(new MultiplayerEvent.ResultsCompletedCountUpdate(amount));
                        
                        break;
                    default:
                        Log.w(TAG, "Unknown message type received.");
                        
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
    
    @Override
    public List<Movie> getMovies() {
        return movies; //TODO movie sending
    }
    
    @Override
    public void saveLikes(List<Integer> movieIDs) {
        saveLikes(null, movieIDs);
    }
    
    @Override
    public void saveLikes(String username, List<Integer> movieIDs) {
        try {
            JSONArray jsonArray = new JSONArray();
            for(int id : movieIDs) {
                jsonArray.put(id);
            }
            
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.LIKES_SAVE.toString());
            jsonObject.put(MessageParameter.LIKES_LIST.toString(), jsonArray);
            
            client.send(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public int getResultsCompletedAmount() {
        return resultsCompletedAmount;
    }
}
