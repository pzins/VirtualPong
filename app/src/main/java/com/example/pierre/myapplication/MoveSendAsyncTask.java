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
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class MoveSendAsyncTask extends AsyncTask<Void, Void, String> {

    Context context;
    String groupOwnerIP;
    Boolean shouldSend = false;
    String direction = "";



    public MoveSendAsyncTask (Context context, String ip) {
        this.context = context;
        this.groupOwnerIP = ip;
    }
    public MoveSendAsyncTask (Context context) {
        this.context = context;
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
        Log.w("PRED= ", Boolean.toString((pred== null)));
        while (true)
        {
            if(shouldSend){
                pred.println(direction);
                shouldSend = false;
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


}