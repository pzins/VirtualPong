package com.mi12.pierre.virtualpong;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.mi12.R;
import com.mi12.pierre.virtualpong.three_phones.local_network.LocalNetwork3Activity;
import com.mi12.pierre.virtualpong.three_phones.wifidirect.WifiDirect3Activity;
import com.mi12.pierre.virtualpong.two_phones.local_network.LocalNetwork2Activity;
import com.mi12.pierre.virtualpong.two_phones.wifidirect.WifiDirect2Activity;

public class MainActivity extends Activity {

    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button) findViewById(R.id.wifi_direct_2p)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, WifiDirect2Activity.class);
                        startActivity(intent);
                    }
                }
        );
        ((Button) findViewById(R.id.local_network_2p)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LocalNetwork2Activity.class);
                        startActivity(intent);
                    }
                }
        );
        ((Button) findViewById(R.id.wifi_direct_3p)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, WifiDirect3Activity.class);
                        startActivity(intent);
                    }
                }
        );
        ((Button) findViewById(R.id.local_network_3p)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, LocalNetwork3Activity.class);
                        startActivity(intent);
                    }
                }
        );
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }


}
