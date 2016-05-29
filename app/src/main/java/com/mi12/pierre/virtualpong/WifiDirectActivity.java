package com.mi12.pierre.virtualpong;

import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by pierre on 26/05/16.
 */
interface DeviceActionListener{
    void showDetails(WifiP2pDevice device);
    void cancelDisconnect();
    void connect(WifiP2pConfig config);
    void disconnect();
}
public abstract class WifiDirectActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener, WifiP2pManager.ConnectionInfoListener, DeviceActionListener
{

    public abstract void setIsWifiP2pEnabled(boolean state);
    public abstract void resetData();

}
