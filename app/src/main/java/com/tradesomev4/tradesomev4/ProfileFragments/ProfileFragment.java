package com.tradesomev4.tradesomev4.ProfileFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.MyAuctionsBids;
import com.tradesomev4.tradesomev4.MyLocation;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_Model.Rate;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/22/2016.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String BUNDLE_KEY = "BUNDLE_KEY";
    private static final String LONG_KEY = "LONG_KEY";
    private static final String LAT_KEY = "LAT_KEY";
    private static final String NAME_KEY = "KEY";
    private ImageView profPic;
    private TextView name;
    private TextView bids;
    private TextView auctions;
    private TextView following;
    private TextView followers;
    private ImageView pin;
    private TextView email;
    private TextView bDate;
    private TextView gender;
    private RatingBar ratingBar;
    private FirebaseUser fUser;
    private DatabaseReference mDatabase;
    private User user;
    private Bundle extras;
    private ImageView myBidsAuctions;
    private ArrayList<Rate> rates;
    private float total;

    public void getData(){


        mDatabase.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                extras = new Bundle();
                extras.putDouble(LAT_KEY, user.getLatitude());
                extras.putDouble(LONG_KEY, user.getLongitude());
                extras.putString(NAME_KEY, user.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setData(){
        Glide.with(getContext())
                .load(fUser.getPhotoUrl())
                .asBitmap().centerCrop()
                .into(profPic);

        name.setText(fUser.getDisplayName());
        email.setText(fUser.getEmail());

       mDatabase.child("following").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               following.setText(String.valueOf(dataSnapshot.getChildrenCount()));
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

        mDatabase.child("follower").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        rates = new ArrayList<Rate>();
        total = 0;

        getData();

        profPic = (ImageView) view.findViewById(R.id.iv_prof_pic);
        name = (TextView) view.findViewById(R.id.tv_name);
        bids = (TextView) view.findViewById(R.id.tv_bids);
        auctions = (TextView) view.findViewById(R.id.tv_auctions);
        following = (TextView) view.findViewById(R.id.tv_following);
        followers = (TextView) view.findViewById(R.id.tv_followers);
        myBidsAuctions = (ImageView) view.findViewById(R.id.iv_my_bid_auction);
        pin = (ImageView)view.findViewById(R.id.iv_pin);
        email = (TextView) view.findViewById(R.id.tv_email);
        bDate = (TextView) view.findViewById(R.id.tv_birth_date);
        gender = (TextView) view.findViewById(R.id.tv_gender);
        ratingBar = (RatingBar) view.findViewById(R.id.rb_rating_bar);

        setData();
        getRating();
        pin.setOnClickListener(this);
        myBidsAuctions.setOnClickListener(this);


        return view;
    }

    public void getRating(){

        mDatabase.child("users").child(fUser.getUid()).child("rating").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Rate rate = dataSnapshot.getValue(Rate.class);
                rates.add(rate);
                total += rate.getRate();
                ratingBar.setRating(total/rates.size());
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_pin:
                Intent intent = new Intent(getContext(), MyLocation.class);
                intent.putExtra(BUNDLE_KEY, extras);
                startActivity(intent);
                break;
            case R.id.iv_my_bid_auction:
                Intent myAuctionsBids = new Intent(getContext(), MyAuctionsBids.class);
                startActivity(myAuctionsBids);
                break;
        }
    }
}
