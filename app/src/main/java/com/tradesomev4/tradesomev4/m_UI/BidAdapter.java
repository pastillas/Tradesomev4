package com.tradesomev4.tradesomev4.m_UI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.RequestManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.MyProfile;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.ViewUserProfile;
import com.tradesomev4.tradesomev4.m_Helpers.CalendarUtils;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.DateHelper;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.Auction;
import com.tradesomev4.tradesomev4.m_Model.Bid;
import com.tradesomev4.tradesomev4.m_Model.Notif;
import com.tradesomev4.tradesomev4.m_Model.Participant;
import com.tradesomev4.tradesomev4.m_Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Pastillas-Boy on 7/16/2016.
 */
public class BidAdapter extends RecyclerView.Adapter<BidAdapter.BidHolder> {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String EXTRAS_POSTER_ID = "POSTER_ID";
    private static final String EXTRAS_BUNDLE = "EXTRAS_BUNDLE";
    Context context;
    DatabaseReference databaseReference;
    LayoutInflater layoutInflater;
    String auctionId;
    String posterId;
    int previousPosition = 0;
    FirebaseUser fUser;
    Auction auctionInstance;
    ArrayList<Bid> bids;
    String name, bidderId;
    Auction auction;
    boolean isAttached;
    RecyclerView recyclerView;
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
    public ArrayList<Participant>participants;


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(bids.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && bids.size() == 0)
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
                        snackBars.showConnectionDisabledDialog();
                        isConnectionDisabledShowed = true;
                    }

                    if(timeOuttimer != null)
                        timeOuttimer.cancel();

                    showConnectionError();
                } else {
                    isConnected = true;
                    isConnectionDisabledShowed = false;

                    if(puta != 1 && !isConnectionRestoredShowed){
                        snackBars.showConnectionRestored();
                        isConnectionRestoredShowed = true;
                    }
                    if(bids.size() == 0 && puta == 2){
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if(bids.size() > 0){
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

        if(tv_internet_connection.getVisibility() == View.GONE && bids.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public BidAdapter(Context context, final String auctionId, final String posterId, boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                      final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        this.context = context;
        this.auctionId = auctionId;
        this.layoutInflater = LayoutInflater.from(context);
        this.bids = new ArrayList<>();
        this.posterId = posterId;
        this.auction = new Auction();
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

        this.fUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Query auctionListen = mDatabase.child("auction").child(auctionId);
        auctionListen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                auctionInstance = dataSnapshot.getValue(Auction.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("auction").child(auctionId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                auction = dataSnapshot.getValue(Auction.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query bidQuery = mDatabase.child("auction").child(auctionId).child("bid");
        bidQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Bid bid = dataSnapshot.getValue(Bid.class);
                addBid(bid);
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

    public void addBid(Bid bid) {
        bids.add(bid);
        notifyItemInserted(getItemCount());
        recyclerView.smoothScrollToPosition(getItemCount());

    }

    @Override
    public BidHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = layoutInflater.inflate(R.layout.bid_model, parent, false);
        BidHolder bidHolder = new BidHolder(view);

        if (position > previousPosition)
            AnimationUtil.animate2(bidHolder, true);
        else
            AnimationUtil.animate2(bidHolder, false);
        return bidHolder;
    }

    public void addNotification(Notif notif){
        for(int i = 0; i < participants.size(); i++){
            Participant participant = participants.get(i);

            if(!participant.getId().equals(fUser.getUid())){
                String key = databaseReference.child("users").child(participant.getId()).child("notifs").push().getKey();
                notif.setKey(key);
                databaseReference.child("users").child(participant.getId()).child("notifs").setValue(notif);
            }
        }
    }

    public Notif newNotif(String type){
        Notif notif = new Notif();
        notif.setType(type);
        notif.setAuctionId(auctionId);
        notif.setRead(false);
        notif.setDate(DateHelper.getCurrentDateInMil());

        return  notif;
    }

    public void displayDialog(){
        new MaterialDialog.Builder(context.getApplicationContext())
                .title("Falied")
                .content("Sorry, were having some issues right now. Please try again later thank you.")
                .positiveText("OK")
                .show();
    }

    @Override
    public void onBindViewHolder(final BidHolder holder, final int position) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        Query userRef = mDatabase.child("users").child(bids.get(position).getUserId());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                name = user.getName();
                bidderId = user.getId();

                if (isAttached) {
                    glide.load(user.getImage())
                            .asBitmap().centerCrop()
                            .into(holder.image);
                }

                holder.name.setText(user.getName());
                if (!bids.get(position).getUserId().equals(fUser.getUid())) {
                    holder.image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle args = new Bundle();
                            args.putString(EXTRAS_POSTER_ID, bids.get(position).getUserId());
                            if (!bids.get(position).getUserId().equals(fUser.getUid())) {
                                Intent viewUserProfile = new Intent(context, ViewUserProfile.class);
                                viewUserProfile.putExtra(EXTRAS_BUNDLE, args);
                                context.startActivity(viewUserProfile);
                            } else {
                                Intent myProfile = new Intent(context, MyProfile.class);
                                context.startActivity(myProfile);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        holder.title.setText(bids.get(position).getTitle());
        String date = CalendarUtils.ConvertMilliSecondsToFormattedDate(String.valueOf(bids.get(position).getBidDate()));
        holder.bidDate.setText(date);
        if (position > previousPosition)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);

        previousPosition = position;
        final int currentPosition = position;
        final Bid bidfinal = bids.get(position);

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (auction.getUid().equals(fUser.getUid()) && auctionInstance.isStatus() && !fUser.getUid().equals(bidfinal.getUserId())) {
                    new MaterialDialog.Builder(context)
                            .title("Confirm?")
                            .content("Do you want to accept the bid of " + name + " for " + bidfinal.getTitle() + "?")
                            .negativeText("No")
                            .positiveText("Yes")
                            .onAny(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    String choice = which.name();
                                    final DatabaseReference updateStatus = FirebaseDatabase.getInstance().getReference();
                                    if (choice.equals("POSITIVE")) {
                                        updateStatus.child("auction").child(auctionId).child("status").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                final Bid bid = new Bid();
                                                bid.setAuctionId(auctionId);
                                                bid.setUserId(fUser.getUid());
                                                bid.setTitle("I accept the bid of " + name + ", amount: " + bidfinal.getTitle() + ".");
                                                bid.setBidDate(Long.parseLong(DateHelper.getCurrentDateInMil()));
                                                String key = updateStatus.child("auction").child(auctionId).push().getKey();
                                                Map<String, Object> bidValues = bid.toMap();
                                                Map<String, Object> childUpdate = new HashMap<>();
                                                childUpdate.put("/auction/" + auctionId + "/bid/" + key, bidValues);
                                                updateStatus.updateChildren(childUpdate);

                                                //send notif to auctioner
                                                String content = auction.getItemTitle() + ": item auction success for " + bidfinal.getTitle() + " by " + name;
                                                Notif notif = new Notif();
                                                notif.setPosterId(fUser.getUid());
                                                notif.setBidderId(bidderId);
                                                notif.setContent(content);
                                                notif.setType("finishAuctioner");
                                                notif.setAuctionId(auctionId);
                                                notif.setRead(true);
                                                notif.setDate(DateHelper.getCurrentDateInMil());
                                                String tmpKey = databaseReference.child("users").child(fUser.getUid()).child("notifs").push().getKey();
                                                notif.setKey(tmpKey);
                                                databaseReference.child("users").child(fUser.getUid()).child("notifs").child(tmpKey).setValue(notif);

                                                String content2 = "You won the auctioned item: " + auction.getItemTitle() + " of " + fUser.getDisplayName() + " for " + bidfinal.getTitle();
                                                notif.setContent(content2);
                                                notif.setType("finishWinner");
                                                notif.setRead(false);
                                                String tmpKey2 = databaseReference.child("users").child(bidderId).child("notifs").push().getKey();
                                                notif.setKey(tmpKey2);
                                                databaseReference.child("users").child(bidderId).child("notifs").child(tmpKey2).setValue(notif);

                                                notif.setType("finishBidder");
                                                notif.setContent(fUser.getDisplayName() + " accepted the offer of " + name + " for " + bidfinal.getTitle());
                                                for(int i = 0; i < participants.size(); i++){
                                                    if(!participants.get(i).getId().equals(bidderId) && !participants.get(i).getId().equals(fUser.getUid())){
                                                        Participant participant = participants.get(i);
                                                        String tmpKey3 = databaseReference.child("users").child(participant.getId()).child("notifs").push().getKey();
                                                        notif.setKey(tmpKey3);
                                                        databaseReference.child("users").child(participant.getId()).child("notifs").child(tmpKey3).setValue(notif);
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                displayDialog();
                                            }
                                        });




                                    }

                                }
                            }).show();

                }

                return true;
            }
        });

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle args = new Bundle();
                args.putString(EXTRAS_POSTER_ID, bids.get(position).getUserId());
                if (!bids.get(position).getUserId().equals(fUser.getUid())) {
                    Intent viewUserProfile = new Intent(context, ViewUserProfile.class);
                    viewUserProfile.putExtra(EXTRAS_BUNDLE, args);
                    context.startActivity(viewUserProfile);
                } else {
                    Intent myProfile = new Intent(context, MyProfile.class);
                    context.startActivity(myProfile);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return bids.size();
    }

    class BidHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView title;
        TextView bidDate;
        private View container;

        public BidHolder(View itemView) {
            super(itemView);

            bidDate = (TextView) itemView.findViewById(R.id.tv_bid_date);
            image = (ImageView) itemView.findViewById(R.id.iv_bidder_image);
            name = (TextView) itemView.findViewById(R.id.tv_bidder_name);
            title = (TextView) itemView.findViewById(R.id.tv_amount);
            container = itemView.findViewById(R.id.cont_bid_root);
        }
    }
}
