package com.example.pierre.myapplication;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import android.net.wifi.p2p.WifiP2pManager.Channel;

/**
 * Created by pierre on 24/03/16.
 */
public class Receiver extends BroadcastReceiver{

    WifiP2pManager manager;
    Channel channel;
    MainActivity myActivity;




    public Receiver(WifiP2pManager _manager, Channel _channel, MainActivity _myActivity){
        this.manager = _manager;
        this.channel = _channel;
        this.myActivity=  _myActivity;
    }

    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();
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

            // The peer list has changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {



        }
    }
}
