package com.tradesomev4.tradesomev4;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;

public class MonitorConnectivity extends AppCompatActivity {
    final String DEBUG_TAG = "NetworkStatusExample";
    TextView tv_connectivity;
    Connectivity connectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_connectivity);

        tv_connectivity = (TextView)findViewById(R.id.tv_connectivity);

        timer();
    }

    public void timer(){

        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            public void onTick(long millisUntilFinished) {
                tv_connectivity.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                connectivity = new Connectivity(getApplicationContext());

                if(connectivity.isConnected())
                    connectivity.showToastConnected();
                else
                    connectivity.showToastDisconnected();

                timer();
            }
        }.start();

    }
}
