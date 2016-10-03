package com.tradesomev4.tradesomev4.BackgroundProcesses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tradesomev4.tradesomev4.MainActivity;
import com.tradesomev4.tradesomev4.R;

/**
 * Created by Pastillas-Boy on 9/19/2016.
 */
public class NotificationService extends FirebaseMessagingService {
    private static final String DEBUG_TAG = "DEBUG_TAG";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(DEBUG_TAG, "MESSAGE FROM: " + remoteMessage.getFrom());

        if(remoteMessage.getData().size() > 0){
            Log.d(DEBUG_TAG, "MESSAGE DATA PAYLOAD: " + remoteMessage.getData());
        }

        if(remoteMessage.getNotification() != null){
            String messageBody = remoteMessage.getNotification().getBody();
            Log.d(DEBUG_TAG, "MESSAGE NOTIFCATION BODY: " + messageBody);
            sendNotification(messageBody);
        }
    }

    private void sendNotification(String messageBody){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificaitonBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_access_time_black_24dp)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificaitonBuilder.build());

    }
}
