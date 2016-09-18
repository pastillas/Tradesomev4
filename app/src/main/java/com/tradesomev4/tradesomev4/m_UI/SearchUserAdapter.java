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
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.Filters.SearchUserFilter;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.SendUserMessage;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/19/2016.
 */
public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserHolder> implements Filterable {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String USER_NAME_KEY = "USER_NAME";
    private static final String USER_IMAGE_KEY = "USER_IMAGE";
    private static final String USER_ID_KEY = "USER_KEY";
    private static final String BUNDLE_EXTRA_KEY = "BUNDLE_EXTRAS";
    public LayoutInflater inflater;
    public ArrayList <User> users;
    public Context context;
    public String search;
    public int prevPos = 0;
    public DatabaseReference mDatabase;
    public FirebaseUser fUser;
    public SearchUserFilter filter; boolean isAttached;
    RequestManager glide;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    CountDownTimer timeOuttimer;
    int puta;
    public boolean isSearching;
    RecyclerView recyclerView;


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(users.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && users.size() == 0)
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

                    if(isSearching && users.size() == 0){
                        tv_items_here.setText("Zero match");
                        showItemsHere();
                    }else{
                        if(!isSearching && users.size() > 0){
                            hideAll();
                        }else{
                            if(!isSearching && users.size() == 0){
                                tv_items_here.setText("Followers appear here.");
                                showItemsHere();
                            }
                        }
                    }

                    if(users.size() == 0 && puta == 2){
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        tv_items_here.setText("Followers appear here.");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if(users.size() > 0){
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

        if(tv_internet_connection.getVisibility() == View.GONE && users.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public void addUser(User user){
        users.add(user);
        notifyItemInserted(getItemCount());
    }

    public SearchUserAdapter(Context context, final String search, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                             final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view){
        this.context = context;
        this.search = search;
        this.inflater = LayoutInflater.from(context);
        this.users = new ArrayList<>();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
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

        Query userRef = mDatabase.child("users").orderByChild("name").limitToFirst(100);

        userRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);

                if(!user.getId().equals(fUser.getUid())){
                    addUser(user);
                    hideAll();
                }


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

        timer();
        timeOut();
    }

    @Override
    public SearchUserHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.search_user_model, parent, false);
        SearchUserHolder holder = new SearchUserHolder(view);

        if(prevPos > position)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);

        return holder;
    }

    @Override
    public void onBindViewHolder(SearchUserHolder holder, int position) {
        glide.load(users.get(position).getImage())
                .asBitmap().centerCrop()
                .into(holder.userImage);

        holder.userName.setText(users.get(position).getName());

        if(prevPos > position)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);

        final Bundle extras = new Bundle();
        extras.putString(USER_ID_KEY, users.get(position).getId());
        extras.putString(USER_NAME_KEY, users.get(position).getName());
        extras.putString(USER_IMAGE_KEY, users.get(position).getImage());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SendUserMessage.class);
                intent.putExtra(BUNDLE_EXTRA_KEY, extras);
                context.startActivity(intent);
            }
        });
        prevPos = position;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new SearchUserFilter(users, this);
        }

        return filter;
    }

    class SearchUserHolder extends RecyclerView.ViewHolder{
        ImageView userImage;
        TextView userName;
        View container;

        public SearchUserHolder(View itemView) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.iv_user_image);
            userName = (TextView) itemView.findViewById(R.id.tv_user_name);
            container = itemView.findViewById(R.id.cont_search_user);
        }
    }
}
