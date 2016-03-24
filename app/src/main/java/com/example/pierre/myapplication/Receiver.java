package com.example.pierre.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import android.net.wifi.p2p.WifiP2pManager.Channel;

/**
 * Created by pierre on 24/03/16.
 */
public class Receiver extends BroadcastReceiver{

    private WifiP2pManager manager;
    private Channel channel;
    private MainActivity myActivity;

    private DeviceListFragment listFragment;



    public Receiver(WifiP2pManager _manager, Channel _channel, MainActivity _myActivity){
        this.manager = _manager;
        this.channel = _channel;
        this.myActivity=  _myActivity;

    }

    public void onReceive(Context context, Intent intent)
    {

        Log.w("----------------", "ololololoolol");
        String action = intent.getAction();
        Log.w("++++++++++++++++", action);
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
                Log.w("(((((()))))))))))", "))))))))))((((((((");
            if (manager != null) {
                manager.requestPeers(channel, listFragment);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {



        }
    }
}
