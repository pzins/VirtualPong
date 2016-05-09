package com.example.pierre.myapplication;

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
 * Created by pierre on 09/05/16.
 */
public class GameReceiverAsyncTask  extends AsyncTask<Void, Integer, String> {

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
    /**
     * @param context
     * @param statusText
     */
    public GameReceiverAsyncTask(Context context, View statusText, TextView v) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.v_x_accel = (TextView) v;
    }
    public GameReceiverAsyncTask(Context context, DrawActivity.GameView game) {
        this.context = context;
        this.gameView = game;
    }
    public GameReceiverAsyncTask(Context context, DrawActivityClient.GameView game) {
        this.context = context;
        this.clientGameView = game;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket s = new ServerSocket(8988);
            Socket soc = s.accept();
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
                printer.println(str);
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

        if(this.clientGameView != null) {
//            this.clientGameView.move(direction);
            this.clientGameView.setPositions(direction);
        } else
        {
//            v_x_accel.setText("DIRECTION : " + direction);
        }
    }

}
