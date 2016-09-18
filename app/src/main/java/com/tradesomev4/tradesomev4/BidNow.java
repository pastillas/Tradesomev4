package com.tradesomev4.tradesomev4;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.DateHelper;
import com.tradesomev4.tradesomev4.m_Helpers.DistanceHelper;
import com.tradesomev4.tradesomev4.m_Helpers.VibrateService;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.Bid;
import com.tradesomev4.tradesomev4.m_Model.Rate;
import com.tradesomev4.tradesomev4.m_Model.User;
import com.tradesomev4.tradesomev4.m_UI.BidAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BidNow extends AppCompatActivity implements View.OnClickListener {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    private EditText bid;
    private ImageView submit;
    private RecyclerView recyclerView;
    private BidAdapter adapter;
    private Bundle extras;
    private FirebaseUser fUser;
    private DatabaseReference mDatabase;
    private User currentUser;
    private double distance;
    private MaterialEditText tv_bid_amount;
    private ImageView bt_bid;
    private TextView tv_title;
    private TextView tv_current_bid;
    private ImageView iv_item_image;
    private ImageView iv_poster_image;
    private TextView tv_poster_name;
    private TextView tv_date_posted;
    private View container_rate;
    private TextView tv_rate_name;
    private RatingBar rb_rate_user;
    private RatingBar rb_auctioner_rate;
    private View container_bid;
    Auction auction;
    User user;
    ArrayList<Rate> rates;
    float total;
    boolean fromDb;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    View content_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bid_now);

        extras = getIntent().getBundleExtra(EXTRAS_BUNDLE);

        initViewsDb();
        initData();
    }


    public void initViewsDb() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        content_main = findViewById(R.id.content_main);
        tv_items_here = (TextView) findViewById(R.id.tv_items_here);
        tv_internet_connection = (TextView) findViewById(R.id.tv_internet_connection);
        progress_wheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        tv_items_here.setVisibility(View.GONE);
        tv_internet_connection.setVisibility(View.GONE);

        tv_bid_amount = (MaterialEditText) findViewById(R.id.tv_bid_amount);
        tv_bid_amount.requestFocus();

        bt_bid = (ImageView) findViewById(R.id.bt_bid);
        bt_bid.setOnClickListener(this);

        iv_item_image = (ImageView) findViewById(R.id.iv_item_image);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_current_bid = (TextView) findViewById(R.id.tv_current_bid);
        iv_poster_image = (ImageView) findViewById(R.id.iv_poster_image);
        tv_poster_name = (TextView) findViewById(R.id.tv_poster_name);
        tv_date_posted = (TextView) findViewById(R.id.tv_date_posted);
        container_rate = findViewById(R.id.container_rate);
        tv_rate_name = (TextView) findViewById(R.id.tv_rate_name);
        rb_rate_user = (RatingBar) findViewById(R.id.rb_rate_user);
        rb_auctioner_rate = (RatingBar) findViewById(R.id.rb_auctioner_rate);
        container_bid = findViewById(R.id.container_bid);

        container_rate.setVisibility(View.GONE);
        tv_rate_name.setVisibility(View.GONE);
        rb_rate_user.setVisibility(View.GONE);

        fromDb = false;
        boolean isAttache;

        onAttachedToWindow();
        isAttache = true;

        recyclerView = (RecyclerView) findViewById(R.id.rv_bid_now);
        adapter = new BidAdapter(this, extras.getString(EXTRAS_AUCTION_ID), extras.getString(EXTRAS_POSTER_ID), isAttache, recyclerView, Glide.with(this), tv_items_here, tv_internet_connection, progress_wheel, content_main);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void initData() {
        rates = new ArrayList<Rate>();
        total = 0;

        mDatabase.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);

                if (extras.getString(EXTRAS_POSTER_ID).equals(currentUser.getId())) {
                    tv_bid_amount.setHint("Type here");
                    tv_bid_amount.setInputType(InputType.TYPE_CLASS_TEXT);
                    tv_bid_amount.setFloatingLabelText("Type here");
                    tv_bid_amount.setMaxCharacters(30);
                } else {
                    tv_bid_amount.setHint("Place bid: Php");
                    tv_bid_amount.setFloatingLabelText("Place bid: Php");
                    tv_bid_amount.setInputType(InputType.TYPE_CLASS_NUMBER);
                    tv_bid_amount.setMaxCharacters(9);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Log.d(DEBUG_TAG, "EXTRAS POSTER ID: " + extras.getString(EXTRAS_POSTER_ID));

        final String posterId = extras.getString(EXTRAS_POSTER_ID);

        mDatabase.child("users").child(posterId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                tv_poster_name.setText(user.getName());
                Glide.with(getApplicationContext())
                        .load(user.getImage())
                        .asBitmap().centerCrop()
                        .into(iv_poster_image);

                if(fUser.getUid().equals(posterId)){
                    distance = 0;
                }else{
                    LatLng user1 = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
                    LatLng user2 = new LatLng(user.getLatitude(), user.getLongitude());
                    distance = DistanceHelper.getDistance(user1, user2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.child("auction").child(extras.getString(EXTRAS_AUCTION_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                auction = dataSnapshot.getValue(Auction.class);

                Glide.with(getApplicationContext())
                        .load(auction.getImage1Uri())
                        .asBitmap().centerCrop()
                        .into(iv_item_image);

                tv_title.setText(auction.getItemTitle());

                String currentBid = "Current bid: Php" + auction.getCurrentBid();
                tv_current_bid.setText(currentBid);

                String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(auction.getDirectoryName());
                tv_date_posted.setText(date);

                if (!auction.isStatus()) {
                    tv_bid_amount.setEnabled(false);
                    bt_bid.setEnabled(false);
                    bt_bid.setClickable(false);

                    if (!fUser.getUid().equals(auction.getUid())) {
                        tv_bid_amount.setVisibility(View.GONE);
                        bt_bid.setVisibility(View.GONE);
                        container_bid.setVisibility(View.GONE);

                        container_rate.setVisibility(View.VISIBLE);
                        tv_rate_name.setVisibility(View.VISIBLE);
                        tv_rate_name.setText("Rate " + user.getName() + ": ");
                        rb_rate_user.setVisibility(View.VISIBLE);

                        rb_rate_user.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            @Override
                            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                Rate rate = new Rate(fUser.getUid(), ratingBar.getRating());
                                mDatabase.child("users").child(auction.getUid()).child("rating").child(fUser.getUid()).setValue(rate);

                                if(!fromDb)
                                    Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabase.child("users").child(posterId).child("rating").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Rate rate = dataSnapshot.getValue(Rate.class);
                rates.add(rate);
                total += rate.getRate();
                rb_auctioner_rate.setRating(total / rates.size());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!fUser.getUid().equals(posterId)) {
            mDatabase.child("users").child(posterId).child("rating").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount() != 0) {
                        Rate rate = dataSnapshot.getValue(Rate.class);
                        rb_rate_user.setRating(rate.getRate());

                        fromDb = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bid, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.auction_info:
                if (extras.getString(EXTRAS_POSTER_ID).equals(fUser.getUid())) {
                    Intent intent = new Intent(getApplicationContext(), ViewMyItem.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ViewItem.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_bid) {
            if (extras.getString(EXTRAS_POSTER_ID).equals(fUser.getUid()))
                showInputDialogAuctioner();
            else {
                if (distance > 20000) {
                    Dialog d = new MaterialDialog.Builder(this)
                            .title("Distance limit")
                            .content("Sorry, your distance to the auctioneer is greater than 20km.")
                            .positiveText(R.string.continueBtn)
                            .show();
                } else
                    bidNow();
            }
        }
    }

    public void bidNow() {
        String tmp = tv_bid_amount.getText().toString();

        if (!TextUtils.isEmpty(tmp) && tmp.length() < 10) {
            int input = Integer.parseInt(tmp);

            if (auction.getCurrentBid() < input) {
                mDatabase.child("auction").child(auction.getAuctionId()).child("currentBid").setValue(input);
                String key = mDatabase.child("auction").child(extras.getString(EXTRAS_AUCTION_ID)).push().getKey();

                Bid bid = new Bid();
                bid.setAuctionId(extras.getString(EXTRAS_AUCTION_ID));
                bid.setUserId(fUser.getUid());
                bid.setTitle("Php" + input);
                bid.setId(key);
                long bidDate = Long.parseLong(DateHelper.getCurrentDateInMil());
                bid.setBidDate(bidDate);

                Log.d("key", key);
                Map<String, Object> bidValues = bid.toMap();
                Map<String, Object> childUpdate = new HashMap<>();
                childUpdate.put("/auction/" + extras.getString(EXTRAS_AUCTION_ID) + "/bid/" + key, bidValues);
                childUpdate.put("/bidHistory/" + fUser.getUid() + "/" + key, bidValues);
                mDatabase.updateChildren(childUpdate);

                tv_bid_amount.setText(null);
            } else {
                Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
                startService(intentVibrate);
                tv_bid_amount.setError("Bid amount must be greater than current bid.");
            }
        } else {
            if (tmp.length() > 9) {
                Intent intentVibrate = new Intent(getApplicationContext(), VibrateService.class);
                startService(intentVibrate);
                tv_bid_amount.setError("Too much numbers.");
            } else {
                tv_bid_amount.setError("Place bid: Php");
            }
        }
    }

    public void showInputDialogAuctioner() {

        String tmp = tv_bid_amount.getText().toString();

        if (!TextUtils.isEmpty(tmp) && tmp.length() < 31) {
            String key = mDatabase.child("auction").child(extras.getString(EXTRAS_AUCTION_ID)).push().getKey();
            Bid bid = new Bid();
            bid.setAuctionId(extras.getString(EXTRAS_AUCTION_ID));
            bid.setUserId(fUser.getUid());
            bid.setTitle(tmp);
            long bidDate = Long.parseLong(DateHelper.getCurrentDateInMil());
            bid.setBidDate(bidDate);
            bid.setId(key);

            Log.d("key", key);
            Map<String, Object> bidValues = bid.toMap();
            Map<String, Object> childUpdate = new HashMap<>();
            childUpdate.put("/auction/" + extras.getString(EXTRAS_AUCTION_ID) + "/bid/" + key, bidValues);
            childUpdate.put("/bidHistory/" + fUser.getUid() + "/" + key, bidValues);
            mDatabase.updateChildren(childUpdate);

            tv_bid_amount.setText(null);
        } else {
            if (tmp.length() > 30)
                tv_bid_amount.setError("Too long.");
            else
                tv_bid_amount.setError("Type message first.");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}
