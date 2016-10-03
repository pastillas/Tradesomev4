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
import com.google.firebase.database.ValueEventListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.BidNow;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.Bid;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/25/2016.
 */
public class UserBidsAdapter extends RecyclerView.Adapter<UserBidsAdapter.MyBidsHolder> {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    public Context context;
    public ArrayList<Bid> bidsHistory;
    public LayoutInflater inflater;
    public FirebaseUser fUser;
    public DatabaseReference mDatabase;
    public String userId;
    RecyclerView recyclerView;
    boolean isAttached;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    RequestManager glide;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    CountDownTimer timeOuttimer;
    int puta;


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(bidsHistory.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && bidsHistory.size() == 0)
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
                    if(bidsHistory.size() == 0 && puta == 2){
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if(bidsHistory.size() > 0){
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

        if(tv_internet_connection.getVisibility() == View.GONE && bidsHistory.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }


    public void addBidHistory(int position, Bid bid){
        bidsHistory.add(position, bid);
        notifyItemInserted(position);
    }



    public UserBidsAdapter(Context context, String userId, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                           final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view){
        this.userId = userId;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        bidsHistory = new ArrayList<>();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
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

        mDatabase.child("bidHistory").child(userId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Bid bid = dataSnapshot.getValue(Bid.class);
                addBidHistory(0, bid);

                hideAll();
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

    int prevPos = 0;

    @Override
    public MyBidsHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.user_bids_model, parent, false);
        MyBidsHolder holder = new MyBidsHolder(view);

        /*if (position > prevPos)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);
        */
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyBidsHolder holder, int position) {
        AnimationUtil.setFadeAnimation(holder.itemView);
        holder.bidAmount.setText(bidsHistory.get(position).getTitle());
        String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(String.valueOf(bidsHistory.get(position).getBidDate()));
        holder.bidDate.setText(date);

        mDatabase.child("auction").child(bidsHistory.get(position).getAuctionId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Auction auction = dataSnapshot.getValue(Auction.class);
                glide.load(auction.getImage1Uri())
                        .asBitmap().centerCrop()
                        .into(holder.itemImage);

                holder.auctionTitle.setText(auction.getItemTitle());

                holder.cont.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle extras = new Bundle();
                        extras.putString(EXTRAS_AUCTION_ID, auction.getAuctionId());
                        extras.putString(EXTRAS_POSTER_ID, auction.getUid());
                        Intent intent = new Intent(context, BidNow.class);
                        intent.putExtra(EXTRAS_BUNDLE, extras);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (position > prevPos)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);
        prevPos = position;
    }

    @Override
    public int getItemCount() {
        return bidsHistory.size();
    }

    class MyBidsHolder extends RecyclerView.ViewHolder{
        View cont;
        ImageView itemImage;
        TextView bidAmount;
        TextView auctionTitle;
        TextView bidDate;

        public MyBidsHolder(View itemView) {
            super(itemView);

            cont = itemView.findViewById(R.id.cont_my_bids);
            itemImage = (ImageView) itemView.findViewById(R.id.iv_item_image);
            bidAmount = (TextView) itemView.findViewById(R.id.tv_bid_amount);
            auctionTitle = (TextView) itemView.findViewById(R.id.tv_auction_title);
            bidDate = (TextView)itemView.findViewById(R.id.tv_bid_date);
        }
    }
}
