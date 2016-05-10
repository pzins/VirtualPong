package com.example.pierre.myapplication;

/**
 * Created by pierre on 01/04/16.
 */

import android.content.Context;
import android.os.AsyncTask;

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
public class MoveReceiveAsyncTask extends AsyncTask<Void, Integer, String> {

    private Context context;
    private String adr = "";

    private DrawActivityServer.GameView gameView = null;

    private String direction = "";

    private Boolean shouldStart = true;
    private GameSendAsyncTask client;

    public MoveReceiveAsyncTask(Context context, DrawActivityServer.GameView game) {
        this.context = context;
        this.gameView = game;
    }

    public MoveReceiveAsyncTask(Context context, DrawActivityServer.GameView game, GameSendAsyncTask client) {
        this.context = context;
        this.gameView = game;
        this.client = client;
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
            client.setAdresseIp(adr);
            client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        if(this.gameView != null) {
            this.gameView.move(direction);
            client.setDirection(this.gameView.getPositions());
        }
    }
}