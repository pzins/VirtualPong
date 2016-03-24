package com.example.pierre.myapplication;


import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.Toast;
import android.widget.TextView;



import java.lang.Math;
import android.net.wifi.p2p.WifiP2pManager.Channel;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gravity;
    private long lastUpdate = 0;
    private long lastUpdate2 = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 600;
    private float last_gx, last_gy, last_gz;


    private boolean isWifiP2pEnabled;


    private final IntentFilter intentFilter = new IntentFilter();
    private Channel mChannel;
    private WifiP2pManager mManager;
    private Receiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isWifiP2pEnabled = true;

        last_x = 0;
        last_y = 0;
        last_z = 0;
        last_gx = 0;
        last_gy = 0;
        last_gz = 0;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);



        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
    }

    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        unregisterReceiver(receiver);

    }

    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        receiver = new Receiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

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


        }

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                if (speed > SHAKE_THRESHOLD) {

                }

                if (true || Math.abs(x - last_x) > 1 || Math.abs(y - last_y) > 1 || Math.abs(z - last_z) > 1) {
//                    Log.w("-----------------------", "---------------------");
//                    Log.w("x = ", String.valueOf(x));
//                    Log.w("y = ", String.valueOf(y));
//                    Log.w("z = ", String.valueOf(z));
//                    Log.w("+++++++++++++++++++++++", "+++++++++++++++++++++");
  /*                  Context context = getApplicationContext();
                    CharSequence text = String.valueOf(x);
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show(); */
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


            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }


    public void setIsWifiP2pEnabled(boolean state)
    {
        isWifiP2pEnabled = state;
    }

}
