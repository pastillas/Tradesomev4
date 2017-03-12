package com.tradesomev4.tradesomev4.direction_helpers;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Jushua Alaracon, Jorge Benigno Pante, Charles Torrente on 7/18/2016.
 * File Name: Route.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\direction_helpers\Route.java
 * Description: Model class for parsing Google Maps returned JSON data.
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
