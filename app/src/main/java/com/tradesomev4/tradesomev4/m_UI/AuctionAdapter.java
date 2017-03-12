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
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
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
import com.tradesomev4.tradesomev4.BidNow;
import com.tradesomev4.tradesomev4.FileItemComplain;
import com.tradesomev4.tradesomev4.MyLocation;
import com.tradesomev4.tradesomev4.MyProfile;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.ViewItem;
import com.tradesomev4.tradesomev4.ViewItemLocation;
import com.tradesomev4.tradesomev4.ViewMyItem;
import com.tradesomev4.tradesomev4.ViewUserProfile;
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
public class AuctionAdapter extends RecyclerView.Adapter<AuctionAdapter.AuctionHolder> {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    Context context;
    ArrayList<Auction> auctions;
    ArrayList<User> auctioners;
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


    public void timeOut() {
        showLoading();
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if (auctions.size() > 0) {
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if (isConnected && auctions.size() == 0)
                    showItemsHere();
            }
        };

        timeOuttimer.start();
    }

    public void timer() {
        final CountDownTimer c = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d(DEBUG_TAG, "TIMER: " + l);
            }

            public void onFinish() {
                Connectivity connectivity = new Connectivity(context.getApplicationContext());

                if (!connectivity.isConnected()) {
                    isConnectionRestoredShowed = false;
                    isConnected = false;

                    if (puta == 1)
                        puta++;

                    if (!isConnectionDisabledShowed) {
                        snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }

                    if (timeOuttimer != null)
                        timeOuttimer.cancel();

                    showConnectionError();
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if (puta != 1 && !isConnectionRestoredShowed) {
                        snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }
                    if (auctions.size() == 0 && puta == 2) {
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if (auctions.size() == 0) {
                    //showLoading();
                }

                timer();
            }
        }.start();
    }

    public void hideAll() {
        if (progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if (tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);
    }

    public void showItemsHere() {
        if (progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if (tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.GONE)
            tv_items_here.setVisibility(View.VISIBLE);
    }

    public void showLoading() {
        if (progress_wheel.getVisibility() == View.GONE)
            progress_wheel.setVisibility(View.VISIBLE);

        if (tv_internet_connection.getVisibility() == View.VISIBLE)
            tv_internet_connection.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);
    }

    public void showConnectionError() {
        if (progress_wheel.getVisibility() == View.VISIBLE)
            progress_wheel.setVisibility(View.GONE);

        if (tv_items_here.getVisibility() == View.VISIBLE)
            tv_items_here.setVisibility(View.GONE);

        if (tv_internet_connection.getVisibility() == View.GONE && auctions.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    public void addAuction(final int position, DataSnapshot dataSnapshot) {
        final Auction auction = dataSnapshot.getValue(Auction.class);


        if (auction.isHidden() == false && auction.isStatus()) {
            if (!auction.getUid().equals(fUser.getUid())) {
                LatLng user1 = new LatLng(currentUser.getLatitude(), currentUser.getLongitude());
                LatLng user2 = new LatLng(auction.getLatitude(), auction.getLongitude());
                Double distance = DistanceHelper.getDistance(user1, user2);
                if (distance <= DistanceHelper.getRadius()) {
                    Log.d(Keys.DEBUG_TAG, String.valueOf(distance));
                    auctions.add(0, auction);
                    notifyItemInserted(position);
                    hideAll();
                }
            } else {
                auctions.add(0, auction);
                notifyItemInserted(position);
                hideAll();
            }
        }

    }

    public void getAuctionTimer() {
        CountDownTimer timer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (currentUser == null) {
                    getAuctionTimer();
                } else {
                    getAuctions();
                    return;
                }
            }
        };
        timer.start();
    }

    public void getAuctions() {
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

    public AuctionAdapter(Context context, final FirebaseUser fUser, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                          final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.isAttached = isAttached;
        this.recyclerView = recyclerView;
        auctions = new ArrayList<>();
        this.glide = glide;
        this.tv_items_here = tv_items_here;
        this.tv_internet_connection = tv_internet_connection;
        this.progress_wheel = progress_wheel;
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;
        snackBars = new SnackBars(view, context.getApplicationContext());
        puta = 1;
        initSwipe();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        auctioners = new ArrayList<>();

        mDatabase.child("users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
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
        View view = inflater.inflate(R.layout.model, parent, false);
        AuctionHolder holder = new AuctionHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final AuctionHolder holder, final int position) {
        try {
            final Auction auction = auctions.get(position);

            glide.load(auction.getImage1Uri())
                    .asBitmap().centerCrop()
                    .into(holder.itemImage);
            holder.title.setText(auction.getItemTitle());

            mDatabase.child("auction").child(auction.getAuctionId()).child("currentBid").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try{
                        int currentBid = dataSnapshot.getValue(Integer.class);
                        holder.tv_current_bid.setText("Php" + currentBid);

                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(auction.getDirectoryName());
            holder.datePost.setText(date);
            holder.tv_current_bid.setText("Php" + auction.getCurrentBid());

            if (position > previousPosition) {
                AnimationUtil.animate(holder, true);
            } else {
                AnimationUtil.animate(holder, false);
            }

            mDatabase.child("users").child(auction.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);

                    glide.load(user.getImage())
                            .asBitmap().centerCrop()
                            .into(holder.posterImage);
                    holder.posterName.setText(user.getName());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            previousPosition = position;
            final int currentPosition = position;

            final Bundle extras = new Bundle();
            extras.putString(EXTRAS_AUCTION_ID, auction.getAuctionId());
            extras.putString(EXTRAS_POSTER_ID, auction.getUid());

            holder.itemImage.setOnClickListener(new View.OnClickListener() {
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

            holder.bidNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BidNow.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    context.startActivity(intent);
                }
            });

            holder.report.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!fUser.getUid().equals(auction.getUid())) {
                        Intent intent = new Intent(context, FileItemComplain.class);
                        intent.putExtra(EXTRAS_BUNDLE, extras);
                        context.startActivity(intent);
                    }
                }
            });

            holder.viewLocation.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
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
            });

            holder.viewUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!auction.getUid().equals(fUser.getUid())) {
                        Bundle extra = new Bundle();
                        extras.putString(EXTRAS_POSTER_ID, auction.getUid());
                        Intent intent = new Intent(context, ViewUserProfile.class);
                        intent.putExtra(EXTRAS_BUNDLE, extras);
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, MyProfile.class);
                        context.startActivity(intent);
                    }
                }
            });
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return auctions.size();
    }

    class AuctionHolder extends RecyclerView.ViewHolder {
        TextView posterName;
        ImageView posterImage;
        ImageView itemImage;
        TextView title;
        TextView datePost;
        ImageView bidNow;
        ImageView viewLocation;
        View viewUser;
        TextView tv_current_bid;
        ImageView report;

        public AuctionHolder(View itemView) {
            super(itemView);

            report = (ImageView) itemView.findViewById(R.id.iv_report);
            posterName = (TextView) itemView.findViewById(R.id.poster_name);
            posterImage = (ImageView) itemView.findViewById(R.id.poster_image);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image_view1);
            title = (TextView) itemView.findViewById(R.id.tv_item_title);
            datePost = (TextView) itemView.findViewById(R.id.tv_date_post);
            bidNow = (ImageView) itemView.findViewById(R.id.bid_image);
            viewLocation = (ImageView) itemView.findViewById(R.id.iv_item_location);
            tv_current_bid = (TextView) itemView.findViewById(R.id.tv_starting_bid);
            viewUser = itemView.findViewById(R.id.cont_view_profile);
        }
    }

    public void initSwipe() {
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

                if (direction == ItemTouchHelper.LEFT) {
                    //notifyItemRemoved(position);
                    Intent intent = new Intent(context, BidNow.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    context.startActivity(intent);
                } else {
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
