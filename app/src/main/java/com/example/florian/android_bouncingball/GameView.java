package com.example.florian.android_bouncingball;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.example.R;

/**
 * Created by Florian on 14/05/2016.
 */
public class GameView extends SurfaceView implements Runnable{
    Thread t = null;
    SurfaceHolder holder;
    boolean status = false;

    Point screenSize = new Point();

    Bitmap ball;
    float x_ball, y_ball;
    float dx_ball, dy_ball;

    public GameView(Context context) {
        super(context);

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

    public void run(){

        while (status){
            if (!holder.getSurface().isValid()){
                continue;
            }

            x_ball += dx_ball;
            if (x_ball <= 0 || x_ball > screenSize.x - ball.getWidth()){
                dx_ball = 0 - dx_ball;
            }
            y_ball += dy_ball;
            if (y_ball <= 0 || y_ball > screenSize.y - ball.getHeight()){
                dy_ball = 0 - dy_ball;
            }

            //lock Before painting
            Canvas c = holder.lockCanvas();
            c.drawARGB(255, 150, 200, 250);
            c.drawBitmap(ball, x_ball, y_ball, null);
            holder.unlockCanvasAndPost(c);
        }
    }

    public void pause(){
        status = false;
        while(true){
            try{
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
        t = null;
    }

    public void resume(){
        status = true;
        t = new Thread(this);
        t.start();
    }
}
