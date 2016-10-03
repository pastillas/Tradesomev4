package com.tradesomev4.tradesomev4.m_Helpers;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.MainActivity;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_Model.Notif;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Pastillas-Boy on 10/2/2016.
 */
public class NotificationReceiver extends Service {
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<Notif>notifs;
    Query notifsListener;
    Query notifListener;
    ChildEventListener notifsChildListener;
    ValueEventListener notifsValueEventListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void initFirebase(){
        try{
            if(FirebaseApp.getInstance() == null){
                Context context = getApplicationContext();
                FirebaseApp.initializeApp(context, FirebaseOptions.fromResource(context));
            }
        }catch (IllegalStateException e){
            Context context = getApplicationContext();
            FirebaseApp.initializeApp(context, FirebaseOptions.fromResource(context));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void onCreate() {
        notifs = new ArrayList<>();
        initFirebase();
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {
            Random random = new Random();
            if(firebaseUser != null && databaseReference != null){
                notificationReceiver();
            }
        }
    };

    public void createNotification(final int pos){
        notifListener = databaseReference.child("users").child(firebaseUser.getUid()).child("notifs").child(notifs.get(pos - 1).getKey());
        notifsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notif notif = dataSnapshot.getValue(Notif.class);


                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int)System.currentTimeMillis(), intent, 0);

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


                NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(pos, builder.build());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        notifListener.addValueEventListener(notifsValueEventListener);
    }

    public void notificationReceiver(){

        notifsListener = databaseReference.child("users").child(firebaseUser.getUid()).child("notifs");
        notifsChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notif notif = dataSnapshot.getValue(Notif.class);
                notifs.add(notif);
                createNotification(notifs.size());
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
        };
        notifListener.addChildEventListener(notifsChildListener);
    }
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent intent = new Intent(this, ServiceBroadcastReceiver.class);
        boolean isRunning = (PendingIntent.getBroadcast(this, 0, intent,PendingIntent.FLAG_NO_CREATE) != null);
        if(isRunning == false){
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager alarmManager  = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000, pendingIntent);
        }

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
        notifListener.removeEventListener(notifsValueEventListener);
        notifsListener.removeEventListener(notifsChildListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }
}
