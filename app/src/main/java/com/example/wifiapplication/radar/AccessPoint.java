package com.example.wifiapplication.radar;

//Хранение информации о точках доступа
public class AccessPoint {
    private String mSSID;
    private String mBSSID;
    private int mChannel;
    private int mLevel;
    private int mSecurity; // 0 - открытая сеть; 1 - WEP; 2 - WPA/WPA2
    private boolean mIsWPS;

    AccessPoint(String SSID, String BSSID, int Channel, int Level, int Security, boolean WPS) {
        this.mSSID = SSID;
        this.mBSSID = BSSID;
        this.mChannel = Channel;
        this.mLevel = Level;
        this.mSecurity = Security;
        this.mIsWPS = WPS;
    }

    String getSSID() {
        return mSSID;
    }

    String getBSSID() {
        return mBSSID;
    }

    int getChannel() {
        return mChannel;
    }

    int getLevel() {
        return mLevel;
    }

    int getSecurity() {
        return mSecurity;
    }

    boolean getWPS() {
        return mIsWPS;
    }
}
