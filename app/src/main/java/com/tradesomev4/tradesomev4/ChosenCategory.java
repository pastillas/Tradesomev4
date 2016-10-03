package com.tradesomev4.tradesomev4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.m_Helpers.Keys;
import com.tradesomev4.tradesomev4.m_UI.ChosenCategoryAdapter;

public class ChosenCategory extends AppCompatActivity {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    public static final String BUNDLE_KEY = "BUNDLE_KEY";
    public static final String CATEGORY_KEY = "CATEGORY_KEY";
    Toolbar toolbar;
    Bundle args;
    private ChosenCategoryAdapter adapter;
    private RecyclerView rv;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    View content_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chosen_category);
        toolbar = (Toolbar) findViewById(R.id.app_bar_messaging);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        args = getIntent().getBundleExtra(BUNDLE_KEY);
        getSupportActionBar().setTitle(args.getString(CATEGORY_KEY));

        content_main = findViewById(R.id.content_main);
        tv_items_here = (TextView) findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView) findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        boolean isAttached;
        onAttachedToWindow();
        isAttached = true;

        rv = (RecyclerView)findViewById(R.id.main_recycler_view);
        adapter = new ChosenCategoryAdapter(this, args.getString(CATEGORY_KEY),  isAttached, rv, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, content_main);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_categories, menu);
        return true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sv_search_messages) {
            Bundle bundle = new Bundle();
            bundle.putString(Keys.CATEGORY_KEY, args.getString(CATEGORY_KEY));
            Intent intent = new Intent(getApplicationContext(), SearchItem.class);
            intent.putExtra(Keys.BUNDLE_KEY, bundle);
            startActivity(intent);
        }

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
