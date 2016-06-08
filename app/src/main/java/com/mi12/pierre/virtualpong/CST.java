package com.mi12.pierre.virtualpong;

import android.hardware.SensorManager;

/**
 * Created by pierre on 02/06/16.
 */
public class CST {
    public final static int PORT_A = 8988;
    public final static int PORT_B = 8989;

    public final static float PLAYER_PERCENT_X = 0.5f;
    public final static float PLAYER_PERCENT_Y_TOP = 0.05f;
    public final static float PLAYER_PERCENT_Y_BTM = 0.95f;
    public final static float PLAYER_PERCENT_W = 0.2f;
    public final static float PLAYER_PERCENT_H = 0.02f;
    public final static byte MOVE_LEFT = 0x1;
    public final static byte MOVE_RIGHT = 0x0;
    public final static int BALL_SCALE_X = 50;
    public final static int BALL_SCALE_Y = 50;
    public final static int SCORE_X = 100;
    public final static float SCORE_PERCENT_BTM_Y = 0.8f;
    public final static float SCORE_PERCENT_TOP_Y = 0.2f;
    public final static int SCORE_TXT_SIZE = 100;
    public final static int LINE_WIDTH = 50;

    public final static int THRESHOLD_SENSOR = 1;

    public final static String MOVE_LEFT_LOCAL = "g";
    public final static String MOVE_RIGHT_LOCAL = "d";

    public final static int BALL_INIT_POS_X = 0;
    public final static int BALL_INIT_POS_Y = 0;
    public final static int BALL_INIT_DX = 4;
    public final static int BALL_INIT_DY = 4;

    public final static int PLAYER_SPEED = 10;
    public final static int SERVER_DELAY = 4; //sleep server send

    public final static int SENSOR_SAMPLING = SensorManager.SENSOR_DELAY_GAME;
}

