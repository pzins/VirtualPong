package com.example.pierre.myapplication;

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
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private WifiP2pDevice device;



    public WifiP2pDevice getWifiP2pDevice()

    {
        return mDevice;
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peersList) {
        peers.clear();
        peers.addAll(peersList.getDeviceList());
        if(peers.size() == 0)
        {
            Log.d(MainActivity.TAG, "No devices found");
            return;
        }

    }

}
