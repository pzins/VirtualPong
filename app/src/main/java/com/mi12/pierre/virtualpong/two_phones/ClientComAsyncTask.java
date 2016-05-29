package com.mi12.pierre.virtualpong.two_phones;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ClientComAsyncTask extends AsyncTask<Void, GamePositions, String> {

    private DrawActivityClient.GameView gameView;

    public ClientComAsyncTask (Context context, DrawActivityClient.GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    protected String doInBackground(Void... params) {
        ServerSocket s;
        ObjectInputStream ois = null;
        Socket soc;
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
                publishProgress((GamePositions) ois.readObject());
            } catch (IOException | ClassNotFoundException e) {
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
    protected void onProgressUpdate(GamePositions... progress) {
        super.onProgressUpdate(progress);
        if(this.gameView != null) {
            this.gameView.setPositions(progress[0]);
        }
    }
}

