package com.tradesomev4.tradesomev4.direction_helpers;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Pastillas-Boy on 7/18/2016.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endlocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
