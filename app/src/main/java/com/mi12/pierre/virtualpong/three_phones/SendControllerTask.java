package com.mi12.pierre.virtualpong.three_phones;



import android.util.Log;

import com.mi12.pierre.virtualpong.CST;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */


public class SendControllerTask extends Thread
{
    private byte dir = CST.MOVE_RIGHT;
    private InetAddress goIp;
    private Integer port;

    public SendControllerTask(InetAddress _ip, Integer _port){
        goIp = _ip;
        port = _port;
    }
    public void setDirection(byte _d){
        dir = _d;
    }
    public void run() {
        Socket socket = null;
        //try goIp:  port 8988
        try {
            socket = new Socket(goIp, port);
        }
        catch (IOException e) {
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