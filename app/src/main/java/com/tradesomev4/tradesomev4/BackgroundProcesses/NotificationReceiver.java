package com.tradesomev4.tradesomev4.BackgroundProcesses;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tradesomev4.tradesomev4.BidNow;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.SendUserMessage;
import com.tradesomev4.tradesomev4.UserNotification;
import com.tradesomev4.tradesomev4.m_Helpers.Keys;
import com.tradesomev4.tradesomev4.m_Model.Notif;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Jorge Benigno Pante, Joshua Alarcon, Charles Torrente on 10/2/2016.
 * File Name: NotificationService.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\BackgroundProcesses\NotificationService.java
 * Description: Create a notification when a new notification from Firebase is Received.
 */
public class NotificationReceiver extends Service {
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<Notif> notifs;
    Map<Integer, Notif> notifMap;

    NotificationManager notificationManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifs = new ArrayList<>();
        initFirebase();
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            if (firebaseUser != null && databaseReference != null) {
                notificationReceiver();
            }
        }
    };


    public void createNotification(final int pos) {
        Notif notif = notifs.get(pos);

        Intent intent;
        PendingIntent pendingIntent;

        if (notif.getType().equals("bid") || notif.getType().equals("auctioner") || notif.getType().equals("finishAuctioner")
                || notif.getType().equals("finishBidder") || notif.getType().equals("finishWinner")) {
            intent = new Intent(getApplicationContext(), BidNow.class);
            Bundle bundle = new Bundle();
            bundle.putString(Keys.EXTRAS_AUCTION_ID, notif.getAuctionId());
            bundle.putString(Keys.EXTRAS_POSTER_ID, notif.getPosterId());
            intent.putExtra(Keys.EXTRAS_BUNDLE, bundle);
            pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            if(notif.getType().equals("message")){
                intent = new Intent(getApplicationContext(), SendUserMessage.class);
                Bundle bundle = new Bundle();
                bundle.putString(Keys.USER_ID_KEY, notif.getPosterId());
                intent.putExtra(Keys.BUNDLE_EXTRA_KEY, bundle);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
            }else{
                intent = new Intent(getApplicationContext(), UserNotification.class);
                pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);
            }
        }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                    .setContentTitle("Tradesome")
                    .setContentText(notif.getContent())
                    .setSmallIcon(R.drawable.ic_title_white_24dp)
                    .setContentIntent(pendingIntent)
                    .setWhen(Long.parseLong(notif.getDate()))
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                    .setColor(getResources().getColor(R.color.colorPrimary))
                    .setDefaults(Notification.FLAG_AUTO_CANCEL)
                    .setDefaults(Notification.DEFAULT_LIGHTS)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setDefaults(Notification.VISIBILITY_PUBLIC);

            notificationManager.notify(pos, builder.build());
    }

    public void notificationReceiver() {
        databaseReference.child("users").child(firebaseUser.getUid()).child("notifsBackground").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notif notif = dataSnapshot.getValue(Notif.class);

                if (!notif.isReceived()) {
                    notifs.add(notif);
                    databaseReference.child("users").child(firebaseUser.getUid()).child("notifsBackground").child(notif.getKey()).child("received").setValue(true);

                    boolean found = false;
                    //bid, auctioner, finishAuctioner, finishBidder, finishWinner, itemComplain, userComplain, message
                    for (int i = 0; i < notifs.size(); i++) {
                        Notif tmp = notifs.get(i);
                        if (tmp.getType().equals("bid") || tmp.getType().equals("auctioner") || tmp.getType().equals("finishAuctioner")
                                || tmp.getType().equals("finishBidder") || tmp.getType().equals("finishWinner")) {
                            if (tmp.getAuctionId().equals(notif.getAuctionId())) {
                                notificationManager.cancel(i);
                                createNotification(notifs.size() - 1);
                                found = true;
                                break;
                            }
                        } else {
                            if (tmp.getType().equals("message")) {
                                notificationManager.cancel(i);
                                createNotification(notifs.size() - 1);
                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found)
                        createNotification(notifs.size() - 1);

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(this, ServiceBroadcastReceiver.class);
        boolean isRunning = (PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_NO_CREATE) != null);
        if (!isRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);
        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
        notificationManager.cancelAll();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}
