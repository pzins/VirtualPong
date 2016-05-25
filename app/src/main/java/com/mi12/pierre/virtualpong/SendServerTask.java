package com.mi12.pierre.virtualpong;

/**
 * Created by pierre on 01/04/16.
 */

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;



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