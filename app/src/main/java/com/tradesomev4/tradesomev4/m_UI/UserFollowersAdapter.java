package com.tradesomev4.tradesomev4.m_UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tradesomev4.tradesomev4.Filters.UserFollowersFilter;
import com.tradesomev4.tradesomev4.MyProfile;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.ViewUserProfile;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Follower;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/22/2016.
 */
public class UserFollowersAdapter extends RecyclerView.Adapter<UserFollowersAdapter.FollowersHolder> implements Filterable {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    public Context context;
    public LayoutInflater inflater;
    public ArrayList <Follower> followers;
    public User user;
    public int prevPos = 0;
    DatabaseReference mDatabase;
    UserFollowersFilter filter;
    String uid;
    private FirebaseUser fUser;
    RecyclerView recyclerView;
    boolean isAttached;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressBar progress_wheel;
    RequestManager glide;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    CountDownTimer timeOuttimer;
    int puta;
    public boolean isSearching;


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(followers.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && followers.size() == 0)
                    showItemsHere();
            }
        };

        timeOuttimer.start();
    }


    public void timer(){
        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "TIMER: " + l);
            }

            public void onFinish() {
                Connectivity connectivity = new Connectivity(context.getApplicationContext());

                if(!connectivity.isConnected()) {
                    isConnectionRestoredShowed = false;
                    isConnected = false;

                    if(puta == 1)
                        puta++;

                    if(!isConnectionDisabledShowed){
                        //snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }

                    if(timeOuttimer != null)
                        timeOuttimer.cancel();

                    showConnectionError();
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if(puta != 1 && !isConnectionRestoredShowed){
                        //snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }

                    if(isSearching && followers.size() == 0){
                        tv_items_here.setText("Zero match");
                        showItemsHere();
                    }else{
                        if(!isSearching && followers.size() > 0){
                            hideAll();
                        }else{
                            if(!isSearching && followers.size() == 0){
                                tv_items_here.setText("Followers appear here.");
                                showItemsHere();
                            }
                        }
                    }

                    if(followers.size() == 0 && puta == 2){
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        tv_items_here.setText("Followers appear here.");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if(followers.size() > 0){
                    //hideAll();
                }

                timer();
            }
        }.start();
    }
    public void hideAll(){
        if(progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if(tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);
    }

    public void showItemsHere(){
        if(progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if(tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.GONE)
            tv_items_here.setVisibility(View.VISIBLE);
    }

    public void showLoading(){
        if(progress_wheel.getVisibility() == View.GONE)
            progress_wheel.setVisibility(View.VISIBLE);

        if(tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);
    }

    public void showConnectionError(){
        if(progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if(tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if(tv_internet_connection.getVisibility() == View.GONE && followers.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public void addFollower(Follower follower){
        followers.add(follower);
        notifyItemInserted(getItemCount());
    }

    public UserFollowersAdapter(Context context, String uid, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                                final TextView tv_items_here, final TextView tv_internet_connection, final ProgressBar progress_wheel, View view){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.followers = new ArrayList<>();
        this.user = new User();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        this.uid = uid;
        this.fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.isAttached = isAttached;
        this.recyclerView = recyclerView;
        this.glide = glide;
        this.tv_items_here = tv_items_here;
        this.tv_internet_connection= tv_internet_connection;
        this.progress_wheel = progress_wheel;
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;
        snackBars = new SnackBars(view, context.getApplicationContext());
        puta = 1;
        isSearching = false;

        mDatabase.child("follower").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Follower follower = dataSnapshot.getValue(Follower.class);
                addFollower(follower);

                hideAll();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Follower follower = dataSnapshot.getValue(Follower.class);

                for(int i = 0; i < followers.size(); i++){
                    if(follower.getId().equals(followers.get(i).getId())){
                        followers.remove(i);
                        notifyItemRemoved(i);
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        timer();
        timeOut();
    }

    @Override
    public FollowersHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.user_followers_model, parent, false);
        FollowersHolder holder = new FollowersHolder(view);

        if(prevPos > position){
            AnimationUtil.animate(holder, true);
        }else{
            AnimationUtil.animate(holder, false);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final FollowersHolder holder, final int position) {
        Query userQuery = mDatabase.child("users").child(followers.get(position).getId());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if(isAttached){
                    glide.load(user.getImage())
                            .asBitmap().centerCrop()
                            .into(holder.image);
                }
                holder.name.setText(user.getName());

                final Bundle extras = new Bundle();
                extras.putString(EXTRAS_POSTER_ID, user.getId());

                holder.cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!fUser.getUid().equals(user.getId())) {
                            Intent intent = new Intent(context, ViewUserProfile.class);
                            intent.putExtra(EXTRAS_BUNDLE, extras);
                            context.startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, MyProfile.class);
                            context.startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(prevPos > position){
            AnimationUtil.animate(holder, true);
        }else{
            AnimationUtil.animate(holder, false);
        }

        prevPos = position;
    }

    @Override
    public int getItemCount() {
        return followers.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new UserFollowersFilter(followers, this);
        }

        return filter;
    }

    class FollowersHolder extends RecyclerView.ViewHolder{
        View cont;
        TextView name;
        ImageView image;

        public FollowersHolder(View itemView) {
            super(itemView);

            cont = itemView.findViewById(R.id.cont_user_followers);
            name = (TextView)itemView.findViewById(R.id.tv_user_followers_name);
            image = (ImageView)itemView.findViewById(R.id.iv_user_followers_image);
        }
    }
}
