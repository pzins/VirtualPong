package com.example.pierre.myapplication;

/**
 * Created by pierre on 01/04/16.
 */

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ServerAsyncTask extends AsyncTask<Void, Integer, String> {

    private Context context;
    private TextView statusText;
    private TextView v_x_accel;
    private String x_accel ="";
    private String y_accel = "";

    private DrawActivity.GameView gameView = null;


    private String direction = "";
    /**
     * @param context
     * @param statusText
     */
    public ServerAsyncTask(Context context, View statusText, TextView v) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.v_x_accel = (TextView) v;
    }

    public ServerAsyncTask(Context context, DrawActivity.GameView game) {
        this.context = context;
        this.gameView = game;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket s = new ServerSocket(8988);
            Socket soc = s.accept();

            // Un BufferedReader permet de lire par ligne.
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(soc.getInputStream())
            );

            // Un PrintWriter possède toutes les opérations print classiques.
            // En mode auto-flush, le tampon est vidé (flush) à l'appel de println.
            PrintWriter printer = new PrintWriter( new BufferedWriter(
                    new OutputStreamWriter(soc.getOutputStream())),
                    true);

            while (true) {
                String str = reader.readLine();
                if (str.equals("END")) break;
                direction = str;
                Log.w("received", str);
                publishProgress();
//                printer.println("message recu");
            }
            reader.close();
            printer.close();
            soc.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "OL";
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        if(this.gameView != null) {
            this.gameView.move(direction);
        } else
        {
            v_x_accel.setText("DIRECTION : " + direction);
        }
    }

}