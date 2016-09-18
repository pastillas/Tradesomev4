package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by Pastillas-Boy on 9/10/2016.
 */
public class Dialogs {

    public static  void showPakonSwelo(Context context){
        new MaterialDialog.Builder(context)
                .title("Sorry!")
                .content("We are experiencing some issues right now, Please try again later. Thank you.")
                .positiveText("OK")
                .show();
    }
}
