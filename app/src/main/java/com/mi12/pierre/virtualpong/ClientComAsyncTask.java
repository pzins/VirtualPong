package com.mi12.pierre.virtualpong;

import android.content.Context;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class ClientComAsyncTask extends AsyncTask<Void, GamePositions, String> {

    private GamePositions direction;
    private DrawActivityClient.GameView gameView;

    public ClientComAsyncTask (Context context, DrawActivityClient.GameView gameView) {
        this.gameView = gameView;
    }

    @Override
    protected String doInBackground(Void... params) {
        ServerSocket s;
        ObjectInputStream ois=  null;
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


//pour l'envoi des données (positions du jeu)
class SendClientTask extends Thread
{
    private byte dir = 0x0;
    private boolean shouldSend = false;
    private String goIp;

    public SendClientTask(String _ip){goIp = _ip;}

    public void setDirection(byte _d){
        dir = _d;
        shouldSend = true;
    }

    public void run() {
        Socket socket = null;
        try {
           socket = new Socket(goIp, 8988);
        } catch (IOException e) {
            e.printStackTrace();
            run();
        }
        DataOutputStream dos = null;

        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        synchronized (this){
            while (true) {
                try {
                    dos.writeByte(dir);
                    wait();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}