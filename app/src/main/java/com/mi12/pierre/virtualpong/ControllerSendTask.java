package com.mi12.pierre.virtualpong;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by pierre on 25/05/16.
 */
public class ControllerSendTask extends Thread
{
        private byte dir = 0x0;
        private boolean shouldSend = false;
        private String goIp;
        private int port;

        public ControllerSendTask(String _ip, int _port){
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
                run();
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
