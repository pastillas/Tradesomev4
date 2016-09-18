package com.tradesomev4.tradesomev4;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.DistanceHelper;
import com.tradesomev4.tradesomev4.m_Helpers.ItemImageSwipe;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.User;

public class ViewItem extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    private String auctionId;
    private String posterId;
    private DatabaseReference mDatabase;
    private ImageView posterImage;
    private TextView posterName;
    private ViewPager swipeItem;
    private ImageView bidNow;
    private ImageView itemLocation;
    private ImageView reportItem;
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
    private TextView bids;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        posterImage = (ImageView) findViewById(R.id.iv_poster_image);
        posterName = (TextView) findViewById(R.id.tv_poster_name);
        swipeItem = (ViewPager) findViewById(R.id.vp_swipe_item_image);
        itemLocation = (ImageView) findViewById(R.id.iv_item_location);
        bidNow = (ImageView)findViewById(R.id.iv_bid_now);
        bidNowBtn = (Button)findViewById(R.id.btn_bid_now);
        itemLocation = (ImageView)findViewById(R.id.iv_item_location);
        reportItem = (ImageView)findViewById(R.id.iv_report_item);
        itemTitle = (TextView)findViewById(R.id.tv_item_title);
        distance = (TextView)findViewById(R.id.tv_distance);
        startingBid = (TextView) findViewById(R.id.tv_starting_bid);
        description = (TextView) findViewById(R.id.tv_description);
        datePost = (TextView)findViewById(R.id.tv_date_post);
        bids = (TextView)findViewById(R.id.tv_bids);

        posterImage.setOnClickListener(this);
        posterName.setOnClickListener(this);
        itemLocation.setOnClickListener(this);
        bidNow.setOnClickListener(this);
        reportItem.setOnClickListener(this);
        bidNowBtn.setOnClickListener(this);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        extras = getIntent().getBundleExtra(EXTRAS_BUNDLE);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        auctionId = extras.getString(EXTRAS_AUCTION_ID);
        posterId = extras.getString(EXTRAS_POSTER_ID);

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


        mDatabase.child("auction").child(auctionId).child("bid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bids.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("auction").child(auctionId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                startingBid.setText(" Php"+ startidBidAmount);
                description.append(auction.getDescription());
                datePost.setText(CalendarUtils.ConvertMilliSecondsToFormattedDate(auction.getPostDate()+ ""));

                Query curUser = mDatabase.child("users").child(fUser.getUid());
                curUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        userLat = user.getLatitude();
                        userLong = user.getLongitude();

                        LatLng user1= new LatLng(userLat, userLong);
                        LatLng user2 = new LatLng(posterLat, posterLong);
                        Double distanceVal = DistanceHelper.getDistance(user1, user2);

                        distance.setText(DistanceHelper.formatNumber(distanceVal));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_bid_now:
                Intent intent = new Intent(getApplicationContext(), BidNow.class);
                intent.putExtra(EXTRAS_BUNDLE, extras);
                startActivity(intent);
                break;
            case R.id.iv_item_location:
                Intent viewLocation = new Intent(getApplicationContext(), ViewItemLocation.class);
                viewLocation.putExtra(EXTRAS_BUNDLE, extras);
                startActivity(viewLocation);
                break;
            case R.id.iv_poster_image:
                Intent viewUserProfile = new Intent(getApplicationContext(), ViewUserProfile.class);
                viewUserProfile.putExtra(EXTRAS_BUNDLE, extras);
                startActivity(viewUserProfile);
                break;
            case R.id.tv_poster_name:
                Intent yeah = new Intent(getApplicationContext(), ViewUserProfile.class);
                yeah.putExtra(EXTRAS_BUNDLE, extras);
                startActivity(yeah);
                break;
            case R.id.btn_bid_now:
                Intent bidNow = new Intent(getApplicationContext(), BidNow.class);
                bidNow.putExtra(EXTRAS_BUNDLE, extras);
                startActivity(bidNow);
                break;
            case R.id.iv_report_item:
                Intent fileAReport = new Intent(getApplicationContext(), FileItemComplain.class);
                fileAReport.putExtra(EXTRAS_BUNDLE, extras);
                startActivity(fileAReport);
                break;
        }
    }
}
