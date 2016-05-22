package com.mi12.pierre.virtualpong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.mi12.R;

public class DrawActivityServer extends Activity  {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FullScreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Display screenSize = getWindowManager().getDefaultDisplay();

        setContentView(R.layout.activity_bouncing_ball);


        int screenWidth = screenSize.getWidth();
        int screenHeight = screenSize.getHeight();
        Player player = new Player(screenWidth * 0.5f, screenHeight * 0.8f, (int) (screenWidth * 0.2f),
                (int) (screenHeight * 0.02f), Color.BLUE);
        Player opp = new Player(screenWidth * 0.5f, screenHeight * 0.2f, (int) (screenWidth * 0.2f),
                (int) (screenHeight * 0.02f), Color.RED);
        gameView = new GameView(this, player, opp, screenWidth, screenHeight);

        setContentView(gameView);


        ServerComAsyncTask comOppAT = new ServerComAsyncTask(this, gameView, 8988);
        ServerComAsyncTask comPlayerAT = new ServerComAsyncTask(this, gameView, 8989);
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
        private Thread thread = null;
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

        //0 : mur, 1 : player, -1 : opp
        private int lastTouch;

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
            ball = BitmapFactory.decodeResource(getResources(), R.drawable.blueball);
            ball = Bitmap.createScaledBitmap(ball, 50, 50, false);

            //initial position and speed
            x_ball = y_ball = 0;
            dx_ball = dy_ball = 4;
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

        public void run(){
            while (status){
                if (!holder.getSurface().isValid()){
                    continue;
                }

                //deplacement balle + rebonds mur/joueur
                x_ball += dx_ball;
                if (x_ball <= 0 || x_ball > screenWidth - ball.getWidth()){
                    dx_ball = 0 - dx_ball;
                    lastTouch = 0;
                }
                y_ball += dy_ball;
                if (y_ball <= 0 || y_ball > screenHeight - ball.getHeight()){
                    dy_ball = 0 - dy_ball;
                    lastTouch = 0;
                }else if(lastTouch != -1 &&
                        y_ball <= opp.getY() + oppBTM.getHeight() &&
                        y_ball >= opp.getY() + oppBTM.getHeight() - 10 &&
                        x_ball + ball.getWidth() <= opp.getX() + oppBTM.getWidth() &&
                        x_ball >= opp.getX()){
                    dy_ball = 0 - dy_ball;
                    lastTouch = -1;
                }else if(lastTouch != 1 &&
                        y_ball + ball.getHeight() >= player.getY() &&
                        y_ball + ball.getHeight() <= player.getY() + 10 &&
                        x_ball + ball.getWidth() <= player.getX() + playerBTM.getWidth() &&
                        x_ball >= player.getX()) {
                    dy_ball = 0 - dy_ball;
                    lastTouch = 1;
                }

                //dessin du jeu
                //lock Before painting
                Canvas c = holder.lockCanvas();
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
    }
}
