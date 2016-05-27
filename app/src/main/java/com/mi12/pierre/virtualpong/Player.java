package com.mi12.pierre.virtualpong;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by pierre on 14/05/16.
 */
public class Player {

    private float x;
    private float  y;
    private int width;
    private int height;
    private int color;
    private int speed;
    private int score;

    public Player(float _x, float _y, int _width, int _height, int _color){
        this.x =    _x;
        this.y = _y;
        this.width = _width;
        this.height = _height;
        this.speed = 10;
        this.color = _color;
        this.score = 0;
    }
    public void addOnePoint(){
        score += 1;
    }

    public int getScore(){return score;}
    public void setScore(int _score){score = _score;}
    public void moveLeft(){
        x -= speed;
    }

    public void moveRight(){
        x += speed;
    }
    public void draw(Canvas canvas, Bitmap bitmap){
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawBitmap(bitmap, x, y, null);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }
}
