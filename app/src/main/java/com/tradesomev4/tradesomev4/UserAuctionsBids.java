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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tradesomev4.tradesomev4.AuctionBidFragment.UserAuctionsFragment;
import com.tradesomev4.tradesomev4.AuctionBidFragment.UserBidsFragment;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.IsBlockedListener;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;

public class UserAuctionsBids extends AppCompatActivity implements MaterialTabListener {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String USER_NAME_KEY = "USER_NAME";
    private static final String USER_IMAGE_KEY = "USER_IMAGE";
    private static final String USER_ID_KEY = "USER_KEY";
    private static final String BUNDLE_EXTRA_KEY = "BUNDLE_EXTRAS";

    private Toolbar toolbar;
    private ViewPager pager;
    private MaterialTabHost mTabs;
    private Bundle args;
    private DatabaseReference mDatabase;
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
        setContentView(R.layout.activity_user_auctions_bids);toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        puta = 1;
        parentView = findViewById(R.id.content_main);
        snackBars = new SnackBars(parentView, getApplicationContext());
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;

        args = getIntent().getBundleExtra(BUNDLE_EXTRA_KEY);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getSupportActionBar().setTitle(args.getString(USER_NAME_KEY));
        new IsBlockedListener(this, false, args.getString(USER_ID_KEY));

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
            Fragment fragment;
            switch (position){
                case 0:
                    fragment = UserAuctionsFragment.getInstance(args);
                    break;
                default:
                    fragment = UserBidsFragment.getInstance(args);
                    break;
            }

            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getStringArray(R.array.tabsAuctionBid)[position];
        }
    }
}
