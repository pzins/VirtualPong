package com.example.pierre.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
    private Boolean shouldSend = false;
    private GamePositions direction;
    private String sendDirection = "";

    private DrawActivityClient.GameView gameView;

    public ClientComAsyncTask (Context context, DrawActivityClient.GameView gameView) {
        this.context = context;
        this.gameView = gameView;
    }

     public void setDirection(String str){
        this.sendDirection = str;
        shouldSend = true;
    }
    @Override
    protected String doInBackground(Void... params) {
        ServerSocket s = null;
        ObjectInputStream ois=  null;
        Socket soc = null;
        try {
            s = new ServerSocket(8989);
            soc = s.accept();
            ois = new ObjectInputStream(soc.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true)
        {
            try {
                GamePositions tmp = (GamePositions) ois.readObject();
                direction = tmp;
                    publishProgress();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if(false){break;}
        }
        try{
            s.close();
            soc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        if(this.gameView != null) {
            this.gameView.setPositions(direction);
        }
    }

}


//pour l'envoi des données (positions du jeu)
class SendClientTask extends Thread
{
    private byte dir = 0x0;
    private boolean shouldSend = false;
    private String goIp;

    public SendClientTask(String _ip){goIp = _ip;}
    public void setDirection(byte _d){dir = _d;shouldSend = true;}
    public void run() {

        Socket socket = null;
        try {
            socket = new Socket(goIp, 8988);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataOutputStream dos = null;

        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        while (true) {
            try {
                if(shouldSend){
                    dos.writeByte(dir);
                    dos.flush();
                    shouldSend = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}