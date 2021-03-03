package com.example.wifiapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.example.wifiapplication.radar.RadarActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private TextView textView;
    private TextView mInfoTextView;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        mInfoTextView = (TextView) findViewById(R.id.textView);
        wiFiState();
        textView.setText(getString(R.string.ipAdress) + getDeviceCurrentIPAddress());

        this.registerReceiver(this.WifiStateChangedReceiver, new IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION));
    }

    private BroadcastReceiver WifiStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int extraWifiState = intent.getIntExtra(
                    WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);

            switch (extraWifiState) {
                case WifiManager.WIFI_STATE_DISABLED:
                    mInfoTextView.setText("WIFI недоступен");
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    mInfoTextView.setText("WIFI отключается");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    mInfoTextView.setText("WIFI доступен");
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    mInfoTextView.setText("WIFI включается");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    mInfoTextView.setText("WIFI: неизвестное состояние");
                    break;
            }
        }
    };

    public String getDeviceCurrentIPAddress(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ip = wifiManager.getConnectionInfo().getIpAddress();

        String ipString = String.format(getString(R.string.format_string), (ip & 0xff),
                (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        return ipString;
    }

    public void wiFiState(){
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){
            if(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED){
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    public void openSetting(View view) {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    public void connectConfiguration(View view) {
        @SuppressLint("MissingPermission") List<WifiConfiguration> wifiManagerList = wifiManager.getConfiguredNetworks();
        if (wifiManagerList.size() > 0) {
            int netID = wifiManagerList.get(0).networkId;
            boolean isEn = true;
            wifiManager.enableNetwork(netID, isEn);
        }
    }

    public void nextInfo(View view) {
        Intent intent = new Intent(this,Activity.class);
        startActivity(intent);
    }

    public void scanOpen(View view) {
        Intent intent = new Intent(this,ScanActivity.class);
        startActivity(intent);
    }

    public void RadarClick(View view) {
        Intent intent = new Intent(this, RadarActivity.class);
        startActivity(intent);
    }


}