package rd.project.events;

import java.net.URI;

/**
 * Fired when the user clicks on an item in the list of hosts
 */
public class JoinListClickEvent {
    private final URI uri;
    
    public JoinListClickEvent(URI uri) {
        this.uri = uri;
    }
    
    public URI getURI() {
        return this.uri;
    }
}
