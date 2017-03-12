package com.tradesomev4.tradesomev4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.IsBlockedListener;
import com.tradesomev4.tradesomev4.m_Helpers.ItemImageSwipe;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.User;
import com.tradesomev4.tradesomev4.m_UI.ParticipantAdapter;

/**
 * Created by Jorge Benigno Pante, Charles Torrente, Joshua Alarcon on 7/17/2016.
 * File name: ViewMyItem.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\ViewMyItem.java
 * Description: View my item full details.
 */

public class ViewMyItem extends AppCompatActivity implements View.OnClickListener {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";


    private static final String AUCTION_ID_KEY = "AUCTION_KEY";
    private static final String BUNDLE_KEY = "BUNDLE_KEY";
    private String auctionId;
    private String posterId;
    private DatabaseReference mDatabase;
    private ImageView posterImage;
    private TextView posterName;
    private ViewPager swipeItem;
    private ImageView bidNow;
    private ImageView edit;
    private TextView itemTitle;
    private TextView distance;
    private TextView startingBid;
    private TextView description;
    private ItemImageSwipe itemImageSwipe;
    private double userLat;
    private double userLong;
    private double posterLat;
    private double posterLong;
    private FirebaseUser fUser;
    private Button bidNowBtn;
    private TextView datePost;
    Bundle extras;
    private SlidingUpPanelLayout mLayout;
    private RecyclerView recyclerView;
    private ParticipantAdapter adapter;
    private SearchView sv;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    View content_main;
    View view_profile;

    public void initSlidingUp() {
        boolean isAttached;
        onAttachedToWindow();
        isAttached = true;
        content_main = findViewById(R.id.content_main);
        recyclerView = (RecyclerView) findViewById(R.id.rv_participants);
        adapter = new ParticipantAdapter(this, extras.getString(EXTRAS_AUCTION_ID), fUser.getUid(), isAttached, recyclerView, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, content_main);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(DEBUG_TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(DEBUG_TAG, "onPanelStateChanged " + newState);
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        mLayout.setAnchorPoint(0.7f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_item);

        try {
            extras = getIntent().getBundleExtra(EXTRAS_BUNDLE);
            fUser = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            auctionId = extras.getString(EXTRAS_AUCTION_ID);
            posterId = extras.getString(EXTRAS_POSTER_ID);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        new IsBlockedListener(getApplicationContext(), false, fUser.getUid());

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        content_main = findViewById(R.id.sliding_layout);
        tv_items_here = (TextView) findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView) findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        initSlidingUp();


        posterImage = (ImageView) findViewById(R.id.iv_poster_image);
        posterName = (TextView) findViewById(R.id.tv_poster_name);
        swipeItem = (ViewPager) findViewById(R.id.vp_swipe_item_image);
        bidNow = (ImageView) findViewById(R.id.iv_bid_now);
        bidNowBtn = (Button) findViewById(R.id.btn_bid_now);
        edit = (ImageView) findViewById(R.id.iv_edit);
        itemTitle = (TextView) findViewById(R.id.tv_item_title);
        distance = (TextView) findViewById(R.id.tv_distance);
        startingBid = (TextView) findViewById(R.id.tv_starting_bid);
        description = (TextView) findViewById(R.id.tv_description);
        datePost = (TextView) findViewById(R.id.tv_date_post);
        view_profile = findViewById(R.id.view_profile);

        posterImage.setOnClickListener(this);
        posterName.setOnClickListener(this);
        bidNow.setOnClickListener(this);
        edit.setOnClickListener(this);
        bidNow.setOnClickListener(this);
        bidNowBtn.setOnClickListener(this);
        view_profile.setOnClickListener(this);

        try {
            Query userRef = mDatabase.child("users").child(posterId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    posterLat = user.getLatitude();
                    posterLong = user.getLongitude();
                    Glide.with(getApplicationContext())
                            .load(user.getImage())
                            .asBitmap().centerCrop()
                            .into(posterImage);
                    posterName.setText(user.getName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        try {
            mDatabase.child("auction").child(auctionId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.d("auctionIDDD", auctionId);
                    Auction auction = dataSnapshot.getValue(Auction.class);
                    itemImageSwipe = new ItemImageSwipe(getApplicationContext(),
                            auction.getImage1Uri(),
                            auction.getImage2Uri(),
                            auction.getImage3Uri(),
                            auction.getImage4Uri());
                    swipeItem.setAdapter(itemImageSwipe);

                    itemTitle.setText(auction.getItemTitle());

                    String startidBidAmount = String.format("%d", auction.getStaringBid());
                    startingBid.setText(" Php" + startidBidAmount);
                    description.append(auction.getDescription());
                    datePost.setText(CalendarUtils.ConvertMilliSecondsToFormattedDate(auction.getPostDate() + ""));
                    distance.setText("0km");


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.iv_bid_now:
                    Intent intent = new Intent(getApplicationContext(), BidNow.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    startActivity(intent);
                    break;
                case R.id.iv_edit:
                    Bundle args = new Bundle();
                    args.putString(AUCTION_ID_KEY, extras.getString(EXTRAS_AUCTION_ID));
                    Intent editPost = new Intent(getApplicationContext(), EditPost.class);
                    editPost.putExtra(BUNDLE_KEY, args);
                    startActivity(editPost);
                    break;
                case R.id.btn_bid_now:
                    Intent bidNow = new Intent(getApplicationContext(), BidNow.class);
                    bidNow.putExtra(EXTRAS_BUNDLE, extras);
                    startActivity(bidNow);
                    break;
                case R.id.view_profile:
                    Intent myProfile = new Intent(getApplicationContext(), MyProfile.class);
                    startActivity(myProfile);
                    break;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        super.onBackPressed();
    }

    @Override
    public boolean onNavigateUp() {

        NavUtils.navigateUpFromSameTask(ViewMyItem.this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
