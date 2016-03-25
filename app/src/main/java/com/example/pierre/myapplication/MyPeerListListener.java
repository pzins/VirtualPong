package com.example.pierre.myapplication;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pierre on 24/03/16.
 */
public class MyPeerListListener implements WifiP2pManager.PeerListListener {

    WifiP2pDevice mDevice;
    private List peers = new ArrayList();



    public WifiP2pDevice getWifiP2pDevice()
    {
        return mDevice;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        // print all peers
        //mDevice = peers.getDeviceList().iterator().next();
        this.peers.clear();
        this.peers.addAll(peers.getDeviceList());

        for (WifiP2pDevice device : peers.getDeviceList()) {
            Log.w("---------", device.deviceName);
        }
    }

}
