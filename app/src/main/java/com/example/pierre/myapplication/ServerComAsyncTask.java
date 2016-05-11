package com.example.pierre.myapplication;

/**
 * Created by pierre on 01/04/16.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
public class ServerComAsyncTask extends AsyncTask<Void, Integer, String> {

    private Context context;
    private String adr = "";

    private DrawActivityServer.GameView gameView = null;

    private String direction = "";
    private  String recDirection = "";

    private String groupOwnerIP;
    private Boolean shouldSend = false;
    public ServerComAsyncTask(Context context, DrawActivityServer.GameView game) {
        this.context = context;
        this.gameView = game;
    }



    public void setAdresseIp(String ip){
        this.groupOwnerIP = ip;
    }

    public void setDirection(String str){
        this.direction = str;
        shouldSend = true;
    }
    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket s = new ServerSocket(8988);
            Socket soc = s.accept();
            adr = soc.getInetAddress().toString().substring(1);

            Socket socket = null;
            try {
                socket = new Socket(adr, 8989);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Un BufferedReader permet de lire par ligne.
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(soc.getInputStream())
            );
            PrintWriter pred = null;
            try {
                pred = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                if(soc.getInputStream().available() > 0) {
                    String str = reader.readLine();
                    if (str.equals("END")) break;
                    recDirection = str;
                    publishProgress();
                }
                if(shouldSend){
                    pred.println(direction);
                    shouldSend = false;
                }
                if(false){break;}
            }
            reader.close();
            pred.close();
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
            this.gameView.move(recDirection);
            setDirection(this.gameView.getPositions());
        }
    }
}