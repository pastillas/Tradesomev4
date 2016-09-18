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

public class ViewMyItem extends AppCompatActivity implements View.OnClickListener {

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_item);

        posterImage = (ImageView) findViewById(R.id.iv_poster_image);
        posterName = (TextView) findViewById(R.id.tv_poster_name);
        swipeItem = (ViewPager) findViewById(R.id.vp_swipe_item_image);
        bidNow = (ImageView) findViewById(R.id.iv_bid_now);
        bidNowBtn = (Button)findViewById(R.id.btn_bid_now);
        edit = (ImageView)findViewById(R.id.iv_edit);
        itemTitle = (TextView)findViewById(R.id.tv_item_title);
        distance = (TextView)findViewById(R.id.tv_distance);
        startingBid = (TextView) findViewById(R.id.tv_starting_bid);
        description = (TextView) findViewById(R.id.tv_description);
        datePost = (TextView) findViewById(R.id.tv_date_post);

        posterImage.setOnClickListener(this);
        posterName.setOnClickListener(this);
        bidNow.setOnClickListener(this);
        edit.setOnClickListener(this);
        bidNow.setOnClickListener(this);
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
            case R.id.poster_image:
                Intent myProfile = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(myProfile);
                break;

            case R.id.poster_name:
                Intent myProfile2 = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(myProfile2);
                break;
        }
    }
}
