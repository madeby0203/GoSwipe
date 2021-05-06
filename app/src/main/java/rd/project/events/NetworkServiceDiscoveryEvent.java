package rd.project.events;

import android.net.nsd.NsdServiceInfo;

/**
 * Fired when NetworkServiceDiscovery detects or loses a service.
 */
public class NetworkServiceDiscoveryEvent {
    private final NsdServiceInfo serviceInfo;
    private final boolean online;
    
    public NetworkServiceDiscoveryEvent(NsdServiceInfo serviceInfo, boolean online) {
        this.serviceInfo = serviceInfo;
        this.online = online;
    }
    
    public NsdServiceInfo getServiceInfo() {
        return this.serviceInfo;
    }
    
    /**
     * Returns if the service is online.
     *
     * @return True if the service was discovered, false if the service was lost.
     */
    public boolean isOnline() {
        return this.online;
    }
}
