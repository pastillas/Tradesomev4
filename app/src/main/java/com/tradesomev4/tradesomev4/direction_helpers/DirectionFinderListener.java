package com.tradesomev4.tradesomev4.direction_helpers;

import java.util.List;

/**
 * Created by Pastillas-Boy on 7/18/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
