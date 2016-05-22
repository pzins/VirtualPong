package com.example.pierre.myapplication;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */


class SendClientTask extends Thread
{
    private byte dir = 0x0;
    private boolean shouldSend = false;
    private String goIp;
    private int port;

    public SendClientTask(String _ip, int _port){
        goIp = _ip;
        port = _port;
    }
    public void setDirection(byte _d){
        dir = _d;
        shouldSend = true;

    }
    public void run() {
        Socket socket = null;
        try {
            socket = new Socket(goIp, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (true) {
            try {
                if(shouldSend){
                    dos.writeByte(dir);
                    dos.flush();
                    shouldSend = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}