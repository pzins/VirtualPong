package com.mi12.pierre.virtualpong;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mi12.R;

/**
 * Created by pierre on 03/04/16.
 */
public class SensorFragment extends Fragment {
    private View mContentView = null;


    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        mContentView = inflater.inflate(R.layout.device_sensors, null);
        return mContentView;
    }



}
