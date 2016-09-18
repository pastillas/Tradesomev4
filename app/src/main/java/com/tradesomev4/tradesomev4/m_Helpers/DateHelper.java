package com.tradesomev4.tradesomev4.m_Helpers;

import java.util.Calendar;

/**
 * Created by Pastillas-Boy on 7/19/2016.
 */
public class DateHelper {
    public static Calendar now = Calendar.getInstance();

    public static String getCurrentDateInMil(){
        return now.getTimeInMillis() + "";
    }
}
