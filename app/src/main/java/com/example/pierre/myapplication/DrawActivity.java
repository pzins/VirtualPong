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
    private int posX;
    private int posY;
    private GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        posX = 500;
        posY = 850;
        gameView = new GameView(this, posX, posY);
        setContentView(gameView);
        gameView.setWillNotDraw(false);
        server = new ServerAsyncTask(this, gameView,this);

        server.execute();
    }



    class GameView extends View
    {
        private int x;
        private int y;
        public int delta  = 0;
        public GameView(Context context, int _x, int _y) {
            super(context);
            x = _x;
            y = _y;

        }
        public void move(String str)
        {
            Log.w("DIRECTION = ", str);
            if(str.equals("d"))
            {
                x += 10;
            }else if(str.equals("g"))
            {
                x -= 10;
            }
            invalidate();

        }


        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            Log.w("--------","++++++++");
            int radius=40;
            Paint p=new Paint();
            p.setColor(Color.RED);
            canvas.drawCircle(x, y, radius, p);
        }

    }

}
