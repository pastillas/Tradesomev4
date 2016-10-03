package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Pastillas-Boy on 10/2/2016.
 */
public class ServiceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotificationReceiver.class));
    }
}
