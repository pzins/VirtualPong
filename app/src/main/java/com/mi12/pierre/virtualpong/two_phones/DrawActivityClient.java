package com.mi12.pierre.virtualpong.two_phones;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

import com.mi12.R;
import com.mi12.pierre.virtualpong.Player;

import java.net.InetAddress;

/**
 * Created by pierre on 08/05/16.
 */
public class DrawActivityClient  extends AppCompatActivity implements SensorEventListener {

    private SendClientTask sendTask;
    private GameView gameView;

    private SensorManager sensorManager;
    private Sensor gravity;

    private InetAddress goIpAddr; //ip adr of the group owner

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //get screen size
        Display screenSize = getWindowManager().getDefaultDisplay();
        int screenWidth = screenSize.getWidth();
        int screenHeight = screenSize.getHeight();

        //creation of the players
        Player player = new Player(screenWidth * 0.5f, screenHeight * 0.05f, (int) (screenWidth * 0.2f),
                (int) (screenHeight * 0.02f), Color.BLUE);
        Player opp = new Player(screenWidth * 0.5f, screenHeight * 0.95f, (int) (screenWidth * 0.2f),
                (int) (screenHeight * 0.02f), Color.RED);

        gameView = new GameView(this, player, opp, screenWidth, screenHeight);
        setContentView(gameView);

        //get parameters
        Bundle b = getIntent().getExtras();
        if(b != null) {
            goIpAddr = (InetAddress) b.getSerializable("ip");
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

        //thread to send orientation to the group owner
        sendTask = new SendClientTask(goIpAddr);
        sendTask.start();

        //thread to get game positions
        new ClientComAsyncTask(this, gameView).execute();
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
        synchronized (sendTask){
            if(mySensor.getType() == Sensor.TYPE_GRAVITY){
                float x = event.values[0];
                //threshold [-1;1] => no movement, otherwise player cannot stay
                if(x > 1) {
                    sendTask.setDirection((byte) 0x0);
                    sendTask.notify(); //wake up thread to send data
                }else if (x < -1) {
                    sendTask.setDirection((byte) 0x1);
                    sendTask.notify(); //wake up thread to send data
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    class GameView extends SurfaceView implements  Runnable
    {
        private Thread thread = null;
        private SurfaceHolder holder;
        private boolean status = false;

        //ball
        private Bitmap ball;
        private float x_ball, y_ball;

        //player
        private Player player;
        private Player opp;
        private Bitmap playerBTM;
        private Bitmap oppBTM;

        private int screenWidth;
        private int screenHeight;

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

            //get ball
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.yellowball);
            ball = Bitmap.createScaledBitmap(ball, 50, 50, false);

            x_ball = y_ball = 0;
        }

        public void setPositions(GamePositions str){
            player.setX(str.player_x * screenWidth);
            opp.setX(str.opp_x * screenWidth);
            x_ball = str.ball_x * screenWidth;
            y_ball = str.ball_y * screenHeight;
            //set score en pensant à inverser Opp/Player
            player.setScore(str.scoreOpp);
            opp.setScore(str.scorePlayer);
        }

        public void run(){
            Canvas c;
            while (status){
                if (!holder.getSurface().isValid()){
                    continue;
                }
                //dessin du jeu
                //lock Before painting
                c = holder.lockCanvas();
                c.drawARGB(255, 0, 0, 0);

                player.draw(c, playerBTM);
                opp.draw(c, oppBTM);

                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                paint.setStyle(Paint.Style.FILL);
                paint.setTextSize(100);
                c.drawText(Integer.toString(player.getScore()), 100, screenHeight * 0.8f, paint);
                paint.setColor(Color.RED);
                c.drawText(Integer.toString(opp.getScore()), 100, screenHeight * 0.2f, paint);
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth(50);
                c.drawLine(0, screenHeight / 2f, screenWidth, screenHeight / 2f, paint);

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
    }
}


