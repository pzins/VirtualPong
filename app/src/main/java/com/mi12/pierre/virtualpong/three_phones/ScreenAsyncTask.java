package com.mi12.pierre.virtualpong.three_phones;


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
public class ScreenAsyncTask extends AsyncTask<Void, Byte, String> {

    private Context context;
    private String adr = "";

    private DrawActivityScreen.GameView gameView = null;


    private String groupOwnerIP;
    private Boolean shouldSend = false;
    private int port;
    public ScreenAsyncTask(Context context, DrawActivityScreen.GameView game, int _port) {
        this.context = context;
        this.gameView = game;
        this.port = _port;
    }



    public void setAdresseIp(String ip){
        this.groupOwnerIP = ip;
    }


    @Override
    protected String doInBackground(Void... params) {
        try
        {
            ServerSocket s = new ServerSocket(port);
            Socket soc = s.accept();
            //noinspection ResourceType
            gameView.readyToStart();

            //levture des données
            DataInputStream dis = new DataInputStream(soc.getInputStream());
            String str;
            while (true) {
                publishProgress(dis.readByte());
                if (false) {
                    break;
                }
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
            if(port == 8988){
                if(progress[0] == 0x0)
                    this.gameView.moveOpponent("g");
                else
                    this.gameView.moveOpponent("d");

            }else{
                if(progress[0] == 0x0)
                    this.gameView.movePlayer("g");
                else
                    this.gameView.movePlayer("d");
            }
        }
    }
}
