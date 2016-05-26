package com.mi12.pierre.virtualpong;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mi12.R;

import java.util.ArrayList;
import java.util.Iterator;

public class WifiDirect3Activity extends WifiDirectActivity {
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;


    private ProgressDialog progressDialog = null;


    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<WifiP2pDevice> list = new ArrayList<WifiP2pDevice>();

    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<WifiP2pDevice> adapter;

    private String currentAdr = "";

    public void setIsWifiP2pEnabled(boolean state) {
        isWifiP2pEnabled = state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        /** Defining the ArrayAdapter to set items to ListView */
        adapter = new ArrayAdapter<WifiP2pDevice>(this, android.R.layout.simple_list_item_single_choice, list);
        ListView lv = ((ListView) findViewById(R.id.listView));
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE); // Enables single selection
        lv.setClickable(true);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                WifiP2pDevice selectedDevice = (WifiP2pDevice) myAdapter.getItemAtPosition(myItemInt);
                currentAdr = selectedDevice.deviceAddress;
            }
        });

        Button bt_rejoindre = (Button) findViewById(R.id.rejoindre);
        bt_rejoindre.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      WifiP2pConfig config = new WifiP2pConfig();
                                      config.deviceAddress = currentAdr;
                                      config.wps.setup = WpsInfo.PBC;
                                      config.groupOwnerIntent = 0; // I want this device to become the owner

                                      if (progressDialog != null && progressDialog.isShowing()) {
                                          progressDialog.dismiss();
                                      }
                                      progressDialog = ProgressDialog.show(WifiDirect3Activity.this, "Press back to cancel",
                                              "Connecting to :" + config.deviceAddress, true, true
                                      );
                                      connect(config);
                                  }
                              }
        );

        Button bt_creer = (Button) findViewById(R.id.creer);
        bt_creer.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      Intent intent = new Intent(WifiDirect3Activity.this, DrawActivityScreen.class);
                                      startActivity(intent);
                                  }
                              }
        );
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        if (!isWifiP2pEnabled) {
            Toast.makeText(WifiDirect3Activity.this, "P2P Wifi is not enabled",
                    Toast.LENGTH_SHORT).show();
        }
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WifiDirect3Activity.this, "Discovery initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiDirect3Activity.this, "Discovery failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
        /** Setting the adapter to the ListView */
    }

    @Override
    public void showDetails(WifiP2pDevice device) {

    }

    @Override
    public void cancelDisconnect() {

    }

    public void connect(WifiP2pConfig config)
    {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //WifiBroadcastReceiver will notify us
                Log.w("FEKIR ", "FEKIR");
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiDirect3Activity.this, "Connection failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {

    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    protected void onResume() {
        super.onResume();
//        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        adapter.clear();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Iterator<WifiP2pDevice> i = peers.getDeviceList().iterator();
        while (i.hasNext()) {
            WifiP2pDevice device = i.next();
            list.add(device);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        Intent intent = new Intent(WifiDirect3Activity.this, DrawActivityClient.class);
        Bundle b = new Bundle();
        b.putSerializable("ip", info.groupOwnerAddress);
        b.putInt("port", 8988);
        intent.putExtras(b);
        startActivity(intent);
    }


}




