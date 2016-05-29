package com.mi12.pierre.virtualpong.three_phones;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.mi12.R;
import com.mi12.pierre.virtualpong.Player;


public class DrawActivityScreen extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display screenSize = getWindowManager().getDefaultDisplay();

        setContentView(R.layout.activity_draw_screen);


        int screenWidth = screenSize.getWidth();
        int screenHeight = screenSize.getHeight();
        Player player = new Player(screenWidth * 0.5f, screenHeight * 0.8f, (int) (screenWidth * 0.2f),
                (int) (screenHeight * 0.02f), Color.BLUE);
        Player opp = new Player(screenWidth * 0.5f, screenHeight * 0.2f, (int) (screenWidth * 0.2f),
                (int) (screenHeight * 0.02f), Color.RED);
        gameView = new GameView(this, player, opp, screenWidth, screenHeight);

        setContentView(gameView);


        ScreenAsyncTask comOppAT = new ScreenAsyncTask(this, gameView, 8988);
        ScreenAsyncTask comPlayerAT = new ScreenAsyncTask(this, gameView, 8989);
        comOppAT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        comPlayerAT.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    class GameView extends SurfaceView implements  Runnable {

        public Thread thread = null;
        private SurfaceHolder holder;
        private boolean status = false;


        private Bitmap ball;
        private float x_ball, y_ball;
        private float dx_ball, dy_ball;


        private Player player;
        private Player opp;
        private Bitmap playerBTM;
        private Bitmap oppBTM;

        private int screenWidth;
        private int screenHeight;

        //player = 1 // opp = -1
        private int lastTouch;
        private Boolean isFirstLaunch;

        private int nbConnectedPlayer;

        public GameView(Context context, Player _player, Player _opp, int _width, int _height) {
            super(context);
            this.player = _player;
            this.opp = _opp;
            this.screenHeight = _height;
            this.screenWidth = _width;
            this.isFirstLaunch = true;
            this.playerBTM = BitmapFactory.decodeResource(getResources(), R.drawable.player);
            this.playerBTM = Bitmap.createScaledBitmap(playerBTM, player.getWidth(), player.getHeight(), false);
            this.oppBTM= BitmapFactory.decodeResource(getResources(), R.drawable.opp);
            this.oppBTM = Bitmap.createScaledBitmap(oppBTM, opp.getWidth(), opp.getHeight(), false);
            this.nbConnectedPlayer = 0;
            holder = getHolder();

            //get ball
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.yellowball);
            ball = Bitmap.createScaledBitmap(ball, 50, 50, false);

            //initial position and speed and lastTouch
            initBall(0,0,4,4);

        }

        public void moveOpponent(String str) {
            if (str.equals("d") && opp.getX() + oppBTM.getWidth() < screenWidth) {
                opp.moveRight();
            } else if (str.equals("g") && opp.getX() > 0) {
                opp.moveLeft();
            }
        }

        public void movePlayer(String str){
            if(str.equals("d") && player.getX() + playerBTM.getWidth() < screenWidth){
                player.moveRight();
            } else if(str.equals("g") && player.getX() > 0){
                player.moveLeft();
            }
        }

        public void initBall(int x, int y, int dx, int dy){
            x_ball = x;
            y_ball = y;

            dx_ball = 3 + (int)(Math.random() * ((6 - 3) + 1));
            dy_ball = 3 + (int)(Math.random() * ((6 - 3) + 1));
            lastTouch = 0;
        }

        public void readyToStart(){
            nbConnectedPlayer++;
            if(nbConnectedPlayer == 2){
                nbConnectedPlayer = 0;
                this.thread.start();
            }
        }
        public void run(){
            Canvas c;
            while (status){
                if (!holder.getSurface().isValid()){
                    continue;
                }

                //deplacement balle + rebonds mur/joueur
                x_ball += dx_ball;
                if (x_ball <= 0 || x_ball > screenWidth - ball.getWidth()){
                    dx_ball = 0 - dx_ball;
                }
                y_ball += dy_ball;

                //balle sort en haut => point player
                if (y_ball <= 0 ){
                    initBall(0,0,4,4);
                    player.addOnePoint();
                }
                //balle sort en bas => point opp
                if(y_ball > screenHeight - ball.getHeight()){
                    initBall(0,0,4,4);
                    opp.addOnePoint();
                }

                //rebond sur opp (joueur en haut)
                if(lastTouch != -1 &&
                        y_ball <= opp.getY() + oppBTM.getHeight() &&
                        x_ball + ball.getWidth() <= opp.getX() + oppBTM.getWidth() &&
                        x_ball >= opp.getX()){
                    dy_ball = 0 - dy_ball;
                    lastTouch = -1;
                }
                //rebond sur player (joueur en bas)
                if(lastTouch != 1 &&
                        y_ball + ball.getHeight() >= player.getY() &&
                        x_ball + ball.getWidth() <= player.getX() + playerBTM.getWidth() &&
                        x_ball >= player.getX()) {
                    dy_ball = 0 - dy_ball;
                    lastTouch = 1;
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
            if(!isFirstLaunch) {
                thread.start();
            }
        }
    }
}
