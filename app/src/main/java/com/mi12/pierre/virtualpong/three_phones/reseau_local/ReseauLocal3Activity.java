package com.mi12.pierre.virtualpong.three_phones.reseau_local;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mi12.R;
import com.mi12.pierre.virtualpong.three_phones.DrawActivityController;
import com.mi12.pierre.virtualpong.three_phones.DrawActivityScreen;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ReseauLocal3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseau_local3);
        Button bt_create = (Button) findViewById(R.id.create);
        bt_create.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ReseauLocal3Activity.this, DrawActivityScreen.class);
                        startActivity(intent);
                    }
                });
        final EditText ipServer   = (EditText)findViewById(R.id.ip);
        Button bt_join = (Button) findViewById(R.id.join);
        bt_join.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(((EditText) findViewById(R.id.ip)).getText().toString().matches("")){
                            Toast.makeText(ReseauLocal3Activity.this, "Please fill the IP address",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(ReseauLocal3Activity.this, DrawActivityController.class);
                            Bundle b = new Bundle();
                            try {
                                b.putSerializable("ip", InetAddress.getByName(ipServer.getText().toString()));
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            intent.putExtras(b);
                            startActivity(intent);
                        }
                    }
                });
        TextView myIp = (TextView) findViewById(R.id.myIp);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        myIp.setText(ip);
    }
}
