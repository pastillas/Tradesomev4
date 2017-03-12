package com.tradesomev4.tradesomev4.m_Helpers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.widget.Toast;


/*
* Created by: Joshua Alarcon, Jorge Benigno Pant, Charles Torrente on 9/14/2016.
* File Name: VibrateService.java
* File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\m_Helpers\VibrateService.java
* Desciption: Background process that will vibrate the phone if a new notification is received from Firebase.
*/

public class VibrateService  extends Service
{



    @Override
    public void onStart(Intent intent, int startId)
    {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);



        Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        // pass the number of millseconds fro which you want to vibrate the phone here we
        // have passed 2000 so phone will vibrate for 2 seconds.

        v.vibrate(2000);

        // If you want to vibrate  in a pattern
        //  long pattern[]={0,800,200,1200,300,2000,400,4000};
        // 2nd argument is for repetition pass -1 if you do not want to repeat the Vibrate
        // v.vibrate(pattern,-1);

        Toast.makeText(getApplicationContext(), "Phone is Vibrating", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO Auto-generated method stub
        return null;
    }

}

