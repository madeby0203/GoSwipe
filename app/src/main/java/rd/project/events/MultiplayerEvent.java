package rd.project.events;

import java.util.List;

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
    
}
