package com.tradesomev4.tradesomev4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

/**
 * Created by Josh on 7/13/2016.
 */

public class Categories extends AppCompatActivity implements CardView.OnClickListener{
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String CATEGORY_KEY = "CATEGORY_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        findViewById(R.id.antiques).setOnClickListener(this);
        findViewById(R.id.commodities).setOnClickListener(this);
        findViewById(R.id.gadgets).setOnClickListener(this);
        findViewById(R.id.collectibles).setOnClickListener(this);
        findViewById(R.id.livestocks).setOnClickListener(this);
        findViewById(R.id.machinery).setOnClickListener(this);
        findViewById(R.id.cars).setOnClickListener(this);
        findViewById(R.id.others).setOnClickListener(this);
        findViewById(R.id.jewelries).setOnClickListener(this);

    }

        @Override
        public void onClick(View view) {
            int item = view.getId();
            Intent chosenCategory = new Intent(getApplicationContext(), ChosenCategory.class);
            Bundle extras = new Bundle();

            switch (item){
                case R.id.antiques:
                    extras.putString(CATEGORY_KEY, "Antiques");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.commodities:
                    extras.putString(CATEGORY_KEY, "Commodities");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.gadgets:
                    extras.putString(CATEGORY_KEY, "Gadgets");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.collectibles:
                    extras.putString(CATEGORY_KEY, "Rare Collectibles");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.jewelries:
                    extras.putString(CATEGORY_KEY, "Jewelries");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.livestocks:
                    extras.putString(CATEGORY_KEY, "Livestock");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.machinery:
                    extras.putString(CATEGORY_KEY, "Industrial Machinery");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.cars:
                    extras.putString(CATEGORY_KEY, "Cars and Motors");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
                case R.id.others:
                    extras.putString(CATEGORY_KEY, "Others");
                    chosenCategory.putExtra(BUNDLE_KEY, extras);
                    startActivity(chosenCategory);
                    break;
            }


        }



}