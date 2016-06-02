package com.mi12.pierre.virtualpong.two_phones;

import android.util.Log;

import com.mi12.pierre.virtualpong.CST;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by pierre on 25/05/16.
 */
public class SendServerTask extends Thread
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
            socket = new Socket(clientIp, CST.PORT_B);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ObjectOutputStream ois = null;
        try {
            ois = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Thread.sleep(CST.SERVER_DELAY); //delay between two game positions sending
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                ois.writeObject(gameView.getPositions());
                ois.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}