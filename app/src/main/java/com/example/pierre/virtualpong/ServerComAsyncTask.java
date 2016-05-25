package com.example.pierre.virtualpong;

/**
 * Created by pierre on 01/04/16.
 */

import android.content.Context;
import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ServerComAsyncTask extends AsyncTask<Void, Byte, String> {

    private Context context;
    private String adr = "";

    private DrawActivityServer.GameView gameView = null;


    public ServerComAsyncTask(Context context, DrawActivityServer.GameView game) {
        this.context = context;
        this.gameView = game;
    }


    @Override
    protected String doInBackground(Void... params) {
        try
        {
            ServerSocket s = new ServerSocket(8988);
            Socket soc = s.accept();
            adr = soc.getInetAddress().toString().substring(1);

            //thread d'envoi des données
            SendServerTask st = new SendServerTask(gameView, adr);
            st.setPriority(Thread.MAX_PRIORITY);
            st.start();

            //lecture des données
            DataInputStream dis = new DataInputStream(soc.getInputStream());

            while (true) {
                publishProgress(dis.readByte());
                if (false) {break;}
            }
            soc.close();
            s.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return "";
    }
    @Override
    protected void onProgressUpdate(Byte... progress) {
        super.onProgressUpdate(progress);
        if(this.gameView != null) {
            if(progress[0] == 0x0)
                this.gameView.moveOpponent("g");
            else
                this.gameView.moveOpponent("d");
        }
    }
}

