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
import com.tradesomev4.tradesomev4.EditPost;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.ViewMyItem;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.AuctionHistory;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/25/2016.
 */
public class MyAuctionsAdapter extends RecyclerView.Adapter<MyAuctionsAdapter.MyAuctionsHolder> {
    private static final String AUCTION_ID_KEY = "AUCTION_KEY";
    private static final String BUNDLE_KEY = "BUNDLE_KEY";


    private static final String EXTRAS_AUCTION_ID = "AUCTION_ID";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";

    private static final String DEBUG_TAG = "DEBUG_TAG";
    public Context context;
    public LayoutInflater inflater;
    public ArrayList<AuctionHistory> auctionHistories;
    public DatabaseReference mDatabase;
    public FirebaseUser fUser;
    public User user;
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
    int counter;


    public void timeOut() {
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if (auctionHistories.size() > 0) {
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if (isConnected && auctionHistories.size() == 0)
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
                        //snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }

                    if (timeOuttimer != null)
                        timeOuttimer.cancel();

                    showConnectionError();
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if (puta != 1 && !isConnectionRestoredShowed) {
                        //snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }
                    if (auctionHistories.size() == 0 && puta == 2) {
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if (auctionHistories.size() > 0) {
                    //hideAll();
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

        if (tv_internet_connection.getVisibility() == View.GONE && auctionHistories.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }


    public void addAuctionHistory(int position, AuctionHistory history) {
        auctionHistories.add(position, history);
        notifyItemInserted(position);
    }

    public MyAuctionsAdapter(Context context, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                             final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view) {
        snackBars = new SnackBars(view, context.getApplicationContext());
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.auctionHistories = new ArrayList<>();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.fUser = FirebaseAuth.getInstance().getCurrentUser();
        this.user = new User();
        this.isAttached = isAttached;
        this.recyclerView = recyclerView;
        this.glide = glide;
        this.tv_items_here = tv_items_here;
        this.tv_internet_connection = tv_internet_connection;
        this.progress_wheel = progress_wheel;
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;
        snackBars = new SnackBars(view, context.getApplicationContext());
        puta = 1;
        initSwipe();

        mDatabase.child("auctionHistory").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("auctionHistory").child(fUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                addAuctionHistory(0, dataSnapshot.getValue(AuctionHistory.class));

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

        mDatabase.child("users").child(fUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
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
    public MyAuctionsHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.my_auctions_model, parent, false);
        MyAuctionsHolder holder = new MyAuctionsHolder(view);

        if (position > prevPos) {
            AnimationUtil.animate(holder, true);
        } else {
            AnimationUtil.animate(holder, false);
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(final MyAuctionsHolder holder, final int position) {

        mDatabase.child("auction").child(auctionHistories.get(position).getAuctionId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Auction auction = dataSnapshot.getValue(Auction.class);

                    if (isAttached) {
                        glide
                                .load(user.getImage())
                                .asBitmap().centerCrop()
                                .into(holder.posterImage);
                    }

                    holder.posterName.setText(user.getName());

                    if (isAttached) {
                        glide
                                .load(auction.getImage1Uri())
                                .asBitmap().centerCrop()
                                .into(holder.itemImage);
                    }
                    holder.title.setText(auction.getItemTitle());

                    String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(auction.getDirectoryName());
                    holder.datePost.setText(date);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("auction").child(auctionHistories.get(position).getAuctionId()).child("bid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                holder.bids.setText(" " + String.valueOf(dataSnapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString(AUCTION_ID_KEY, auctionHistories.get(position).getAuctionId());
                Intent editPost = new Intent(context, EditPost.class);
                editPost.putExtra(BUNDLE_KEY, args);
                context.startActivity(editPost);
            }
        });

        final Bundle extras = new Bundle();
        extras.putString(EXTRAS_AUCTION_ID, auctionHistories.get(position).getAuctionId());
        extras.putString(EXTRAS_POSTER_ID, auctionHistories.get(position).getPosterId());

        holder.bidNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bidNow = new Intent(context, BidNow.class);
                bidNow.putExtra(EXTRAS_BUNDLE, extras);
                context.startActivity(bidNow);
            }
        });

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewItem = new Intent(context, ViewMyItem.class);
                viewItem.putExtra(EXTRAS_BUNDLE, extras);
                context.startActivity(viewItem);
            }
        });

        if (position > prevPos) {
            AnimationUtil.animate(holder, true);
        } else {
            AnimationUtil.animate(holder, false);
        }

        prevPos = position;
    }

    @Override
    public int getItemCount() {
        return auctionHistories.size();
    }

    class MyAuctionsHolder extends RecyclerView.ViewHolder {
        TextView posterName;
        ImageView posterImage;
        ImageView itemImage;
        TextView title;
        TextView bids;
        TextView datePost;
        ImageView bidNow;
        View viewUser;
        ImageView edit;

        public MyAuctionsHolder(View itemView) {
            super(itemView);

            edit = (ImageView) itemView.findViewById(R.id.iv_edit_item);
            posterName = (TextView) itemView.findViewById(R.id.poster_name);
            posterImage = (ImageView) itemView.findViewById(R.id.poster_image);
            itemImage = (ImageView) itemView.findViewById(R.id.item_image_view1);
            title = (TextView) itemView.findViewById(R.id.tv_item_title);
            bids = (TextView) itemView.findViewById(R.id.tv_total_bids);
            datePost = (TextView) itemView.findViewById(R.id.tv_date_post);
            bidNow = (ImageView) itemView.findViewById(R.id.bid_image);
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

                final Bundle extras = new Bundle();
                extras.putString(EXTRAS_AUCTION_ID, auctionHistories.get(position).getAuctionId());
                extras.putString(EXTRAS_POSTER_ID, auctionHistories.get(position).getPosterId());

                if (direction == ItemTouchHelper.LEFT) {
                    Intent intent = new Intent(context, BidNow.class);
                    intent.putExtra(EXTRAS_BUNDLE, extras);
                    context.startActivity(intent);
                } else {
                    Bundle args = new Bundle();
                    args.putString(AUCTION_ID_KEY, auctionHistories.get(position).getAuctionId());
                    Intent editPost = new Intent(context, EditPost.class);
                    editPost.putExtra(BUNDLE_KEY, args);
                    context.startActivity(editPost);
                }
            }
        };


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

}
