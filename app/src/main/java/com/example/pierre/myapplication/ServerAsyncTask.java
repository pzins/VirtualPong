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

    private String adr = "";

    private DrawActivity.GameView gameView = null;
    private DrawActivityClient.GameView clientGameView = null;

    private String direction = "";

    private Boolean shouldStart = true;
    private GameAsyncTask client;
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
    public ServerAsyncTask(Context context, DrawActivityClient.GameView game) {
        this.context = context;
        this.clientGameView = game;
    }
    public ServerAsyncTask(Context context, DrawActivity.GameView game, GameAsyncTask client) {
        this.context = context;
        this.gameView = game;
        this.client = client;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket s = new ServerSocket(8988);
            Log.w("AVANT","ACCEPT");
            Socket soc = s.accept();
            Log.w("AVANT","ACCEPT");
            adr = soc.getInetAddress().toString().substring(1);
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
                publishProgress();
//                printer.println(str);
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
        if(shouldStart ) {
            shouldStart = false;
//            new GameAsyncTask(context, adr).execute();
            Log.w("--------", "-------------");
            client.setAdresseIp(adr);
            client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        if(this.gameView != null) {
            this.gameView.move(direction);
        } else
        {
            v_x_accel.setText("DIRECTION : " + direction);
        }
    }

}