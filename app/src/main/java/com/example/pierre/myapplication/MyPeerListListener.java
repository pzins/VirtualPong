package com.example.pierre.myapplication;

import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by pierre on 24/03/16.
 */
public class MyPeerListListener implements WifiP2pManager.PeerListListener {


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        // print all peers
        for (WifiP2pDevice device : peers.getDeviceList()) {
            Log.w("............", device.deviceName);

        }
    }
}
