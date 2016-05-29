package com.mi12.pierre.virtualpong.two_phones;

/**
 * Created by pierre on 01/04/16.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ServerComAsyncTask extends AsyncTask<Void, Byte, String> {

    private Context context;
    private ProgressDialog progress; //to wait for the sceond player
    private String adr; //ip adr of the other player

    private DrawActivityServer.GameView gameView = null;


    public ServerComAsyncTask(Context context, DrawActivityServer.GameView game) {
        this.context = context;
        this.gameView = game;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = ProgressDialog.show(context, "Wait", "for the other player", true, true);
    }

    @Override
    protected String doInBackground(Void... params) {
        try
        {
            ServerSocket s = new ServerSocket(8988);
            Socket soc = s.accept();
            progress.dismiss(); //stop dialog progress when the other player is connected
            adr = soc.getInetAddress().toString().substring(1); //get ip adr of the other player

            //starting a new thread to send data to the player
            SendServerTask st = new SendServerTask(gameView, adr);
            st.setPriority(Thread.MAX_PRIORITY);
            st.start();

            gameView.thread.start(); //start game thread
            gameView.setIsFirstLaunch(false); //the game is launched

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

