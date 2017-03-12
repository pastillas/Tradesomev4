package com.tradesomev4.tradesomev4.direction_helpers;

import java.util.List;


/**
 * Created by Charles Torrente, Joshua Alarcon, Jorge Benigno Pante on 7/18/2016.
 * File Name: DirectionFinderListener.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\direction_helpers\DirectionFinderListener.java
 * Description: Parsing the Direction from Google Maps API reurned JSON data.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
