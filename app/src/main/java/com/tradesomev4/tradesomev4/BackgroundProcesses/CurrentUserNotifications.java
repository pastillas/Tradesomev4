package com.tradesomev4.tradesomev4.BackgroundProcesses;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tradesomev4.tradesomev4.MainActivity;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_Model.Notif;

import java.util.ArrayList;
import java.util.List;

public class CurrentUserNotifications extends Service {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    Context context;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    ArrayList<Notif> notifs;
    int prevPos = 0;
    NotificationManagerCompat notificationManager;
    NotificationCompat.BigTextStyle bigStyle;

    public CurrentUserNotifications() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void createNotification(int position){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)System.currentTimeMillis(), intent, 0);

        Notif notif = notifs.get(notifs.size() - 1);
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Tradesome")
                .setContentText(notif.getContent())
                .setSmallIcon(R.drawable.ic_title_white_24dp)
                .setContentIntent(pendingIntent)
                .setWhen(Long.parseLong(notif.getDate()))
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setGroup(DEBUG_TAG)
                .setStyle(bigStyle)
                .build();


        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.visibility |= Notification.VISIBILITY_PUBLIC;
        notificationManager.notify(position, notification);
    }

    public void timer(){
        CountDownTimer timer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if( isForeground("com.tradesomev4.tradesomev4"))
                    notificationManager.cancelAll();
            }
        };
        timer.start();
    }

    public boolean isForeground(String myPackage) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager.getRunningTasks(1);
        ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
        return componentInfo.getPackageName().equals(myPackage);
    }

    public void startListener(){

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
        notifs = new ArrayList<>();
        notificationManager =  NotificationManagerCompat.from(this);
        bigStyle  = new NotificationCompat.BigTextStyle();
        //timer();

        databaseReference.child("users").child(firebaseUser.getUid()).child("notifs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notif notif = dataSnapshot.getValue(Notif.class);

                if(!notif.isRead()){
                    notifs.add(notif);
                    createNotification(notifs.size()-1);
                    Log.d(DEBUG_TAG, notif.getContent());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Notif notif = dataSnapshot.getValue(Notif.class);
                for (int i = 0; i < notifs.size(); i++){
                    if(notif.getKey().equals(notifs.get(i).getKey())){
                        notifs.remove(i);
                        break;
                    }
                }
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
    public void onCreate() {
        Log.e(DEBUG_TAG, "onCreate");

        startListener();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(DEBUG_TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(DEBUG_TAG, "onDestroy");
        super.onDestroy();
    }
}
