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

import org.w3c.dom.Text;

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
    private float xx = 0;
    private float yy = 0;
    private Activity act;
    /**
     * @param context
     * @param statusText
     */
    public ServerAsyncTask(Context context, View statusText, TextView v) {
        this.context = context;
        this.statusText = (TextView) statusText;
        this.v_x_accel = (TextView) v;
    }

    public ServerAsyncTask(Context context, DrawActivity.GameView game, Activity drawAct) {
        this.context = context;
        this.gameView = game;
        this.act = drawAct;
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
//                System.out.println(str);
                String[] sep = str.split("\\|");
                x_accel = sep[0];
                y_accel = sep[1];
                x_accel = x_accel.replaceAll(",",".");
                y_accel = y_accel.replaceAll(",", ".");

                xx = Float.parseFloat(x_accel);
                yy = Float.parseFloat(y_accel);
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
        // Update the ProgressBar
/*        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Your UI changes here.
                gameView.setX(xx);
            }
        });*/

        if(this.gameView != null) {
            this.gameView.setXPos(Math.round(xx));
            this.gameView.setYPos(Math.round(yy));
        } else
        {
            v_x_accel.setText("X:" + x_accel + "  Y:" + y_accel);
        }
    }

}