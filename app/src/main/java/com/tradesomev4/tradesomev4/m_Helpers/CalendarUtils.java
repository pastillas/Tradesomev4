package com.tradesomev4.tradesomev4.m_Helpers;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Pastillas-Boy on 7/20/2016.
 */
public class CalendarUtils {

    public static String dateFormat = "dd-MM-yyyy hh:mm a";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String ConvertMilliSecondsToFormattedDate(String milliSeconds){
        if(TextUtils.isEmpty(milliSeconds)) {
            return null;
        }else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(milliSeconds));
            return simpleDateFormat.format(calendar.getTime());
        }
    }
}