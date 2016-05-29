package com.mi12.pierre.virtualpong;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mi12.R;
import com.mi12.pierre.virtualpong.three_phones.reseau_local.ReseauLocal3Activity;
import com.mi12.pierre.virtualpong.three_phones.wifidirect.WifiDirect3Activity;
import com.mi12.pierre.virtualpong.two_phones.reseau_local.ReseauLocal2Activity;
import com.mi12.pierre.virtualpong.two_phones.wifidirect.WifiDirect2Activity;

public class MainActivity extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.reseau_local2){

            Intent intent = new Intent(MainActivity.this, ReseauLocal2Activity.class);
            startActivity(intent);
        }
        else if(id == R.id.wifidirect2){
            Intent intent = new Intent(MainActivity.this, WifiDirect2Activity.class);
            startActivity(intent);
        } else if(id == R.id.wifidirect3){
            Intent intent = new Intent(MainActivity.this, WifiDirect3Activity.class);
            startActivity(intent);
        } else if(id == R.id.reseau_local3){
            Intent intent = new Intent(MainActivity.this, ReseauLocal3Activity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
