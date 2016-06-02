package com.mi12.pierre.virtualpong.two_phones;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private FileOutputStream fstream;

    public SendServerTask(DrawActivityServer.GameView _gw, String _ip){
        gameView = _gw;
        clientIp = _ip;
        Thread.currentThread().setPriority(MAX_PRIORITY);
        File dir = new File (Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/echantillonnage");

        dir.mkdirs();
        File file = new File(dir, "server_send.txt");
        try {
            fstream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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
//                writeFile(Long.toString(System.currentTimeMillis()) + "\n");

            } catch (IOException e) {
                e.printStackTrace();
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