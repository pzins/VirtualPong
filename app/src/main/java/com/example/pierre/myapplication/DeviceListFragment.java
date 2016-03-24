package com.example.pierre.myapplication;

import android.app.ListFragment;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;


/**
 * Created by pierre on 24/03/16.
 */
public class DeviceListFragment extends ListFragment implements PeerListListener{


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.w("*****************", peerList.toString());

    }




}
