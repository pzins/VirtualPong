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

public class ReseauLocal3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reseau_local3);
        Button bt_create = (Button) findViewById(R.id.create);
        bt_create.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(ReseauLocal3Activity.this, DrawActivityScreen.class);
                                            startActivity(intent);
                                        }
                                    }
        );
        final EditText ipServer   = (EditText)findViewById(R.id.ip);
        Button bt_rejoindre = (Button) findViewById(R.id.join);
        bt_rejoindre.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(ReseauLocal3Activity.this, DrawActivityController.class);
                                                Bundle b = new Bundle();
                                                try {
                                                    b.putSerializable("ip", InetAddress.getByName(ipServer.getText().toString()));
                                                } catch (UnknownHostException e) {
                                                    e.printStackTrace();
                                                }
                                                b.putInt("port", 8988);
                                                intent.putExtras(b);
                                                startActivity(intent);
                                            }
                                        }
        );
    }
}
