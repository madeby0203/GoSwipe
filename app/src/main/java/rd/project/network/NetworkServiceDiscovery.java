package rd.project.network;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import org.greenrobot.eventbus.EventBus;
import rd.project.events.NetworkServiceDiscoveryEvent;

public class NetworkServiceDiscovery {
    // Based on https://developer.android.com/training/connect-devices-wirelessly/nsd
    
    private final String TAG = "NetworkServiceDiscovery";
    
    private final String SERVICE_NAME = "RDProject";
    private final String SERVICE_TYPE = "_ws._tcp.";
    
    private final NsdManager nsdManager;
    
    private NsdManager.RegistrationListener registrationListener;
    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;
    
    // true if the last service was found, false if the last service was lost
    private boolean lastServiceOnline = true;
    
    public NetworkServiceDiscovery(Context context) {
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        initializeResolveListener();
    }
    
    public void initializeRegistrationListener() {
        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Service registration failed, error code " + errorCode);
            }
            
            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Service unregistration failed, error code " + errorCode);
            }
            
            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service registered");
            }
            
            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Service unregistered");
            }
        };
    }
    
    public void registerService(int port, String name) {
        unregisterService();
        
        initializeRegistrationListener();
        
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(port);
        serviceInfo.setAttribute("name", name);
        
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }
    
    public void unregisterService() {
        if (registrationListener != null) {
            nsdManager.unregisterService(registrationListener);
            registrationListener = null;
        }
    }
    
    public void initializeDiscoveryListener() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            
            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.i(TAG, "Started discovery: " + serviceType);
            }
            
            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Stopped discovery: " + serviceType);
            }
            
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Starting discovery failed: " + serviceType + ", error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
            
            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Stopping discovery failed: " + serviceType + ", error code: " + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
            
            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service found, resolving...");
                if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                    lastServiceOnline = true;
                    nsdManager.resolveService(serviceInfo, resolveListener);
                }
            }
            
            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service lost, resolving...");
                lastServiceOnline = false;
                nsdManager.resolveService(serviceInfo, resolveListener);
            }
        };
    }
    
    public void initializeResolveListener() {
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                EventBus.getDefault().post(new NetworkServiceDiscoveryEvent(serviceInfo, lastServiceOnline));
            }
            
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.w(TAG, "Couldn't resolve service:  " + serviceInfo + ", Error code: " + errorCode);
            }
        };
    }
    
    public void discoverServices() {
        stopDiscovering();
        
        initializeDiscoveryListener();
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }
    
    public void stopDiscovering() {
        if (discoveryListener != null) {
            nsdManager.stopServiceDiscovery(discoveryListener);
            discoveryListener = null;
        }
    }
}
