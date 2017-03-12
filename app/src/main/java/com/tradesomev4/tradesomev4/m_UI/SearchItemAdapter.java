package com.tradesomev4.tradesomev4.m_UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.BidNow;
import com.tradesomev4.tradesomev4.Filters.SearchItemFilter;
import com.tradesomev4.tradesomev4.MyLocation;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.ViewItem;
import com.tradesomev4.tradesomev4.ViewItemLocation;
import com.tradesomev4.tradesomev4.ViewMyItem;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.DistanceHelper;
import com.tradesomev4.tradesomev4.m_Helpers.Keys;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/14/2016.
 */
public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.AuctionHolder> implements Filterable{
    private static final String DEBUG_TAG = "DEBUG_TAG";
    Context context;
    public ArrayList<Auction> auctions;
    LayoutInflater inflater;
    RecyclerView recyclerView;
    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";

    private static final String BUNDLE_KEY = "BUNDLE_KEY";
    private static final String LONG_KEY = "LONG_KEY";
    private static final String LAT_KEY = "LAT_KEY";
    private static final String NAME_KEY = "KEY";
    int previousPosition = 0;
    FirebaseUser fUser;
    User currentUser;
    DatabaseReference mDatabase;
    public ValueEventListener userListener;
    public ValueEventListener auctionListener;
    String category;
    SearchItemFilter filter;
    boolean isAttached;
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


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(auctions.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && auctions.size() == 0)
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

                    if(isSearching && auctions.size() == 0){
                        tv_items_here.setText("Zero match");
                        showItemsHere();
                    }else{
                        if(!isSearching && auctions.size() > 0){
                            hideAll();
                        }else{
                            if(!isSearching && auctions.size() == 0){
                                tv_items_here.setText("Items appear here.");
                                showItemsHere();
                            }
                        }
                    }

                    if(auctions.size() == 0 && puta == 2){
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        tv_items_here.setText("Items appear here.");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
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

        if(tv_internet_connection.getVisibility() == View.GONE && auctions.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public void addAuction(final int position, DataSnapshot dataSnapshot) {
        final Auction auction = dataSnapshot.getValue(Auction.class);
        if(auction.isHidden() == false && auction.isStatus()){
            if(!auction.getUid().equals(fUser.getUid())){
                LatLng user1 = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
                LatLng user2 = new LatLng(auction.getLatitude(), auction.getLongitude());
                Double distance = DistanceHelper.getDistance(user1, user2);
                if (distance <= DistanceHelper.getRadius()) {

                    if(category.equals("All")){
                        auctions.add(0, auction);
                        notifyItemInserted(position);
                        hideAll();
                    }else{
                        if(auction.getCategory().equals(category)){
                            auctions.add(0, auction);
                            notifyItemInserted(position);
                            hideAll();
                        }
                    }
                }
            }else{
                if(category.equals("All")){
                    auctions.add(0, auction);
                    notifyItemInserted(position);
                    hideAll();
                }else{
                    if(auction.getCategory().equals(category)){
                        auctions.add(0, auction);
                        notifyItemInserted(position);
                        hideAll();
                    }
                }
            }
        }
    }

    public void getAuctionTimer(){
        CountDownTimer timer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if(currentUser == null) {
                    getAuctionTimer();
                }
                else{
                    getAuctions();
                    return;
                }
            }
        };
        timer.start();
    }

    public void getAuctions(){
        mDatabase.child("auction").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d(Keys.DEBUG_TAG, dataSnapshot.getKey());
                addAuction(0, dataSnapshot);

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


    public SearchItemAdapter(Context context, FirebaseUser fUser, String category, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                             final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fUser = fUser;
        auctions = new ArrayList<>();
        this.category = category;
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
        initSwipe();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(fUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUser = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        timer();
        timeOut();
        getAuctionTimer();
    }

    @Override
    public AuctionHolder onCreateViewHolder(ViewGroup parent, final int position) {
        View view = inflater.inflate(R.layout.search_item_model, parent, false);
        AuctionHolder holder = new AuctionHolder(view);

        /*if (position > previousPosition) {
            AnimationUtil.animate(holder, true);
        } else {
            AnimationUtil.animate(holder, false);
        }*/

        return holder;
    }

    @Override
    public void onBindViewHolder(final AuctionHolder holder, final int position) {
        hideAll();
        final Auction auction = auctions.get(position);

        glide.load(auction.getImage1Uri())
                .asBitmap().centerCrop()
                .into(holder.itemImage);
        holder.title.setText(auction.getItemTitle());

        String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(auction.getDirectoryName());
        holder.date.setText(date);

        holder.distance.setText("Php " + auction.getCurrentBid());

        if (position > previousPosition) {
            AnimationUtil.animate(holder, true);
        } else {
            AnimationUtil.animate(holder, false);
        }

        final Bundle extras = new Bundle();
        extras.putString(EXTRAS_AUCTION_ID, auction.getAuctionId());
        extras.putString(EXTRAS_POSTER_ID, auction.getUid());

        holder.cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (auction.getUid().equals(fUser.getUid())) {
                    Intent intent = new Intent(context, ViewMyItem.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, ViewItem.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return auctions.size();
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new SearchItemFilter(auctions, this);
        }

        return filter;
    }

    class AuctionHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView title;
        View cont;
        TextView date;
        TextView distance;

        public AuctionHolder(View itemView) {
            super(itemView);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image_view1);
            title = (TextView) itemView.findViewById(R.id.tv_item_title);
            cont = itemView.findViewById(R.id.cont_search_item);
            date = (TextView)itemView.findViewById(R.id.date);
            distance = (TextView)itemView.findViewById(R.id.distance);
        }
    }


    public void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                final Auction auction = auctions.get(position);

                final Bundle extras = new Bundle();
                extras.putString(EXTRAS_AUCTION_ID, auction.getAuctionId());
                extras.putString(EXTRAS_POSTER_ID, auction.getUid());

                if(direction == ItemTouchHelper.LEFT){
                    Intent intent = new Intent(context, BidNow.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    context.startActivity(intent);
                }else{
                    if (!fUser.getUid().equals(extras.getString(EXTRAS_POSTER_ID))) {
                        Intent intent = new Intent(context, ViewItemLocation.class);
                        intent.putExtra(EXTRAS_BUNDLE, extras);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, MyLocation.class);

                        Bundle args = new Bundle();
                        args.putString(NAME_KEY, currentUser.getName());
                        args.putDouble(LAT_KEY, currentUser.getLatitude());
                        args.putDouble(LONG_KEY, currentUser.getLongitude());
                        intent.putExtra(BUNDLE_KEY, args);

                        context.startActivity(intent);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
