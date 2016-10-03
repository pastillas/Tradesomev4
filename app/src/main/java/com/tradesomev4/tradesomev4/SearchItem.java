package com.tradesomev4.tradesomev4;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.m_Helpers.Keys;
import com.tradesomev4.tradesomev4.m_UI.SearchItemAdapter;

public class SearchItem extends AppCompatActivity {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private RecyclerView recyclerView;
    private SearchItemAdapter adapter;
    private FirebaseUser fUser;
    int prevChoice = 0;
    CharSequence category = "All";
    SearchView sv;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    View content_main;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item);
        extras = getIntent().getBundleExtra(Keys.BUNDLE_KEY);

        content_main = findViewById(R.id.content_main);
        tv_items_here = (TextView) findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView) findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);


        boolean isAttached;
        onAttachedToWindow();
        isAttached = true;

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) findViewById(R.id.rv_search_item);
        adapter = new SearchItemAdapter(this, fUser, "All", isAttached, recyclerView, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, content_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_item, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv = (SearchView)menu.findItem(R.id.search_item).getActionView();

        sv.requestFocus();

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(category + ":" + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(category + ":"+newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.categories:
                new MaterialDialog.Builder(this)
                        .title("Categories")
                        .items(R.array.chooseCategory)
                        .itemsCallbackSingleChoice(prevChoice, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                prevChoice = which;
                                category = text;
                                adapter.getFilter().filter(category + ":");
                                return true; // allow selection
                            }
                        })
                        .positiveText(R.string.md_choose_label)
                        .show();

                break;
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }
}
