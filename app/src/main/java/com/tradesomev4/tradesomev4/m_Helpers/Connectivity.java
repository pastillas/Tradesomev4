package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by Pastillas-Boy on 9/5/2016.
 */
public class Connectivity {
    ConnectivityManager connectivityManager;
    NetworkInfo wifiData;
    NetworkInfo mobileData;
    Context context;
    static boolean statusDisplayed = false;
    String provider;

    public Connectivity(Context context) {
        this.context = context;
        provider = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    }

    public boolean gpsOn(){
        if(provider.contains("gps"))
            return true;
        else
            return false;
    }

    public boolean isConnected(){
        return wifiData.isConnected() || mobileData.isConnected();
    }

    public void showToastConnected(){
        if(statusDisplayed) {
            Toast.makeText(context, "Connected!", Toast.LENGTH_SHORT).show();
            statusDisplayed = false;
        }
    }

    public void showToastDisconnected(){
        if(!statusDisplayed) {
            statusDisplayed = true;
            Toast.makeText(context, "Disconnected! some functionalities are disabled.", Toast.LENGTH_SHORT).show();
        }
    }
}
