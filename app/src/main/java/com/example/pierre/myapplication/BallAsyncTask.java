package com.example.pierre.myapplication;

import android.os.AsyncTask;

/**
 * Created by pierre on 11/05/16.
 */
public class BallAsyncTask  extends AsyncTask<Void, Integer, String> {

    DrawActivityServer.GameView gameView;

    public BallAsyncTask(DrawActivityServer.GameView gm) {
        gameView = gm;
    }


    @Override
    protected String doInBackground(Void... params) {
        while(true) {
            publishProgress();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(false){break;}
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        try {
            gameView.moveBall();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
