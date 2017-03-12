package com.tradesomev4.tradesomev4.m_UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
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
import com.tradesomev4.tradesomev4.m_Helpers.Keys;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Notif;

import java.util.ArrayList;

/**/

/**
 * Created by Jorge Benigno Pante, Charles Torrente, Joshua Alarcon on 7/17/2016.
 * File name: NoftifAdapter.java
 * File Path: Tradesomev4\app\src\main\java\com\tradesomev4\tradesomev4\m_UI\NotifAdapter.java
 * Description: Fetch and display all notifications from firebase to client's android device.
 */
public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.NotifHolder> {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    ArrayList<Notif> notifs;
    Context context;
    LayoutInflater inflater;
    String userId;
    DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    int prevPos = 0;
    boolean isAttached;
    RecyclerView recyclerView;
    TextView tv_items_here;
    TextView tv_internet_connection;
    ProgressWheel progress_wheel;
    RequestManager glide;
    boolean isConnected;
    boolean isConnectionDisabledShowed;
    boolean isConnectionRestoredShowed;
    SnackBars snackBars;
    CountDownTimer timeOuttimer;
    View parentView;
    int puta;


    public void timeOut() {
        showLoading();
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if (notifs.size() > 0) {
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if (isConnected && notifs.size() == 0)
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
                    if (notifs.size() == 0 && puta == 2) {
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if (notifs.size() == 0) {
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

        if (tv_internet_connection.getVisibility() == View.GONE && notifs.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }


    public NotifAdapter(Context context, String userId, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                        final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view) {
        this.notifs = new ArrayList<>();
        this.context = context;
        this.userId = userId;
        this.inflater = LayoutInflater.from(context);
        this.isAttached = isAttached;
        this.recyclerView = recyclerView;
        this.glide = glide;
        this.tv_items_here = tv_items_here;
        this.tv_internet_connection = tv_internet_connection;
        this.progress_wheel = progress_wheel;
        isConnectionDisabledShowed = false;
        isConnectionRestoredShowed = false;
        parentView = view;
        snackBars = new SnackBars(view, context.getApplicationContext());
        puta = 1;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(firebaseUser.getUid()).child("notifs").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Notif notif = dataSnapshot.getValue(Notif.class);

                if (!notif.getType().equals("message")) {
                    addItem(0, notif);
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

    public void addItem(int position, Notif notif) {
        notifs.add(position, notif);
        notifyItemInserted(position);
    }

    @Override
    public NotifHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.notif_model, parent, false);
        NotifHolder notifHolder = new NotifHolder(view);

        return notifHolder;
    }

    //bid, auctioner, finishAuctioner, finishBidder, finishWinner, itemComplain, userComplain,
    @Override
    public void onBindViewHolder(final NotifHolder holder, final int position) {
        AnimationUtil.setFadeAnimation(holder.itemView);

        final Notif notif = notifs.get(position);
        try {
            Log.d(Keys.DEBUG_TAG, notif.getContent() + " null");
            holder.tv_item_title.setText(notif.getContent());
            String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(notif.getDate());
            holder.date.setText(date);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Log.d(Keys.DEBUG_TAG, notif.getType() + ": TRUE");
        if (notif.getType().equals("bid") || notif.getType().equals("auctioner") || notif.getType().equals("finishAuctioner") || notif.getType().equals("finishBidder") || notif.getType().equals("finishWinner")) {

            mDatabase.child("auction").child(notif.getAuctionId()).child("image1Uri").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        String tmp = dataSnapshot.getValue(String.class);

                        if (!tmp.isEmpty())
                            glide.load(tmp)
                                    .asBitmap().centerCrop()
                                    .into(holder.item_image_view1);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            if (notif.isRead()) {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            } else {
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGray));
            }

            final Bundle extras = new Bundle();
            extras.putString(Keys.EXTRAS_AUCTION_ID, notif.getAuctionId());
            extras.putString(Keys.EXTRAS_POSTER_ID, notif.getPosterId());


            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDatabase.child("users").child(firebaseUser.getUid()).child("notifs").child(notif.getKey()).child("read").setValue(true);

                    if (!notif.isRead()) {
                        holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                    }

                    Intent intent = new Intent(context.getApplicationContext(), BidNow.class);
                    intent.putExtra(Keys.EXTRAS_BUNDLE, extras);
                    context.startActivity(intent);
                }
            });
        } else {
            if (notif.getType() == "itemComplain" || notif.getType() == "userComplain") {
                holder.item_image_view1.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.logo));
                holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.orange));
            }
        }
    }

    @Override
    public int getItemCount() {
        return notifs.size();
    }

    class NotifHolder extends RecyclerView.ViewHolder {
        ImageView item_image_view1;
        TextView tv_item_title;
        TextView date;
        View container;

        public NotifHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.cont);
            item_image_view1 = (ImageView) itemView.findViewById(R.id.item_image_view1);
            tv_item_title = (TextView) itemView.findViewById(R.id.tv_item_title);
            date = (TextView) itemView.findViewById(R.id.date);
        }
    }

}
