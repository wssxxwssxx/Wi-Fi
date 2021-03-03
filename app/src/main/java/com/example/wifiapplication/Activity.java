package com.example.wifiapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

public class Activity extends AppCompatActivity {

    TextView tvConnected, tvIP, tvSsid, tvBssid, tvMac, tvSpeed, textRssi,tvFrequency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_);
        tvConnected = findViewById(R.id.Connected);
        tvIP = findViewById(R.id.tvIP);
        tvSsid = findViewById(R.id.tvSsid);
        tvBssid = findViewById(R.id.tvBssid);
        tvMac = findViewById(R.id.tvMac);
        tvSpeed = findViewById(R.id.tvSpeed);
        textRssi = findViewById(R.id.tvRssi);
        tvFrequency = findViewById(R.id.Frequency);

        displayWifiState();

        this.registerReceiver(this.myWifiReceiver,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        this.registerReceiver(this.myRssiChangeReceiver,
                new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    private BroadcastReceiver myRssiChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int newRssi = arg1.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
            textRssi.setText(String.valueOf(newRssi));
        }
    };

    private BroadcastReceiver myWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                displayWifiState();
            }
        }
    };

    private void displayWifiState() {
        ConnectivityManager myConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo myNetworkInfo = myConnManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // В Android 6.0 и выше не работает
        tvMac.setText(wifiInfo.getMacAddress());

        if (myNetworkInfo.isConnected()) {
            int myIp = wifiInfo.getIpAddress();

            tvConnected.setText("Есть соединение");

            int intMyIp3 = myIp / 0x1000000;
            int intMyIp3mod = myIp % 0x1000000;

            int intMyIp2 = intMyIp3mod / 0x10000;
            int intMyIp2mod = intMyIp3mod % 0x10000;

            int intMyIp1 = intMyIp2mod / 0x100;
            int intMyIp0 = intMyIp2mod % 0x100;

            tvIP.setText(String.valueOf(intMyIp0) + "."
                    + String.valueOf(intMyIp1) + "." + String.valueOf(intMyIp2)
                    + "." + String.valueOf(intMyIp3));

            tvSsid.setText(wifiInfo.getSSID());
            tvBssid.setText(wifiInfo.getBSSID());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tvFrequency.setText(wifiInfo.getFrequency() + wifiInfo.FREQUENCY_UNITS);
            }

            tvSpeed.setText(String.valueOf(wifiInfo.getLinkSpeed()) + " "
                    + WifiInfo.LINK_SPEED_UNITS);
            textRssi.setText(String.valueOf(wifiInfo.getRssi()));

        } else {
            tvConnected.setText("Нет соединения");
            tvIP.setText("---");
            tvSsid.setText("---");
            tvBssid.setText("---");
            tvSpeed.setText("---");
            textRssi.setText("---");
        }
    }
}