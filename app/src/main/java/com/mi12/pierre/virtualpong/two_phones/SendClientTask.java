package com.mi12.pierre.virtualpong.two_phones;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by pierre on 25/05/16.
 */

//pour l'envoi des données (positions du jeu)
public class SendClientTask extends Thread
{
    private byte dir = 0x0;
    private boolean shouldSend = false;
    private InetAddress goIp;

    public SendClientTask(InetAddress _ip){goIp = _ip;}

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