package com.example.wifiapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class ScanActivity extends AppCompatActivity {

    private WifiManager mManager;
    private TextView mInfoTextView;

    // Приёмник для сканирования точек доступа
    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = mManager.getScanResults();
            ScanResult bestSignal = null;
            mInfoTextView.setText("Результат сканирования: " + results.size() + " точек");

            for (ScanResult result : results) {
                if(bestSignal == null || WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0){
                    bestSignal = result;
                }

                mInfoTextView.append("\nSSID: " + result.SSID);
                mInfoTextView.append("\nLevel: " + result.level + " dBm");
                Log.i("Уровень: ", result.BSSID.toString());
                mInfoTextView.append("\nFrequency: " + result.frequency + " MHz");
                mInfoTextView.append(("\nCapabilities: " + result.capabilities));
            }

            mInfoTextView.append("Лучший сигнал: " + bestSignal.SSID);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);

        mInfoTextView = findViewById(R.id.textViewResult);

        mManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public void onClick(View view) {
        registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mManager.startScan();
        Log.i("Клик на кнопку: ",mManager.getConnectionInfo().toString());
    }
}
