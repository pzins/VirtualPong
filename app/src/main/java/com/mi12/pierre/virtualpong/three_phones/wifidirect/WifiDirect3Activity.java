package com.mi12.pierre.virtualpong.three_phones.wifidirect;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.mi12.R;
import com.mi12.pierre.virtualpong.WifiDirectActivity;
import com.mi12.pierre.virtualpong.WifiDirectBroadcastReceiver;
import com.mi12.pierre.virtualpong.three_phones.DrawActivityController;
import com.mi12.pierre.virtualpong.three_phones.DrawActivityScreen;
import java.util.ArrayList;
import java.util.Iterator;

public class WifiDirect3Activity extends WifiDirectActivity {

    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel channel;
    private BroadcastReceiver receiver = null;


    private ProgressDialog progressDialog = null;

    //to know if the user clicked on the button start or join
    //prevent game launched directly if already connected in wifidirect (WifiDirectBroadcastReceiver)
    private boolean isStart;
    //
    private int nbPlayer;

    //for ListView
    /** Items entered by the user is stored in this ArrayList variable */
    ArrayList<WifiP2pDevice> list = new ArrayList<WifiP2pDevice>();
    /** Declaring an ArrayAdapter to set items to ListView */
    ArrayAdapter<WifiP2pDevice> adapter;


    private String currentAdr;

    public void setIsWifiP2pEnabled(boolean state) {
        isWifiP2pEnabled = state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_direct3);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        isStart = false;
        nbPlayer = 0;

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
                ((Button) findViewById(R.id.join)).setEnabled(true);
            }
        });

        final Button bt_join = (Button) findViewById(R.id.join);
        bt_join.setEnabled(false);
        bt_join.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = currentAdr;
                        config.wps.setup = WpsInfo.PBC;
                        config.groupOwnerIntent = 0;

                        //start progress dialog
                        progressDialog = ProgressDialog.show(WifiDirect3Activity.this,
                                "Press back to cancel",
                                "Connecting to the server", true, true);
                        isStart = true;
                        bt_join.setEnabled(false);
                        ((Button) findViewById(R.id.disconnect)).setEnabled(true);
                        connect(config);
                    }
                });

        Button bt_create = (Button) findViewById(R.id.create);
        bt_create.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isStart = true;
                        //start progress dialog
                        progressDialog = ProgressDialog.show(WifiDirect3Activity.this, "Press back to cancel",
                             "Creating a game", true, true);
                    }
                });

        Button bt_disconnect = (Button) findViewById(R.id.disconnect);
        bt_disconnect.setEnabled(false);
        bt_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });

        //start discovery : connection
        beginDiscovery();
    }

    public void beginDiscovery(){
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
    }



    public void connect(WifiP2pConfig config)
    {
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //WifiBroadcastReceiver will notify us
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiDirect3Activity.this, "Connection failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    protected void onResume() {
        super.onResume();
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peers) {
        adapter.clear();
        Iterator<WifiP2pDevice> i = peers.getDeviceList().iterator();
        while (i.hasNext()) {
            WifiP2pDevice device = i.next();
            list.add(device);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        //called when wifibroadcastreceiver detect a connection
        final TextView tv = (TextView) findViewById(R.id.connected);
        tv.setText(info.toString());
        ((Button) findViewById(R.id.disconnect)).setEnabled(true);

        //test : the game do not start automatically (it needs a click on join or start button)
        if(isStart){
            if(info.isGroupOwner == true){
                nbPlayer++;
                if(nbPlayer == 2){
                    Intent intent = new Intent(WifiDirect3Activity.this, DrawActivityScreen.class);
                    progressDialog.dismiss(); //stop Dialog progress
                    startActivity(intent);
                }
            }else {
                Intent intent = new Intent(WifiDirect3Activity.this, DrawActivityController.class);
                Bundle b = new Bundle();
                b.putSerializable("ip", info.groupOwnerAddress);
                intent.putExtras(b);
                progressDialog.dismiss(); //stop Dialog progress
                startActivity(intent);
            }
        }
    }

    public void disconnect() {
        if(manager != null)
        {
            manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                @Override
                public void onFailure(int reasonCode) {
                    Log.d("My app", "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {
                    Log.d("My app", "Disconnect succeed");
                }

            });
        }
    }
    public void resetData(){
        //called when wifibroadastreceiver detects a disconnect
        TextView tv = (TextView) findViewById(R.id.connected);
        tv.setText("Not connected");
        ((Button) findViewById(R.id.disconnect)).setEnabled(false);
        ((Button) findViewById(R.id.join)).setEnabled(false);
        beginDiscovery(); //we restart a new discovery of peers
    }

}




