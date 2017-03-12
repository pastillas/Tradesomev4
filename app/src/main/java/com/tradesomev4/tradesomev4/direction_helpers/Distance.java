package com.tradesomev4.tradesomev4.direction_helpers;

/**
 * Created by Charles Torrente, Joshua Alarcon, Jorge Benigno Pante on 7/18/2016.
 * File Name: Distance.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\direction_helpers\Distance.java
 * Description: Model class for parsing the distance from Google Maps API reurned JSON data.
 */
public class Distance {
    public String text;
    public int value;

    public Distance(String text, int value){
        this.text = text;
        this.value = value;
    }
}
