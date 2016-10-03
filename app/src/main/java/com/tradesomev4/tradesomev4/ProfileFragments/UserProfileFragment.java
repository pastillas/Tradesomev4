package com.tradesomev4.tradesomev4.ProfileFragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.SendUserMessage;
import com.tradesomev4.tradesomev4.UserAuctionsBids;
import com.tradesomev4.tradesomev4.ViewItemLocation;
import com.tradesomev4.tradesomev4.m_Helpers.DateHelper;
import com.tradesomev4.tradesomev4.m_Model.Follower;
import com.tradesomev4.tradesomev4.m_Model.Following;
import com.tradesomev4.tradesomev4.m_Model.Rate;
import com.tradesomev4.tradesomev4.m_Model.User;
import com.tradesomev4.tradesomev4.m_Model.UserComplain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pastillas-Boy on 7/22/2016.
 */
public class UserProfileFragment extends Fragment implements View.OnClickListener {
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";


    private static final String USER_NAME_KEY = "USER_NAME";
    private static final String USER_IMAGE_KEY = "USER_IMAGE";
    private static final String USER_ID_KEY = "USER_KEY";
    private static final String BUNDLE_EXTRA_KEY = "BUNDLE_EXTRAS";

    private Button follow;
    private ImageView profPic;
    private TextView name;
    private TextView bids;
    private TextView auctions;
    private TextView followingSum;
    private TextView followers;
    private ImageView pin;
    private ImageView rate;
    private ImageView sendMessage;
    private TextView email;
    private TextView bDate;
    private TextView gender;
    private RatingBar ratingBar;
    private FirebaseUser fUser;
    private DatabaseReference mDatabase;
    private User user;
    private Bundle extras;
    private String userName;
    private boolean isFollowed;
    private ImageView bidAuction;
    private Button complain;
    private Dialog complainChoices;
    private Dialog comfirmDialog;
    private ArrayList<Rate>rates;
    private float total;


