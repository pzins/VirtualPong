package com.mi12.pierre.virtualpong.two_phones;

import android.os.Environment;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by pierre on 25/05/16.
 */

public class SendClientTask extends Thread
{
    private byte dir = 0x0;
    private InetAddress goIp;
    private FileOutputStream fstream;

    public SendClientTask(InetAddress _ip){
        goIp = _ip;

        File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/echantillonnage");

        dir.mkdirs();
        File file = new File(dir, "send.txt");
        try {
            fstream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setDirection(byte _d){
        dir = _d;
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
        int counter = 0;
        long curTime;
        //should be synchro, for : wait() and notify()
        synchronized (this){
            while (true) {
                try {
                    dos.writeByte(dir);
                    curTime = System.currentTimeMillis();
                    writeFile(Integer.toString(counter++) + " " + Long.toString(curTime) + "\n");
                    wait(); //the thread sleep, it will wake up when sensor detect gravity change
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void writeFile(String d)
    {
        try {
            fstream.write(d.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}