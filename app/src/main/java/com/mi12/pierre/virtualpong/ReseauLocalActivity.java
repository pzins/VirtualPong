package com.mi12.pierre.virtualpong;

import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mi12.R;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ReseauLocalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseau_local);
        Button bt_rejoindre = (Button) findViewById(R.id.rejoindre);
        bt_rejoindre.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(ReseauLocalActivity.this, DrawActivityServer.class);
                                                startActivity(intent);
                                            }
                                        }
        );
        final EditText mEdit   = (EditText)findViewById(R.id.editText);
        Button bt_creer = (Button) findViewById(R.id.creer);
        bt_creer.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(ReseauLocalActivity.this, DrawActivityClient.class);
                                                Bundle b = new Bundle();
                                                try {
                                                    b.putSerializable("ip", InetAddress.getByName(mEdit.getText().toString()));
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                }
                                                b.putInt("port", 8988);
                                                intent.putExtras(b);
                                                startActivity(intent);
                                            }
                                        }
        );

        TextView myIp = (TextView) findViewById(R.id.myIp);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        myIp.setText(ip);
    }
}
