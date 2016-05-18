package com.example.pierre.myapplication;

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
public class ClientComAsyncTask extends AsyncTask<Void, Integer, String> {

    private Context context;
    private String groupOwnerIP;
    private Boolean shouldSend = false;
    private String direction = "";
    private String sendDirection = "";

    private DrawActivityClient.GameView gameView;

    public ClientComAsyncTask (Context context, String ip, DrawActivityClient.GameView gameView) {
        this.context = context;
        this.groupOwnerIP = ip;
        this.gameView = gameView;
    }

    public void setAdresseIp(String ip){
        this.groupOwnerIP = ip;
    }

     public void setDirection(String str){
        this.sendDirection = str;
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
        PrintWriter pred = null;
        try {
            pred = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        ServerSocket s = null;
        BufferedReader reader = null;
        Socket soc = null;
        try {
            s = new ServerSocket(8989);
            soc = s.accept();
            reader = new BufferedReader(
                new InputStreamReader(soc.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        String str;
        int counter = 0;
        while (true)
        {
            if(shouldSend){
                pred.println(sendDirection);
                shouldSend = false;
            }

            try {
                if(soc.getInputStream().available() > 0) {
                    str = reader.readLine();
                    if (str.equals("END")) break;
                    direction = str;
                    publishProgress();
                    Log.w("COUNTER", Integer.toString(counter));
                    counter++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(false){break;}
        }
        pred.println("END");


        pred.close();
        try {
            reader.close();
            s.close();
            soc.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "OL";
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        if(this.gameView != null) {
//            this.clientGameView.move(direction);
            this.gameView.setPositions(direction);
        }
    }

}

