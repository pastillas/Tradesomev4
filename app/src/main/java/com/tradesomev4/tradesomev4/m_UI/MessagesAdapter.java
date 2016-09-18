package com.tradesomev4.tradesomev4.m_UI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.pnikosis.materialishprogress.ProgressWheel;
import com.tradesomev4.tradesomev4.Filters.MessageFilter;
import com.tradesomev4.tradesomev4.R;
import com.tradesomev4.tradesomev4.SendUserMessage;
import com.tradesomev4.tradesomev4.m_Helpers.Connectivity;
import com.tradesomev4.tradesomev4.m_Helpers.SnackBars;
import com.tradesomev4.tradesomev4.m_Model.MessageRoot;
import com.tradesomev4.tradesomev4.m_Model.User;
import com.tradesomev4.tradesomev4.m_Model.UserMessage;

import java.util.ArrayList;

/**
 * Created by Pastillas-Boy on 7/19/2016.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder> implements Filterable {
    private static final String DEBUG_TAG = "DEBUG_TAG";
    private static final String USER_NAME_KEY = "USER_NAME";
    private static final String USER_IMAGE_KEY = "USER_IMAGE";
    private static final String USER_ID_KEY = "USER_KEY";
    private static final String BUNDLE_EXTRA_KEY = "BUNDLE_EXTRAS";

    public ArrayList<MessageRoot> messageRoots;
    DatabaseReference mDatabase;
    Context context;
    LayoutInflater inflater;
    FirebaseUser fUser;
    int prevPos = 0;
    UserMessage message;
    MessageFilter filter;
    private ValueEventListener users;
    public RecyclerView recyclerView;
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
    View parentView;


    public void timeOut(){
        timeOuttimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                if(messageRoots.size() > 0){
                    hideAll();
                }
            }

            @Override
            public void onFinish() {
                if(isConnected && messageRoots.size() == 0)
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

                    if(isSearching && messageRoots.size() == 0){
                        tv_items_here.setText("Zero match");
                        showItemsHere();
                    }else{
                        if(!isSearching && messageRoots.size() > 0){
                            hideAll();
                        }else{
                            if(!isSearching &&  messageRoots.size() == 0){
                                tv_items_here.setText("Followers appear here.");
                                showItemsHere();
                            }
                        }
                    }

                    if(messageRoots.size() == 0 && puta == 2){
                        Log.d(DEBUG_TAG, "PUTA 2: TRUE");
                        tv_items_here.setText("Followers appear here.");
                        hideAll();
                        showLoading();
                        puta--;
                        timeOut();
                    }
                }

                if(messageRoots.size() > 0){
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

        if(tv_internet_connection.getVisibility() == View.GONE && messageRoots.size() == 0)
            tv_internet_connection.setVisibility(View.VISIBLE);

    }

    public void addMessage(int pos, String message){
        if(!fUser.getUid().equals(message)){
            MessageRoot root = new MessageRoot();
            root.setUserId(message);
            messageRoots.add(pos, root);
            Log.d("MessageId", message);
            notifyItemInserted(pos);
        }
    }

    public MessagesAdapter(Context context,  boolean isAttached, RecyclerView recyclerView, RequestManager glide,
                           final TextView tv_items_here, final TextView tv_internet_connection, final ProgressWheel progress_wheel, View view){
        this.messageRoots = new ArrayList<>();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        fUser = FirebaseAuth.getInstance().getCurrentUser();
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
        parentView = view;

        Query messagesRef = mDatabase.child("messages").child(fUser.getUid());
        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String message = dataSnapshot.getKey();
                addMessage(0, message);

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
    public MessagesHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = inflater.inflate(R.layout.messages_model, parent, false);
        MessagesHolder holder = new MessagesHolder(view);

        if (position > prevPos)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);

        return holder;
    }

    @Override
    public int getItemCount() {
        return messageRoots.size();
    }

    @Override
    public void onBindViewHolder(final MessagesHolder holder, final int position) {
        Query messageRef = mDatabase.child("messages").child(fUser.getUid()).child(messageRoots.get(position).getUserId()).limitToLast(1);
        messageRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                message = dataSnapshot.getValue(UserMessage.class);
                if(!message.isRead()) {
                    holder.senderMessage.setText(message.getMessage());
                    holder.senderMessage.setTypeface(null, Typeface.BOLD);
                    holder.senderName.setTypeface(null, Typeface.BOLD);
                }else{
                    holder.senderMessage.setText(message.getMessage());
                    holder.senderMessage.setTypeface(null, Typeface.NORMAL);
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

        users = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                messageRoots.get(position).setName(user.getName());

                if(!message.isRead()){
                    holder.senderName.setText(user.getName());
                }else{
                    holder.senderName.setText(user.getName());
                    holder.senderName.setTypeface(null, Typeface.NORMAL);
                }

                if(isAttached){
                    glide.load(user.getImage())
                            .asBitmap().centerCrop()
                            .into(holder.senderImage);
                }

                holder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.senderName.setTypeface(null, Typeface.NORMAL);
                        holder.senderMessage.setTypeface(null, Typeface.NORMAL);
                        Bundle extras = new Bundle();
                        extras.putString(USER_NAME_KEY, user.getName());
                        extras.putString(USER_IMAGE_KEY, user.getImage());
                        extras.putString(USER_ID_KEY, user.getId());
                        Intent intent = new Intent(context, SendUserMessage.class);
                        intent.putExtra(BUNDLE_EXTRA_KEY, extras);
                        context.startActivity(intent);
                    }
                });

                holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new MaterialDialog.Builder(context)
                                .title("Delete Message")
                                .content("Are your sure your want to delete this messages?")
                                .positiveText("Yes")
                                .negativeText("No")
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                        if(which.toString().equals("POSITIVE")){
                                            mDatabase.child("messages").child(fUser.getUid()).child(messageRoots.get(position).getUserId()).removeValue();
                                            mDatabase.child("users").child(messageRoots.get(position).getUserId()).removeEventListener(users);
                                            Log.d("alfred", messageRoots.get(position).getUserId());
                                            messageRoots.remove(position);
                                            notifyItemRemoved(position);
                                        }
                                    }
                                })
                                .show();

                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.child("users").child(messageRoots.get(position).getUserId()).addValueEventListener(users);


        if (position > prevPos)
            AnimationUtil.animate(holder, true);
        else
            AnimationUtil.animate(holder, false);

        prevPos = position;
    }

    @Override
    public Filter getFilter() {
        if(filter == null){
            filter = new MessageFilter(this, messageRoots);
        }

        return filter;
    }

    class MessagesHolder extends RecyclerView.ViewHolder{
        ImageView senderImage;
        TextView senderName;
        TextView senderMessage;
        View container;

        public MessagesHolder(View itemView) {
            super(itemView);

            senderImage = (ImageView) itemView.findViewById(R.id.iv_sender_image);
            senderName = (TextView) itemView.findViewById(R.id.tv_sender_name);
            senderMessage = (TextView) itemView.findViewById(R.id.tv_sender_message);
            container =itemView.findViewById(R.id.cont_message_root);
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
                final int pos = viewHolder.getAdapterPosition();
                parentView.setTag(pos);
                final MessageRoot messageRoot= messageRoots.get(pos);
                messageRoots.remove(pos);
                notifyItemRemoved(pos);
                //Snackbar.make(view, R.string.notice_removed, Snackbar.LENGTH_SHORT).show();
                Snackbar.make(parentView, "Message will be deleted.", Snackbar.LENGTH_LONG)
                        .setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                Log.d(DEBUG_TAG, "ONDISMESSED: TRUE" + messageRoot.getUserId());
                                if(isConnected){
                                    //mDatabase.child("bidHistory").child(fUser.getUid()).child(bid.getId()).setValue(null);
                                }else{
                                    messageRoots.add(pos, messageRoot);
                                    notifyItemInserted(pos);
                                    snackBars.showCheckYourConnection();
                                }
                            }
                        })
                        .setAction("UNDO", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                messageRoots.add(pos, messageRoot);
                                notifyItemInserted(pos);
                            }
                        })
                        .show();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
}
