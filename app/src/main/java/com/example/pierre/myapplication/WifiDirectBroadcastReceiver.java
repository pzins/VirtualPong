package com.example.pierre.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pierre on 24/03/16.
 */
public class WifiDirectBroadcastReceiver extends BroadcastReceiver{

    private WifiP2pManager manager;
    private Channel channel;
    private MainActivity myActivity;



    private List peers = new ArrayList();

    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            peers.clear();
            peers.addAll(peerList.getDeviceList());

            // If an AdapterView is backed by this data, notify it
            // of the change.  For instance, if you have a ListView of available
            // peers, trigger an update.
//            ((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
//            if (peers.size() == 0) {
//                Log.d(WiFiDirectActivity.TAG, "No devices found");
//                return;
//            }
        }
    };


    public WifiDirectBroadcastReceiver(WifiP2pManager _manager, Channel _channel, MainActivity _myActivity){
        this.manager = _manager;
        this.channel = _channel;
        this.myActivity=  _myActivity;

    }

    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

    Log.w("àààààààààààà","ààààààààààà");
        if(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                myActivity.setIsWifiP2pEnabled(true);
            }else
            {
                myActivity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
//                MyPeerListListener myListener = new MyPeerListListener();
                manager.requestPeers(channel, peerListListener);
                Log.w(":::::::::::::::", "change peer");
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        }
    }

    public void connect() {
        // Picking the first device found on the network.
        WifiP2pDevice device = (WifiP2pDevice) peers.get(0);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
//                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
//                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
