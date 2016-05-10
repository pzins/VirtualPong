package com.example.pierre.myapplication;

import android.content.Context;
import android.os.AsyncTask;
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
public class GameReceiveAsyncTask  extends AsyncTask<Void, Integer, String> {

    Context context;
    DrawActivityClient.GameView clientGameView = null;

    String direction = "";
    String adr;

    public GameReceiveAsyncTask(Context context, DrawActivityClient.GameView game) {
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
        }
    }
}
