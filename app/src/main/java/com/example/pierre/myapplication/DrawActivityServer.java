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

public class DrawActivityServer extends AppCompatActivity implements SensorEventListener {

    private Paint paint = new Paint();
    private ServerComAsyncTask comAT;

    private Player player;
    private Player opp;
    private GameView gameView;


    private SensorManager sensorManager;
    private Sensor gravity;
    private BallAsyncTask ballAT;
    private Display screenSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        screenSize = getWindowManager().getDefaultDisplay();

        int screenWidth = screenSize.getWidth();
        int screenHeight = screenSize.getHeight();
        opp = new Player(screenWidth * 0.5f, screenHeight * 0.2f, 100, 10);
        player = new Player(screenWidth * 0.5f, screenHeight * 0.8f, 100, 10);
        Log.w("width", Integer.toString(screenWidth));
        Log.w("height", Integer.toString(screenHeight));
        gameView = new GameView(this, player, opp, screenWidth, screenHeight);

        setContentView(gameView);
        gameView.setWillNotDraw(false);



        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        comAT = new ServerComAsyncTask(this, gameView);
        comAT.execute();

   /*     ballAT = new BallAsyncTask(gameView);
        ballAT.execute();*/
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
        if (mySensor.getType() == Sensor.TYPE_GRAVITY) {
            float x = event.values[0];
            if (x > 1) {
                gameView.movePlayer("g");
                comAT.setDirection(gameView.getPositions());
            } else if (x < -1) {
                gameView.movePlayer("d");
                comAT.setDirection(gameView.getPositions());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    class GameView extends SurfaceView {
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


        public String getPositions() {
            return Float.toString(opp.getX() / screenWidth) + " " + Float.toString(player.getX() / screenWidth);
        }


        public void moveOpponent(String str) {
            if (str.equals("d")) {
                opp.moveRight();
            } else if (str.equals("g")) {
                opp.moveLeft();
            }
            invalidate();
        }

        public void movePlayer(String str){
            if(str.equals("d")){
                player.moveRight();
            } else if(str.equals("g")){
                player.moveLeft();
            }
            invalidate();
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int radius = 40;
            Paint p = new Paint();
            p.setColor(Color.BLUE);
            player.draw(canvas, p);
            p.setColor(Color.RED);
            opp.draw(canvas, p);
        }

    }

}
