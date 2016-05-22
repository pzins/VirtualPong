package com.example.pierre.myapplication;

/**
 * Created by pierre on 01/04/16.
 */

import android.content.Context;
import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private GamePositions direction;

    private String groupOwnerIP;
    private Boolean shouldSend = false;
    public ServerComAsyncTask(Context context, DrawActivityServer.GameView game) {
        this.context = context;
        this.gameView = game;
    }



    public void setAdresseIp(String ip){
        this.groupOwnerIP = ip;
    }

    public void setDirection(GamePositions str){
        this.direction = str;
        shouldSend = true;
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
            if(progress[0] == 0x0)
                this.gameView.moveOpponent("g");
            else
                this.gameView.moveOpponent("d");
        }
    }
}

//pour l'envoi des données (positions du jeu)
class SendServerTask extends Thread
{
    private DrawActivityServer.GameView gameView;
    private String clientIp;
    public SendServerTask(DrawActivityServer.GameView _gw, String _ip){
        gameView = _gw;
        clientIp = _ip;
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(clientIp, 8989);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectOutputStream ois = null;
        try {
            ois = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String str;

        while (true) {
            try {
                Thread.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                ois.writeObject(gameView.getPositions());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}