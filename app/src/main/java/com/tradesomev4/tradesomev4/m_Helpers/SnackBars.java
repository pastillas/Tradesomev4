package com.tradesomev4.tradesomev4.m_Helpers;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.widget.Snackbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;

import com.tradesomev4.tradesomev4.R;

/**
 * Created by Pastillas-Boy on 9/7/2016.
 */
public class SnackBars {
    View view;
    Context context;

    public SnackBars(View view, Context context) {
        this.view = view;
        this.context = context;
    }

    public void showConnectionRestored(){
        String str = "@ Connected";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_network_wifi_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, tmp, Snackbar.LENGTH_LONG).show();
    }

    public void showConnectionDisabledDialog(){
        String str = "@ No Internet Connection";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, tmp, Snackbar.LENGTH_LONG).show();
    }


    public void showCheckYourConnection(){
        String str = "@ Please check your connection.";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_signal_wifi_off_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, tmp, Snackbar.LENGTH_LONG).show();
    }

    public void gpsRestored(){String str = "@ GPS Active";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_location_on_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, tmp, Snackbar.LENGTH_LONG).show();
    }

    public void showGpsDiabledDialog(){
        String str = "@ GPS Required";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_location_off_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, tmp, Snackbar.LENGTH_LONG).show();
    }

    public void failedToGetLocation(){
        String str = "@ Failed to get location, Please try again.";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_location_off_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, tmp, Snackbar.LENGTH_LONG).show();
    }

    public void isAccountBlocked(){
        String str = "@ Sorry this account blocked.";
        SpannableString tmp = new SpannableString(str);

        int index = str.indexOf('@');
        Drawable icon = context.getResources().getDrawable(R.drawable.ic_warning_white_24dp);
        icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(icon, ImageSpan.ALIGN_BOTTOM);

        tmp.setSpan(span, index, index + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        Snackbar.make(view, tmp, Snackbar.LENGTH_LONG).show();
    }
}
