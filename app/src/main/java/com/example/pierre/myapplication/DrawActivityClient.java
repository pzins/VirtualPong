package com.example.pierre.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.example.R;

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
        player = new Player(screenWidth * 0.5f, screenHeight * 0.2f, (int)(screenWidth * 0.2f),
                (int)(screenHeight * 0.02f), Color.BLUE);
        opp = new Player(screenWidth * 0.5f, screenHeight * 0.8f, (int)(screenWidth * 0.2f),
                (int)(screenHeight * 0.02f), Color.RED);
        gameView = new GameView(this, player, opp, screenWidth, screenHeight);

        setContentView(gameView);
//        gameView.setWillNotDraw(false);

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
        gameView.pause();
        if(sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    protected void onResume() {
        super.onResume();
        gameView.resume();
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

    class GameView extends SurfaceView implements  Runnable
    {

        private Thread thread = null;
        private SurfaceHolder holder;
        private boolean status = false;

        Point screenSize = new Point();

        Bitmap ball;
        float x_ball, y_ball;
        float dx_ball, dy_ball;
        private Player player;
        private Player opp;
        private int screenWidth;
        private int screenHeight;
        private Bitmap playerBTM;
        private Bitmap oppBTM;

        public GameView(Context context, Player _player, Player _opp, int _width, int _height) {
            super(context);
            this.player = _player;
            this.opp = _opp;
            this.screenHeight = _height;
            this.screenWidth = _width;

            this.playerBTM = BitmapFactory.decodeResource(getResources(), R.drawable.player);
            this.playerBTM = Bitmap.createScaledBitmap(playerBTM, player.getWidth(), player.getHeight(), false);
            this.oppBTM= BitmapFactory.decodeResource(getResources(), R.drawable.opp);
            this.oppBTM = Bitmap.createScaledBitmap(oppBTM, opp.getWidth(), opp.getHeight(), false);
            holder = getHolder();

            //Get screen size
            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            display.getSize(screenSize);

            //get ball
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.blueball);
            ball = Bitmap.createScaledBitmap(ball, 50, 50, false);

            //initial position and speed
            x_ball = y_ball = 0;
            dx_ball = dy_ball = 4;
        }

        public void setPositions(String str){
            String[] array = str.split(" ");
            player.setX(Float.parseFloat(array[0]) * screenWidth);
            opp.setX(Float.parseFloat(array[1]) * screenWidth);
            x_ball = Float.parseFloat(array[2]) * screenWidth;
            y_ball = Float.parseFloat(array[3]) * screenHeight;
//            invalidate();
        }


        public void run(){
            Canvas c;
            while (status){
                if (!holder.getSurface().isValid()){
                    continue;
                }

                //lock Before painting
                c = holder.lockCanvas();
                c.drawARGB(255, 150, 200, 250);
                player.draw(c, playerBTM);
                opp.draw(c, oppBTM);
                c.drawBitmap(ball, x_ball, y_ball, null);
                holder.unlockCanvasAndPost(c);
            }
        }

        public void pause(){
            status = false;
            while(true){
                try{
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            thread = null;
        }

        public void resume(){
            status = true;
            thread = new Thread(this);
            thread.start();
        }
/*        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            Paint p=new Paint();
            p.setColor(Color.BLUE);
            player.draw(canvas);
            p.setColor(Color.RED);
            opp.draw(canvas);
        }*/
    }
}


