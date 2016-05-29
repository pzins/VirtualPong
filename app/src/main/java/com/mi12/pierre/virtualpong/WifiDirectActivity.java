package com.mi12.pierre.virtualpong;

import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;


public abstract class WifiDirectActivity extends AppCompatActivity
        implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener
{

    public abstract void setIsWifiP2pEnabled(boolean state);
    public abstract void resetData();

}
