package rd.project.events;

/**
 * Fired when the user clicks on an item in the list of hosts
 */
public class JoinListClickEvent {
    private final String ip;
    
    public JoinListClickEvent(String ip) {
        this.ip = ip;
    }
    
    public String getIp() {
        return this.ip;
    }
}
