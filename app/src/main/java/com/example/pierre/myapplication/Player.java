package com.example.pierre.myapplication;

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
    private float speed;

    public Player(float _x, float _y, int _width, int _height, float _speed, int _color){
        this.x =    _x;
        this.y = _y;
        this.width = _width;
        this.height = _height;
        this.speed = _speed;
        this.color = _color;
    }
    public void setSpeed(float _speed){
        speed = _speed;
    }
    public float getSpeed(){return speed;}
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
//        canvas.drawRect(x - 100, y - 52, x + width * 0.5f, y + height * 0.5f, paint);
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
