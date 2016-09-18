package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.Context;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Pastillas-Boy on 9/5/2016.
 */
public class ToastFactory {
    Context context;

    public ToastFactory(Context context){
        this.context = context;
    }

    public void gpsDisabled(){
        Toast.makeText(context, "GPS is disabled, Please enable GPS.", Toast.LENGTH_SHORT).show();
    }

    public void gpsEnabled(){
        Toast.makeText(context, "GPS enabled.", Toast.LENGTH_SHORT).show();
    }

    public void gpsDisabledDialog(){

    }
}
