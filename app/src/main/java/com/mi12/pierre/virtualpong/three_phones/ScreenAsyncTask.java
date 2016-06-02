package com.mi12.pierre.virtualpong.three_phones;


import android.content.Context;
import android.os.AsyncTask;

import com.mi12.pierre.virtualpong.CST;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ScreenAsyncTask extends AsyncTask<Void, Byte, String> {

    private DrawActivityScreen.GameView gameView = null;

    private Boolean shouldSend = false;
    private int port;
    public ScreenAsyncTask(DrawActivityScreen.GameView game, int _port) {
        this.gameView = game;
        this.port = _port;
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
            if(port == CST.PORT_A){
                if(progress[0] == CST.MOVE_RIGHT)
                    this.gameView.moveOpponent("g");
                else
                    this.gameView.moveOpponent("d");

            }else{
                if(progress[0] == CST.MOVE_RIGHT)
                    this.gameView.movePlayer("g");
                else
                    this.gameView.movePlayer("d");
            }
        }
    }
}
