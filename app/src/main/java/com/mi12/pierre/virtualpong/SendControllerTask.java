package com.mi12.pierre.virtualpong;



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
    private byte dir = 0x0;
    private boolean shouldSend = false;
    private InetAddress goIp;
    private int port;

    public SendControllerTask(InetAddress _ip, int _port){
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
            socket = new Socket(goIp, 8988);
        }
        catch(ConnectException ce){
            ce.printStackTrace();
            try {
                socket = new Socket(goIp, 8989);
            } catch (IOException e) {
                e.printStackTrace();
                run();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            try {
                socket.connect(new InetSocketAddress(goIp, 8989), 100);
            } catch (IOException e) {
                e.printStackTrace();
                run();
            }
        }
       /* try {
            socket = new Socket(goIp, port);
        } catch (IOException e) {
            e.printStackTrace();
            run();
        }*/


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