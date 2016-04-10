package com.example.pierre.myapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class MainActivity extends AppCompatActivity implements SensorEventListener ,
        WifiP2pManager.ChannelListener, DeviceListFragment.DeviceActionListener{


    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private long lastUpdate = 0;
    private long lastUpdate2 = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private float last_gx, last_gy, last_gz;

    public static final String TAG = "wifidemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;

    ClientAsyncTask client;

    private float globX = 0;


    public void setIsWifiP2pEnabled(boolean state)
    {
        isWifiP2pEnabled = state;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        last_x = 0;
        last_y = 0;
        last_z = 0;
        last_gx = 0;
        last_gy = 0;
        last_gz = 0;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);



        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        unregisterReceiver(receiver);
    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        receiver = new WifiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.atn_direct_discover){
            new Thread(new Runnable(){
                public void run(){
                    Socket socket = null;
                    try {
                        socket = new Socket("192.168.0.15", 8988);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PrintWriter pred = null;
                    try {
                        pred = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    while(true){
                        pred.println(Float.toString(globX));
                    }
                    //open socket
                }
            }).start();


        }
        else if(id == R.id.atn_direct_enable){
            Intent intent = new Intent(MainActivity.this, DrawActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.action_settings) {
            if(!isWifiP2pEnabled){
                Toast.makeText(MainActivity.this, "P2P Wifi is not enabled",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(MainActivity.this, "Discovery initiated",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(MainActivity.this, "Discovery failed",
                            Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                Toast.makeText(MainActivity.this, "Connection failed",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);

            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }

        });
    }
    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void cancelDisconnect() {

        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {

                manager.cancelConnect(channel, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(MainActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }


    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);

    }


    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager().findFragmentById(R.id.frag_detail);

        Sensor mySensor = event.sensor;
/*
        if (mySensor.getType() == Sensor.TYPE_GRAVITY) {


            TextView textViewgx = (TextView) findViewById(R.id.gx);
            textViewgx.setText(String.valueOf(event.values[0]));
            TextView textViewgy = (TextView) findViewById(R.id.gy);
            textViewgy.setText(String.valueOf(event.values[1]));
            TextView textViewgz = (TextView) findViewById(R.id.gz);
            textViewgz.setText(String.valueOf(event.values[2]));
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate2) > 200) {
                lastUpdate2 = curTime;

                float gx = Math.abs(event.values[0]);
                float gy = Math.abs(event.values[1]);
                float gz = Math.abs(event.values[2]);


                float last_sum = Math.abs(last_gx) + Math.abs(last_gy) + Math.abs(last_gz);
                float sum = gx + gy + gz;
                TextView calc = (TextView) findViewById(R.id.calc);
                calc.setText(String.valueOf(last_sum - sum));

                if(last_sum - sum > 0.05)
                {
                    TextView dir = (TextView) findViewById(R.id.direction);
                    dir.setText("Up");
                }
                else if(last_sum - sum < 0.05)
                {
                    TextView dir = (TextView) findViewById(R.id.direction);
                    dir.setText("Down");
                }else
                {
                    TextView dir = (TextView) findViewById(R.id.direction);
                    dir.setText("...");
                }
                last_gx = gx;
                last_gy = gy;
                last_gz = gz;

            }


        }*/

//        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            globX = x;
            if(fragment != null) {
                if(fragment.getClient() != null){
                    fragment.getClient().setX_accel(x);
                }
            }
            TextView textViewX = (TextView) findViewById(R.id.x);
            TextView textViewY = (TextView) findViewById(R.id.y);
            TextView textViewZ = (TextView) findViewById(R.id.z);
            textViewX.setText(String.valueOf(x));
            textViewY.setText(String.valueOf(y));
            textViewZ.setText(String.valueOf(z));
/*
            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }

                if (true || Math.abs(x - last_x) > 1 || Math.abs(y - last_y) > 1 || Math.abs(z - last_z) > 1) {
                    Log.w("-----------------------", "---------------------");
                    Log.w("x = ", String.valueOf(x));
                    Log.w("y = ", String.valueOf(y));
                    Log.w("z = ", String.valueOf(z));
                    Log.w("+++++++++++++++++++++++", "+++++++++++++++++++++");
                    Context context = getApplicationContext();
                    CharSequence text = String.valueOf(x);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show(); /*
                    TextView textViewX = (TextView) findViewById(R.id.x);
                    TextView textViewY = (TextView) findViewById(R.id.y);
                    TextView textViewZ = (TextView) findViewById(R.id.z);
                    textViewX.setText(String.valueOf(x));
                    textViewY.setText(String.valueOf(y));
                    textViewZ.setText(String.valueOf(z));

                }

                last_x = x;
                last_y = y;
                last_z = z;


            }*/
        }
    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

 /*   @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }*/





}
