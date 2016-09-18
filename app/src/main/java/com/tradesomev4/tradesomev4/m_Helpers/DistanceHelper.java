package com.tradesomev4.tradesomev4.m_Helpers;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

/**
 * Created by Pastillas-Boy on 7/25/2016.
 */
public class DistanceHelper {
    public static double getDistance(LatLng user1, LatLng user2){
        double distance = SphericalUtil.computeDistanceBetween(user1, user2);

        return distance;
    }

    public static String formatNumber(double distance) {
        String unit = "m";
        if (distance < 1) {
            distance *= 1000;
            unit = "mm";
        } else if (distance > 1000) {
            distance /= 1000;
            unit = "km";
        }

        return String.format("%4.3f%s", distance, unit);
    }
}
