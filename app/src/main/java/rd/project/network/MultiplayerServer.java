package rd.project.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rd.project.Application;
import rd.project.api.*;
import rd.project.events.MovieEvent;
import rd.project.events.MultiplayerEvent;
import rd.project.events.WSServerEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class MultiplayerServer implements Multiplayer {
    private final String TAG = "MultiplayerServer";
    
    private Context context;
    private WSServer server;
    private NetworkServiceDiscovery nsd;
    
    private List<Movie> movies;
    
    private boolean closed = false;
    
    public MultiplayerServer(Context context) {
        System.out.println("Starting multiplayer server...");
        
        this.context = context;
    
        // Register events
        EventBus.getDefault().register(this);
        
        // Start WebSocket server
        server = new WSServer();
        server.start();

        // Initialize Network Service Discovery
        nsd = new NetworkServiceDiscovery(context);
    }
    
    @Override
    public void sendMessage(String message) throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        server.broadcast(message);
        System.out.println("Broadcast message: " + message);
    }
    
    public void startDiscoveryBroadcast(int port) throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        nsd.registerService(port, ((Application) context.getApplicationContext()).getUsername());
    }
    
    public void stopDiscoveryBroadcast() throws ClosedException {
        if (closed) {
            throw new ClosedException();
        }
        nsd.unregisterService();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerOpen(WSServerEvent.Open event) {
        // Send playerlist to newly connected client
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.PLAYER_LIST.toString());
            
            JSONArray jsonArray = new JSONArray(getConnectedUsernames());
            jsonObject.put(MessageParameter.USER_LIST.toString(), jsonArray);
        
            event.getWebSocket().send(jsonObject.toString());
            System.out.println("Sending message to new user: " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerError(WSServerEvent.Error event) {
        close();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerStart(WSServerEvent.Start event) {
        System.out.println("Server started on port " + event.getPort() + ".");
        try {
            startDiscoveryBroadcast(event.getPort());
        } catch (ClosedException e) {
            e.printStackTrace();
        }
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
        
        // Stop WebSocket server
        System.out.println("Stopping WebSocket server...");
        try {
            server.stop();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        server = null;
        
        // Stop NetworkServiceDiscovery
        try {
            stopDiscoveryBroadcast();
        } catch (ClosedException e) {
            e.printStackTrace();
        }
        nsd = null;
    
        closed = true;
    }
    
    public List<String> getConnectedUsernames() {
        List<String> usernames = new ArrayList<>();
        usernames.add(((Application) context.getApplicationContext()).getUsername());
        usernames.addAll(server.getConnected().values());
        return usernames;
    }
    
    public String getJoinAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append("ws://");
        
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        sb.append(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        
        sb.append(":");
        
        sb.append(server.getPort());
        
        return sb.toString();
    }
    
    /**
     * Send message to all clients to disable buttons in lobby screen, and disables new players from joining.
     */
    public void startPrepare() {
        // Prevent new players from joining
        server.allowJoining = false;
        
        // Send START_PREPARE to all clients
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.START_PREPARE.toString());
            server.broadcast(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Send message to own lobby
        EventBus.getDefault().post(new MultiplayerEvent.StartPrepare());
    }
    
    /**
     * Send message to all clients to start the lobby countdown.
     */
    public void startCountdown() {
        // Send COUNTDOWN_START to all clients
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.START_COUNTDOWN.toString());
            server.broadcast(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Send message to own lobby
        EventBus.getDefault().post(new MultiplayerEvent.StartCountdown());
    }
    
    /**
     * Fetch list of movies, store it and send it to all connected clients.
     */
    public void fetchMovies() {
        Log.d(TAG, "Fetching movies...");
        long startTime = System.currentTimeMillis();
        
        movies = null;
        
        Thread t = new Thread(() -> {
            String api = "a443e45153a06c5830898cf8889fa27e";
            String region = "NL";
            String providers = Providers.Videoland.getId();
            Date release = new Date();
            //release.setTime();
            String releaseDate = "2020-01-01T00:00:00.000Z";
            int minVote = 8;
//            StringBuilder genres = new StringBuilder();
//            genres.append(Genres.Action.getId());
            String genres = "";
        
            RequestType request = null;
            try {
                request = new DiscoverMovies(api, region, providers, "", releaseDate, minVote);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Error fetching movies.");
                e.printStackTrace();
                if (!closed) {
                    EventBus.getDefault().post(new MovieEvent.FetchError());
                }
                return;
            }
            Connect discover = new Connect(request);
            org.json.simple.JSONObject test = discover.Send();
            
            request.UpdateData(test);
            ArrayList<Movie> movies = request.GetData();
            
            Log.d(TAG, "Fetched movies, size: " + movies.size());
            Log.d(TAG, "Fetching took " + (System.currentTimeMillis() - startTime));
            
            if (!closed) {
                EventBus.getDefault().post(new MovieEvent.FetchSuccess(movies));
            }
        });
        t.start();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMovieFetchSuccess(MovieEvent.FetchSuccess event) {
        // Store movie list
        this.movies = event.getMovies();
        
        // Send movie list to clients
        try {
            JSONArray moviesJSONArray = new JSONArray();
            for(Movie movie : movies) {
                JSONObject movieJSON = new JSONObject();
                movieJSON.put("overview", movie.getOverview());
                movieJSON.put("title", movie.getTitle());
                movieJSON.put("poster", movie.getPoster());
                movieJSON.put("vote", movie.getVote());
                movieJSON.put("id", movie.getId());
                movieJSON.put("year", movie.getYear());
                movieJSON.put("genre", movie.getGenre());
                movieJSON.put("platform", movie.getPlatform());
                moviesJSONArray.put(movieJSON);
            }
        
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.MOVIE_LIST.toString());
            jsonObject.put(MessageParameter.MOVIE_LIST.toString(), moviesJSONArray);
            
            server.broadcast(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        startCountdown(); // Start lobby countdown
    }
    
    public List<Movie> getMovies() {
        return movies;
    }
    
    @Override
    public void saveLikes(List<Integer> movieIDs) {
        // TODO implement
    }
}
