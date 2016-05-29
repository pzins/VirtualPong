package com.mi12.pierre.virtualpong.two_phones;

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
        while (true) {
            try {
                Thread.sleep(4); //delay between two game positions sending
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