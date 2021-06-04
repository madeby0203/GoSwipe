package rd.project.events;

import java.util.List;

@SuppressWarnings("unused")
public class MultiplayerEvent {
    
    /**
     * Fired when the client receives the player list from the host
     */
    public static class PlayerList {
        private final List<String> playerList;
        
        public PlayerList(List<String> playerList) {
            this.playerList = playerList;
        }
        
        public List<String> getPlayerList() {
            return this.playerList;
        }
    }
    
    /**
     * Fired when a player joins the host
     */
    public static class PlayerJoin {
        private final String username;
        
        public PlayerJoin(String username) {
            this.username = username;
        }
        
        public String getUsername() {
            return this.username;
        }
    }
    
    /**
     * Fired when a player leaves the host
     */
    public static class PlayerLeave {
        private final String username;
        
        public PlayerLeave(String username) {
            this.username = username;
        }
        
        public String getUsername() {
            return this.username;
        }
    }
    
    /**
     * Fired when a session is about to start.
     */
    public static class StartPrepare {
    
    }
    
    /**
     * Fired when a session's starting preparations are cancelled.
     * This happens for example when there aren't enough movies available with the specified filters.
     */
    public static class CancelPrepare {
    
    }
    
    /**
     * Fired when the lobby countdown is started.
     */
    public static class StartCountdown {
    
    }
    
    /**
     * Fired when the swipe section completion amount is updated.
     */
    public static class ResultsCompletedCountUpdate {
        int completed;
        
        public ResultsCompletedCountUpdate(int completed) {
            this.completed = completed;
        }
        
        public int getCompleted() {
            return completed;
        }
    }
    
    /**
     * Fired when the results are complete.
     */
    public static class Results {
    
    }
}
