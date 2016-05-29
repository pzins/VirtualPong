package com.mi12.pierre.virtualpong.three_phones;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mi12.R;

import java.net.InetAddress;


/**
 * Created by pierre on 08/05/16.
 */
public class DrawActivityController  extends AppCompatActivity implements SensorEventListener {

    private SendControllerTask sendTask;
    private SensorManager sensorManager;
    private Sensor gravity;

    private InetAddress goIpAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_controller);

        Bundle b = getIntent().getExtras();
        if (b != null) {
            goIpAddr = (InetAddress) b.getSerializable("ip");
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        //task to send movement to the screen
        sendTask = new SendControllerTask(goIpAddr);
        sendTask.start();
    }

    protected void onPause() {
        super.onPause();
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    protected void onResume() {
        super.onResume();
        if (sensorManager != null) {
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        synchronized (sendTask){
            if (mySensor.getType() == Sensor.TYPE_GRAVITY) {
                float x = event.values[0];
                //threshold [-1;1]
                if (x > 1) {
                    sendTask.setDirection((byte) 0x0);
                    sendTask.notify();
                } else if (x < -1) {
                    sendTask.setDirection((byte) 0x1);
                    sendTask.notify();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}



