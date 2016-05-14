package com.example.pierre.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by pierre on 14/05/16.
 */
public class Player {

    private float x;
    private float  y;
    private float  width;
    private float  height;
    private Color color;
    private int speed;

    public Player(float _x, float _y, float _width, float _height){
        this.x =    _x;
        this.y = _y;
        this.width = _width;
        this.height = _height;
        this.speed = 10;
    }

    public void moveLeft(){
        x -= speed;
    }

    public void moveRight(){
        x += speed;
    }
    public void draw(Canvas canvas, Paint    paint){
        canvas.drawRect(x - 100, y - 52, x + width * 0.5f, y + height * 0.5f, paint);
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
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
