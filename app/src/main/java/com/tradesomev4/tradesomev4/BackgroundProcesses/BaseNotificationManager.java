package com.tradesomev4.tradesomev4.BackgroundProcesses;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Pastillas-Boy on 9/23/2016.
 */
public class BaseNotificationManager extends BroadcastReceiver {


    public static final String BaseAction = "Com.TST.BaseAction";
    public static final String FireService = "Com.TST.FireNotificationAction";


    private static final long timeFrame = 5000;  // 5 mints

    public BaseNotificationManager() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            /// add base alarm manager
            startBaseAlarmManager(context);

        } else if (BaseAction.equals(intent.getAction())) {

            context.startService(new Intent(context.getApplicationContext(), LocationService.class));

            context.startService(new Intent(context.getApplicationContext(), CurrentUserNotifications.class));
        }
    }

    public static void startBaseAlarmManager(Context context) {


        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BaseNotificationManager.class);
        intent.setAction(BaseAction);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                1000, timeFrame, alarmIntent);

    }
}
