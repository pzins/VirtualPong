package com.example.pierre.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by pierre on 08/05/16.
 */
public class DrawActivityClient  extends AppCompatActivity implements SensorEventListener {
    private Paint paint;
    private ClientComAsyncTask comAT;

    private Player player;
    private Player opp;

    private GameView gameView;

    private SensorManager sensorManager;
    private Sensor gravity;

    private String goIpAddr;
    private Display screenSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        screenSize = getWindowManager().getDefaultDisplay();

        int screenWidth = screenSize.getWidth();
        int screenHeight = screenSize.getHeight();
        player = new Player(screenWidth * 0.5f, screenHeight * 0.2f, 100, 10);
        opp = new Player(screenWidth * 0.5f, screenHeight * 0.8f, 100, 10);
        Log.w("width", Integer.toString(screenWidth));
        Log.w("height", Integer.toString(screenHeight));
        gameView = new GameView(this, player, opp, screenWidth, screenHeight);
        setContentView(gameView);
        gameView.setWillNotDraw(false);

        Bundle b = getIntent().getExtras();


        if(b != null) {
            goIpAddr = b.getString("ip");
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        comAT = new ClientComAsyncTask(this, goIpAddr, gameView);
        comAT.execute();
    }

    protected void onPause() {
        super.onPause();
        if(sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    protected void onResume() {
        super.onResume();
        if(sensorManager != null) {
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;
        if(mySensor.getType() == Sensor.TYPE_GRAVITY){
            float x = event.values[0];
            if(x > 1) {
                comAT.setDirection("g");
            }else if (x < -1) {
                comAT.setDirection("d");
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    class GameView extends SurfaceView
    {
        private Player player;
        private Player opp;
        private int screenWidth;
        private int screenHeight;

        public GameView(Context context, Player _player, Player _opp, int _width, int _height) {
            super(context);
            this.player = _player;
            this.opp = _opp;
            this.screenHeight = _height;
            this.screenWidth = _width;
        }

        public void setPositions(String str){
            String[] array = str.split(" ");
            player.setX(Float.parseFloat(array[0]) * screenWidth);
            opp.setX(Float.parseFloat(array[1]) * screenWidth);
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            Paint p=new Paint();
            p.setColor(Color.BLUE);
            player.draw(canvas, p);
            p.setColor(Color.RED);
            opp.draw(canvas, p);
        }
    }
}


