package com.mi12.pierre.virtualpong.two_phones;

import com.mi12.pierre.virtualpong.CST;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by pierre on 25/05/16.
 */

public class SendClientTask extends Thread
{
    private byte dir = CST.MOVE_RIGHT;
    private InetAddress goIp;

    public SendClientTask(InetAddress _ip){goIp = _ip;}

    public void setDirection(byte _d){
        dir = _d;
    }

    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(goIp, CST.PORT_A);
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
        //should be synchro, for : wait() and notify()
        synchronized (this){
            while (true) {
                try {
                    dos.writeByte(dir);
                    dos.flush();
                    wait(); //the thread sleep, it will wake up when sensor detect gravity change
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

}