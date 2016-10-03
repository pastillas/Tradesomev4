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
import android.widget.RatingBar;
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
import com.tradesomev4.tradesomev4.MyProfile;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.ViewUserProfile;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Participant;
import com.tradesomev4.tradesomev4.m_Model.Rate;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 9/21/2016.
 */
public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> implements Filterable {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    Context context;
    LayoutInflater inflater;
    ArrayList<Participant> participants;
    String auctionId;
    String posterId;
    ArrayList<Rate>rates;
    float total;
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
    boolean isAttached;


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(participants.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && participants.size() == 0)
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
                    if(participants.size() == 0 && puta == 2){
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if (participants.size() > 0) {
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

        if(tv_internet_connection.getVisibility() == View.GONE && participants.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public ParticipantAdapter(Context context, String auctionId, String posterId,  boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                              final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view){
        this.auctionId = auctionId;
        this.posterId = posterId;
        this.context = context;
        this.glide = glide;
        inflater = LayoutInflater.from(context);
        participants = new ArrayList<Participant>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rates = new ArrayList<>();
        total = 0;
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

        Log.d(DEBUG_TAG, "Auction ID: " + auctionId);
        databaseReference.child("auction").child(auctionId).child("participants").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Participant participant = dataSnapshot.getValue(Participant.class);
                participants.add(participant);
                notifyItemInserted(participants.size());

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

    @Override
    public ParticipantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.participant_model, parent, false);
        ParticipantViewHolder holder = new ParticipantViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ParticipantViewHolder holder, int position) {
        AnimationUtil.setFadeAnimation(holder.itemView);
        final Participant participant = participants.get(position);
        databaseReference.child("users").child(participant.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                if(participant.getId().equals(posterId))
                    holder.tv_name.setText(user.getName() + " (Owner)");
                else
                    holder.tv_name.setText(user.getName());

                glide.load(user.getImage())
                        .asBitmap().centerCrop()
                        .into(holder.iv_bidder_image);

                getRating(user.getId(), holder);

                holder.cont_bid_root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(firebaseUser.getUid().equals(posterId)){
                            Intent intent = new Intent(context.getApplicationContext(), MyProfile.class);
                            context.startActivity(intent);
                        }else{
                            Intent intent = new Intent(context.getApplicationContext(), ViewUserProfile.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(EXTRAS_POSTER_ID, user.getId());
                            intent.putExtra(EXTRAS_BUNDLE, bundle);
                            context.startActivity(intent);
                        }
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void getRating(String id, final ParticipantViewHolder holder){

        databaseReference.child("users").child(id).child("rating").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Rate rate = dataSnapshot.getValue(Rate.class);
                rates.add(rate);
                total += rate.getRate();
                holder.rb_bidder_rate.setRating(total/rates.size());
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
    public int getItemCount() {
        return participants.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder {
        RatingBar rb_bidder_rate;
        TextView tv_name;
        ImageView iv_bidder_image;
        View cont_bid_root;

        public ParticipantViewHolder(View itemView) {
            super(itemView);

            cont_bid_root = itemView.findViewById(R.id.cont_bid_root);
            rb_bidder_rate = (RatingBar)itemView.findViewById(R.id.rb_bidder_rate);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_bidder_image = (ImageView)itemView.findViewById(R.id.iv_bidder_image);
        }
    }
}
