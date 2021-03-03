package com.example.wifiapplication.radar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.example.wifiapplication.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RadarActivity extends AppCompatActivity {

    static final int maxSignalLevel = 100;

    private WifiManager mWifiManager;

    private ArrayList mAccessPoints;

    private RadarView mRadarView;

    final static ArrayList<Integer> channelsFrequency = new ArrayList<>(
            Arrays.asList(0, 2412, 2417, 2422, 2427, 2432, 2437, 2442, 2447,
                    2452, 2457, 2462, 2467, 2472, 2484));

    public static int getChannelFromFrequency(int frequency) {
        return channelsFrequency.indexOf(frequency);
    }

    public static int getSecurity(String capabilities) {
        if (capabilities.contains("[WPA")) return 2;
        else if (capabilities.contains("[WEP")) return 1;
        else return 0;
    }

    public static boolean getWPS(String capabilities) {
        return capabilities.contains("[WPS]");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radar);

        mRadarView = (RadarView) findViewById(R.id.radarView);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // Включаем WiFi
        if (!mWifiManager.isWifiEnabled())
            if (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                mWifiManager.setWifiEnabled(true);

        // Массив беспроводных точек доступа
        mAccessPoints = new ArrayList<>();
        mRadarView.setData(mAccessPoints);

        // Запуск сканирования эфира
        mWifiManager.startScan();
    }

    BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            List<ScanResult> results = mWifiManager.getScanResults();
            if (results == null) return;

            mAccessPoints.clear();
            for (ScanResult result : results) {
                AccessPoint AP = new AccessPoint(result.SSID,
                        result.BSSID,
                        getChannelFromFrequency(result.frequency),
                        WifiManager.calculateSignalLevel(result.level, maxSignalLevel),
                        getSecurity(result.capabilities),
                        getWPS(result.capabilities)
                );
                mAccessPoints.add(AP);
            }

            mRadarView.invalidate();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(scanReceiver, filter);
    }

    @Override
    public void onPause() {
        unregisterReceiver(scanReceiver);
        super.onPause();
    }
}