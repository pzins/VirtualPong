package com.mi12.pierre.virtualpong.three_phones;



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

    public SendControllerTask(InetAddress _ip){
        goIp = _ip;
    }
    public void setDirection(byte _d){
        dir = _d;
    }
    public void run() {
        Socket socket = null;
        //try goIp:  port 8988
        try {
            socket = new Socket(goIp, CST.PORT_A);
        }
        catch(ConnectException ce){
            //if connection failed : try a second port : 8989
            try {
                socket = new Socket(goIp, CST.PORT_B);
            } catch (IOException e) {
                //if connection failed loop to run
                // => wait until the controller is connected to the screen)
                run();
            }
        } catch (IOException e) {
            e.printStackTrace();
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