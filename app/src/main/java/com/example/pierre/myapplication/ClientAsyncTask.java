package com.example.pierre.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ClientAsyncTask extends AsyncTask<Void, Void, String> {

    private Context context;
    PrintWriter out;
    BufferedReader in;
    String groupOwnerIP;
    float x_accel;
    float y_accel;
    Boolean shouldSend = false;
    String direction = "";
    /**
     * @param context
     */
    public ClientAsyncTask(Context context, String ip) {
        this.context = context;
        this.groupOwnerIP = ip;
        this.x_accel = 0;
        this.y_accel = 0;
    }

    public void setX_accel(float x)
    {
        this.x_accel = x;
        shouldSend = true;
    }
    public void setY_accel(float y)
    {
        this.y_accel = y;
        shouldSend = true;
    }
    public void setDirection(String str){
        this.direction = str;
        shouldSend = true;
    }
    @Override
    protected String doInBackground(Void... params) {
        Socket socket = null;
        try {
            socket = new Socket(groupOwnerIP, 8988);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader plec = null;
        try {
            plec = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        PrintWriter pred = null;
        try {
            pred = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str;
        while (true)
        {
//            pred.println(Float.toString(x_accel) + "|" + Float.toString(y_accel));
            if(shouldSend){
//                String strx = String.format("%.2f", x_accel);
//                String stry = String.format("%.2f", y_accel);
//                String res = strx + "|" + stry;

                pred.println(direction);
                shouldSend = false;
            }
//            Log.w("######", res);
//            pred.println("X =>" + Float.toString(x_accel));
/*            try {
                str = plec.readLine();      // lecture de reponse
                System.out.println(str);
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            if(false){
                break;
            }
        }
        pred.println("END");
        try {
            plec.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pred.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "OL";
    }


}