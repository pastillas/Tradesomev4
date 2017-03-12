package com.tradesomev4.tradesomev4;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.tradesomev4.tradesomev4.ProfileFragments.UserFollowersFragment;
import com.tradesomev4.tradesomev4.ProfileFragments.UserFollowingFragment;
import com.tradesomev4.tradesomev4.ProfileFragments.UserProfileFragment;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.IsBlockedListener;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


/**
 * Created by Jorge Benigno Pante, Charles Torrente, Joshua Alarcon on 7/17/2016.
 * File name: ViewUserProfile.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\ViewUserProfile.java
 * Description: View other user's profile.
 */

public class ViewUserProfile extends AppCompatActivity implements MaterialTabListener{
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    private Toolbar toolbar;
    private ViewPager pager;
    private MaterialTabHost mTabs;
    public Bundle extras;
    public String posterId;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    View parentView;
    int puta;


    public void timer(){
        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "TIMER: " + l);
            }

            public void onFinish() {
                Connectivity connectivity = new Connectivity(getApplicationContext());

                if(!connectivity.isConnected()) {
                    isConnectionRestoredShowed = false;
                    isConnected = false;

                    if(puta == 1)
                        puta++;

                    if(!isConnectionDisabledShowed){
                        snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if(puta != 1 && !isConnectionRestoredShowed){
                        snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }
                }

                timer();
            }
        }.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_profile);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        puta = 1;
        parentView = findViewById(R.id.content_main);
        snackBars = new SnackBars(parentView, getApplicationContext());
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;

        extras = getIntent().getBundleExtra(EXTRAS_BUNDLE);
        posterId = extras.getString(EXTRAS_POSTER_ID);

        new IsBlockedListener(getApplicationContext(), false, posterId);
        Log.d("posterId", posterId);


        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        mTabs = (MaterialTabHost) findViewById(R.id.materialTabHost);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mTabs.setSelectedNavigationItem(position);
            }
        });

        for(int i = 0; i < adapter.getCount(); i++){
            mTabs.addTab(
                    mTabs.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        timer();
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle args = new Bundle();
            args.putString(EXTRAS_POSTER_ID, posterId);

            Fragment fragment = UserProfileFragment.getInstance(args);
            switch (position){
                case 0:
                    fragment = UserProfileFragment.getInstance(args);
                    break;
                case 1:
                    fragment = UserFollowingFragment.getInstance(args);
                    break;
                case 2:
                    fragment = UserFollowersFragment.getInstance(args);
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tabsViewUser)[position];
        }
    }
}
