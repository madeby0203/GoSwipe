package rd.project.network;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import androidx.fragment.app.FragmentTransaction;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import rd.project.Application;
import rd.project.MainActivity;
import rd.project.R;
import rd.project.Settings;
import rd.project.api.*;
import rd.project.events.MovieEvent;
import rd.project.events.MultiplayerEvent;
import rd.project.events.WSServerEvent;
import rd.project.fragments.ResultsFragment;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class MultiplayerServer implements Multiplayer {
    private final String TAG = "MultiplayerServer";
    
    private Context context;
    private WSServer server;
    private NetworkServiceDiscovery nsd;
    
    private List<Movie> movies;
    private final Map<String, List<Integer>> likes = new HashMap<>();
    
    // Used on the results screen, if everyone is disconnected we want to stop the server
    private boolean stopIfEveryoneDisconnected = false;
    
    private boolean closed = false;
    
    public MultiplayerServer(Context context) {
        Log.i(TAG, "Starting multiplayer server...");
        
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
        Log.d(TAG, "Broadcast message: " + message);
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
            Log.d(TAG, "Sending message to new user: " + jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerLeave(MultiplayerEvent.PlayerLeave event) {
        // Remove player from results
        likes.remove(event.getUsername());
    
        try {
            JSONObject json = new JSONObject();
            json.put(MessageParameter.TYPE.toString(), MessageType.RESULTS_COMPLETED_AMOUNT.toString());
            json.put(MessageParameter.AMOUNT.toString(), getResultsCompletedAmount());
            server.broadcast(json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        EventBus.getDefault().post(new MultiplayerEvent.ResultsCompletedCountUpdate(getResultsCompletedAmount()));
        
        // If we are on the results screen and everyone has disconnected, stop the server
        if (stopIfEveryoneDisconnected && server.getConnections().size() == 0) {
            close();
        }
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerMessage(WSServerEvent.Message event) {
        try {
            JSONObject jsonObject = new JSONObject(event.getMessage());
            if (jsonObject.has(MessageParameter.TYPE.toString())) {
                String typeString = jsonObject.getString(MessageParameter.TYPE.toString());
            
                MessageType type = MessageType.valueOf(typeString);
                String username = server.getConnected().get(event.getWebSocket());
            
                switch (type) {
                    case LIKES_SAVE:
                        JSONArray jsonArray = jsonObject.getJSONArray(MessageParameter.LIKES_LIST.toString());
                    
                        List<Integer> liked = new ArrayList<>();
                        for(int i = 0; i < jsonArray.length(); i++) {
                            liked.add(jsonArray.getInt(i));
                        }
                    
                        saveLikes(username, liked);
                        
                        // Broadcast updated amount to clients
                        JSONObject json = new JSONObject();
                        json.put(MessageParameter.TYPE.toString(), MessageType.RESULTS_COMPLETED_AMOUNT.toString());
                        json.put(MessageParameter.AMOUNT.toString(), getResultsCompletedAmount());
                        server.broadcast(json.toString());
                    
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
    public void onServerError(WSServerEvent.Error event) {
        close();
    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onServerStart(WSServerEvent.Start event) {
        Log.i(TAG, "Server started on port " + event.getPort() + ".");
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
        
        Log.i(TAG, "Closing...");
        
        // Unregister events
        EventBus.getDefault().unregister(this);
        
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
     * Send message to all clients to disable buttons in lobby screen, and disables new players from joining.
     */
    public void cancelPrepare() {
        // Allow new players to join
        server.allowJoining = true;
        
        // Send START_PREPARE to all clients
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.CANCEL_PREPARE.toString());
            server.broadcast(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        // Send message to own lobby
        EventBus.getDefault().post(new MultiplayerEvent.CancelPrepare());
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
            String providers = Providers.Netflix.getId();
            Date release = new Date();
            String releaseDate = "2020-01-01T00:00:00.000Z";
            int minVote = 6;
            String genres = Genres.Action.getId();

            Application application = (Application) context.getApplicationContext();

            Settings settings = application.getSettings();
            providers = settings.getProvider();
            minVote = 5;
            if(settings.getRating() != "") {
                minVote = Integer.parseInt(settings.getRating());
            }
            genres = settings.getGenre();
            releaseDate = settings.getYear() + "-01-01T00:00:00.000Z";
            Log.d(TAG, "Minvote: " + minVote);
            Log.d(TAG, "providers: " + providers);
            Log.d(TAG, "genres: " + genres);

            RequestType request = null;
            try {
                request = new DiscoverMovies(api, region, providers, genres, releaseDate, minVote);
            } catch (MalformedURLException e) {
                Log.e(TAG, "Error fetching movies.");
                e.printStackTrace();
                if (!closed) {
                    EventBus.getDefault().post(new MovieEvent.FetchError());
                }
                return;
            }
            Log.d(TAG, "URL: " + request.GetUrl().toString());
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
        saveLikes(((Application) context.getApplicationContext()).getUsername(), movieIDs);
    }
    
    @Override
    public void saveLikes(String username, List<Integer> movieIDs) {
        likes.put(username, movieIDs);
    
        // Send an event so other parts of the app can update the player completed counter
        EventBus.getDefault().post(new MultiplayerEvent.ResultsCompletedCountUpdate(getResultsCompletedAmount()));
    
        // Check if every player has finished swiping
        // If so, compute the results and send them to the results screen
        if(likes.size() == getConnectedUsernames().size()) {
            computeResults();
        }
    }
    
    @Override
    public int getResultsCompletedAmount() {
        return likes.size();
    }
    
    /**
     * Compute results and send them to all connected clients.
     */
    public void computeResults() {
        Map<Integer, Integer> likedIDs = new HashMap<>(); // Movie ID, amount of likes
        
        for (List<Integer> userLiked : likes.values()) {
            for (int id : userLiked) {
                likedIDs.put(id, likedIDs.getOrDefault(id, 0) + 1);
            }
        }
        
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(MessageParameter.TYPE.toString(), MessageType.RESULTS.toString());
            
            JSONObject results = new JSONObject();
            for(int id : likedIDs.keySet()) {
                results.put(String.valueOf(id), likedIDs.get(id));
            }
            jsonObject.put(MessageParameter.RESULTS_LIST.toString(), results);
            
            server.broadcast(jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        ((Application) context).results.clear();
        ((Application) context).results.putAll(this.convertLikedIDsToLikedMovies(movies, likedIDs));
        
        EventBus.getDefault().post(new MultiplayerEvent.Results());
    
        stopIfEveryoneDisconnected = true;
    }
}
