package com.tradesomev4.tradesomev4.direction_helpers;

/**
 * Created by Jushua Alaracon, Jorge Benigno Pante, Charles Torrente on 7/18/2016.
 * File Name: Duration.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\direction_helpers\Duration.java
 * Description: Model class for getting the duration or how long it will take to travel from first
 *              coorinate to the second.
 */
public class Duration {
    public String text;
    public int value;

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }
}
