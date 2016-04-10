package com.example.pierre.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class DrawActivity extends AppCompatActivity {

    private Paint paint = new Paint();
    private ServerAsyncTask server;
    private int x;
    private int y;
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView(this, x, y);
        setContentView(gameView);
        gameView.setWillNotDraw(false);
        server = new ServerAsyncTask(this, gameView,this);
        x = 50;
        y = 50;
        server.execute();
    }


    class GameView extends View
    {
        private int x;
        private int y;
        public GameView(Context context, int _x, int _y) {
            super(context);
            x = _x;
            y = _y;

        }

        public void setXPos(int _x){this.x = _x;invalidate();}
        public void setY(int _y){this.y = _y;invalidate();}
        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            int radius=40;
            Paint p=new Paint();
            p.setColor(Color.RED);
            canvas.drawCircle(x*20+500, 1000, radius, p);
            Log.w("----------------","------------------");
        }

    }

}
