package com.tradesomev4.tradesomev4.BackgroundProcesses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Jorge Benigno Pante, Joshua Alarcon, Charles Torrente on 10/2/2016.
 * File Name: ServiceBroadcastReceiver.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\BackgroundProcesses\ServiceBroadcastReceiver.java
 * Description: Service Broadcast Receiver that will start Notification Service if an action signal is received from android OS.
 */


public class ServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationReceiver.class));
    }
}