    public static UserProfileFragment getInstance(Bundle extras) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();
        userProfileFragment.setArguments(extras);
        return userProfileFragment;
    }

    public void setUserData() {
        mDatabase.child("users").child(extras.getString(EXTRAS_POSTER_ID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                Glide.with(getActivity())
                        .load(user.getImage())
                        .asBitmap().centerCrop()
                        .into(profPic);
                name.setText(user.getName());
                email.setText(user.getEmail());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void isFollowed(){
        Query followingQuery = mDatabase.child("following").child(fUser.getUid()).child(extras.getString(EXTRAS_POSTER_ID));

        followingQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 3){
                    isFollowed = true;
                    follow.setText("Unfollow");
                }else{
                    isFollowed = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getFollowersCount(){
        mDatabase.child("follower").child(extras.getString(EXTRAS_POSTER_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followers.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void getFollowingCount(){
        mDatabase.child("following").child(extras.getString(EXTRAS_POSTER_ID)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                followingSum.setText(String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        extras = getArguments();

        rates = new ArrayList<Rate>();
        total = 0;

        complain = (Button)view.findViewById(R.id.btn_complain);
        follow = (Button) view.findViewById(R.id.btn_follow);
        profPic = (ImageView) view.findViewById(R.id.iv_prof_pic);
        name = (TextView) view.findViewById(R.id.tv_name);
        bidAuction = (ImageView) view.findViewById(R.id.iv_bid_auction);
        bids = (TextView) view.findViewById(R.id.tv_bids);
        auctions = (TextView) view.findViewById(R.id.tv_auctions);
        followingSum = (TextView) view.findViewById(R.id.tv_following);
        followers = (TextView) view.findViewById(R.id.tv_followers);
        pin = (ImageView) view.findViewById(R.id.iv_pin);
        sendMessage = (ImageView) view.findViewById(R.id.iv_send_message);
        email = (TextView) view.findViewById(R.id.tv_email);
        ratingBar = (RatingBar) view.findViewById(R.id.rb_rating_bar);


        isFollowed();

        getFollowersCount();
        getFollowingCount();
        setUserData();
        getRating();

        complain.setOnClickListener(this);
        bidAuction.setOnClickListener(this);
        bidAuction.setOnClickListener(this);
        follow.setOnClickListener(this);
        pin.setOnClickListener(this);
        sendMessage.setOnClickListener(this);

        return view;
    }

    public void getRating(){

        mDatabase.child("users").child(extras.getString(EXTRAS_POSTER_ID)).child("rating").addChildEventListener(new ChildEventListener() {
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

    public void removeFollowing(){
        mDatabase.child("following").child(fUser.getUid()).child(extras.getString(EXTRAS_POSTER_ID)).removeValue();
    }

    public void removeFollower(){
        mDatabase.child("follower").child(extras.getString(EXTRAS_POSTER_ID)).child(fUser.getUid()).removeValue();
    }

    public void unFollowThisUser(){
        follow.setText("Follow");
        removeFollowing();
        removeFollower();
        isFollowed = false;
    }

    public void followThisUser(){
        follow.setText("Unfollow");
        addFollowing();
        addFollower();
        isFollowed = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_follow:
                if(isFollowed)
                    unFollowThisUser();
                else
                    followThisUser();
                break;
            case R.id.iv_bid_auction:
                Bundle args = new Bundle();
                args.putString(USER_NAME_KEY, user.getName());
                args.putString(USER_IMAGE_KEY, user.getImage());
                args.putString(USER_ID_KEY, user.getId());
                Intent userAuctionBids = new Intent(getContext(), UserAuctionsBids.class);
                userAuctionBids.putExtra(BUNDLE_EXTRA_KEY, args);
                startActivity(userAuctionBids);
                break;
            case R.id.iv_pin:
                Intent viewItemlocation = new Intent(getContext(), ViewItemLocation.class);
                viewItemlocation.putExtra(EXTRAS_BUNDLE, extras);
                startActivity(viewItemlocation);
                break;
            case R.id.iv_send_message:
                Bundle argsSendMessage = new Bundle();
                argsSendMessage.putString(USER_NAME_KEY, user.getName());
                argsSendMessage.putString(USER_IMAGE_KEY, user.getImage());
                argsSendMessage.putString(USER_ID_KEY, user.getId());
                Intent sendUserMessage = new Intent(getContext(), SendUserMessage.class);
                sendUserMessage.putExtra(BUNDLE_EXTRA_KEY, argsSendMessage);
                startActivity(sendUserMessage);
                break;
            case R.id.btn_complain:
                 new MaterialDialog.Builder(getContext())
                    .title("Reasons")
                    .items(R.array.userComplain)
                    .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                        @Override
                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            //showToast(which + ": " + text);
                            dialog.dismiss();

                            String key = mDatabase.child("userComplains").push().getKey();
                            UserComplain userComplain = new UserComplain(which, fUser.getUid(), extras.getString(EXTRAS_POSTER_ID), key, DateHelper.getCurrentDateInMil());
                            Map<String, Object> map = userComplain.toMap();
                            Map<String, Object> addChild = new HashMap<String, Object>();
                            addChild.put("/userComplains/" + key, map);
                            mDatabase.updateChildren(addChild);
                            Dialog d = new MaterialDialog.Builder(getContext())
                                    .title("Tradesome")
                                    .content("Your complain has succesfully sent.")
                                    .positiveText(R.string.continueBtn)
                                    .show();

                            return true;
                        }
                    })
                    .positiveText(R.string.md_choose_label)
                    .show();
                break;
        }
    }

    public void addFollower(){
        mDatabase.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Follower follower = new Follower(fUser.getUid(), fUser.getUid(), user.getName());
                Map<String, Object> map = follower.toMap();
                Map<String, Object> addFollower = new HashMap<String, Object>();
                addFollower.put("/follower/" + extras.getString(EXTRAS_POSTER_ID) +"/" + fUser.getUid(), map);
                mDatabase.updateChildren(addFollower);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addFollowing(){
        mDatabase.child("users").child(extras.getString(EXTRAS_POSTER_ID)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Following following = new Following(extras.getString(EXTRAS_POSTER_ID), user.getId(), user.getName());
                Map<String, Object> map = following.toMap();
                Map<String, Object>addFollowing = new HashMap<>();
                addFollowing.put("/following/" + fUser.getUid() + "/" + user.getId(), map);
                mDatabase.updateChildren(addFollowing);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
