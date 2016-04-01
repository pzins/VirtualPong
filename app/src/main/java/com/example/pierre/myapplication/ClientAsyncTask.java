package com.example.pierre.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ClientAsyncTask extends AsyncTask<Void, Integer, String> {

    private Context context;
    PrintWriter out;
    BufferedReader in;
    String groupOwnerIP;
    float x_accel;

    TextView v_x_accel;

    /**
     * @param context
     */
    public ClientAsyncTask(Context context, String ip, TextView v) {
        this.context = context;
        this.groupOwnerIP = ip;
        this.x_accel = 0;
        this.v_x_accel = v;
    }

    public void setX_accel(float x)
    {
        this.x_accel = x;
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
            pred.println("X =>" + Float.toString(x_accel));
            publishProgress();
            try {
                str = plec.readLine();      // lecture de reponse
                System.out.println(str);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
    @Override
    protected void onProgressUpdate(Integer... progress) {


        super.onProgressUpdate(progress);
        // Update the ProgressBar
        v_x_accel.setText(Float.toString(x_accel));


    }

}